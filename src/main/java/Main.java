import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main extends JFrame {
    private GameController gameController;
    private BoardView boardView;
    private JLabel gameStatusLabel;
    private JLabel currentPlayerLabel;

    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fall back to default look and feel
        }

        SwingUtilities.invokeLater(() -> {
            new Main().createAndShowGUI();
        });
    }

    private void createAndShowGUI() {
        setTitle("Four Seasons Chess");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        // Initialize game
        startNewGame();

        // Create menu bar
        setJMenuBar(createMenuBar());

        // Setup main layout
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(31, 54, 93));

        // Create status panel
        JPanel statusPanel = createStatusPanel();

        // Create main content panel with proper sizing
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(31, 54, 93));
        contentPanel.add(boardView, BorderLayout.CENTER);
        contentPanel.add(statusPanel, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);

        // Pack to preferred size, then center
        pack();
        setLocationRelativeTo(null);

        // Set minimum size to current size to prevent shrinking too small
        setMinimumSize(getSize());

        setVisible(true);
    }

    private void startNewGame() {
        gameController = new GameController();
        boardView = new BoardView(gameController);

        setupCallbacks();
    }

    private void setupCallbacks() {
        gameController.setBoardViewUpdateCallback(() ->
                SwingUtilities.invokeLater(() -> boardView.updateHighlights()));

        gameController.setPieceUpdateCallback(() ->
                SwingUtilities.invokeLater(() -> boardView.updateAllCellViews()));

        gameController.setStatusCallback(player ->
                SwingUtilities.invokeLater(() -> updateCurrentPlayer(player)));

        gameController.setGameStatusCallback(message ->
                SwingUtilities.invokeLater(() -> updateGameStatusMessage(message)));
    }

    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setBackground(new Color(31, 54, 93));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Current player display with background
        JPanel playerPanel = new JPanel();
        playerPanel.setBackground(new Color(220, 224, 192));
        playerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 166), 10),
                BorderFactory.createEmptyBorder(15, 30, 15, 30)
        ));

        currentPlayerLabel = new JLabel("Current Player: Spring", JLabel.CENTER);
        currentPlayerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        currentPlayerLabel.setForeground(new Color(41, 79, 66));
        playerPanel.add(currentPlayerLabel);

        // Game status label
        gameStatusLabel = new JLabel(" ", JLabel.CENTER);
        gameStatusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gameStatusLabel.setForeground(new Color(255, 107, 107));

        statusPanel.add(Box.createVerticalGlue());
        statusPanel.add(playerPanel);
        statusPanel.add(Box.createVerticalStrut(10));
        statusPanel.add(gameStatusLabel);
        statusPanel.add(Box.createVerticalGlue());

        return statusPanel;
    }

    private void updateCurrentPlayer(Player player) {
        currentPlayerLabel.setText("Current Player: " + player.toString());
        currentPlayerLabel.setForeground(player.color());
    }

    private void updateGameStatusMessage(String message) {
        gameStatusLabel.setText(message);

        // Auto-clear message after 5 seconds if it's not a game over message
        if (!message.contains("wins!")) {
            Timer timer = new Timer(5000, e -> {
                if (gameStatusLabel.getText().equals(message)) {
                    gameStatusLabel.setText(" ");
                }
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(41, 79, 66));
        menuBar.setBorderPainted(false);

        // Game Menu
        JMenu gameMenu = new JMenu("Game");
        gameMenu.setForeground(new Color(200, 205, 166));
        gameMenu.setFont(new Font("Arial", Font.BOLD, 14));

        JMenuItem newGame = new JMenuItem("New Game");
        JMenuItem exit = new JMenuItem("Exit");

        newGame.addActionListener(e -> {
            startNewGame();
            // Update the display
            remove(((BorderLayout) getContentPane().getLayout()).getLayoutComponent(BorderLayout.CENTER));

            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.setBackground(new Color(31, 54, 93));
            contentPanel.add(boardView, BorderLayout.CENTER);
            contentPanel.add(((BorderLayout) getContentPane().getLayout()).getLayoutComponent(BorderLayout.SOUTH), BorderLayout.SOUTH);

            add(contentPanel, BorderLayout.CENTER);
            pack();
            repaint();
        });

        exit.addActionListener(e -> System.exit(0));

        gameMenu.add(newGame);
        gameMenu.addSeparator();
        gameMenu.add(exit);

        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setForeground(new Color(200, 205, 166));
        helpMenu.setFont(new Font("Arial", Font.BOLD, 14));

        JMenuItem about = new JMenuItem("About");
        JMenuItem rules = new JMenuItem("Rules");
        JMenuItem controls = new JMenuItem("Controls");

        about.addActionListener(e -> showAboutDialog());
        rules.addActionListener(e -> showRulesDialog());
        controls.addActionListener(e -> showControlsDialog());

        helpMenu.add(rules);
        helpMenu.add(controls);
        helpMenu.addSeparator();
        helpMenu.add(about);

        menuBar.add(gameMenu);
        menuBar.add(helpMenu);
        return menuBar;
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "Four Seasons Chess Variant\n\n" +
                        "A four-player chess variant for hot-seat play.\n\n" +
                        "Version: 2.0 (Swing)\n" +
                        "Supports local hot-seat games with full chess rules including\n" +
                        "check, checkmate, and piece inheritance.",
                "About Four Seasons",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showRulesDialog() {
        JOptionPane.showMessageDialog(this,
                "Four Seasons is played on an 8x8 board with four players.\n\n" +
                        "Players: Spring (Green), Summer (Red), Fall (Blue), Winter (White)\n\n" +
                        "Pieces move similarly to traditional chess:\n" +
                        "• King: One square in any direction\n" +
                        "• Rook: Horizontal and vertical lines\n" +
                        "• Knight: L-shaped moves\n" +
                        "• Elephant: Two squares diagonally\n" +
                        "• Pawn: Forward one square, captures diagonally\n" +
                        "• General: One square diagonally (promoted pawn)\n\n" +
                        "Turn order: Spring → Summer → Fall → Winter\n\n" +
                        "Chess Rules:\n" +
                        "• Players must move out of check\n" +
                        "• Checkmate eliminates the player and transfers their pieces\n" +
                        "• Inherited pieces move in their original direction\n" +
                        "• Last player with a king wins\n\n" +
                        "Hot-seat play: Players share the same device and take turns",
                "Game Rules",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showControlsDialog() {
        JOptionPane.showMessageDialog(this,
                "Click on a piece to select it.\n" +
                        "Click on a highlighted square to move.\n" +
                        "Click elsewhere to deselect.\n\n" +
                        "Hot-seat Play:\n" +
                        "• Players take turns using the same device\n" +
                        "• Only the current player can move their pieces\n" +
                        "• Pass the device to the next player after your turn\n\n" +
                        "Turn-Based Play:\n" +
                        "• You can only move on your turn\n" +
                        "• Turn order is Spring → Summer → Fall → Winter\n" +
                        "• Eliminated players are skipped",
                "Controls",
                JOptionPane.INFORMATION_MESSAGE);
    }
}