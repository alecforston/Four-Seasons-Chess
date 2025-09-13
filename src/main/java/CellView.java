import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CellView extends JPanel {
    private final Cell cell;
    private final GameController gameController;
    private boolean isHighlighted = false;

    private static final Color LIGHT_COLOR = new Color(213, 212, 120);
    private static final Color DARK_COLOR = new Color(106, 50, 45);
    private static final Color GREEN_ZONE_COLOR = new Color(41, 79, 66);
    private static final Color SEPARATOR_COLOR = new Color(200, 205, 166);
    private static final Color HIGHLIGHT_COLOR = Color.YELLOW;

    private static final int CELL_SIZE = 80;

    public CellView(Cell cell, GameController gameController) {
        this.cell = cell;
        this.gameController = gameController;

        setPreferredSize(new Dimension(CELL_SIZE, CELL_SIZE));
        setMinimumSize(new Dimension(CELL_SIZE, CELL_SIZE));
        setMaximumSize(new Dimension(CELL_SIZE, CELL_SIZE));
        setOpaque(true);

        // Set cell background color
        int r = cell.getRow();
        int c = cell.getCol();
        boolean isDark = (r + c) % 2 == 1;
        setBackground(isDark ? DARK_COLOR : LIGHT_COLOR);

        // Add mouse click handler
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    gameController.handleCellClick(cell);
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int r = cell.getRow();
        int c = cell.getCol();

        // Draw central cross lines for center region (2-5)
        if ((r >= 2 && r <= 5) && (c >= 2 && c <= 5)) {
            g2d.setStroke(new BasicStroke(10));

            if (r - c == 0) { // Main diagonal
                g2d.setColor(LIGHT_COLOR);
                if (r == 2) {
                    g2d.drawLine(7, 7, 80, 80);
                } else {
                    g2d.drawLine(8, 8, 80, 80);
                }
            }
            if (r + c == 7) { // Anti-diagonal
                g2d.setColor(DARK_COLOR);
                if (r == 2) {
                    g2d.drawLine(80, 0, 7, 73);
                } else {
                    g2d.drawLine(80, 0, 0, 80);
                }
            }
        }

        // Draw piece image
        Piece piece = cell.getPiece();
        if (piece != null) {
            Image pieceImage = piece.getScaledImage(CELL_SIZE - 10);
            if (pieceImage != null) {
                // Add border shadow effect - draw multiple offset copies with decreasing opacity
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
                g2d.drawImage(pieceImage, 5, 5, this); // Bottom-right shadow
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                g2d.drawImage(pieceImage, 7, 7, this); // Bottom-right shadow
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
                g2d.drawImage(pieceImage, 6, 6, this); // Middle shadow
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
                g2d.drawImage(pieceImage, 8, 8, this); // Far shadow

                // Draw the main piece image
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                g2d.drawImage(pieceImage, 5, 5, this);
            } else {
                // Fallback: draw a colored circle with text and border shadow
                // Draw shadow
                g2d.setColor(new Color(0, 0, 0, 60));
                g2d.fillOval(12, 12, CELL_SIZE - 20, CELL_SIZE - 20);

                // Draw main piece
                g2d.setColor(piece.getControllingPlayer().color());
                g2d.fillOval(10, 10, CELL_SIZE - 20, CELL_SIZE - 20);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 10));
                String text = piece.getType().name().substring(0, 1);
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();
                g2d.drawString(text, (CELL_SIZE - textWidth) / 2, (CELL_SIZE + textHeight / 2) / 2);
            }
        }

        // Draw highlight if selected - single color only
        if (isHighlighted) {
            g2d.setColor(HIGHLIGHT_COLOR);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRect(1, 1, CELL_SIZE - 3, CELL_SIZE - 3);
        }
    }

    public void highlight() {
        isHighlighted = true;
        repaint();
    }

    public void unhighlight() {
        isHighlighted = false;
        repaint();
    }

    public void updatePieceDisplay() {
        repaint();
    }

    public Cell getCell() {
        return cell;
    }
}