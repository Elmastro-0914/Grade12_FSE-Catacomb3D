import java.awt.*;

public class Fireball {
    // Position, velocity, damage state
    double x, y;
    double dx, dy;
    int damage;
    boolean alive;
    private int frameCount;

    static final double SPEED = 6.0;

    Image[] weakFrames;
    Image[] strongFrames;

    // Constructor, sets movement direction from angle
    public Fireball(double x, double y, double angle, int damage,
                    Image[] weakFrames, Image[] strongFrames) {
        this.x = x;
        this.y = y;
        this.dx = Math.cos(angle) * SPEED;
        this.dy = Math.sin(angle) * SPEED;
        this.damage = damage;
        this.alive = true;
        this.frameCount = 0;
        this.weakFrames = weakFrames;
        this.strongFrames = strongFrames;
    }

    // Moves the fireball each tick
    public void move() {
        x += dx;
        y += dy;
        frameCount++;
    }

    // Returns scale, animation frame, and sprite based on charge
    public double getRenderScale() {
        return 0.02 + (damage / 100.0) * 0.04;
    }

    public int getAnimationFrame() { return (frameCount / 6) % 2; }

    public boolean isStrong() { return damage >= 75; }

    public Image getCurrentSprite() {
        return isStrong() ? strongFrames[getAnimationFrame()] : weakFrames[getAnimationFrame()];
    }
}