import javax.swing.*;
import java.awt.*;

public class BoardView extends JPanel {
    private final CellView[][] cellViews;
    private final GameController gameController;

    private static final Color GREEN_ZONE_COLOR = new Color(41, 79, 66);
    private static final Color SEPARATOR_COLOR = new Color(200, 205, 166);
    private static final Color BACKGROUND_COLOR = new Color(31, 54, 93);

    private static final int CELL_SIZE = 80;
    private static final int BORDER_SIZE = 13;
    private static final int PINK_ZONE_HEIGHT = 20;

    public BoardView(GameController gameController) {
        this.gameController = gameController;
        this.cellViews = new CellView[Board.SIZE][Board.SIZE];

        initializeCellViews();
        setupLayout();
    }

    private void initializeCellViews() {
        Board board = gameController.getBoard();

        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                Cell cell = board.getCell(r, c);
                CellView cellView = new CellView(cell, gameController);
                cellViews[r][c] = cellView;
            }
        }
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // Create the main board panel
        JPanel mainBoard = createMainBoard();

        // Create a wrapper panel that centers the board and prevents stretching
        JPanel centeringPanel = new JPanel(new GridBagLayout());
        centeringPanel.setBackground(BACKGROUND_COLOR);
        centeringPanel.add(mainBoard, new GridBagConstraints());

        add(centeringPanel, BorderLayout.CENTER);
    }

    private JPanel createMainBoard() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 0)); // Remove gaps between components
        mainPanel.setBackground(SEPARATOR_COLOR);

        // Top green zone
        JPanel topGreenZone = createGreenZone();

        // Top pink zone
        JPanel topPinkZone = createPinkZone();

        // Bottom pink zone
        JPanel bottomPinkZone = createPinkZone();

        // Bottom green zone
        JPanel bottomGreenZone = createGreenZone();

        // Main chess board with fixed size
        JPanel chessBoard = new JPanel(new GridLayout(Board.SIZE, Board.SIZE, 0, 0));
        // Set fixed size for the chess board to prevent stretching
        Dimension boardSize = new Dimension(CELL_SIZE * Board.SIZE, CELL_SIZE * Board.SIZE);
        chessBoard.setPreferredSize(boardSize);
        chessBoard.setMinimumSize(boardSize);
        chessBoard.setMaximumSize(boardSize);

        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                chessBoard.add(cellViews[r][c]);
            }
        }

        // Create the center panel that will contain pink zones and chess board
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(SEPARATOR_COLOR); // Set background to pink to eliminate any gaps
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(topPinkZone, BorderLayout.NORTH);
        centerPanel.add(chessBoard, BorderLayout.CENTER);
        centerPanel.add(bottomPinkZone, BorderLayout.SOUTH);

        // Add all components to main panel
        mainPanel.add(topGreenZone, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomGreenZone, BorderLayout.SOUTH);

        // Create container with borders
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(SEPARATOR_COLOR);
        container.setBorder(BorderFactory.createEmptyBorder(BORDER_SIZE, BORDER_SIZE, BORDER_SIZE, BORDER_SIZE));
        container.add(mainPanel, BorderLayout.CENTER);

        // Don't set explicit size on container - let it size naturally based on components
        return container;
    }

    private JPanel createGreenZone() {
        JPanel zone = new JPanel(new GridLayout(1, Board.SIZE, 0, 0));
        zone.setPreferredSize(new Dimension(CELL_SIZE * Board.SIZE, CELL_SIZE));
        zone.setMinimumSize(zone.getPreferredSize());
        zone.setMaximumSize(zone.getPreferredSize());

        for (int i = 0; i < Board.SIZE; i++) {
            JPanel cell = new JPanel();
            cell.setBackground(GREEN_ZONE_COLOR);
            cell.setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
            cell.setMinimumSize(new Dimension(CELL_SIZE, CELL_SIZE));
            cell.setMaximumSize(new Dimension(CELL_SIZE, CELL_SIZE));
            zone.add(cell);
        }
        return zone;
    }

    private JPanel createPinkZone() {
        JPanel zone = new JPanel();
        zone.setBackground(SEPARATOR_COLOR);
        Dimension pinkZoneSize = new Dimension(CELL_SIZE * Board.SIZE, PINK_ZONE_HEIGHT);
        zone.setPreferredSize(pinkZoneSize);
        zone.setMinimumSize(pinkZoneSize);
        zone.setMaximumSize(pinkZoneSize);
        return zone;
    }

    public void updateHighlights() {
        // Clear all highlights first
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                cellViews[r][c].unhighlight();
            }
        }

        // Highlight selected cell
        Cell selected = gameController.getSelectedCell();
        if (selected != null) {
            cellViews[selected.getRow()][selected.getCol()].highlight();

            // Highlight legal moves
            for (Cell legalMove : gameController.getLegalMoves(selected)) {
                cellViews[legalMove.getRow()][legalMove.getCol()].highlight();
            }
        }
    }

    public void updateAllCellViews() {
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                cellViews[r][c].updatePieceDisplay();
            }
        }
    }
}