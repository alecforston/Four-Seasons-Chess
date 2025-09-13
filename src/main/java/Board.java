public class Board {
    public static final int SIZE = 8;
    private final Cell[][] cells = new Cell[SIZE][SIZE];

    public Board() {
        initializeCells();
    }

    private void initializeCells() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                cells[r][c] = new Cell(r, c);
            }
        }
    }

    public Cell getCell(int row, int col) {
        if (isValidPosition(row, col)) {
            return cells[row][col];
        }
        return null;
    }

    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }

    public void setPiece(int row, int col, Piece piece) {
        if (isValidPosition(row, col)) {
            cells[row][col].setPiece(piece);
        }
    }

    public void removePiece(int row, int col) {
        if (isValidPosition(row, col)) {
            cells[row][col].setPiece(null);
        }
    }

    public Piece getPiece(int row, int col) {
        if (isValidPosition(row, col)) {
            return cells[row][col].getPiece();
        }
        return null;
    }

    public Cell[][] getAllCells() {
        return cells;
    }
}