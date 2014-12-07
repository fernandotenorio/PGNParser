import java.util.*;

public class PGNStats{
	
	String[] moves;
	public PGNStats(String[] moves) {
		this.moves = moves;
	}

	public int[][] centralDominance() {

		PGNParser parser = new PGNParser();
		int side = PGNParser.WHITE;
		
		List<List<Piece>> whitePieces = parser.getWhitePieces();
		List<List<Piece>> blackPieces = parser.getBlackPieces();
		int[] whiteCentral = new int[moves.length];
		int[] whiteMob = new int[moves.length];
		int[] blackCentral = new int[moves.length];
		int[] blackMob = new int[moves.length];

		int k = 0;
		for (String move : moves){
			parser.procMove(move, side);
			int cWhite = 0;			
			int mWhite = 0;
			int cBlack = 0;
			int mBlack = 0;

			//white
			for (List<Piece> pieceList: whitePieces){
				for (Piece p : pieceList){
					List<Square> moves = parser.getMovesForPiece(p, PGNParser.WHITE);					
					mWhite += moves.size();

					if (moves.size() == 0)
						continue;

					for (Square sq : moves) {
						if (sq.isCentral())
							cWhite++;
					}
				}	
			}

			//black
			for (List<Piece> pieceList: blackPieces){
				for (Piece p : pieceList){
					List<Square> moves = parser.getMovesForPiece(p, PGNParser.BLACK);
					mBlack += moves.size();

					if (moves.size() == 0)
						continue;
					
					for (Square sq : moves) {
						if (sq.isCentral())
							cBlack++;
					}
				}	
			}
			whiteCentral[k] = cWhite;
			whiteMob[k] = mWhite;
			blackCentral[k] = cBlack;
			blackMob[k] = mBlack;
			k++;
			side *= -1;
		}
		return new int[][]{whiteCentral, whiteMob, blackCentral, blackMob};
	}

	public static void main(String[] args) {

		String x = "1. d4 d5 2. c4 c6 3. Nc3 Nf6 4. cxd5 cxd5 5. Bf4 Nc6 6. e3 Bg4 7. f3 Bd7 8." +
					"Bd3 g6 9. Bg5 e5 10. Nge2 exd4 11. exd4 Bg7 12. O-O h6 13. Bh4 Ne7 14. Qb3 "+
					"Bc6 15. f4 Qb6 16. Qa3 a5 17. Qd6 Qd8 18. Qe5 Nh5 19. Qe3 Nf6 20. f5 g5 21."+
					"Bg3 Qd7 22. h3 Kf8 23. Be5 Re8 24. a4 Kg8 25. Ng3 Nc8 26. Nb5 Nd6 27. b3 "+
					"Nxb5 28. axb5 Bxb5 29. Rxa5 Bxd3 30. Qxd3 h5 31. Ne2 Rh6 32. Nc3 Rc8 33."+
					"Rc5 Rc6 34. Qe3 b6 35. Rxc6 Qxc6 36. Rc1 g4 37. Bf4 Rh8 38. Ne4 Qd7 39."+
					"Nxf6+ Bxf6 40. Rc7 Qd8 41. Be5 Bxe5 42. Qxe5 Rh6 43. h4 Qf6 44. Qxd5 Kg7 "+
					"45. g3 Rh8 46. Rd7 Rc8 47. Rd6 Rc1+ 48. Kf2 Rc2+ 49. Ke3 Qe7+ 50. Qe5+ Kf8 "+
					"51. Qxe7+ Kxe7 52. Rxb6 Rc3+ 53. Kf4 f6 54. Re6+ Kf7 55. Re3 Rc1 56. Ke4 "+
					"Ke7 57. Kd5+ Kd7 58. Re6 Rc3 59. Rxf6 Rxg3 60. Rh6 Rxb3 61. Rxh5 g3 62. Rg5 "+
					"Ke7 63. Rg6 1-0";

		x = x.replaceAll("\\d+\\.\\s*", "").replaceAll("\\s*\\d-\\d", "").replaceAll("\\d\\/\\/\\d", "");
		String[] pgn = x.split(" ");

		PGNStats stats = new PGNStats(pgn);
		int[][] dominance = stats.centralDominance();
		
		System.out.print("white_central,white_mobility,black_central,black_mobility");
		for (int i = 0; i < dominance[0].length; i++){
			System.out.println();
			System.out.print(dominance[0][i] + "," + dominance[1][i] + "," + dominance[2][i] + "," + dominance[3][i]);
		}

	}

}