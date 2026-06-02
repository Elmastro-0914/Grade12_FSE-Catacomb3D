public class Player {
    double x, y;
    static final double MOVE_SPEED = 2.5;
    static final int LEFT = 37, UP = 38, RIGHT = 39, DOWN = 40;

    public Player(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void move(boolean[] keys) {
        if (keys[LEFT])  x -= MOVE_SPEED;
        if (keys[RIGHT]) x += MOVE_SPEED;
        if (keys[UP])    y -= MOVE_SPEED;
        if (keys[DOWN])  y += MOVE_SPEED;
    }
}