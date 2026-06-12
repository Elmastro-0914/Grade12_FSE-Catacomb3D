import java.awt.*;

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
    static final int MAP_W = 25;
    static final int MAP_H = 20;

    static final int[][] MAP = {
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,1},
        {1,0,0,1,0,0,0,0,0,1,1,1,0,0,0,0,0,0,1,0,0,0,0,0,1},
        {1,0,0,1,0,0,0,0,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,1,0,1,0,0,1,1,1,1,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,1,1,0,0,0,0,0,0,1,0,0,1,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,1,0,0,0,0,0,0,1,0,0,1,0,1,1,1,0,0,1},
        {1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,1,0,0,1},
        {1,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1,0,0,1},
        {1,0,0,0,1,0,0,0,0,0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,1,0,0,1,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,1,0,0,0,1,0,0,1,0,0,1,1,1,1,1,0,0,0,1},
        {1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,1},
        {1,0,1,1,0,0,1,1,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,1},
        {1,0,0,1,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
    };

    static final int NUM_RAYS = GAME_W;
    static final double FOV   = Math.toRadians(60);

    int screen   = 0; // 0=menu, 1=game

    Player player  = new Player(TILE * 2 + TILE/2, TILE * 2 + TILE/2);
    int prevMx     = -1;
    int hp = 100;
    int maxHp = 100;
    int potions = 5;

    int attackCharge = 0;

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
    }

    @Override
    public void move() {
        if (screen == 0) moveMenu();
        else             moveGame();
    }

    void moveMenu() {
        if (keys[10]) { 
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
        if (MAP[(int)(player.y/TILE)][(int)(player.x/TILE)] == 1) {
            player.x = ox; player.y = oy;
        }

        if (keys[SPACE]) {
            attackCharge = Math.min(100, attackCharge + 2);
        } else {
            attackCharge = Math.max(0, attackCharge - 3);
        }

        int targetY = HAND_REST - (int)((HAND_REST - HAND_RAISED) * (attackCharge / 40.0));
        handY += (targetY - handY);
        handY = Math.max(HAND_RAISED, handY);

        

        castRays();
    }

    void castRays() {
        for (int i = 0; i < NUM_RAYS; i++) {
            double angle = (player.angle - FOV/2) + (FOV / (NUM_RAYS-1)) * i;
            double rdx = Math.cos(angle), rdy = Math.sin(angle);

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
        if (screen == 0) drawMenu(g);
        else             drawGame(g);
    }

    void drawMenu(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, SCREEN_W, SCREEN_H);

        g.setFont(new Font("Monospaced", Font.BOLD, 40));
        FontMetrics fm = g.getFontMetrics();
        String text = "PRESS ENTER TO START";
        g.setColor(Color.BLACK);
        g.drawString(text, SCREEN_W/2 - fm.stringWidth(text)/2, SCREEN_H/2);
    }

    void drawGame(Graphics g) {
        draw3D(g);
        drawRightPanel(g);
        drawBottomPanel(g);
    }

    void draw3D(Graphics g) {
        // ceiling & floor
        g.setColor(new Color(50, 50, 50));
        g.fillRect(0, 0, GAME_W, GAME_H/2);
        g.setColor(new Color(100, 100, 100));
        g.fillRect(0, GAME_H/2, GAME_W, GAME_H/2);

        // walls
        for (int i = 0; i < NUM_RAYS; i++) {
            if (rayDist[i] == 0) continue;
            int sliceH = Math.min(GAME_H, (int)(GAME_H * TILE / rayDist[i]));
            int top    = GAME_H/2 - sliceH/2;
            int bright = Math.max(40, 255 - (int)(rayDist[i] * 0.4));
            g.setColor(new Color(bright, bright, bright));
            g.fillRect(i, top, 1, sliceH);
        }

        // hand 
        int hx = GAME_W/2 - HAND_W/2;
        if (handImage != null)
            g.drawImage(handImage, hx, handY, HAND_W, HAND_H, null);
    }

    void drawRightPanel(Graphics g) {
        int ox = GAME_W;
        int pad = 8;

        g.setColor(Color.WHITE);
        g.fillRect(ox, 0, RIGHT_W, SCREEN_H);
        g.setColor(Color.BLACK);
        g.drawLine(ox, 0, ox, SCREEN_H);

        // portrait
        g.setColor(new Color(220, 220, 220));
        g.fillRect(ox + pad, pad, RIGHT_W - pad*2, PERSON_H);
        g.setColor(Color.BLACK);
        g.drawRect(ox + pad, pad, RIGHT_W - pad*2, PERSON_H);
        if (personImage != null)
            g.drawImage(personImage, ox + pad, pad, RIGHT_W - pad*2, PERSON_H, null);

        // strength bar
        int barTop = PERSON_H + 20;
        int barH = GAME_H - barTop - pad; // 272
        int barX = ox + pad;
        int barW = RIGHT_W - pad*2; // 104


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
        int oy  = GAME_H;
        int pad = 8;

        g.setColor(Color.WHITE);
        g.fillRect(0, oy, GAME_W, BOTTOM_H);
        g.setColor(Color.BLACK);
        g.drawLine(0, oy, GAME_W, oy);

        // health label + bar
        g.setFont(new Font("Monospaced", Font.BOLD, 9));
        g.setColor(Color.BLACK);
        g.drawString("HEALTH", pad + 2, oy + 14);

        int bx = pad, by = oy + 18, bw = HEALTH_W - pad*2, bh = BOTTOM_H - 26;
        g.setColor(new Color(220, 220, 220));
        g.fillRect(bx, by, bw, bh);
        g.setColor(Color.BLACK);
        g.drawRect(bx, by, bw, bh);
        g.setColor(Color.BLACK);
        g.fillRect(bx + 1, by + 1, (bw - 1) * hp / maxHp, bh - 1);
        g.setFont(new Font("Monospaced", Font.BOLD, 11));
        g.setColor(Color.BLACK);
        g.drawString(hp + " / " + maxHp, bx + bw/2 - 22, by + bh/2 + 4);

        g.setColor(Color.BLACK);
        g.drawLine(HEALTH_W, oy, HEALTH_W, oy + BOTTOM_H);

        // inventory label + potion slot
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
        g.drawString("Potions: " + potions, slotX + slotS + 8, slotY + slotS/2 + 4);
    }

    public static void main(String[] args) { new Game(); }
}