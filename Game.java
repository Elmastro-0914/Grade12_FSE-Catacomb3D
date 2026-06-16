import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Game extends BaseFrame {

    static final int GAME_W   = 580;
    static final int GAME_H   = 420;
    static final int RIGHT_W  = 120;
    static final int BOTTOM_H = 80;
    static final int SCREEN_W = GAME_W + RIGHT_W;
    static final int SCREEN_H = GAME_H + BOTTOM_H;
    static final int PERSON_H = 140;
    static final int HEALTH_W = GAME_W / 2;

    static final int TILE  = 40;
    static final int MAP_W = 30;
    static final int MAP_H = 22;

    private static final int[][] MAP_TEMPLATE = {
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,1,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,3,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
    };

    static int[][] MAP;

    static final int NUM_RAYS = GAME_W;
    static final double FOV = Math.toRadians(60);

    int screen = 0;

    Player player;
    List<Orc> orcs;
    List<Fireball> fireballs;
    int goalTileX, goalTileY;
    boolean goalFound;

    int prevMx = -1;

    int attackCharge = 0;
    boolean spaceWasDown = false;
    boolean eWasDown = false;

    static final int HAND_W = 160;
    static final int HAND_H = 140;
    static final int HAND_REST = GAME_H;
    static final int HAND_RAISED = GAME_H - HAND_H;
    int handY = HAND_REST;

    Image handImage = loadImage("hand.png");
    Image personImage = loadImage("person.png");
    Image potionImage = loadImage("potion.png");
    Image boomImage = loadImage("boom.png");

    double[] rayDist = new double[NUM_RAYS];
    double[] rayEndX = new double[NUM_RAYS];
    double[] rayEndY = new double[NUM_RAYS];

    public Game() {
        super("Catacombs 3D", SCREEN_W, SCREEN_H);
        MAP = copyMap(MAP_TEMPLATE);
        initGame();
    }

    private static int[][] copyMap(int[][] src) {
        int[][] dst = new int[src.length][];
        for (int i = 0; i < src.length; i++) {
            dst[i] = src[i].clone();
        }
        return dst;
    }

    void initGame() {
        player = new Player(TILE * 2 + TILE / 2, TILE * 2 + TILE / 2);
        orcs = new ArrayList<>();
        fireballs = new ArrayList<>();
        spawnOrcs(15);
        findGoal();
        attackCharge = 0;
        spaceWasDown = false;
        eWasDown = false;
        handY = HAND_REST;
    }

    void findGoal() {
        goalFound = false;
        for (int r = 0; r < MAP_H; r++) {
            for (int c = 0; c < MAP_W; c++) {
                if (MAP[r][c] == 2) {
                    goalTileX = c;
                    goalTileY = r;
                    goalFound = true;
                    return;
                }
            }
        }
    }

    void spawnOrcs(int count) {
        Random rand = new Random();
        List<int[]> positions = new ArrayList<>();
        List<int[]> keyTiles = new ArrayList<>();

        for (int r = 0; r < MAP_H; r++)
            for (int c = 0; c < MAP_W; c++)
                if (MAP[r][c] == 3) keyTiles.add(new int[]{c, r});

        int[] goal = null;
        for (int r = 0; r < MAP_H; r++)
            for (int c = 0; c < MAP_W; c++)
                if (MAP[r][c] == 2) goal = new int[]{c, r};
        if (goal != null) {
            for (int i = 0; i < 4 && positions.size() < count; i++)
                placeNear(positions, goal[0], goal[1], 3, rand);
        }

        for (int[] p : keyTiles) {
            for (int i = 0; i < 2 && positions.size() < count; i++)
                placeNear(positions, p[0], p[1], 2, rand);
        }

        while (positions.size() < count) {
            int x = rand.nextInt(MAP_W);
            int y = rand.nextInt(MAP_H);
            if (MAP[y][x] == 0 && !(x == 2 && y == 2) && uniquePos(positions, x, y)) {
                positions.add(new int[]{x, y});
            }
        }

        for (int[] pos : positions) {
            Orc o = new Orc(pos[0] * TILE + TILE / 2, pos[1] * TILE + TILE / 2);
            o.mode = rand.nextDouble() < 0.5 ? 0 : 1;
            orcs.add(o);
        }
    }

    private void placeNear(List<int[]> positions, int cx, int cy, int range, Random rand) {
        for (int attempt = 0; attempt < 20; attempt++) {
            int dx = rand.nextInt(range * 2 + 1) - range;
            int dy = rand.nextInt(range * 2 + 1) - range;
            if (dx == 0 && dy == 0) continue;
            int nx = cx + dx, ny = cy + dy;
            if (nx >= 0 && nx < MAP_W && ny >= 0 && ny < MAP_H && MAP[ny][nx] == 0
                && !(nx == 2 && ny == 2) && uniquePos(positions, nx, ny)) {
                positions.add(new int[]{nx, ny});
                return;
            }
        }
    }

    private boolean uniquePos(List<int[]> positions, int x, int y) {
        for (int[] p : positions)
            if (p[0] == x && p[1] == y) return false;
        return true;
    }

    @Override
    public void move() {
        if (screen == 0) moveMenu();
        else if (screen == 1) moveGame();
        else moveEnd();
    }

    void moveMenu() {
        if (keys[10]) {
            screen = 1;
            keys[10] = false;
        }
    }

    void moveEnd() {
        if (keys[10]) {
            MAP = copyMap(MAP_TEMPLATE);
            initGame();
            screen = 1;
            keys[10] = false;
        }
    }

    void moveGame() {
        if (mb == 1) {
            if (prevMx >= 0) player.angle += (mx - prevMx) * 0.004;
            prevMx = mx;
        } else {
            prevMx = -1;
        }

        double ox = player.x, oy = player.y;
        player.move(keys);
        if (MAP[(int) (player.y / TILE)][(int) (player.x / TILE)] == 1) {
            player.x = ox;
            player.y = oy;
        }

        if (keys[SPACE]) {
            attackCharge = Math.min(100, attackCharge + 2);
        } else {
            if (spaceWasDown && attackCharge > 0) {
                int dmg = attackCharge < 25 ? 25 :
                          attackCharge < 50 ? 50 :
                          attackCharge < 75 ? 80 : 100;
                fireballs.add(new Fireball(player.x, player.y, player.angle, dmg));
            }
            attackCharge = 0;
        }
        spaceWasDown = keys[SPACE];

        int targetY = HAND_REST - (int) ((HAND_REST - HAND_RAISED) * (attackCharge / 40.0));
        handY += (targetY - handY);
        handY = Math.max(HAND_RAISED, handY);

        if (keys[77] && !eWasDown) {
            player.usePotion();
        }
        eWasDown = keys[77];

        for (Orc o : orcs) {
            if (o.alive) o.update(player, MAP, MAP_W, MAP_H);
        }

        for (int i = fireballs.size() - 1; i >= 0; i--) {
            Fireball fb = fireballs.get(i);
            fb.move();

            int tx = (int) (fb.x / TILE);
            int ty = (int) (fb.y / TILE);
            if (tx < 0 || tx >= MAP_W || ty < 0 || ty >= MAP_H || MAP[ty][tx] == 1) {
                fireballs.remove(i);
                continue;
            }

            boolean hit = false;
            for (Orc o : orcs) {
                if (!o.alive) continue;
                double dx = fb.x - o.x;
                double dy = fb.y - o.y;
                if (dx * dx + dy * dy < TILE * TILE / 4) {
                    o.takeDamage(fb.damage);
                    hit = true;
                    break;
                }
            }
            if (hit) fireballs.remove(i);
        }

        int px = (int) (player.x / TILE);
        int py = (int) (player.y / TILE);
        if (px >= 0 && px < MAP_W && py >= 0 && py < MAP_H && MAP[py][px] == 3) {
            player.potions++;
            MAP[py][px] = 0;
        }

        if (px >= 0 && px < MAP_W && py >= 0 && py < MAP_H && MAP[py][px] == 2) {
            screen = 3;
        }

        if (!player.isAlive()) {
            screen = 2;
        }

        castRays();
    }

    void castRays() {
        for (int i = 0; i < NUM_RAYS; i++) {
            double angle = (player.angle - FOV / 2) + (FOV / (NUM_RAYS - 1)) * i;
            double rdx = Math.cos(angle), rdy = Math.sin(angle);

            int tx = (int) (player.x / TILE);
            int ty = (int) (player.y / TILE);

            double ddx = Math.abs(1.0 / rdx);
            double ddy = Math.abs(1.0 / rdy);

            int sx = rdx < 0 ? -1 : 1;
            int sy = rdy < 0 ? -1 : 1;
            double sdx = (rdx < 0 ? player.x / TILE - tx : tx + 1.0 - player.x / TILE) * ddx;
            double sdy = (rdy < 0 ? player.y / TILE - ty : ty + 1.0 - player.y / TILE) * ddy;

            boolean hitX = false;
            rayDist[i] = Double.MAX_VALUE;
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
        if (screen == 0) drawMenu(g);
        else if (screen == 1) drawGame(g);
        else if (screen == 2) drawGameOver(g);
        else if (screen == 3) drawWin(g);
    }

    void drawMenu(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, SCREEN_W, SCREEN_H);
        g.setFont(new Font("Monospaced", Font.BOLD, 40));
        FontMetrics fm = g.getFontMetrics();
        String text = "PRESS ENTER TO START";
        g.setColor(Color.BLACK);
        g.drawString(text, SCREEN_W / 2 - fm.stringWidth(text) / 2, SCREEN_H / 2);
    }

    void drawGameOver(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, SCREEN_W, SCREEN_H);
        g.setFont(new Font("Monospaced", Font.BOLD, 48));
        FontMetrics fm = g.getFontMetrics();
        String text = "GAME OVER";
        g.setColor(Color.RED);
        g.drawString(text, SCREEN_W / 2 - fm.stringWidth(text) / 2, SCREEN_H / 2 - 20);
        g.setFont(new Font("Monospaced", Font.BOLD, 18));
        String sub = "PRESS ENTER TO RETRY";
        g.setColor(Color.WHITE);
        g.drawString(sub, SCREEN_W / 2 - g.getFontMetrics().stringWidth(sub) / 2, SCREEN_H / 2 + 20);
    }

    void drawWin(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, SCREEN_W, SCREEN_H);
        g.setFont(new Font("Monospaced", Font.BOLD, 48));
        FontMetrics fm = g.getFontMetrics();
        String text = "YOU WIN!";
        g.setColor(Color.CYAN);
        g.drawString(text, SCREEN_W / 2 - fm.stringWidth(text) / 2, SCREEN_H / 2 - 20);
        g.setFont(new Font("Monospaced", Font.BOLD, 18));
        String sub = "PRESS ENTER TO PLAY AGAIN";
        g.setColor(Color.WHITE);
        g.drawString(sub, SCREEN_W / 2 - g.getFontMetrics().stringWidth(sub) / 2, SCREEN_H / 2 + 20);
    }

    void drawGame(Graphics g) {
        draw3D(g);
        drawRightPanel(g);
        drawBottomPanel(g);
    }

    void draw3D(Graphics g) {
        g.setColor(new Color(50, 50, 50));
        g.fillRect(0, 0, GAME_W, GAME_H / 2);
        g.setColor(new Color(100, 100, 100));
        g.fillRect(0, GAME_H / 2, GAME_W, GAME_H / 2);

        for (int i = 0; i < NUM_RAYS; i++) {
            if (rayDist[i] == Double.MAX_VALUE) continue;
            int sliceH = Math.min(GAME_H, (int) (GAME_H * TILE / rayDist[i]));
            int top = GAME_H / 2 - sliceH / 2;
            int bright = Math.max(40, 255 - (int) (rayDist[i] * 0.4));
            g.setColor(new Color(bright, bright, bright));
            g.fillRect(i, top, 1, sliceH);
        }

        renderEntities(g);

        int hx = GAME_W / 2 - HAND_W / 2;
        if (handImage != null)
            g.drawImage(handImage, hx, handY, HAND_W, HAND_H, null);
    }

    void renderEntities(Graphics g) {
        List<RenderItem> items = new ArrayList<>();

        for (Orc o : orcs) {
            if (!o.alive) continue;
            double dx = o.x - player.x;
            double dy = o.y - player.y;
            double dist = Math.sqrt(dx * dx + dy * dy);
            items.add(new RenderItem(dist, 0, o.x, o.y));
        }

        for (Fireball fb : fireballs) {
            if (!fb.alive) continue;
            double dx = fb.x - player.x;
            double dy = fb.y - player.y;
            double dist = Math.sqrt(dx * dx + dy * dy);
            items.add(new RenderItem(dist, 1, fb.x, fb.y, fb));
        }

        for (int r = 0; r < MAP_H; r++) {
            for (int c = 0; c < MAP_W; c++) {
                if (MAP[r][c] == 3) {
                    double px = c * TILE + TILE / 2;
                    double py = r * TILE + TILE / 2;
                    double dx = px - player.x;
                    double dy = py - player.y;
                    double dist = Math.sqrt(dx * dx + dy * dy);
                    items.add(new RenderItem(dist, 2, px, py));
                }
            }
        }

        if (goalFound) {
            double gx = goalTileX * TILE + TILE / 2;
            double gy = goalTileY * TILE + TILE / 2;
            double dx = gx - player.x;
            double dy = gy - player.y;
            double dist = Math.sqrt(dx * dx + dy * dy);
            items.add(new RenderItem(dist, 3, gx, gy));
        }

        Collections.sort(items, (a, b) -> Double.compare(b.dist, a.dist));

        for (RenderItem ri : items) {
            switch (ri.type) {
                case 0: renderBillboard(g, ri.x, ri.y, Color.RED, 0.5); break;
                case 1:
                    Fireball fb = (Fireball) ri.data;
                    renderCircleBillboard(g, ri.x, ri.y, Color.ORANGE, fb.getRenderSize());
                    break;
                case 2: renderBillboard(g, ri.x, ri.y, Color.GREEN, 0.25); break;
                case 3: renderBillboard(g, ri.x, ri.y, Color.BLUE, 1.0); break;
            }
        }
    }

    void renderBillboard(Graphics g, double ex, double ey, Color color, double wRatio) {
        double dx = ex - player.x;
        double dy = ey - player.y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist < 0.5) return;

        double angleTo = Math.atan2(dy, dx) - player.angle;
        while (angleTo > Math.PI) angleTo -= 2 * Math.PI;
        while (angleTo < -Math.PI) angleTo += 2 * Math.PI;

        if (Math.abs(angleTo) > FOV / 2 + 0.1) return;

        double screenX = GAME_W / 2 + (angleTo / (FOV / 2)) * (GAME_W / 2);
        double spriteH = TILE * GAME_H / dist;
        double spriteW = spriteH * wRatio;
        double top = GAME_H / 2 - spriteH / 2;
        double left = screenX - spriteW / 2;

        int drawLeft = Math.max(0, (int) left);
        int drawRight = Math.min(GAME_W - 1, (int) (left + spriteW));

        for (int x = drawLeft; x <= drawRight; x++) {
            if (dist < rayDist[x]) {
                g.setColor(color);
                g.fillRect(x, (int) top, 1, (int) spriteH);
            }
        }
    }

    void renderCircleBillboard(Graphics g, double ox, double oy, Color color, double sizeRatio) {
        double dx = ox - player.x;
        double dy = oy - player.y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist < 0.5) return;

        double angleTo = Math.atan2(dy, dx) - player.angle;
        while (angleTo > Math.PI) angleTo -= 2 * Math.PI;
        while (angleTo < -Math.PI) angleTo += 2 * Math.PI;

        if (Math.abs(angleTo) > FOV / 2 + 0.1) return;

        double screenX = GAME_W / 2 + (angleTo / (FOV / 2)) * (GAME_W / 2);
        double radius = Math.min(TILE * GAME_H / dist * sizeRatio, GAME_H / 2);

        int drawLeft = Math.max(0, (int) (screenX - radius));
        int drawRight = Math.min(GAME_W - 1, (int) (screenX + radius));

        for (int x = drawLeft; x <= drawRight; x++) {
            if (dist < rayDist[x]) {
                double dxPx = Math.abs(x - screenX);
                if (dxPx < radius) {
                    double halfH = Math.sqrt(radius * radius - dxPx * dxPx);
                    g.setColor(color);
                    g.fillRect(x, (int) (GAME_H / 2 - halfH), 1, (int) (halfH * 2));
                }
            }
        }
    }

    void drawRightPanel(Graphics g) {
        int ox = GAME_W;
        int pad = 8;

        g.setColor(Color.WHITE);
        g.fillRect(ox, 0, RIGHT_W, SCREEN_H);
        g.setColor(Color.BLACK);
        g.drawLine(ox, 0, ox, SCREEN_H);

        g.setColor(new Color(220, 220, 220));
        g.fillRect(ox + pad, pad, RIGHT_W - pad * 2, PERSON_H);
        g.setColor(Color.BLACK);
        g.drawRect(ox + pad, pad, RIGHT_W - pad * 2, PERSON_H);
        if (personImage != null)
            g.drawImage(personImage, ox + pad, pad, RIGHT_W - pad * 2, PERSON_H, null);

        int barTop = PERSON_H + 20;
        int barH = GAME_H - barTop - pad;
        int barX = ox + pad;
        int barW = RIGHT_W - pad * 2;

        g.setColor(new Color(220, 220, 220));
        g.fillRect(barX, barTop, barW, barH);
        g.drawImage(boomImage, barX, barTop, barW, barH, null);
        g.setColor(Color.BLACK);
        g.drawRect(barX, barTop, barW, barH);

        int fillH = barH * attackCharge / 100;
        g.setColor(Color.BLACK);
        g.fillRect(barX + 1, barTop, barW - 1, barH - fillH);
    }

    void drawBottomPanel(Graphics g) {
        int oy = GAME_H;
        int pad = 8;

        g.setColor(Color.WHITE);
        g.fillRect(0, oy, GAME_W, BOTTOM_H);
        g.setColor(Color.BLACK);
        g.drawLine(0, oy, GAME_W, oy);

        g.setFont(new Font("Monospaced", Font.BOLD, 9));
        g.setColor(Color.BLACK);
        g.drawString("HEALTH", pad + 2, oy + 14);

        int bx = pad, by = oy + 18, bw = HEALTH_W - pad * 2, bh = BOTTOM_H - 26;
        g.setColor(new Color(220, 220, 220));
        g.fillRect(bx, by, bw, bh);
        g.setColor(Color.BLACK);
        g.drawRect(bx, by, bw, bh);

        int healthPct = player.health * (bw - 1) / player.maxHealth;
        g.setColor(Color.BLACK);
        g.fillRect(bx + 1, by + 1, healthPct, bh - 1);

        g.setFont(new Font("Monospaced", Font.BOLD, 11));
        g.setColor(Color.WHITE);
        g.drawString(player.health + " / " + player.maxHealth, bx + bw / 2 - 22, by + bh / 2 + 4);

        g.setColor(Color.BLACK);
        g.drawLine(HEALTH_W, oy, HEALTH_W, oy + BOTTOM_H);

        int ix = HEALTH_W + pad;
        g.setFont(new Font("Monospaced", Font.BOLD, 9));
        g.setColor(Color.BLACK);
        g.drawString("INVENTORY", ix + 2, oy + 14);

        int slotS = bh;
        int slotX = ix, slotY = oy + 18;
        g.setColor(new Color(220, 220, 220));
        g.fillRect(slotX, slotY, slotS, slotS);
        g.setColor(Color.BLACK);
        g.drawRect(slotX, slotY, slotS, slotS);
        if (potionImage != null)
            g.drawImage(potionImage, slotX + 2, slotY + 2, slotS - 4, slotS - 4, null);

        g.setFont(new Font("Monospaced", Font.BOLD, 12));
        g.setColor(Color.BLACK);
        g.drawString("Potions: " + player.potions, slotX + slotS + 8, slotY + slotS / 2 + 4);
    }

    public static void main(String[] args) { new Game(); }

    private static class RenderItem {
        double dist;
        int type;
        double x, y;
        Object data;
        RenderItem(double dist, int type, double x, double y) {
            this(dist, type, x, y, null);
        }
        RenderItem(double dist, int type, double x, double y, Object data) {
            this.dist = dist;
            this.type = type;
            this.x = x;
            this.y = y;
            this.data = data;
        }
    }
}
