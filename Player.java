public class Player {
    double x, y, angle;
    int health = 100;
    int maxHealth = 100;
    int potions = 5;

    static final double MOVE_SPEED = 2.5;
    static final double TURN_SPEED = 0.115;
    static final int LEFT = 37, UP = 38, RIGHT = 39, DOWN = 40;

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
    }

    public void takeDamage(int dmg) {
        health = Math.max(0, health - dmg);
    }

    public void usePotion() {
        if (potions > 0 && health < maxHealth) {
            potions--;
            health = Math.min(maxHealth, health + 10);
        }
    }

    public boolean isAlive() {
        return health > 0;
    }
}
