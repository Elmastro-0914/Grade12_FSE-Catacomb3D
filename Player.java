import java.awt.*;

public class Player {
    double x, y, angle;
    int health = 100;
    public Rectangle playerBounds = new Rectangle((int)x - 10, (int)y - 10, 20, 20);
    final double MOVE_SPEED = 2.5;
    final double TURN_SPEED = 0.115;
    final int LEFT = 37, UP = 38, RIGHT = 39, DOWN = 40;

    public Player(double x, double y) {
        this.x = x;
        this.y = y;
        angle = 0;
    }

    public void move(boolean[] keys) {
        if (keys[LEFT])  angle -= TURN_SPEED;
        if (keys[RIGHT]) angle += TURN_SPEED;
        if (keys[UP]) {
            x += MOVE_SPEED * Math.cos(angle);
            y += MOVE_SPEED * Math.sin(angle);
        }
        if (keys[DOWN]) {
            x -= MOVE_SPEED * Math.cos(angle);
            y -= MOVE_SPEED * Math.sin(angle);
        }
        playerBounds.x = (int)x - 10;
        playerBounds.y = (int)y - 10;
    }

    public boolean collide(Rectangle wall) {
        return playerBounds.intersects(wall);
    }
}