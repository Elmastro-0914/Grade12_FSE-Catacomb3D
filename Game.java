import java.awt.*;

public class Game extends BaseFrame {
    static final int TILE = 40;
    static final int[][] MAP = {
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,1,1,0,0,0,0,1,0,0,0,0,1},
        {1,0,0,1,0,0,0,0,0,1,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,1,1,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,1,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,1,0,1,1,0,0,0,0,0,1},
        {1,0,0,0,0,1,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,1,1,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
    };

    Player player = new Player(TILE * 2.5, TILE * 2.5);

    public Game() {
        super("Top Down", MAP[0].length * TILE, MAP.length * TILE);
    }

    @Override
    public void move() {
        player.move(keys);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, pane.getWidth(), pane.getHeight());

        g.setColor(Color.BLACK);
        for (int row = 0; row < MAP.length; row++) {
            for (int col = 0; col < MAP[row].length; col++) {
                if (MAP[row][col] == 1) {
                    g.fillRect(col * TILE, row * TILE, TILE, TILE);
                }
            }
        }

        g.setColor(Color.RED);
        g.fillOval((int)player.x - 10, (int)player.y - 10, 20, 20);
    }

    public static void main(String[] args) {
        new Game();
    }
}
