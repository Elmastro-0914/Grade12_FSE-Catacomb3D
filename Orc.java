import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Orc {
    double x, y;
    int health = 80;
    boolean isAttacking = false;
    final double MOVE_SPEED = 2.4;
    final int SEARCH_RADIUS = 10;
    final int ATTACK_RADIUS = 2;

    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public Orc(int x, int y) {
        this.x = x;
        this.y = y;
    }

    boolean canSee(Player player) {
        if (((Math.abs(x - player.x)) * (Math.abs(x - player.x))) + ((Math.abs(y - player.y)) * (Math.abs(y - player.y))) <= SEARCH_RADIUS*SEARCH_RADIUS) {
            return true;
        } else {
            return false;
        }
    }

    boolean canAttack(Player player) {
        if (((Math.abs(x - player.x)) * (Math.abs(x - player.x))) + ((Math.abs(y - player.y)) * (Math.abs(y - player.y))) <= ATTACK_RADIUS*ATTACK_RADIUS) {
            return true;
        } else {
            return false;
        }
    }

    void attack(Player player) {
        if (canAttack(player)) {
            isAttacking = true;
            Runnable task = () -> player.health -= 10;
            scheduler.scheduleAtFixedRate(task, 0, 2, TimeUnit.SECONDS);
        }else {
            Runnable task = () -> System.out.println("random");
            scheduler.scheduleAtFixedRate(task, 0, 0, TimeUnit.SECONDS);

        }

    }

    void move(Player player) {
        if (canSee(player)) {
            if (!isAttacking) {
                // move to player
            }
        }
    }


    
}
