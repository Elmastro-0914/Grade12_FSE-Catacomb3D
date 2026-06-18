public class Player {
    // Position, stats, and abilities
    double x, y, angle;
    int health = 100;
    int maxHealth = 100;
    int potions = 5;
    boolean invincible;

    static final double MOVE_SPEED = 2.5;
    static final double TURN_SPEED = 0.115;
    static final int LEFT = 37, UP = 38, RIGHT = 39, DOWN = 40;
    static final int A = 65, D = 68, W = 87, S = 83;

    // Constructor
    public Player(double x, double y) {
        this.x = x;
        this.y = y;
        angle = 0;
    }

    // Handles turning and movement from arrow keys or WASD
    public void move(boolean[] keys) {
        if (keys[LEFT] || keys[A]) angle -= TURN_SPEED;
        if (keys[RIGHT] || keys[D]) angle += TURN_SPEED;
        if (keys[UP] || keys[W]) {
            x += MOVE_SPEED * Math.cos(angle);
            y += MOVE_SPEED * Math.sin(angle);
        }
        if (keys[DOWN] || keys[S]) {
            x -= MOVE_SPEED * Math.cos(angle);
            y -= MOVE_SPEED * Math.sin(angle);
        }
    }

    // Takes damage unless invincible
    public void takeDamage(int dmg) {
        if (invincible) return;
        health = Math.max(0, health - dmg);
    }

    // Uses a potion if available and not at full health
    public void usePotion() {
        if (potions > 0 && health < maxHealth) {
            potions--;
            health = Math.min(maxHealth, health + 10);
        }
    }

    // Checks if alive
    public boolean isAlive() {
        return health > 0;
    }
}