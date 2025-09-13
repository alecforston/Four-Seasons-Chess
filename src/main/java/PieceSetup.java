public class PieceSetup {
    private final Board board;

    public PieceSetup(Board board) {
        this.board = board;
    }

    public void setupInitialPieces() {
        setupSummer();
        setupWinter();
        setupFall();
        setupSpring();
    }

    private void setupSummer() {
        // SUMMER (top left corner, rows 0-2, cols 0-2)
        board.setPiece(1, 0, new Piece(PieceType.ROOK, Player.SUMMER));
        board.setPiece(0, 0, new Piece(PieceType.KING, Player.SUMMER));
        board.setPiece(1, 1, new Piece(PieceType.ELEPHANT, Player.SUMMER));
        board.setPiece(0, 1, new Piece(PieceType.KNIGHT, Player.SUMMER));
        board.setPiece(0, 2, new Piece(PieceType.PAWN, Player.SUMMER));
        board.setPiece(1, 2, new Piece(PieceType.PAWN, Player.SUMMER));
        board.setPiece(2, 1, new Piece(PieceType.PAWN, Player.SUMMER));
        board.setPiece(2, 0, new Piece(PieceType.PAWN, Player.SUMMER));
    }

    private void setupWinter() {
        // WINTER (bottom right corner, rows 5-7, cols 5-7)
        board.setPiece(6, 7, new Piece(PieceType.ROOK, Player.WINTER));
        board.setPiece(7, 7, new Piece(PieceType.KING, Player.WINTER));
        board.setPiece(6, 6, new Piece(PieceType.ELEPHANT, Player.WINTER));
        board.setPiece(7, 6, new Piece(PieceType.KNIGHT, Player.WINTER));
        board.setPiece(7, 5, new Piece(PieceType.PAWN, Player.WINTER));
        board.setPiece(6, 5, new Piece(PieceType.PAWN, Player.WINTER));
        board.setPiece(5, 6, new Piece(PieceType.PAWN, Player.WINTER));
        board.setPiece(5, 7, new Piece(PieceType.PAWN, Player.WINTER));
    }

    private void setupFall() {
        // FALL (bottom left corner, rows 5-7, cols 0-2)
        board.setPiece(6, 0, new Piece(PieceType.ROOK, Player.FALL));
        board.setPiece(7, 0, new Piece(PieceType.KING, Player.FALL));
        board.setPiece(6, 1, new Piece(PieceType.ELEPHANT, Player.FALL));
        board.setPiece(7, 1, new Piece(PieceType.KNIGHT, Player.FALL));
        board.setPiece(5, 0, new Piece(PieceType.PAWN, Player.FALL));
        board.setPiece(5, 1, new Piece(PieceType.PAWN, Player.FALL));
        board.setPiece(6, 2, new Piece(PieceType.PAWN, Player.FALL));
        board.setPiece(7, 2, new Piece(PieceType.PAWN, Player.FALL));
    }

    private void setupSpring() {
        // SPRING (top right corner, rows 0-2, cols 5-7)
        board.setPiece(1, 7, new Piece(PieceType.ROOK, Player.SPRING));
        board.setPiece(0, 7, new Piece(PieceType.KING, Player.SPRING));
        board.setPiece(1, 6, new Piece(PieceType.ELEPHANT, Player.SPRING));
        board.setPiece(0, 6, new Piece(PieceType.KNIGHT, Player.SPRING));
        board.setPiece(0, 5, new Piece(PieceType.PAWN, Player.SPRING));
        board.setPiece(1, 5, new Piece(PieceType.PAWN, Player.SPRING));
        board.setPiece(2, 6, new Piece(PieceType.PAWN, Player.SPRING));
        board.setPiece(2, 7, new Piece(PieceType.PAWN, Player.SPRING));
    }
}