import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class Piece {
    private PieceType type;
    private Player player; // Original owner of the piece
    private Player controllingPlayer; // Player who currently controls this piece (null if same as player)

    // Cache of images
    private static final Map<String, ImageIcon> images = new HashMap<>();

    static {
        // Load all images once using multiple fallback methods
        for (PieceType t : PieceType.values()) {
            for (Player p : Player.values()) {
                String fileName = t.name() + p.name() + ".png"; // e.g., "KNIGHTSPRING.png"
                ImageIcon image = loadImage(fileName);
                if (image != null) {
                    images.put(fileName, image);
                }
            }
        }
    }

    private static ImageIcon loadImage(String fileName) {
        try {
            // Method 1: Try as resource (works when running from JAR)
            InputStream stream = Piece.class.getResourceAsStream("/pieces/" + fileName);
            if (stream != null) {
                BufferedImage img = ImageIO.read(stream);
                return new ImageIcon(img);
            }
        } catch (Exception e) {
            // Ignore and try next method
        }

        try {
            // Method 2: Try as file path relative to current directory
            File file = new File("src/main/resources/pieces/" + fileName);
            if (file.exists()) {
                BufferedImage img = ImageIO.read(file);
                return new ImageIcon(img);
            }
        } catch (Exception e) {
            // Ignore and try next method
        }

        try {
            // Method 3: Try resources folder in different location
            File file = new File("pieces/" + fileName);
            if (file.exists()) {
                BufferedImage img = ImageIO.read(file);
                return new ImageIcon(img);
            }
        } catch (Exception e) {
            System.err.println("Could not load image: " + fileName);
            return null;
        }

        return null;
    }

    public Piece(PieceType type, Player player) {
        this.type = type;
        this.player = player;
        this.controllingPlayer = null; // Initially controlled by original player
    }

    public PieceType getType() {
        return type;
    }

    public void setType(PieceType type) {
        this.type = type;
    }

    public Player getPlayer() {
        return player;
    }

    /**
     * Get the player who currently controls this piece
     * @return controlling player if different from original, otherwise original player
     */
    public Player getControllingPlayer() {
        return controllingPlayer != null ? controllingPlayer : player;
    }

    /**
     * Set the controlling player (used when pieces are inherited after checkmate)
     * @param controllingPlayer the new controlling player, or null to return control to original player
     */
    public void setControllingPlayer(Player controllingPlayer) {
        this.controllingPlayer = controllingPlayer;
    }

    /**
     * Check if this piece is controlled by someone other than its original player
     * @return true if the piece has been inherited by another player
     */
    public boolean isInherited() {
        return controllingPlayer != null;
    }

    /**
     * Get the original owner of this piece
     * @return the original player who owned this piece at game start
     */
    public Player getOriginalPlayer() {
        return player;
    }

    public ImageIcon getImageIcon(int size) {
        String key = type.name() + player.name() + ".png"; // e.g., "KNIGHTSPRING.png"
        ImageIcon originalIcon = images.get(key);

        if (originalIcon == null) {
            return null;
        }

        // Scale the image to the requested size
        Image originalImage = originalIcon.getImage();
        Image scaledImage = originalImage.getScaledInstance(size, size, Image.SCALE_SMOOTH);

        return new ImageIcon(scaledImage);
    }

    /**
     * Get a scaled image for drawing on components
     */
    public Image getScaledImage(int size) {
        ImageIcon icon = getImageIcon(size);
        return icon != null ? icon.getImage() : null;
    }

    @Override
    public String toString() {
        String prefix = isInherited() ? "(" + controllingPlayer + ")" : "";
        return prefix + player + " " + type;
    }
}