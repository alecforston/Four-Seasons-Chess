import java.util.ArrayList;
import java.util.List;

public class MoveValidator {
    private final Board board;

    public MoveValidator(Board board) {
        this.board = board;
    }

    public boolean isLegalMove(Cell from, Cell to) {
        return getLegalMoves(from).contains(to);
    }

    public List<Cell> getLegalMoves(Cell from) {
        List<Cell> moves = new ArrayList<>();
        if (from.isEmpty()) return moves;

        Piece piece = from.getPiece();
        int r = from.getRow();
        int c = from.getCol();

        switch (piece.getType()) {
            case KING:
                addKingMoves(moves, r, c);
                break;
            case ROOK:
                addRookMoves(moves, r, c);
                break;
            case ELEPHANT:
                addElephantMoves(moves, r, c);
                break;
            case KNIGHT:
                addKnightMoves(moves, r, c);
                break;
            case PAWN:
                addPawnMoves(moves, from);
                break;
            case GENERAL:
                addGeneralMoves(moves, r, c);
                break;
        }

        // Remove moves to squares occupied by friendly pieces (considering controlling player)
        Player effectivePlayer = piece.getControllingPlayer() != null ?
                piece.getControllingPlayer() : piece.getPlayer();
        moves.removeIf(cell -> isFriendlyPiece(cell, effectivePlayer));
        return moves;
    }

    /**
     * Check if a cell contains a piece friendly to the given player
     */
    private boolean isFriendlyPiece(Cell cell, Player player) {
        if (cell.isEmpty()) {
            return false;
        }
        Piece piece = cell.getPiece();
        Player pieceController = piece.getControllingPlayer() != null ?
                piece.getControllingPlayer() : piece.getPlayer();
        return pieceController == player;
    }

    private void addKingMoves(List<Cell> moves, int r, int c) {
        int[][] directions = {{-1,-1},{-1,0},{-1,1},{0,-1},{0,1},{1,-1},{1,0},{1,1}};
        for (int[] dir : directions) {
            addIfValid(moves, r + dir[0], c + dir[1]);
        }
    }

    private void addRookMoves(List<Cell> moves, int r, int c) {
        int[][] directions = {{-1,0},{1,0},{0,-1},{0,1}};
        for (int[] dir : directions) {
            addSlidingMoves(moves, r, c, dir[0], dir[1]);
        }
    }

    private void addElephantMoves(List<Cell> moves, int r, int c) {
        int[][] directions = {{-2,-2},{2,2},{-2,2},{2,-2}};
        for (int[] dir : directions) {
            addIfValid(moves, r + dir[0], c + dir[1]);
        }
    }

    private void addKnightMoves(List<Cell> moves, int r, int c) {
        int[][] directions = {{-2,-1},{-2,1},{-1,-2},{-1,2},{1,-2},{1,2},{2,-1},{2,1}};
        for (int[] dir : directions) {
            addIfValid(moves, r + dir[0], c + dir[1]);
        }
    }

    private void addPawnMoves(List<Cell> moves, Cell from) {
        Piece piece = from.getPiece();
        int[] forwardDelta = getPawnForwardDelta(from); // Use original player's direction
        int r = from.getRow();
        int c = from.getCol();
        int dr = forwardDelta[0];
        int dc = forwardDelta[1];

        // Forward move
        int newR = r + dr;
        int newC = c + dc;
        if (board.isValidPosition(newR, newC) && board.getCell(newR, newC).isEmpty()) {
            moves.add(board.getCell(newR, newC));
        }

        // Capture moves - perpendicular to forward direction
        Player effectivePlayer = piece.getControllingPlayer() != null ?
                piece.getControllingPlayer() : piece.getPlayer();
        if (dr == 0 && dc != 0) {
            // Forward is horizontal, capture vertically
            addIfCapture(moves, r + 1, c + dc, effectivePlayer);
            addIfCapture(moves, r - 1, c + dc, effectivePlayer);
        } else if (dc == 0 && dr != 0) {
            // Forward is vertical, capture horizontally
            addIfCapture(moves, r + dr, c + 1, effectivePlayer);
            addIfCapture(moves, r + dr, c - 1, effectivePlayer);
        }
    }

    private void addGeneralMoves(List<Cell> moves, int r, int c) {
        int[][] directions = {{-1,-1},{-1,1},{1,-1},{1,1}};
        for (int[] dir : directions) {
            addIfValid(moves, r + dir[0], c + dir[1]);
        }
    }

    private void addIfValid(List<Cell> moves, int r, int c) {
        if (board.isValidPosition(r, c)) {
            moves.add(board.getCell(r, c));
        }
    }

    private void addIfCapture(List<Cell> moves, int r, int c, Player player) {
        if (board.isValidPosition(r, c)) {
            Cell cell = board.getCell(r, c);
            if (!cell.isEmpty()) {
                Piece piece = cell.getPiece();
                Player pieceController = piece.getControllingPlayer() != null ?
                        piece.getControllingPlayer() : piece.getPlayer();
                if (pieceController != player) {
                    moves.add(cell);
                }
            }
        }
    }

    private void addSlidingMoves(List<Cell> moves, int r, int c, int dr, int dc) {
        int newR = r + dr;
        int newC = c + dc;

        while (board.isValidPosition(newR, newC)) {
            Cell cell = board.getCell(newR, newC);
            moves.add(cell);

            if (!cell.isEmpty()) {
                break; // Stop sliding if we hit a piece
            }

            newR += dr;
            newC += dc;
        }
    }

    private int[] getPawnForwardDelta(Cell from) {
        Player player = from.getPiece().getPlayer(); // Always use original player for movement direction
        int r = from.getRow();
        int c = from.getCol();

        // SPRING corner: rows 0-2, cols 5-7
        if (player == Player.SPRING) {
            if ((c + r) <= 7) return new int[]{0, -1}; // move left
            else return new int[]{1, 0}; // move down
        }
        // SUMMER corner: rows 0-2, cols 0-2
        if (player == Player.SUMMER) {
            if (c > r) return new int[]{0, 1}; // move right
            else return new int[]{1, 0}; // move down
        }
        // FALL corner: rows 5-7, cols 0-2
        if (player == Player.FALL) {
            if ((c + r) >= 7) return new int[]{0, 1}; // move right
            else return new int[]{-1, 0}; // move up
        }
        // WINTER corner: rows 5-7, cols 5-7
        if (player == Player.WINTER) {
            if (c < r) return new int[]{0, -1}; // move left
            else return new int[]{-1, 0}; // move up
        }

        // Fallback
        return new int[]{0, 0};
    }
}