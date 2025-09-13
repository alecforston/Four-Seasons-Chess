public class Cell {
    private final int row;
    private final int col;
    private Piece piece;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.piece = null;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public boolean isEmpty() {
        return piece == null;
    }

    public boolean hasEnemyPiece(Player player) {
        if (piece == null) return false;
        Player controllingPlayer = piece.getControllingPlayer() != null ?
                piece.getControllingPlayer() : piece.getPlayer();
        return controllingPlayer != player;
    }

    public boolean hasFriendlyPiece(Player player) {
        if (piece == null) return false;
        Player controllingPlayer = piece.getControllingPlayer() != null ?
                piece.getControllingPlayer() : piece.getPlayer();
        return controllingPlayer == player;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Cell cell = (Cell) obj;
        return row == cell.row && col == cell.col;
    }

    @Override
    public int hashCode() {
        return row * 31 + col;
    }
}