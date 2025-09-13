import java.awt.Color;

public enum Player {
    SPRING(new Color(28, 101, 74)),
    SUMMER(new Color(138, 51, 56)),
    FALL(new Color(33, 43, 52)),
    WINTER(new Color(201, 207, 197));

    private final Color color;

    Player(Color color) {
        this.color = color;
    }

    public Color color() {
        return color;
    }

    public Player next() {
        switch(this){
            case SPRING: return SUMMER;
            case SUMMER: return FALL;
            case FALL: return WINTER;
            case WINTER: return SPRING;
        }
        return SPRING;
    }

    // forward vector for pawn movement as (dr, dc)
    public int[] forwardDelta() {
        switch (this) {
            case SUMMER: return new int[]{1, 0};   // moves down
            case WINTER: return new int[]{-1, 0};  // moves up
            case FALL:  return new int[]{0, 1};   // moves right
            case SPRING:  return new int[]{0, -1};  // moves left
        }
        return new int[]{0,0};
    }

    @Override
    public String toString() {
        switch(this){
            case SUMMER: return "Summer";
            case SPRING: return "Spring";
            case WINTER: return "Winter";
            default: return "Fall";
        }
    }
}