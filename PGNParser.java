import java.util.*;

class Piece {

	int code;
	int[] square;
	String str;
	boolean captured;

	public Piece(int code, int r, int f, String str) {
		this.code = code;
		this.square = new int[]{r, f};
		this.str = str;
	}
}

class Square {
	int r;
	int f;

	public Square(int r, int f){
		if (r < 0 || r > 7 || f < 0 || f > 7)
			System.out.println("Invalid square: out of range");
		this.r = r;
		this.f = f;
	}

	public String toString() {
		return "r= " + r + " f= " + f;
	}

	public boolean equals(Object other){
		Square sq = (Square)other;
		return r == sq.r && f == sq.f;
	}

	public String toNotation() {
		return PGNParser.FILES[f] + PGNParser.RANKS[r];
	}
}

public class PGNParser {

	static int UNKNOW_MOVE = -50;

	static String[] RANKS = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
	static String[] FILES = new String[]{"a", "b", "c", "d", "e", "f", "g", "h"};
	static int WHITE = 100;
	static int BLACK = -100;

	static int MOVE = 300;
	static int CAPTURE = 400;
	static int PROMOTION = 500;
	static int PROMOTION_CAPTURE = 600;

	static int EMPTY = 0;
	static int CASTLE_QS = -10;
	static int CASTLE_KS = 10;

	static int WHITE_PAWN = 1;
	static int WHITE_ROOK = 2;
	static int WHITE_KNIGHT = 3;
	static int WHITE_BISHOP = 4;
	static int WHITE_QUEEN = 5;
	static int WHITE_KING = 6;

	static int BLACK_PAWN = -1;
	static int BLACK_ROOK = -2;
	static int BLACK_KNIGHT = -3;
	static int BLACK_BISHOP = -4;
	static int BLACK_QUEEN = -5;
	static int BLACK_KING = -6;

	static int[][] KNIGHT_DIRS = new int[][]{{2, -2}, {1, -1}, {1, -1}, {2, -2}};
	static int[][] BISHOP_DIRS = new int[][]{{1, -1}, {1, 1}, {-1, -1}, {-1, 1}};
	static int[][] ROOK_DIRS = new int[][]{{1, 0}, {-1, 0}, {0, -1}, {0, 1}};

	//board[r][f] is rank r + 1 file f + 1
	private int[][] board = new int[8][8];
	private int white_castle;
	private int black_castle;

	private Piece[] white_pawns = new Piece[8];
	private Piece[] white_rooks = new Piece[16];
	private Piece[] white_knights = new Piece[16];
	private Piece[] white_bishops = new Piece[16];
	private Piece[] white_queens = new Piece[16];
	private Piece white_king = new Piece(WHITE_KING, 0, 4, "K");

	private Piece[] black_pawns = new Piece[8];
	private Piece[] black_rooks = new Piece[16];
	private Piece[] black_knights = new Piece[16];
	private Piece[] black_bishops = new Piece[16];
	private Piece[] black_queens = new Piece[16];
	private Piece black_king = new Piece(BLACK_KING, 7, 4, "K");

	private void addToBoard(Piece[] pieces){

		for (Piece p : pieces){
			if (p != null)
				addToBoard(p);
		}
	}

	private void addToBoard(Piece p){

		if (p != null) {
			int r = p.square[0];
			int f = p.square[1];
			board[r][f] = p.code;
		}
	}

	// white pieces as base, color here does not matter
	public static int[] getMoveInfo(String mv){

		mv = mv.trim();
		int len = mv.length();
		boolean pawnMove = false;

		for (String f:FILES){
			if (mv.startsWith(f) && (!mv.contains("="))) {
				if (mv.contains("x"))
					return new int[]{CAPTURE, WHITE_PAWN};
				else
					return new int[]{MOVE, WHITE_PAWN};
			}
		}

		if (mv.equals("O-O"))
			return new int[]{CASTLE_KS, 0};
		if (mv.equals("O-O-O"))
			return new int[]{CASTLE_QS, 0};

		if (mv.startsWith("R")){
			if (mv.contains("x"))
				return new int[]{CAPTURE, WHITE_ROOK};
			else 
				return new int[]{MOVE, WHITE_ROOK};
		}
		if (mv.startsWith("N")){
			if (mv.contains("x"))
				return new int[]{CAPTURE, WHITE_KNIGHT};
			else 
				return new int[]{MOVE, WHITE_KNIGHT};
		}
		if (mv.startsWith("B")){
			if (mv.contains("x"))
				return new int[]{CAPTURE, WHITE_BISHOP};
			else 
				return new int[]{MOVE, WHITE_BISHOP};
		}
		if (mv.startsWith("Q")){
			if (mv.contains("x"))
				return new int[]{CAPTURE, WHITE_QUEEN};
			else 
				return new int[]{MOVE, WHITE_QUEEN};
		}
		if (mv.startsWith("K")){
			if (mv.contains("x"))
				return new int[]{CAPTURE, WHITE_KING};
			else 
				return new int[]{MOVE, WHITE_KING};
		}

		if (mv.endsWith("=Q") || mv.endsWith("=Q+")) {
			if (mv.contains("x"))
				return new int[]{PROMOTION_CAPTURE, WHITE_QUEEN};
			else
				return new int[]{PROMOTION, WHITE_QUEEN};
		}
			
		if (mv.endsWith("=R") || mv.endsWith("=R+")){
			if (mv.contains("x"))
				return new int[]{PROMOTION_CAPTURE, WHITE_ROOK};
			else
				return new int[]{PROMOTION, WHITE_ROOK};
		}
			
		if (mv.endsWith("=N") || mv.endsWith("=N+")){
			if (mv.contains("x"))
				return new int[]{PROMOTION_CAPTURE, WHITE_KNIGHT};
			else
				return new int[]{PROMOTION, WHITE_KNIGHT};
		}
			
		if (mv.endsWith("=B") || mv.endsWith("=B+")){
			if (mv.contains("x"))
				return new int[]{PROMOTION_CAPTURE, WHITE_BISHOP};
			else
				return new int[]{PROMOTION, WHITE_BISHOP};
		}
		return null;
	}

	public static String codeToStr(int code) {

		if (code == WHITE_PAWN || code == BLACK_PAWN)
			return "P";
		else if (code == WHITE_ROOK || code == BLACK_ROOK)
			return "R";
		else if (code == WHITE_KNIGHT || code == BLACK_KNIGHT)
			return "N";
		else if (code == WHITE_BISHOP || code == BLACK_BISHOP)
			return "B";
		else if (code == WHITE_QUEEN || code == BLACK_QUEEN)
			return "Q";
		else if (code == WHITE_KING || code == BLACK_KING)
			return "K";
		else if (code == 0)
			return "+";
		else
			return null;
	}

	public void printBoard() {

		for (int r = 7; r >= 0; r--) {
			for (int f = 0; f < 8; f++)
				System.out.print(codeToStr(board[r][f]) + " ");
			System.out.println();
		}
	}

	public void initPosition() {		

		//white
		for (int i = 0; i < 8; i++)
			white_pawns[i] = new Piece(WHITE_PAWN, 1, i, "");
		white_rooks[0] = new Piece(WHITE_ROOK, 0, 0, "R");
		white_rooks[1] = new Piece(WHITE_ROOK, 0, 7, "R");
		white_knights[0] = new Piece(WHITE_KNIGHT, 0, 1, "N");
		white_knights[1] = new Piece(WHITE_KNIGHT, 0, 6, "N");
		white_bishops[0] = new Piece(WHITE_BISHOP, 0, 2, "B");
		white_bishops[1] = new Piece(WHITE_BISHOP, 0, 5, "B");
		white_queens[0] = new Piece(WHITE_QUEEN, 0, 3, "Q");

		//black
		for (int i = 0; i < 8; i++)
			black_pawns[i] = new Piece(BLACK_PAWN, 6, i, "");
		black_rooks[0] = new Piece(BLACK_ROOK, 7, 0, "R");
		black_rooks[1] = new Piece(BLACK_ROOK, 7, 7, "R");
		black_knights[0] = new Piece(BLACK_KNIGHT, 7, 1, "N");
		black_knights[1] = new Piece(BLACK_KNIGHT, 7, 6, "N");
		black_bishops[0] = new Piece(BLACK_BISHOP, 7, 2, "B");
		black_bishops[1] = new Piece(BLACK_BISHOP, 7, 5, "B");
		black_queens[0] = new Piece(BLACK_QUEEN, 7, 3, "Q");

		//board
		addToBoard(white_pawns);
		addToBoard(white_rooks);
		addToBoard(white_knights);
		addToBoard(white_bishops);
		addToBoard(white_queens);
		addToBoard(white_king);

		addToBoard(black_pawns);
		addToBoard(black_rooks);
		addToBoard(black_knights);
		addToBoard(black_bishops);
		addToBoard(black_queens);
		addToBoard(black_king);
	}

	public static List<Square> getKnightAttackSquares(int r, int f){

		List<Square> squares = new ArrayList<Square>();

		for (int d = 0; d < KNIGHT_DIRS.length; d+= 2){
			int[] up = KNIGHT_DIRS[d];
			int[] left = KNIGHT_DIRS[d + 1];

			for (int i = 0; i < up.length; i++) {
				for (int j = 0; j < left.length; j++){
					int ri = r + up[i];
					int fi = f + left[j];

					if (ri >= 0 && ri < 8 && fi >= 0 && fi < 8)
						squares.add(new Square(ri, fi));
				}
			}		
		}

		return squares;
	}

	public static List<Square> getDirectionAttackSquares(int r, int f, int[][] direction){

		List<Square> squares = new ArrayList<Square>();

		for (int d = 0; d < direction.length; d++){
			int[] dir = direction[d];

			int dy = dir[0];
			int dx = dir[1];
			int i = 1;
			while (r + i*dy >= 0 && r + i*dy < 8 && f + i*dx >= 0 && f + i*dx < 8) {
				squares.add(new Square(r + i*dy, f + i*dx));
				i++;
			}
		}

		return squares;
	}

	public static List<Square> getBishopAttackSquares(int r, int f){

		return getDirectionAttackSquares(r, f, BISHOP_DIRS);
	}

	public static List<Square> getRookAttackSquares(int r, int f){

		return getDirectionAttackSquares(r, f, ROOK_DIRS);
	}

	public static List<Square> getQueenAttackSquares(int r, int f){

		List<Square> rook = getRookAttackSquares(r, f);
		List<Square> bishop = getBishopAttackSquares(r, f);
		bishop.addAll(rook);

		return bishop;
	}

	//TODO en-passant rule
	public static List<Square> getPawnAttackSquares(int r, int f, int side){

		if ((side == WHITE && r == 0) || (side == BLACK && r == 7)) {
			System.out.println("Invalid pawn position");
			return null;
		}

		List<Square> squares = new ArrayList<Square>();
		int[] dir = null;

		int dy = side == WHITE ? 1:-1;
		int ri = r + dy;
		int fi = f - 1;

		if (ri >= 0 && ri < 8 && fi >= 0 && fi < 8)
			squares.add(new Square(ri, fi));
		
		ri = r + dy;
		fi = f + 1;
		if (ri >= 0 && ri < 8 && fi >= 0 && fi < 8)
			squares.add(new Square(ri, fi));
		
		return squares;
	}

	public static List<Square> getPawnMoveSquares(int r, int f, int side){

		if ((side == WHITE && r == 0) || (side == BLACK && r == 7)) {
			System.out.println("Invalid pawn position");
			return null;
		}

		List<Square> squares = new ArrayList<Square>();
		int dy = side == WHITE ? 1:-1;

		int ri = r + dy;
		int fi = f;

		if (ri >= 0 && ri < 8 && fi >= 0 && fi < 8)
			squares.add(new Square(ri, fi));

		if (side == WHITE && r == 1)
			squares.add(new Square(r + 2, fi));
		else if (side == BLACK && r == 6)
			squares.add(new Square(r - 2, fi));

		return squares;
	}

	// square to: always last 2 chars? (if ignore = +)
	public static int[] getRankFile(String mv){

		String rankStr = mv.replaceAll("\\D+","");
		int rank = -1;

		if (rankStr.length() > 1) {
			String[] tk = rankStr.split("");
			rank = Integer.parseInt(tk[tk.length - 1]) - 1;
		}
		else
			rank = Integer.parseInt(rankStr) - 1;

		int file = -1;
		int[] idxs = new int[]{rank, -1}; //rankTo, fileTo, fileFrom
		String[] tokens = mv.trim().split("");

		for (int i = tokens.length - 1; i >= 0; i--) {
			for (int j = 0; j < FILES.length; j++)
				if (FILES[j].equals(tokens[i])){
					idxs[1] = j;
					return idxs;
				}
		}
		return null;
	}

	public static boolean isAmbigRank(String mv) {

		String m = mv.replaceAll("\\D+","").trim();
		return m.length() > 1;
	}

	public static boolean isAmbigFile(String mv) {

		String m = mv.replaceAll("\\d+|[RNBQKx=+#]+", "");
		return m.length() > 1;
	}

	public void procMove(String mv, int color){

		int[] moveInfo = getMoveInfo(mv);

		if (moveInfo[0] == CAPTURE) {
			int piece = moveInfo[1];
		}
		else if(moveInfo[0] == MOVE){
			int piece = moveInfo[1];
		}
		else if (moveInfo[0] == CASTLE_KS){
			
		}
		else if (moveInfo[0] == CASTLE_QS){
			
		}
		else if (moveInfo[0] == PROMOTION_CAPTURE){
			int promoteTo = moveInfo[1];
		}
		else if (moveInfo[0] == PROMOTION){
			int promoteTo = moveInfo[1];	
		}
		else {
			System.out.println("Invalid move type: " + moveInfo[0]);
		}
		
	}

	public static void main(String[] args) {
		PGNParser p = new PGNParser();
		p.initPosition();
		p.printBoard();

		//List<Square> sq = getKnightAttackSquares(0, 1);
		//List<Square> sq = getRookAttackSquares(4, 4);
		List<Square> sq = getPawnAttackSquares(2, 3, WHITE);
		// for (Square s:sq)
		// 	System.out.println(s.toNotation());

		String[] moves = new String[]{"Nf3", "Nf6", "c4", "g6", "Nc3", "Bg7", "d4", "O-O", "g3", "c6", "Bg2", "d5", "b3", "dxc4",
					"bxc4", "c5", "e3", "Nc6", "O-O", "Bf5", "Ne5", "cxd4", "Nxc6", "bxc6", "exd4", "Qb6",
					"Ba3", "Qa6", "Qa4", "Qxa4", "Nxa4", "Rfd8", "Bxc6", "Rac8", "d5", "Bd3", "Bxe7", "Bxf1",
					"Rxf1", "Ne4", "Bxd8", "Rxd8", "c5", "Bd4", "Rc1", "Nxf2", "Kg2", "Ng4", "d6", "Kg7",
					"Bb5", "f5", "c6", "Rxd6", "c7", "Be3", "Rc2", "Bb6", "c8=Q", "Ne3+", "Kf3"};

		for(String mv:moves){
			//p.procMove(mv, 0);
			if (mv.equals("O-O") || mv.equals("O-O-O"))
				continue;
			
			int[] r = getRankFile(mv);
			System.out.println(mv + ": " + r[0] + ", " + r[1] + ": " + isAmbigFile(mv));
		}

	}

}
//r, f = rank, file to move
//ambF = isAmbigFile
//ambR = isAmbigRank
//p[] = piece which moves given given color
//for (piece if = p in color.pieces)
//	m = get.moves(piece)
