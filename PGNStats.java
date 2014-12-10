import java.util.*;

public class PGNStats{
	
	String[] moves;
	public PGNStats(String[] moves) {
		this.moves = moves;
	}
	
	public int[][] pieceMobility(int code) {

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

					//skip other pieces
					if (p.code != code)
						continue;
					
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

					//skip other pieces
					if (p.code != -code)
						continue;

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

	public int[][] doubledPawns() {

		PGNParser parser = new PGNParser();
		int side = PGNParser.WHITE;
		int[] whiteD = new int[moves.length];
		int[] blackD = new int[moves.length];
		int k = 0;

		for (String move : moves) {

			parser.procMove(move, side);
			int wp = 0;
			int bp = 0;

			for (int f = 0; f < 8; f++) {
				int white = 0;
				int black = 0;
				for (int r = 0; r < 8; r++) {
					if (parser.board[r][f] == PGNParser.WHITE_PAWN)
						white++;
					if (parser.board[r][f] == PGNParser.BLACK_PAWN)
						black++;
				}
				wp += white > 1 ? white : 0;
				bp += black > 1 ? black : 0;
			}
			side *= -1;
			whiteD[k] = wp;
			blackD[k] = bp;
			k++;
		}
		return new int[][]{whiteD, blackD};
	}

	public static void main(String[] args) {

		String x = "1. e4 d5 2. exd5 Qxd5 3. Nc3 Qd8 4. d4 Nf6 5. Nf3 Bg4 6. h3 Bxf3 7. Qxf3 c6 "+
			"8. Be3 e6 9. Bd3 Nbd7 10. O-O Bd6 11. Ne4 Nxe4 12. Bxe4 Nf6 13. Bd3 Nd5 14. "+
			"Bd2 Qf6 15. Qxf6 gxf6 16. Be4 f5 17. Bf3 O-O-O 18. Rfd1 Be7 19. c4 Nb6 20. "+
			"Ba5 Rd7 21. Bxb6 axb6 22. d5 cxd5 23. cxd5 e5 24. Rac1+ Kb8 25. Be2 Rhd8 "+
			"26. Bc4 Bc5 27. Kf1 Rd6 28. Rc3 f4 29. Rc2 Rg8 30. Re2 Rdg6 31. Rxe5 Rxg2 "+
			"32. Re8+ Kc7 33. Rxg8 Rxg8 34. d6+ Bxd6 35. Bxf7 Rg5 36. Bd5 Be5 37. b3 b5 " +
			"38. Bf3 b4 39. Rd5 Bf6 40. Rxg5 Bxg5 41. Ke2 b6 1/2-1/2";

		x = x.replaceAll("\\d+\\.\\s*", "").replaceAll("\\s*\\d-\\d", "").replaceAll("\\d\\/\\/\\d", "");
		String[] pgn = x.split(" ");

		PGNStats stats = new PGNStats(pgn);
		int[][] doubledPawns = stats.doubledPawns();
		
		System.out.print("white_double,black_double");
		for (int i = 0; i < doubledPawns[0].length; i++){
			System.out.println();
			System.out.print(doubledPawns[0][i] + "," + doubledPawns[1][i]);
		}

	}

}