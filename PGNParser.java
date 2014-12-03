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
	
	static String[] RANKS = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
	static String[] FILES = new String[]{"a", "b", "c", "d", "e", "f", "g", "h"};
	static int WHITE = 100;
	static int BLACK = -100;

	static int EMPTY = 0;
	static int CASTLE_QS = -1;
	static int CASTLE_KS = 1;

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
	
	public static void main(String[] args) {
		PGNParser p = new PGNParser();
		p.initPosition();
		p.printBoard();

		//List<Square> sq = getKnightAttackSquares(0, 1);
		//List<Square> sq = getRookAttackSquares(4, 4);
		List<Square> sq = getPawnAttackSquares(2, 3, WHITE);
		for (Square s:sq)
			System.out.println(s.toNotation());

		
	}

}
