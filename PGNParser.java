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
	static String NAME_CODES[] = new String[]{null, "", "R", "N", "B", "Q", "K"};
	static String FILE_STR = "abcdefgh";
	static String RANK_STR = "12345678";
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

	private List<Piece> white_pawns = new ArrayList<Piece>();
	private List<Piece> white_rooks = new ArrayList<Piece>();
	private List<Piece> white_knights = new ArrayList<Piece>();
	private List<Piece> white_bishops = new ArrayList<Piece>();
	private List<Piece> white_queens = new ArrayList<Piece>();
	//private List<Piece> white_king = new Piece(WHITE_KING, 0, 4, "K");
	private List<Piece> white_king = new ArrayList<Piece>();

	private List<Piece> black_pawns = new ArrayList<Piece>();
	private List<Piece> black_rooks = new ArrayList<Piece>();
	private List<Piece> black_knights = new ArrayList<Piece>();
	private List<Piece> black_bishops = new ArrayList<Piece>();
	private List<Piece> black_queens = new ArrayList<Piece>();
	// private List<Piece> black_king = new Piece(BLACK_KING, 7, 4, "K");
	private List<Piece> black_king = new ArrayList<Piece>();

	private void addToBoard(List<Piece> pieces){

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

		if (mv.endsWith("=Q") || mv.endsWith("=Q+") || mv.endsWith("=Q#")) {
			if (mv.contains("x"))
				return new int[]{PROMOTION_CAPTURE, WHITE_QUEEN};
			else
				return new int[]{PROMOTION, WHITE_QUEEN};
		}
			
		if (mv.endsWith("=R") || mv.endsWith("=R+") || mv.endsWith("=R#")){
			if (mv.contains("x"))
				return new int[]{PROMOTION_CAPTURE, WHITE_ROOK};
			else
				return new int[]{PROMOTION, WHITE_ROOK};
		}
			
		if (mv.endsWith("=N") || mv.endsWith("=N+") || mv.endsWith("=N#")){
			if (mv.contains("x"))
				return new int[]{PROMOTION_CAPTURE, WHITE_KNIGHT};
			else
				return new int[]{PROMOTION, WHITE_KNIGHT};
		}
			
		if (mv.endsWith("=B") || mv.endsWith("=B+") || mv.endsWith("=B#")){
			if (mv.contains("x"))
				return new int[]{PROMOTION_CAPTURE, WHITE_BISHOP};
			else
				return new int[]{PROMOTION, WHITE_BISHOP};
		}
		return null;
	}

	public static String codeToStr(int code) {

		if (code == WHITE_PAWN)
			return "P";
		else if (code == BLACK_PAWN)
			return "p"; 			
		else if (code == WHITE_ROOK)
			return "R";
		else if (code == BLACK_ROOK)
			return "r";
		else if (code == WHITE_KNIGHT)
			return "N";
		else if (code == BLACK_KNIGHT)
			return "n";
		else if (code == WHITE_BISHOP)
			return "B";
		else if (code == BLACK_BISHOP)
			return "b";
		else if (code == WHITE_QUEEN)
			return "Q";
		else if (code == BLACK_QUEEN)
			return "q";
		else if (code == WHITE_KING)
			return "K";
		else if (code == BLACK_KING)
			return "k";
		else if (code == 0)
			return ".";
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
			white_pawns.add(new Piece(WHITE_PAWN, 1, i, ""));

		white_king.add(new Piece(WHITE_KING, 0, 4, "k"));
		white_rooks.add(new Piece(WHITE_ROOK, 0, 0, "R"));
		white_rooks.add(new Piece(WHITE_ROOK, 0, 7, "R"));
		white_knights.add(new Piece(WHITE_KNIGHT, 0, 1, "N"));
		white_knights.add(new Piece(WHITE_KNIGHT, 0, 6, "N"));
		white_bishops.add(new Piece(WHITE_BISHOP, 0, 2, "B"));
		white_bishops.add(new Piece(WHITE_BISHOP, 0, 5, "B"));
		white_queens.add(new Piece(WHITE_QUEEN, 0, 3, "Q"));

		//black
		for (int i = 0; i < 8; i++)
			black_pawns.add(new Piece(BLACK_PAWN, 6, i, ""));

		black_king.add(new Piece(BLACK_KING, 7, 4, "K"));
		black_rooks.add(new Piece(BLACK_ROOK, 7, 0, "R"));
		black_rooks.add(new Piece(BLACK_ROOK, 7, 7, "R"));
		black_knights.add(new Piece(BLACK_KNIGHT, 7, 1, "N"));
		black_knights.add(new Piece(BLACK_KNIGHT, 7, 6, "N"));
		black_bishops.add(new Piece(BLACK_BISHOP, 7, 2, "B"));
		black_bishops.add(new Piece(BLACK_BISHOP, 7, 5, "B"));
		black_queens.add(new Piece(BLACK_QUEEN, 7, 3, "Q"));

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

	public int getColor(int r, int c){

		if (board[r][c] == EMPTY)
			return EMPTY;
		else if (board[r][c] >= WHITE_PAWN && board[r][c] <= WHITE_KING)
			return WHITE;
		else 
			return BLACK;	
	}

	public List<Square> getDirectionMoveSquares(int r, int f, int color, int[][] direction){

		List<Square> squares = new ArrayList<Square>();
		int opKing = color == WHITE ? BLACK_KING : WHITE_KING;
		int opColor = color == WHITE ? BLACK : WHITE;

		for (int d = 0; d < direction.length; d++){
			int[] dir = direction[d];

			int dy = dir[0];
			int dx = dir[1];
			int i = 1;
			while (r + i*dy >= 0 && r + i*dy < 8 && f + i*dx >= 0 && f + i*dx < 8) {
				int rr = r +i*dy;
				int ff = f + i*dx;
				int sq = getColor(rr, ff);
				
				if (sq == EMPTY){
					squares.add(new Square(rr, ff));
				}
				else if (sq == opColor && board[rr][ff] != opKing){
					squares.add(new Square(rr, ff));
					break;
				}
				else if (sq == opColor || sq == color){
					break;
				}
				i++;
			}
		}		
		return squares;
	}

	public List<Square> getKnightMoveSquares(int r, int f, int color){

		List<Square> squares = new ArrayList<Square>();
		int opKing = color == WHITE ? BLACK_KING : WHITE_KING;
		int opColor = color == WHITE ? BLACK : WHITE;

		for (int d = 0; d < KNIGHT_DIRS.length; d+= 2){
			int[] up = KNIGHT_DIRS[d];
			int[] left = KNIGHT_DIRS[d + 1];

			for (int i = 0; i < up.length; i++) {
				for (int j = 0; j < left.length; j++){
					int ri = r + up[i];
					int fi = f + left[j];

					if (ri >= 0 && ri < 8 && fi >= 0 && fi < 8){
						int sq = getColor(ri, fi);
						if  (sq == EMPTY)
							squares.add(new Square(ri, fi));
						else if (sq == opColor && board[ri][fi] != opKing){
							squares.add(new Square(ri, fi));							
						}
					}
				}
			}		
		}

		return squares;
	}

	public List<Square> getRookMoveSquares(int r, int f, int color){
		return getDirectionMoveSquares(r, f, color, ROOK_DIRS);
	}

	public List<Square> getBishopMoveSquares(int r, int f, int color){
		return getDirectionMoveSquares(r, f, color, BISHOP_DIRS);
	}

	public List<Square> getQueenMoveSquares(int r, int f, int color){
		List<Square> rook = getRookMoveSquares(r, f, color);
		List<Square> bishop = getBishopMoveSquares(r, f, color);
		bishop.addAll(rook);

		return bishop;
	}

	//TODO en-passant rule
	public List<Square> getPawnMoveSquares(int r, int f, int side){

		if ((side == WHITE && r == 0) || (side == BLACK && r == 7)) {
			System.out.println("Invalid pawn position");
			return null;
		}

		List<Square> squares = new ArrayList<Square>();
		int dy = side == WHITE ? 1:-1;

		int ri = r + dy;
		int fi = f;

		//moves
		if (ri >= 0 && ri < 8 && fi >= 0 && fi < 8){
			if (board[ri][fi] == EMPTY)
				squares.add(new Square(ri, fi));	
		}	

		if (side == WHITE && r == 1){
			if (board[r + 2][fi] == EMPTY)
				squares.add(new Square(r + 2, fi));
		}
		else if (side == BLACK && r == 6){
			if (board[r - 2][fi] == EMPTY)
				squares.add(new Square(r - 2, fi));
		}

		//captures
		int opKing = side == WHITE ? BLACK_KING : WHITE_KING;
		int opColor = side == WHITE ? BLACK : WHITE;
		fi = f - 1;

		if (ri >= 0 && ri < 8 && fi >= 0 && fi < 8) {
			if (board[ri][fi] != side && board[ri][fi] != opKing && board[ri][fi] != EMPTY)
				squares.add(new Square(ri, fi));
		}

		fi = f + 1;
		if (ri >= 0 && ri < 8 && fi >= 0 && fi < 8) {
			if (board[ri][fi] != side && board[ri][fi] != opKing && board[ri][fi] != EMPTY)
				squares.add(new Square(ri, fi));
		}

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
			for (int j = 0; j < FILES.length; j++) {
				if (FILES[j].equals(tokens[i])){
					idxs[1] = j;
					return idxs;
				}
			}
		}
		return null;
	}

	public static boolean isAmbigRank(String mv) {

		String m = mv.replaceAll("\\D+","");
		return m.length() > 1;
	}

	public static boolean isAmbigFile(String mv) {

		String m = mv.replaceAll("\\d+|[RNBQKx=+#]+", "");
		return m.length() > 1;
	}

	public List<Square> getMovesForPiece(Piece piece, int color){

		List<Square> moves = null;
		int r = piece.square[0];
		int f = piece.square[1];

		if (piece.code == WHITE_PAWN || piece.code == BLACK_PAWN){
			moves = getPawnMoveSquares(r, f, color);
		}
		else if (piece.code == WHITE_ROOK || piece.code == BLACK_ROOK){
			moves = getRookMoveSquares(r, f, color);
		}
		else if (piece.code == WHITE_KNIGHT || piece.code == BLACK_KNIGHT){
			moves = getKnightMoveSquares(r, f, color);
		}
		else if (piece.code == WHITE_BISHOP || piece.code == BLACK_BISHOP){
			moves = getBishopMoveSquares(r, f, color);
		}
		else if (piece.code == WHITE_QUEEN || piece.code == BLACK_QUEEN){
			moves = getQueenMoveSquares(r, f, color);
		}
		else
			System.out.println("Unknow piece");

		return moves;
	}

	public List<Piece> getPieces(int piece, int color){

		if (piece == WHITE_PAWN) {

			if (color == WHITE)
				return white_pawns;
			else
				return black_pawns;
		}
		else if (piece == WHITE_ROOK){
			if (color == WHITE)
				return white_rooks;
			else
				return black_rooks;

		}
		else if (piece == WHITE_KNIGHT){
			if (color == WHITE)
				return white_knights;
			else
				return black_knights;

		}
		else if (piece == WHITE_BISHOP){
			if (color == WHITE)
				return white_bishops;
			else
				return black_bishops;

		}
		else if (piece == WHITE_QUEEN){
			if (color == WHITE)
				return white_queens;
			else
				return black_queens;

		}
		else if (piece == WHITE_KING){
			if (color == WHITE)
				return white_king;
			else
				return black_king;
		}
		return null;
	}

	public void procMove(String mv, int color){

		int opColor = color == WHITE ? BLACK : WHITE;
		int[] moveInfo = getMoveInfo(mv);
		//moveInfo[1] is always a white piece code

		if(moveInfo[0] == MOVE || moveInfo[0] == CAPTURE){
			int pieceCode = moveInfo[1];			
			List<Piece> pieces = getPieces(pieceCode, color);
			int[] rankFile = getRankFile(mv);
			int r = rankFile[0];
			int f = rankFile[1];

			if (moveInfo[0] == CAPTURE){
				Piece toRemove = null;							
				//using white piece code
				int capPiece = board[r][f] > 0 ? board[r][f]: -board[r][f];

				//En Passant
				if (capPiece == EMPTY) {
					List<Piece> pawns = color == WHITE ? white_pawns : black_pawns;
					int pawnFile = FILE_STR.indexOf(mv.replaceAll("\\d+|[RNBQKx=+#]+", "").substring(0, 1));
					int pawnRank = color == WHITE ? r - 1 : r + 1;

					for (Piece pp : pawns) {
						if (pp.square[0] == pawnRank && pp.square[1] == pawnFile){
							board[pawnRank][pawnFile] = EMPTY;
							board[r][f] = color == WHITE ? pieceCode : -pieceCode;
							pp.square[0] = r;
							pp.square[1] = f;
						}
					}
					//remove opp pawn
					List<Piece> opPieces = color == WHITE ? black_pawns : white_pawns;					
					for (Piece pp : opPieces) {
						if (pp.square[0] == pawnRank && pp.square[1] == f){
							toRemove = pp;
							break;
						}
					}
					opPieces.remove(toRemove);
					board[pawnRank][f] = EMPTY;
					return;
				}
				else {
					List<Piece> opPieces = getPieces(capPiece, opColor);
					for (Piece op : opPieces) {
						if (op.square[0] == r && op.square[1] == f) {
							op.captured = true;
							toRemove = op;
							break;
						}
					}
					opPieces.remove(toRemove);
				}
			}

			//Somente uma peca deste tipo
			if (pieces.size() == 1) {
				Piece p = pieces.get(0);				
				board[p.square[0]][p.square[1]] = EMPTY;
				//board[r][f] = pieceCode;
				board[r][f] = color == WHITE ? pieceCode : -pieceCode;
				p.square[0] = r;
				p.square[1] = f;				
			}
			else {
				boolean ambF = isAmbigFile(mv);
				boolean ambR = isAmbigRank(mv);
				//somente uma das pecas pode fazer o lance
				if (!(ambF || ambR)){
					outer:
					for (Piece p : pieces){
						List<Square> moves = getMovesForPiece(p, color);
						for (Square m : moves){
							if (m.r == r && m.f == f){
								board[p.square[0]][p.square[1]] = EMPTY;
								//board[r][f] = pieceCode;
								board[r][f] = color == WHITE ? pieceCode : -pieceCode;
								p.square[0] = r;
								p.square[1] = f;
								break outer;
							}
						}
					}
				}
				else {
					int decideIdx = -1;
					int decideVal = -1;
					if (ambF) {
						decideIdx = 1; //decide by file
						decideVal = FILE_STR.indexOf(mv.replaceAll("\\d+|[RNBQKx=+#]+", "").substring(0, 1));
					}
					else{
						decideIdx = 0;
						decideVal = RANK_STR.indexOf(mv.replaceAll("\\D+","").substring(0, 1));
					}
					outer:
					for (Piece p : pieces){
						List<Square> moves = getMovesForPiece(p, color);
						for (Square m : moves){
							if (m.r == r && m.f == f && p.square[decideIdx] == decideVal){
								board[p.square[0]][p.square[1]] = EMPTY;
								// board[r][f] = pieceCode;
								board[r][f] = color == WHITE ? pieceCode : -pieceCode;
								p.square[0] = r;
								p.square[1] = f;
								break outer;
							}
						}
					}
				}				
			}
		}
		else if (moveInfo[0] == CASTLE_KS){
			int rk = color == WHITE ? 0 : 7;
			Piece king = color == WHITE ? white_king.get(0) : black_king.get(0);
			List<Piece> rooks = color == WHITE ? white_rooks : black_rooks;

			board[rk][4] = EMPTY;
			board[rk][7] = EMPTY;
			board[rk][5] = color == WHITE ? WHITE_ROOK : BLACK_ROOK;
			board[rk][6] = color == WHITE ? WHITE_KING : BLACK_KING;
			king.square[1] = 6;

			for (Piece rook : rooks){
				if (rook.square[0] == rk && rook.square[1] == 7){
					rook.square[1] = 5;
					break;
				}
			}
		}
		else if (moveInfo[0] == CASTLE_QS){
			int rk = color == WHITE ? 0 : 7;
			Piece king = color == WHITE ? white_king.get(0) : black_king.get(0);
			List<Piece> rooks = color == WHITE ? white_rooks : black_rooks;

			board[rk][4] = EMPTY;
			board[rk][0] = EMPTY;
			board[rk][3] = color == WHITE ? WHITE_ROOK : BLACK_ROOK;
			board[rk][2] = color == WHITE ? WHITE_KING : BLACK_KING;
			king.square[1] = 2;

			for (Piece rook : rooks){
				if (rook.square[0] == rk && rook.square[1] == 0){
					rook.square[1] = 3;
					break;
				}
			}
		}
		else if (moveInfo[0] == PROMOTION_CAPTURE){			
			int promoteTo = moveInfo[1];
			List<Piece> pieces = color == WHITE ? white_pawns : black_pawns;
			int[] rankFile = getRankFile(mv);
			int r = rankFile[0];
			int f = rankFile[1];
			int decideIdx = 1;
			int decideVal = FILE_STR.indexOf(mv.replaceAll("\\d+|[RNBQKx=+#]+", "").substring(0, 1));
			
			Piece toRemove = null;
			outer:
			for (Piece p : pieces) {
				List<Square> moves = getMovesForPiece(p, color);
				for (Square m : moves) {
					if (m.r == r && m.f == f && p.square[decideIdx] == decideVal){
						board[p.square[0]][p.square[1]] = EMPTY;
						board[r][f] = color == WHITE ? promoteTo : -promoteTo;						
						toRemove = p;						
						break outer;
					}
				}
			}
			//remove promoted pawn add promoted piece
			pieces.remove(toRemove);					
			List<Piece> promoPieces = getPieces(promoteTo, color);
			promoPieces.add(new Piece(color == WHITE ? promoteTo : -promoteTo, r, f, NAME_CODES[promoteTo]));

			//remove captured piece
			int capPiece = board[r][f] > 0 ? board[r][f]: -board[r][f];
			List<Piece> opPieces = getPieces(capPiece, opColor);
			toRemove = null;
			for (Piece op : opPieces) {
				if (op.square[0] == r && op.square[1] == f) {
					op.captured = true;
					toRemove = op;
					break;
				}
			}
			opPieces.remove(toRemove);
		}
		else if (moveInfo[0] == PROMOTION){
			int promoteTo = moveInfo[1];	
			List<Piece> pieces = color == WHITE ? white_pawns : black_pawns;
			int[] rankFile = getRankFile(mv);
			int r = rankFile[0];
			int f = rankFile[1];
			Piece toRemove = null;

			outer:
			for (Piece p : pieces) {
				List<Square> moves = getMovesForPiece(p, color);
				for (Square m : moves) {
					if (m.r == r && m.f == f){
						board[p.square[0]][p.square[1]] = EMPTY;
						board[r][f] = color == WHITE ? promoteTo : -promoteTo;	
						toRemove = p;						
						break outer;
					}
				}
			}

			//remove promoted pawn add promoted piece
			pieces.remove(toRemove);
			List<Piece> promoPieces = getPieces(promoteTo, color);
			promoPieces.add(new Piece(color == WHITE ? promoteTo : -promoteTo, r, f, NAME_CODES[promoteTo]));
		}
		else {
			System.out.println("Invalid move type: " + moveInfo[0]);
		}
		
	}

	public static void main(String[] args) {

		PGNParser  game = new PGNParser();
		game.initPosition();

		String[] pgn = new String[]{"Nf3", "Nf6", "c4", "c5", "b3", "g6", "Bb2", "Bg7", "e3", "O-O", "Be2", "b6",
		"O-O", "Bb7", "Nc3", "Nc6", "Qc2", "Rc8", "Rac1", "d5", "Nxd5", "Nxd5", "Bxg7", "Nf4", "exf4", "Kxg7", 
		"Qc3+", "Kg8", "Rcd1", "Qd6", "d4", "cxd4", "Nxd4", "Qxf4", "Bf3", "Qf6", "Nb5", "Qxc3"};

		int side = WHITE;		
		// for (String m : pgn) {			
		// 	game.procMove(m, side );
		// 	side *= -1;			
			
		// }
		
		// game.printBoard();

		String x = "1. e4 e6 2. d4 d5 3. e5 c5 4. c3 cxd4 5. cxd4 Bb4+ 6. Nc3 Nc6 "+
"7. Nf3 Nge7 8. Bd3 O-O 9. Bxh7+ Kxh7 10. Ng5+ Kg6 11. h4 Nxd4 "+
"12. Qg4 f5 13. h5+ Kh6 14. Nxe6+ g5 15. hxg6# 1-0";
		String[] t = x.split(" ");
		int k = 0;

		for (int i = 0; i < t.length - 1; i++) {
			if (i % 3 != 0) {
				if (side == WHITE)
					System.out.println(k/2 + 1 + ". " + t[i]);				
				else
					System.out.println(k/2 + 1 + ".. " + t[i]);
				game.procMove(t[i], side);
				side *= -1;	
				k++;
				game.printBoard();
				System.out.println();
			}
		}
		
	}

}
//r, f = rank, file to move
//ambF = isAmbigFile
//ambR = isAmbigRank
//p[] = piece which moves given given color
//for (piece if = p in color.pieces)
//	m = get.moves(piece)
