public class Orc {
    double x, y;
    int health;
    boolean alive;
    int mode;
    private int frameCount;
    private int attackTimer;

    static final int MAX_HEALTH = 80;
    static final double SPEED = 1.5;
    static final double CHASE_RADIUS = 400.0;
    static final double ATTACK_RADIUS = 32.0;
    static final int ATTACK_DAMAGE = 10;
    static final int ATTACK_COOLDOWN = 30;

    public Orc(double x, double y) {
        this.x = x;
        this.y = y;
        this.health = MAX_HEALTH;
        this.alive = true;
        this.frameCount = 0;
        this.attackTimer = 0;
    }

    public void update(Player player, int[][] map, int mapW, int mapH) {
        if (!alive) return;
        if (attackTimer > 0) attackTimer--;
        frameCount++;

        double dx = player.x - x;
        double dy = player.y - y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist < ATTACK_RADIUS && attackTimer == 0) {
            player.takeDamage(ATTACK_DAMAGE);
            attackTimer = ATTACK_COOLDOWN;
        } else if (dist < CHASE_RADIUS) {
            double nx, ny;
            if (mode == 0) {
                nx = x + (dx / dist) * SPEED;
                ny = y + (dy / dist) * SPEED;
            } else {
                double toAngle = Math.atan2(dy, dx);
                double perpAngle = toAngle + Math.PI / 2;
                double strafe = Math.sin(frameCount * 0.1);
                nx = x + Math.cos(toAngle) * SPEED * 0.6
                      + Math.cos(perpAngle) * SPEED * 0.4 * strafe;
                ny = y + Math.sin(toAngle) * SPEED * 0.6
                      + Math.sin(perpAngle) * SPEED * 0.4 * strafe;
            }
            int tx = (int) (nx / 40);
            int ty = (int) (ny / 40);
            if (tx >= 0 && tx < mapW && ty >= 0 && ty < mapH && map[ty][tx] != 1) {
                x = nx;
                y = ny;
            }
        }
    }

    public void takeDamage(int dmg) {
        health -= dmg;
        if (health <= 0) alive = false;
    }
}
