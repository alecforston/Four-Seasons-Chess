import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.function.Consumer;

/**
 * GameController for local hot-seat play with strict turn order, check detection, and checkmate handling
 */
public class GameController {
    private final Board board;
    private final MoveValidator moveValidator;
    private final PieceSetup pieceSetup;
    private Player currentPlayer;
    private final Set<Player> eliminatedPlayers; // Players who have been checkmated
    private final Set<Player> activePlayers; // Players still in the game
    private Cell selectedCell;
    private Consumer<Player> statusCallback;
    private Consumer<String> gameStatusCallback; // For check/checkmate notifications
    private Runnable boardViewUpdateCallback;
    private Runnable pieceUpdateCallback;

    public GameController() {
        this.board = new Board();
        this.moveValidator = new MoveValidator(board);
        this.pieceSetup = new PieceSetup(board);
        this.currentPlayer = Player.SPRING;
        this.selectedCell = null;
        this.eliminatedPlayers = new HashSet<>();
        this.activePlayers = new HashSet<>();

        // Initialize all players as active
        for (Player player : Player.values()) {
            activePlayers.add(player);
        }

        // Setup initial pieces
        pieceSetup.setupInitialPieces();
    }

    public Board getBoard() {
        return board;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Cell getSelectedCell() {
        return selectedCell;
    }

    public void setStatusCallback(Consumer<Player> callback) {
        this.statusCallback = callback;
    }

    public void setGameStatusCallback(Consumer<String> callback) {
        this.gameStatusCallback = callback;
    }

    public void setBoardViewUpdateCallback(Runnable callback) {
        this.boardViewUpdateCallback = callback;
    }

    public void setPieceUpdateCallback(Runnable callback) {
        this.pieceUpdateCallback = callback;
    }

    public void handleCellClick(Cell cell) {
        // Skip turn if current player is eliminated
        if (eliminatedPlayers.contains(currentPlayer)) {
            nextTurn();
            return;
        }

        if (selectedCell == null) {
            // No piece selected, try to select a piece
            if (cell.getPiece() != null && canPlayerControlPiece(currentPlayer, cell.getPiece())) {
                selectedCell = cell;
            }
        } else {
            if (cell == selectedCell) {
                // Deselect current piece
                selectedCell = null;
            } else {
                // Try to make a move
                if (isLegalMoveWithCheckValidation(selectedCell, cell)) {
                    makeMove(selectedCell, cell);
                    selectedCell = null;

                    // Check for check/checkmate after the move
                    checkForCheckAndCheckmate();

                    nextTurn();
                } else {
                    // Select a different piece if it belongs to current player or is controlled by them
                    if (cell.getPiece() != null && canPlayerControlPiece(currentPlayer, cell.getPiece())) {
                        selectedCell = cell;
                    } else {
                        selectedCell = null;
                    }
                }
            }
        }

        // Notify view to update highlights
        if (boardViewUpdateCallback != null) {
            boardViewUpdateCallback.run();
        }

        if (statusCallback != null) {
            statusCallback.accept(currentPlayer);
        }
    }

    /**
     * Check if a player can control a piece (either their own or inherited from eliminated players)
     */
    private boolean canPlayerControlPiece(Player player, Piece piece) {
        if (piece.getPlayer() == player) {
            return true;
        }

        // Check if this piece belonged to an eliminated player that this player now controls
        return eliminatedPlayers.contains(piece.getPlayer()) &&
                piece.getControllingPlayer() != null &&
                piece.getControllingPlayer() == player;
    }

    public List<Cell> getLegalMoves(Cell fromCell) {
        List<Cell> baseMoves = moveValidator.getLegalMoves(fromCell);
        List<Cell> legalMoves = new ArrayList<>();

        // Filter out moves that would put the player in check
        for (Cell toCell : baseMoves) {
            if (isLegalMoveWithCheckValidation(fromCell, toCell)) {
                legalMoves.add(toCell);
            }
        }

        return legalMoves;
    }

    /**
     * Check if a move is legal considering check rules
     */
    private boolean isLegalMoveWithCheckValidation(Cell from, Cell to) {
        if (!moveValidator.isLegalMove(from, to)) {
            return false;
        }

        // Simulate the move to check if it leaves the player in check
        Piece movingPiece = from.getPiece();
        Piece capturedPiece = to.getPiece();

        // Make temporary move
        to.setPiece(movingPiece);
        from.setPiece(null);

        // Check if the current player's king is in check after this move
        boolean wouldBeInCheck = isPlayerInCheck(getEffectivePlayer(movingPiece));

        // Restore board state
        from.setPiece(movingPiece);
        to.setPiece(capturedPiece);

        return !wouldBeInCheck;
    }

    /**
     * Get the effective controlling player for a piece
     */
    private Player getEffectivePlayer(Piece piece) {
        if (piece.getControllingPlayer() != null) {
            return piece.getControllingPlayer();
        }
        return piece.getPlayer();
    }

    private void makeMove(Cell from, Cell to) {
        Piece piece = from.getPiece();
        if (piece == null) return;

        // Make the move
        to.setPiece(piece);
        from.setPiece(null);

        // Check for pawn promotion
        if (piece.getType() == PieceType.PAWN && isPromotionSquare(piece.getPlayer(), to)) {
            piece.setType(PieceType.GENERAL);
        }

        // Update piece display
        if (pieceUpdateCallback != null) {
            pieceUpdateCallback.run();
        }
    }

    /**
     * Check if a player's king is in check
     */
    public boolean isPlayerInCheck(Player player) {
        Cell kingCell = findKing(player);
        if (kingCell == null) {
            return false; // No king, can't be in check
        }

        // Check if any opponent piece can attack the king
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                Piece piece = board.getPiece(r, c);
                if (piece != null && getEffectivePlayer(piece) != player) {
                    List<Cell> moves = moveValidator.getLegalMoves(board.getCell(r, c));
                    if (moves.contains(kingCell)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Find a player's king on the board
     */
    private Cell findKing(Player player) {
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                Piece piece = board.getPiece(r, c);
                if (piece != null && piece.getType() == PieceType.KING &&
                        getEffectivePlayer(piece) == player) {
                    return board.getCell(r, c);
                }
            }
        }
        return null;
    }

    /**
     * Check if a player is in checkmate
     */
    public boolean isPlayerInCheckmate(Player player) {
        if (!isPlayerInCheck(player)) {
            return false;
        }

        // Try all possible moves to see if any can get out of check
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                Piece piece = board.getPiece(r, c);
                if (piece != null && getEffectivePlayer(piece) == player) {
                    Cell fromCell = board.getCell(r, c);
                    List<Cell> possibleMoves = moveValidator.getLegalMoves(fromCell);

                    for (Cell toCell : possibleMoves) {
                        if (isLegalMoveWithCheckValidation(fromCell, toCell)) {
                            return false; // Found a legal move, not checkmate
                        }
                    }
                }
            }
        }
        return true; // No legal moves found, it's checkmate
    }

    /**
     * Check if a player is in stalemate
     */
    public boolean isPlayerInStalemate(Player player) {
        if (isPlayerInCheck(player)) {
            return false;
        }

        // Try all possible moves to see if any can get out of check
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                Piece piece = board.getPiece(r, c);
                if (piece != null && getEffectivePlayer(piece) == player) {
                    Cell fromCell = board.getCell(r, c);
                    List<Cell> possibleMoves = moveValidator.getLegalMoves(fromCell);

                    for (Cell toCell : possibleMoves) {
                        if (isLegalMoveWithCheckValidation(fromCell, toCell)) {
                            return false; // Found a legal move, not stalemate
                        }
                    }
                }
            }
        }
        return true; // No legal moves found, it's stalemate
    }

    /**
     * Check for check and checkmate conditions after a move
     */
    private void checkForCheckAndCheckmate() {
        List<Player> playersToCheck = new ArrayList<>(activePlayers);
        playersToCheck.remove(currentPlayer); // Don't check the player who just moved

        for (Player player : playersToCheck) {
            if (eliminatedPlayers.contains(player)) {
                continue;
            }

            if (isPlayerInCheckmate(player) || isPlayerInStalemate(player)) {
                handleCheckmate(player, currentPlayer);
            } else if (isPlayerInCheck(player)) {
                if (gameStatusCallback != null) {
                    gameStatusCallback.accept(player + " is in check!");
                }
            }
        }

        // Check for game over
        if (getActivePlayerCount() <= 1) {
            handleGameOver();
        }
    }

    /**
     * Handle checkmate: eliminate player and transfer pieces
     */
    private void handleCheckmate(Player checkmatedPlayer, Player victor) {
        // Remove player from game entirely
        eliminatedPlayers.add(checkmatedPlayer);
        activePlayers.remove(checkmatedPlayer);

        // Remove the king from the board
        Cell kingCell = findKing(checkmatedPlayer);
        if (kingCell != null) {
            kingCell.setPiece(null);
        }

        // Transfer remaining pieces to the victor
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                Piece piece = board.getPiece(r, c);
                if (piece != null && piece.getPlayer() == checkmatedPlayer) {
                    piece.setControllingPlayer(victor);
                    // Pawns keep their original movement direction
                }
            }
        }

        if (gameStatusCallback != null) {
            gameStatusCallback.accept(victor + " checkmates " + checkmatedPlayer + "! " +
                    victor + " now controls " + checkmatedPlayer + "'s pieces.");
        }
    }

    /**
     * Handle game over condition
     */
    private void handleGameOver() {
        Player winner = null;
        for (Player player : activePlayers) {
            if (!eliminatedPlayers.contains(player)) {
                winner = player;
                break;
            }
        }

        if (winner != null && gameStatusCallback != null) {
            gameStatusCallback.accept("Game Over! " + winner + " wins!");
        }
    }

    /**
     * Get the number of active players
     */
    public int getActivePlayerCount() {
        return (int) activePlayers.stream().filter(p -> !eliminatedPlayers.contains(p)).count();
    }

    /**
     * Check if a player is eliminated
     */
    public boolean isPlayerEliminated(Player player) {
        return eliminatedPlayers.contains(player);
    }

    private boolean isPromotionSquare(Player player, Cell cell) {
        int r = cell.getRow();
        int c = cell.getCol();
        switch (player) {
            case SUMMER: return r == 7 || c == 7;
            case WINTER: return r == 0 || c == 0;
            case FALL: return r == 0 || c == 7;
            case SPRING: return r == 7 || c == 0;
        }
        return false;
    }

    /**
     * Move to next turn, skipping eliminated players
     */
    private void nextTurn() {
        do {
            currentPlayer = currentPlayer.next();
        } while (eliminatedPlayers.contains(currentPlayer) && getActivePlayerCount() > 1);

        if (statusCallback != null) {
            statusCallback.accept(currentPlayer);
        }
    }

    public void resetGame() {
        // Clear board
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                board.removePiece(r, c);
            }
        }

        // Reset state
        selectedCell = null;
        currentPlayer = Player.SPRING;
        eliminatedPlayers.clear();
        activePlayers.clear();
        for (Player player : Player.values()) {
            activePlayers.add(player);
        }

        // Setup initial pieces
        pieceSetup.setupInitialPieces();

        // Notify callbacks
        if (boardViewUpdateCallback != null) {
            boardViewUpdateCallback.run();
        }
        if (pieceUpdateCallback != null) {
            pieceUpdateCallback.run();
        }
        if (statusCallback != null) {
            statusCallback.accept(currentPlayer);
        }
    }

    public boolean isGameOver() {
        return getActivePlayerCount() <= 1;
    }
}