import java.awt.*;

public class Game extends BaseFrame {

    static final int VIEW_W = 600;
    static final int VIEW_H = 400;

    static final int TILE  = 40;
    static final int MAP_W = 15;
    static final int MAP_H = 13;

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

    static final int    NUM_RAYS = VIEW_W;
    static final double FOV      = Math.toRadians(60);

    Player player = new Player(TILE * 2 + TILE/2, TILE * 2 + TILE/2);
    int prevMx = -1;

    double[] rayDist = new double[NUM_RAYS];
    double[] rayEndX = new double[NUM_RAYS];
    double[] rayEndY = new double[NUM_RAYS];

    public Game() {
        super("Catacombs", VIEW_W, VIEW_H);
    }

    @Override
    public void move() {
        if (mb == 1) {
            if (prevMx >= 0) player.angle += (mx - prevMx) * 0.004;
            prevMx = mx;
        } else {
            prevMx = -1;
        }

        double ox = player.x, oy = player.y;
        player.move(keys);
        if (MAP[(int)(player.y/TILE)][(int)(player.x/TILE)] == 1) {
            player.x = ox; player.y = oy;
        }

        castRays();
    }

    void castRays() {
        for (int i = 0; i < NUM_RAYS; i++) {
            double angle = (player.angle - FOV/2) + (FOV / (NUM_RAYS-1)) * i;
            double rdx = Math.cos(angle);
            double rdy = Math.sin(angle);

            int tx = (int)(player.x/TILE);
            int ty = (int)(player.y/TILE);

            double ddx = Math.abs(1.0/rdx);
            double ddy = Math.abs(1.0/rdy);

            int sx = rdx < 0 ? -1 : 1;
            int sy = rdy < 0 ? -1 : 1;

            double sdx = (rdx < 0 ? player.x/TILE - tx : tx + 1.0 - player.x/TILE) * ddx;
            double sdy = (rdy < 0 ? player.y/TILE - ty : ty + 1.0 - player.y/TILE) * ddy;

            boolean hitX = false;
            while (true) {
                if (sdx < sdy) { sdx += ddx; tx += sx; hitX = true; }
                else           { sdy += ddy; ty += sy; hitX = false; }
                if (ty < 0 || ty >= MAP_H || tx < 0 || tx >= MAP_W) break;
                if (MAP[ty][tx] == 1) {
                    double perpDist = hitX ? (sdx - ddx) * TILE : (sdy - ddy) * TILE;
                    rayDist[i] = perpDist * Math.cos(angle - player.angle);
                    rayEndX[i] = player.x + rdx * perpDist;
                    rayEndY[i] = player.y + rdy * perpDist;
                    break;
                }
            }
        }
    }

    @Override
    public void draw(Graphics g) {
        // ceiling
        g.setColor(new Color(50, 50, 50));
        g.fillRect(0, 0, VIEW_W, VIEW_H/2);
        // floor
        g.setColor(new Color(100, 100, 100));
        g.fillRect(0, VIEW_H/2, VIEW_W, VIEW_H/2);

        // wall slices
        for (int i = 0; i < NUM_RAYS; i++) {
            if (rayDist[i] == 0) continue;
            int sliceH = Math.min(VIEW_H, (int)(VIEW_H * TILE / rayDist[i]));
            int top    = VIEW_H/2 - sliceH/2;
            int bright = Math.max(40, 255 - (int)(rayDist[i] * 0.5));
            g.setColor(new Color(bright, bright, bright));
            g.fillRect(i, top, 1, sliceH);
        }

        // g.drawImage(handImage, VIEW_W/2 - hw/2, VIEW_H - hh, hw, hh, null);
    }

    public static void main(String[] args) { new Game(); }
}