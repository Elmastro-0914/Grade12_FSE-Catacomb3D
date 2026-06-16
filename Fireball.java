public class Fireball {
    double x, y;
    double dx, dy;
    int damage;
    boolean alive;

    static final double SPEED = 6.0;

    public Fireball(double x, double y, double angle, int damage) {
        this.x = x;
        this.y = y;
        this.dx = Math.cos(angle) * SPEED;
        this.dy = Math.sin(angle) * SPEED;
        this.damage = damage;
        this.alive = true;
    }

    public void move() {
        x += dx;
        y += dy;
    }

    public double getRenderSize() {
        return 0.04 + (damage / 100.0) * 0.06;
    }
}
