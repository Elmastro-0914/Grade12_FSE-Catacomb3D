import java.awt.*;

public class Orc {
    // Position, state, and movement constants
    double x, y;
    int health;
    boolean alive;
    int mode;                // 0=direct chase, 1=strafe
    private int frameCount;
    private int attackTimer;

    static final int MAX_HEALTH = 80;
    static final double SPEED = 1.5;
    static final double CHASE_RADIUS = 400.0;
    static final double ATTACK_RADIUS = 32.0;
    static final int ATTACK_DAMAGE = 10;
    static final int ATTACK_COOLDOWN = 30;
    static final double COLLISION_RADIUS = 8.0;
    static final int TILE_SIZE = 40;

    Image[] walkFrames;
    Image[] attackFrames;

    // Constructor
    public Orc(double x, double y, Image[] walkFrames, Image[] attackFrames) {
        this.x = x;
        this.y = y;
        this.health = MAX_HEALTH;
        this.alive = true;
        this.frameCount = 0;
        this.attackTimer = 0;
        this.walkFrames = walkFrames;
        this.attackFrames = attackFrames;
    }

    // Chases the player, strafes when in range, attacks when close
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
                // direct pursuit
                nx = x + (dx / dist) * SPEED;
                ny = y + (dy / dist) * SPEED;
            } else {
                // strafe pursuit (sinusoidal lateral offset)
                double toAngle = Math.atan2(dy, dx);
                double perpAngle = toAngle + Math.PI / 2;
                double strafe = Math.sin(frameCount * 0.1);
                nx = x + Math.cos(toAngle) * SPEED * 0.6
                      + Math.cos(perpAngle) * SPEED * 0.4 * strafe;
                ny = y + Math.sin(toAngle) * SPEED * 0.6
                      + Math.sin(perpAngle) * SPEED * 0.4 * strafe;
            }
            int tileX = (int) (nx / TILE_SIZE);
            int tileY = (int) (ny / TILE_SIZE);
            // check center tile and bounding box corners to prevent wall clipping
            boolean blocked = false;
            if (tileX < 0 || tileX >= mapW || tileY < 0 || tileY >= mapH || map[tileY][tileX] == 1) {
                blocked = true;
            } else {
                double r = COLLISION_RADIUS;
                int[][] corners = {{-1,-1}, {1,-1}, {-1,1}, {1,1}};
                for (int[] c : corners) {
                    int tx = (int)((nx + c[0] * r) / TILE_SIZE);
                    int ty = (int)((ny + c[1] * r) / TILE_SIZE);
                    if (tx < 0 || tx >= mapW || ty < 0 || ty >= mapH || map[ty][tx] == 1) {
                        blocked = true;
                        break;
                    }
                }
            }
            if (!blocked) {
                x = nx;
                y = ny;
            }
        }
    }

    // Applies damage, returns true if the orc died
    public boolean takeDamage(int dmg) {
        health -= dmg;
        if (health <= 0 && alive) {
            alive = false;
            return true;
        }
        return false;
    }

    // Returns current sprite frame based on state
    public boolean isAttacking() { return attackTimer > 0; }

    public int getAnimationFrame() { return (frameCount / 8) % 4; }

    public Image getCurrentSprite() {
        return isAttacking() ? attackFrames[getAnimationFrame()] : walkFrames[getAnimationFrame()];
    }

    public double getRenderScale() { return 0.5; }
}