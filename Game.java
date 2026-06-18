import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Game extends BaseFrame {

    // Screen and rendering constants

    static final int GAME_W   = 580;
    static final int GAME_H   = 420;
    static final int RIGHT_W  = 120;
    static final int BOTTOM_H = 80;
    static final int SCREEN_W = GAME_W + RIGHT_W;
    static final int SCREEN_H = GAME_H + BOTTOM_H;


    static final int TILE  = 40;
    static final int MAP_W = 30;
    static final int MAP_H = 22;

    static final int NUM_RAYS = GAME_W;
    static final double FOV = Math.toRadians(60);

    // Map layout: 1=wall, 0=floor, 2=goal, 3=key/potion spawn point
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

    // Runtime state

    static int[][] map;

    int screen; // 0=menu, 1=playing, 2=gameOver, 3=win

    Player player;
    List<Orc> orcs;
    List<Fireball> fireballs;
    int goalTileX, goalTileY;
    boolean goalFound;

    // Input tracking and timing

    int prevMouseX = -1;
    int attackCharge = 0;
    boolean spaceWasDown = false;
    int spaceReleaseCount = 0;
    boolean useItemWasDown = false;
    boolean cWasDown = false;
    int tickCount;

    // Hand weapon animation

    static final int HAND_W = 160;
    static final int HAND_H = 140;
    static final int HAND_REST = GAME_H;
    static final int HAND_RAISED = GAME_H - HAND_H;
    int handY = HAND_REST;

    int score;
    int orcsKilled;
    int bestScore;

    // Loaded sprites and assets

    Sound gameSong;
    Image handImage   = loadImage("Sprites/hand.png");
    Image personImage = loadImage("Sprites/person.png");
    Image potionImage = loadImage("Sprites/potion.png");
    Image boomImage   = loadImage("Sprites/boom.png");

    Image[] orcWalkFrames        = new Image[4];
    Image[] orcAttackFrames      = new Image[4];
    Image[] portalFrames         = new Image[4];
    Image[] weakFireballFrames   = new Image[2];
    Image[] strongFireballFrames = new Image[2];

    double[] rayDist = new double[NUM_RAYS];

    // Constructor, loads sprites and inits game
    public Game() {
        super("Catacombs 3D", SCREEN_W, SCREEN_H);
        map = copyMap(MAP_TEMPLATE);
        for (int i = 0; i < 4; i++) {
            orcWalkFrames[i]   = loadImage("Sprites/OrcWalk/" + i + ".png");
            orcAttackFrames[i] = loadImage("Sprites/OrcAttack/" + i + ".png");
            portalFrames[i]    = loadImage("Sprites/Portal/" + i + ".png");
        }
        for (int i = 0; i < 2; i++) {
            weakFireballFrames[i]   = loadImage("Sprites/WeakFireball/" + i + ".png");
            strongFireballFrames[i] = loadImage("Sprites/StrongFireball/" + i + ".png");
        }
        initGame();
    }

    // Deep-copies the map template

    private static int[][] copyMap(int[][] src) {
        int[][] dst = new int[src.length][];
        for (int i = 0; i < src.length; i++) dst[i] = src[i].clone();
        return dst;
    }

    // Resets all game state for a new run

    void initGame() {
        player = new Player(TILE * 2 + TILE / 2, TILE * 2 + TILE / 2);
        orcs = new ArrayList<>();
        fireballs = new ArrayList<>();
        spawnOrcs(15);
        findGoal();
        attackCharge = 0;
        spaceWasDown = false;
        useItemWasDown = false;
        handY = HAND_REST;
        tickCount = 0;
        score = 0;
        orcsKilled = 0;
        gameSong = new Sound("Too Hot to Handle.mid");
        gameSong.play();
    }

    // Finds the goal tile on the map

    void findGoal() {
        goalFound = false;
        for (int row = 0; row < MAP_H; row++) {
            for (int col = 0; col < MAP_W; col++) {
                if (map[row][col] == 2) {
                    goalTileX = col;
                    goalTileY = row;
                    goalFound = true;
                    return;
                }
            }
        }
    }

    // Places orcs near key tiles and randomly

    void spawnOrcs(int count) {
        Random rand = new Random();
        List<int[]> positions = new ArrayList<>();
        List<int[]> keyTiles = new ArrayList<>();

        for (int row = 0; row < MAP_H; row++)
            for (int col = 0; col < MAP_W; col++)
                if (map[row][col] == 3) keyTiles.add(new int[]{col, row});

        int[] goal = null;
        for (int row = 0; row < MAP_H; row++)
            for (int col = 0; col < MAP_W; col++)
                if (map[row][col] == 2) goal = new int[]{col, row};
        if (goal != null) {
            for (int i = 0; i < 4 && positions.size() < count; i++)
                placeNear(positions, goal[0], goal[1], 3, rand);
        }

        for (int[] tilePos : keyTiles) {
            for (int i = 0; i < 2 && positions.size() < count; i++)
                placeNear(positions, tilePos[0], tilePos[1], 2, rand);
        }

        while (positions.size() < count) {
            int x = rand.nextInt(MAP_W);
            int y = rand.nextInt(MAP_H);
            if (map[y][x] == 0 && !(x == 2 && y == 2) && isUniquePos(positions, x, y)) {
                positions.add(new int[]{x, y});
            }
        }

        for (int[] pos : positions) {
            Orc orc = new Orc(pos[0] * TILE + TILE / 2, pos[1] * TILE + TILE / 2, orcWalkFrames, orcAttackFrames);
            orc.mode = rand.nextDouble() < 0.5 ? 0 : 1;
            orcs.add(orc);
        }
    }

    private void placeNear(List<int[]> positions, int centerX, int centerY, int range, Random rand) {
        for (int attempt = 0; attempt < 20; attempt++) {
            int dx = rand.nextInt(range * 2 + 1) - range;
            int dy = rand.nextInt(range * 2 + 1) - range;
            if (dx == 0 && dy == 0) continue;
            int nx = centerX + dx, ny = centerY + dy;
            if (nx >= 0 && nx < MAP_W && ny >= 0 && ny < MAP_H && map[ny][nx] == 0
                && !(nx == 2 && ny == 2) && isUniquePos(positions, nx, ny)) {
                positions.add(new int[]{nx, ny});
                return;
            }
        }
    }

    private boolean isUniquePos(List<int[]> positions, int x, int y) {
        for (int[] pos : positions)
            if (pos[0] == x && pos[1] == y) return false;
        return true;
    }

    // Main update dispatch

    @Override
    public void move() {
        if (screen == 0) moveMenu();
        else if (screen == 1) moveGame();
        else moveEndScreen();
    }

    void moveMenu() {
        if (keys[10]) { screen = 1; keys[10] = false; }
    }

    // End screen handling: enter to restart

    void moveEndScreen() {
        if (keys[10]) {
            map = copyMap(MAP_TEMPLATE);
            gameSong.stop();
            initGame();
            screen = 1;
            keys[10] = false;
        }
    }

    // Main gameplay tick

    void moveGame() {
        tickCount++;

        if (mb == 1) {
            if (prevMouseX >= 0) player.angle += (mx - prevMouseX) * 0.004;
            prevMouseX = mx;
        } else {
            prevMouseX = -1;
        }

        double oldX = player.x, oldY = player.y;
        player.move(keys);
        if (map[(int) (player.y / TILE)][(int) (player.x / TILE)] == 1) {
            player.x = oldX;
            player.y = oldY;
        }

        if (keys[67] && !cWasDown) player.invincible = !player.invincible;
        cWasDown = keys[67];

        updateAttack();
        updatePotionUse();
        updateOrcs();
        updateFireballs();
        checkTileEvents();

        if (!player.isAlive()) {
            gameSong.stop();
            bestScore = Math.max(bestScore, score);
            screen = 2;
        }

        castRays();
    }

    // Hold space to charge, release to fire

    void updateAttack() {
        boolean spaceDown = keys[SPACE];
        if (spaceDown) {
            attackCharge = Math.min(100, attackCharge + 2);
            spaceReleaseCount = 0;
        } else if (attackCharge > 0) {
            spaceReleaseCount++;
            if (spaceReleaseCount > 3) {
                int damage = attackCharge < 25 ? 25
                           : attackCharge < 50 ? 50
                           : attackCharge < 75 ? 80
                           : 100;
                fireballs.add(new Fireball(player.x, player.y, player.angle, damage, weakFireballFrames, strongFireballFrames));
                attackCharge = 0;
            }
        }
        spaceWasDown = spaceDown;

        int targetY = HAND_REST - (int) ((HAND_REST - HAND_RAISED) * (attackCharge / 40.0));
        handY = Math.max(HAND_RAISED, targetY);
    }

    // Press Q to drink a potion

    void updatePotionUse() {
        boolean useItemDown = keys[81]; // Q
        if (useItemDown && !useItemWasDown) player.usePotion();
        useItemWasDown = useItemDown;
    }

    // Updates all orcs then pushes overlapping ones apart

    void updateOrcs() {
        for (Orc orc : orcs) {
            if (orc.alive) orc.update(player, map, MAP_W, MAP_H);
        }
        separateOrcs();
    }

    // Pushes overlapping orcs apart so a pack chasing the same player doesn't pile onto
    // the same tile. Without this, several orcs could end up at nearly the same spot,
    // which (combined with how close-range billboards render) is what produced the giant
    // overlapping color blob -- multiple oversized sprites stacked at the same position.
    static final double ORC_MIN_SEPARATION = TILE * 0.7;

    void separateOrcs() {
        for (int i = 0; i < orcs.size(); i++) {
            Orc a = orcs.get(i);
            if (!a.alive) continue;
            for (int j = i + 1; j < orcs.size(); j++) {
                Orc b = orcs.get(j);
                if (!b.alive) continue;

                double dx = b.x - a.x;
                double dy = b.y - a.y;
                double dist = Math.sqrt(dx * dx + dy * dy);

                if (dist < ORC_MIN_SEPARATION) {
                    // if perfectly overlapping, nudge in an arbitrary fixed direction
                    // rather than dividing by zero
                    double nx = dist < 0.0001 ? 1 : dx / dist;
                    double ny = dist < 0.0001 ? 0 : dy / dist;
                    double overlap = ORC_MIN_SEPARATION - dist;
                    double pushX = nx * overlap * 0.5;
                    double pushY = ny * overlap * 0.5;

                    tryPushOrc(a, -pushX, -pushY);
                    tryPushOrc(b, pushX, pushY);
                }
            }
        }
    }

    // Moves an orc by (dx, dy) only if the destination tile is walkable, so separation
    // pushes can never shove an orc through a wall.
    void tryPushOrc(Orc orc, double dx, double dy) {
        double nx = orc.x + dx;
        double ny = orc.y + dy;
        int tileX = (int) (nx / TILE);
        int tileY = (int) (ny / TILE);
        if (tileX >= 0 && tileX < MAP_W && tileY >= 0 && tileY < MAP_H && map[tileY][tileX] != 1) {
            orc.x = nx;
            orc.y = ny;
        }
    }

    // Moves fireballs, removes on wall hit or orc hit

    void updateFireballs() {
        for (int i = fireballs.size() - 1; i >= 0; i--) {
            Fireball fireball = fireballs.get(i);
            fireball.move();

            int tileX = (int) (fireball.x / TILE);
            int tileY = (int) (fireball.y / TILE);
            if (tileX < 0 || tileX >= MAP_W || tileY < 0 || tileY >= MAP_H || map[tileY][tileX] == 1) {
                fireballs.remove(i);
                continue;
            }

            boolean hitOrc = false;
            for (Orc orc : orcs) {
                if (!orc.alive) continue;
                double dx = fireball.x - orc.x;
                double dy = fireball.y - orc.y;
                if (dx * dx + dy * dy < TILE * TILE / 4) {
                    if (orc.takeDamage(fireball.damage)) { score += 50; orcsKilled++; }
                    hitOrc = true;
                    break;
                }
            }
            if (hitOrc) fireballs.remove(i);
        }
    }

    // Checks if player is on a potion or goal tile

    void checkTileEvents() {
        int playerTileX = (int) (player.x / TILE);
        int playerTileY = (int) (player.y / TILE);
        if (playerTileX < 0 || playerTileX >= MAP_W || playerTileY < 0 || playerTileY >= MAP_H) return;

        if (map[playerTileY][playerTileX] == 3) {
            player.potions++;
            score += 10;
            map[playerTileY][playerTileX] = 0;
        }
        if (map[playerTileY][playerTileX] == 2) {
            score += 200;
            bestScore = Math.max(bestScore, score);
            gameSong.stop();
            screen = 3;
        }
    }

    // Raycasting using the DDA algorithm, populates rayDist for each column

    void castRays() {
        for (int i = 0; i < NUM_RAYS; i++) {
            double angle = (player.angle - FOV / 2) + (FOV / (NUM_RAYS - 1)) * i;
            double rdx = Math.cos(angle), rdy = Math.sin(angle);

            int tileX = (int) (player.x / TILE), tileY = (int) (player.y / TILE);
            double ddx = Math.abs(1.0 / rdx), ddy = Math.abs(1.0 / rdy);
            int stepX = rdx < 0 ? -1 : 1, stepY = rdy < 0 ? -1 : 1;
            double sideDistX = (rdx < 0 ? player.x / TILE - tileX : tileX + 1.0 - player.x / TILE) * ddx;
            double sideDistY = (rdy < 0 ? player.y / TILE - tileY : tileY + 1.0 - player.y / TILE) * ddy;

            boolean hitOnXSide = false;
            rayDist[i] = Double.MAX_VALUE;
            while (true) {
                if (sideDistX < sideDistY) { sideDistX += ddx; tileX += stepX; hitOnXSide = true; }
                else                       { sideDistY += ddy; tileY += stepY; hitOnXSide = false; }
                if (tileY < 0 || tileY >= MAP_H || tileX < 0 || tileX >= MAP_W) break;
                if (map[tileY][tileX] == 1) {
                    double perpDist = hitOnXSide ? (sideDistX - ddx) * TILE : (sideDistY - ddy) * TILE;
                    rayDist[i] = perpDist * Math.cos(angle - player.angle);
                    break;
                }
            }
        }
    }

    // Draw dispatch

    @Override
    public void draw(Graphics g) {
        if (screen == 0) drawMenu(g);
        else if (screen == 1) drawGame(g);
        else drawEndScreen(g);
    }

    // Menu screen with title, controls hint, and start prompt

    void drawMenu(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, SCREEN_W, SCREEN_H);

        // dark stone border
        g.setColor(new Color(40, 34, 28));
        g.fillRect(0, 0, SCREEN_W, 3);
        g.fillRect(0, SCREEN_H - 3, SCREEN_W, 3);

        // title
        g.setFont(new Font("Monospaced", Font.BOLD, 48));
        FontMetrics fm = g.getFontMetrics();
        String title = "CATACOMBS 3D";
        g.setColor(new Color(180, 30, 20));
        g.drawString(title, SCREEN_W / 2 - fm.stringWidth(title) / 2, SCREEN_H / 2 - 110);

        // subtitle
        g.setFont(new Font("Monospaced", Font.PLAIN, 14));
        fm = g.getFontMetrics();
        String sub = "A RAYCASTING DUNGEON CRAWLER";
        g.setColor(new Color(130, 120, 110));
        g.drawString(sub, SCREEN_W / 2 - fm.stringWidth(sub) / 2, SCREEN_H / 2 - 85);

        // bronze divider
        g.setColor(new Color(120, 90, 40));
        g.fillRect(SCREEN_W / 2 - 140, SCREEN_H / 2 - 65, 280, 2);

        // sprites
        if (personImage != null)
            g.drawImage(personImage, SCREEN_W / 2 - 35, SCREEN_H / 2 - 55, 70, 70, null);
        if (handImage != null)
            g.drawImage(handImage, SCREEN_W / 2 + 50, SCREEN_H / 2 - 55, 55, 55, null);
        if (potionImage != null)
            g.drawImage(potionImage, SCREEN_W / 2 - 115, SCREEN_H / 2 - 45, 35, 35, null);

        // controls hint
        g.setFont(new Font("Monospaced", Font.PLAIN, 12));
        fm = g.getFontMetrics();
        g.setColor(new Color(100, 95, 90));
        String[] controls = {
            "MOVE: Arrow Keys / WASD",
            "ATTACK: Hold SPACE, release to fire",
            "POTION: Q",
            "INVINCIBLE: C (toggle)"
        };
        int cy = SCREEN_H / 2 + 30;
        for (String line : controls) {
            g.drawString(line, SCREEN_W / 2 - fm.stringWidth(line) / 2, cy);
            cy += 18;
        }

        // prompt
        g.setFont(new Font("Monospaced", Font.BOLD, 20));
        fm = g.getFontMetrics();
        String start = "PRESS ENTER TO START";
        g.setColor(new Color(200, 180, 160));
        g.drawString(start, SCREEN_W / 2 - fm.stringWidth(start) / 2, cy + 20);
    }

    // Routes to game over or win screen

    void drawEndScreen(Graphics g) {
        if (screen == 2) drawGameOverScreen(g);
        else drawWinScreen(g);
    }

    // Game over screen showing score, stats, and retry prompt

    void drawGameOverScreen(Graphics g) {
        g.setColor(new Color(5, 3, 3));
        g.fillRect(0, 0, SCREEN_W, SCREEN_H);

        // dark blood border
        g.setColor(new Color(35, 5, 5));
        g.fillRect(0, 0, SCREEN_W, 6);
        g.fillRect(0, SCREEN_H - 6, SCREEN_W, 6);
        g.fillRect(0, 0, 4, SCREEN_H);
        g.fillRect(SCREEN_W - 4, 0, 4, SCREEN_H);

        g.setFont(new Font("Monospaced", Font.BOLD, 50));
        FontMetrics fm = g.getFontMetrics();
        String title = "GAME OVER";
        g.setColor(new Color(160, 25, 20));
        g.drawString(title, SCREEN_W / 2 - fm.stringWidth(title) / 2, SCREEN_H / 2 - 95);

        g.setFont(new Font("Monospaced", Font.PLAIN, 13));
        fm = g.getFontMetrics();
        g.setColor(new Color(120, 70, 60));
        String deathText = "THE DUNGEON CLAIMS ANOTHER SOUL";
        g.drawString(deathText, SCREEN_W / 2 - fm.stringWidth(deathText) / 2, SCREEN_H / 2 - 65);

        g.setFont(new Font("Monospaced", Font.BOLD, 26));
        fm = g.getFontMetrics();
        String scoreStr = "SCORE: " + score;
        g.setColor(Color.WHITE);
        g.drawString(scoreStr, SCREEN_W / 2 - fm.stringWidth(scoreStr) / 2, SCREEN_H / 2 + 40);

        g.setFont(new Font("Monospaced", Font.PLAIN, 14));
        fm = g.getFontMetrics();
        g.setColor(new Color(160, 150, 140));
        String stats = "Orcs slain: " + orcsKilled + "    Potions held: " + player.potions;
        g.drawString(stats, SCREEN_W / 2 - fm.stringWidth(stats) / 2, SCREEN_H / 2 + 62);

        g.setFont(new Font("Monospaced", Font.PLAIN, 12));
        fm = g.getFontMetrics();
        g.setColor(new Color(100, 95, 90));
        String bestStr = "Best this session: " + bestScore;
        g.drawString(bestStr, SCREEN_W / 2 - fm.stringWidth(bestStr) / 2, SCREEN_H / 2 + 80);

        g.setFont(new Font("Monospaced", Font.BOLD, 18));
        fm = g.getFontMetrics();
        g.setColor(new Color(140, 100, 40));
        String retry = "PRESS ENTER TO RETRY";
        g.drawString(retry, SCREEN_W / 2 - fm.stringWidth(retry) / 2, SCREEN_H / 2 + 105);
    }

    // Win screen showing final score, stats, and play again prompt

    void drawWinScreen(Graphics g) {
        g.setColor(new Color(8, 8, 6));
        g.fillRect(0, 0, SCREEN_W, SCREEN_H);

        // gold border
        g.setColor(new Color(120, 100, 30));
        g.fillRect(0, 0, SCREEN_W, 4);
        g.fillRect(0, SCREEN_H - 4, SCREEN_W, 4);
        g.fillRect(0, 0, 4, SCREEN_H);
        g.fillRect(SCREEN_W - 4, 0, 4, SCREEN_H);

        g.setFont(new Font("Monospaced", Font.BOLD, 50));
        FontMetrics fm = g.getFontMetrics();
        String title = "YOU WIN!";
        g.setColor(new Color(220, 185, 30));
        g.drawString(title, SCREEN_W / 2 - fm.stringWidth(title) / 2, SCREEN_H / 2 - 95);

        g.setFont(new Font("Monospaced", Font.PLAIN, 13));
        fm = g.getFontMetrics();
        g.setColor(new Color(140, 130, 80));
        String victoryText = "YOU ESCAPED THE CATACOMBS ALIVE";
        g.drawString(victoryText, SCREEN_W / 2 - fm.stringWidth(victoryText) / 2, SCREEN_H / 2 - 65);

        g.setFont(new Font("Monospaced", Font.BOLD, 26));
        fm = g.getFontMetrics();
        String scoreStr = "FINAL SCORE: " + score;
        g.setColor(Color.WHITE);
        g.drawString(scoreStr, SCREEN_W / 2 - fm.stringWidth(scoreStr) / 2, SCREEN_H / 2 + 40);

        g.setFont(new Font("Monospaced", Font.PLAIN, 14));
        fm = g.getFontMetrics();
        g.setColor(new Color(160, 150, 140));
        String stats = "Orcs slain: " + orcsKilled + "    Potions held: " + player.potions;
        g.drawString(stats, SCREEN_W / 2 - fm.stringWidth(stats) / 2, SCREEN_H / 2 + 62);

        g.setFont(new Font("Monospaced", Font.PLAIN, 12));
        fm = g.getFontMetrics();
        g.setColor(new Color(100, 95, 90));
        String bestStr = "Best this session: " + bestScore;
        g.drawString(bestStr, SCREEN_W / 2 - fm.stringWidth(bestStr) / 2, SCREEN_H / 2 + 80);

        g.setFont(new Font("Monospaced", Font.BOLD, 18));
        fm = g.getFontMetrics();
        g.setColor(new Color(180, 150, 30));
        String again = "PRESS ENTER TO PLAY AGAIN";
        g.drawString(again, SCREEN_W / 2 - fm.stringWidth(again) / 2, SCREEN_H / 2 + 105);
    }

    // Draws the game viewport: 3D scene, right panel, bottom HUD

    void drawGame(Graphics g) {
        draw3D(g);
        drawRightPanel(g);
        drawBottomPanel(g);
    }

    // Renders ceiling, floor, and wall columns from raycast data

    void draw3D(Graphics g) {
        // dungeon ceiling (very dark)
        g.setColor(new Color(10, 8, 7));
        g.fillRect(0, 0, GAME_W, GAME_H / 2);
        // stone floor
        g.setColor(new Color(45, 38, 32));
        g.fillRect(0, GAME_H / 2, GAME_W, GAME_H / 2);

        for (int i = 0; i < NUM_RAYS; i++) {
            if (rayDist[i] == Double.MAX_VALUE) continue;
            int sliceH = Math.min(GAME_H, (int) (GAME_H * TILE / rayDist[i]));
            int top = GAME_H / 2 - sliceH / 2;
            // warm stone wall tint — far walls darker, near walls lighter
            double distFactor = Math.max(0.15, 1.0 - rayDist[i] * 0.004);
            int base = (int)(140 * distFactor);
            int r = Math.max(30, base + 20);
            int gr = Math.max(25, base + 10);
            int b = Math.max(20, base);
            g.setColor(new Color(r, gr, b));
            g.fillRect(i, top, 1, sliceH);
        }

        drawEntities(g);

        if (handImage != null)
            g.drawImage(handImage, GAME_W / 2 - HAND_W / 2, handY, HAND_W, HAND_H, null);
    }

    // Collects all entities, sorts by distance, draws as billboards
    void drawEntities(Graphics g) {
        List<Object[]> entities = new ArrayList<>();

        for (Orc orc : orcs) {
            if (!orc.alive) continue;
            entities.add(new Object[]{distanceTo(orc.x, orc.y), 0, orc.x, orc.y, orc});
        }

        for (Fireball fireball : fireballs) {
            if (!fireball.alive) continue;
            entities.add(new Object[]{distanceTo(fireball.x, fireball.y), 1, fireball.x, fireball.y, fireball});
        }

        for (int row = 0; row < MAP_H; row++) {
            for (int col = 0; col < MAP_W; col++) {
                if (map[row][col] == 3) {
                    double px = col * TILE + TILE / 2;
                    double py = row * TILE + TILE / 2;
                    entities.add(new Object[]{distanceTo(px, py), 2, px, py, null});
                }
            }
        }

        if (goalFound) {
            double gx = goalTileX * TILE + TILE / 2;
            double gy = goalTileY * TILE + TILE / 2;
            entities.add(new Object[]{distanceTo(gx, gy), 3, gx, gy, null});
        }

        Collections.sort(entities, (a, b) -> Double.compare((Double) b[0], (Double) a[0]));

        for (Object[] entry : entities) {
            int type = (Integer) entry[1];
            double x = (Double) entry[2];
            double y = (Double) entry[3];
            Object data = entry[4];
            switch (type) {
                case 0:
                    Orc orc = (Orc) data;
                    drawBillboard(g, x, y, orc.getCurrentSprite(), orc.getRenderScale(), 0.5);
                    break;
                case 1:
                    Fireball fireball = (Fireball) data;
                    drawBillboard(g, x, y, fireball.getCurrentSprite(), fireball.getRenderScale(), 0.5);
                    break;
                case 2:
                    drawBillboard(g, x, y, potionImage, 0.4, 0.1);
                    break;
                case 3:
                    drawBillboard(g, x, y, portalFrames[(tickCount / 8) % 4], 1.0, 0.5);
                    break;
            }
        }
    }

    // Euclidean distance from player to a point

    double distanceTo(double x, double y) {
        double dx = x - player.x, dy = y - player.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    // Draws a sprite as a billboard column by column

    // Draws a sprite facing the camera.
    //
    // IMPORTANT FIX: sprite SIZE must be based on true straight-line distance
    // (dist), not "perpendicular distance" (dist * cos(angleTo)).
    //
    // perpDist exists to fix fisheye for WALLS, because each wall column is
    // sampled by its own ray and the raw ray length overstates how far away
    // the wall plane is at that angle. A billboard sprite isn't a flat plane
    // being sampled per-column though -- it's a single point that should
    // just face the camera and shrink/grow with real distance only.
    //
    // Using perpDist for sprite size made size depend on view angle: as an
    // orc drifted toward the edge of the screen purely from the player
    // turning (with the orc's real distance unchanged), cos(angleTo) shrank,
    // so perpDist shrank, so the sprite grew -- the "squish and expand on
    // turn" bug. Switching the SIZE calculation to plain dist removes that
    // dependency on angle entirely; size now only changes when distance
    // actually changes. perpDist is still used (correctly) for the
    // occlusion check below, since rayDist[] is also perpendicular distance
    // and the two need to be in the same units to compare fairly.
    // groundOffset: 0.5 = centered on horizon (default for upright things),
    //                0.0 = top at horizon (sprite below horizon),
    //                0.2 = slight rise above horizon etc.
    void drawBillboard(Graphics g, double entityX, double entityY, Image img, double scale,
                       double groundOffset) {
        if (img == null) return;
        double dx = entityX - player.x;
        double dy = entityY - player.y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist < 1.0) return;

        double angleTo = Math.atan2(dy, dx) - player.angle;
        while (angleTo > Math.PI)  angleTo -= 2 * Math.PI;
        while (angleTo < -Math.PI) angleTo += 2 * Math.PI;
        if (Math.abs(angleTo) > FOV / 2 + 0.3) return; // small margin so wide sprites don't pop at the edge

        int imgW = img.getWidth(null);
        int imgH = img.getHeight(null);
        if (imgW <= 0 || imgH <= 0) return;

        // scale is "fraction of a full wall-tile's screen height" -- same unit a wall slice uses.
        // sized by TRUE distance so size never depends on turning, only on actually moving closer/farther.
        double spriteH = (GAME_H * TILE) / dist * scale;
        // CLAMP: as dist -> 0, spriteH -> infinity. The wall renderer already clamps slice height
        // (Math.min(GAME_H, ...) in draw3D); billboards need the same kind of cap or an orc that
        // gets close to the player balloons past the screen and overlaps into a giant blob with
        // anything else nearby. Cap at a bit over full screen height, already "uncomfortably in
        // your face" for a melee-range monster.
        double maxSpriteH = GAME_H * 1.5;
        if (spriteH > maxSpriteH) spriteH = maxSpriteH;
        double spriteW = spriteH * ((double) imgW / imgH);

        double screenX = GAME_W / 2.0 + (angleTo / (FOV / 2)) * (GAME_W / 2.0);
        double top  = GAME_H / 2.0 - spriteH * groundOffset;
        double left = screenX - spriteW / 2;

        // perpendicular distance -- ONLY for comparing against rayDist[], which is also
        // perpendicular distance. This is a fair apples-to-apples occlusion test and is
        // unrelated to (and doesn't affect) the sprite's on-screen size above.
        double perpDist = dist * Math.cos(angleTo);
        if (perpDist < 1.0) return;

        int drawLeft = Math.max(0, (int) left);
        int drawRight = Math.min(GAME_W - 1, (int) (left + spriteW));
        int drawTop = (int) top;
        int drawBottom = (int) (top + spriteH);

        for (int x = drawLeft; x <= drawRight; x++) {
                if (dist < rayDist[x]) {
                int srcX = (int) ((x - left) / spriteW * imgW);
                int srcX2 = (int) (((x + 1) - left) / spriteW * imgW);
                if (srcX2 <= srcX) srcX2 = srcX + 1;
                g.drawImage(img, x, drawTop, x + 1, drawBottom,
                            srcX, 0, srcX2, imgH, null);
            }
        }
    }

    // Right side panel with portrait and navy charge bar

    void drawRightPanel(Graphics g) {
        int ox = GAME_W, pad = 8;
        int panelH = SCREEN_H - BOTTOM_H;

        // dark stone background
        g.setColor(new Color(35, 30, 28));
        g.fillRect(ox, 0, RIGHT_W, panelH);
        g.setColor(new Color(20, 17, 15));
        g.drawLine(ox, 0, ox, panelH);

        // portrait frame
        int portraitH = (int)(panelH * 0.45);
        g.setColor(new Color(50, 44, 38));
        g.fillRoundRect(ox + 6, pad, RIGHT_W - 12, portraitH, 6, 6);
        g.setColor(new Color(70, 60, 50));
        g.drawRoundRect(ox + 6, pad, RIGHT_W - 12, portraitH, 6, 6);

        if (personImage != null) {
            int imgPad = 8;
            g.drawImage(personImage, ox + 6 + imgPad, pad + imgPad,
                        RIGHT_W - 12 - imgPad * 2, portraitH - imgPad * 2, null);
        }

        // charge bar section
        int barTop = portraitH + pad + 10;
        int barH = panelH - barTop - pad;
        int barX = ox + 10;
        int barW = RIGHT_W - 20;

        // charge bar background
        g.setColor(new Color(15, 12, 10));
        g.fillRoundRect(barX, barTop, barW, barH, 4, 4);

        // boom image drawn behind the navy fill so it shows through as charge builds
        if (boomImage != null && barH > 0) {
            g.drawImage(boomImage, barX + 1, barTop + 1, barW - 2, barH - 2, null);
        }

        // solid navy fill from top (recedes as charge builds)
        int fillH = barH * attackCharge / 100;
        if (barH - fillH > 0) {
            g.setColor(new Color(1, 1, 95));
            g.fillRect(barX + 1, barTop + 1, barW - 2, barH - fillH);
        }

        // charge bar border
        g.setColor(new Color(60, 50, 40));
        g.drawRoundRect(barX, barTop, barW, barH, 4, 4);

        // charge percentage text
        if (attackCharge > 0) {
            g.setFont(new Font("Monospaced", Font.BOLD, 9));
            g.setColor(new Color(200, 220, 255));
            String pct = attackCharge + "%";
            FontMetrics fm = g.getFontMetrics();
            int barBottom = barTop + barH;
            g.drawString(pct, barX + barW / 2 - fm.stringWidth(pct) / 2, barBottom - 4);
        }
    }

    // Bottom HUD: health bar, inventory slot, score, invincible toggle hint

    void drawBottomPanel(Graphics g) {
        int oy = GAME_H;
        int pad = 8;
        int panelH = BOTTOM_H;

        // dark stone background (full screen width)
        g.setColor(new Color(28, 24, 22));
        g.fillRect(0, oy, SCREEN_W, panelH);
        g.setColor(new Color(18, 15, 14));
        g.drawLine(0, oy, SCREEN_W, oy);

        // Health section (left 45%)
        int healthW = (int)(GAME_W * 0.45);
        int hx = pad;
        int hy = oy + 6;
        int hw = healthW - pad * 2;
        int hh = panelH - 12;

        // health label
        g.setFont(new Font("Monospaced", Font.BOLD, 9));
        g.setColor(new Color(200, 180, 160));
        g.drawString("HEALTH", hx + 2, hy + 10);

        // health bar background with rounded corners
        int barY = hy + 14;
        int barH = hh - 18;
        g.setColor(new Color(15, 12, 10));
        g.fillRoundRect(hx, barY, hw, barH, 4, 4);

        // health fill
        int healthPx = player.health * (hw - 2) / player.maxHealth;
        Color healthColor = player.health > 60 ? new Color(60, 160, 60)
                          : player.health > 30 ? new Color(200, 180, 40)
                          : new Color(200, 40, 40);
        g.setColor(healthColor);
        g.fillRoundRect(hx + 1, barY + 1, healthPx, barH - 2, 3, 3);

        // health text centered
        g.setFont(new Font("Monospaced", Font.BOLD, 11));
        g.setColor(Color.WHITE);
        String healthText = player.health + " / " + player.maxHealth;
        FontMetrics fm = g.getFontMetrics();
        g.drawString(healthText, hx + hw / 2 - fm.stringWidth(healthText) / 2, barY + barH / 2 + 4);

        // subtle outline
        g.setColor(new Color(60, 50, 40));
        g.drawRoundRect(hx, barY, hw, barH, 4, 4);

        // Divider
        g.setColor(new Color(40, 34, 30));
        g.drawLine(healthW, oy + 4, healthW, oy + panelH - 4);

        // Inventory section (middle)
        int invW = (int)(GAME_W * 0.35);
        int ix = healthW + pad;
        g.setFont(new Font("Monospaced", Font.BOLD, 9));
        g.setColor(new Color(200, 180, 160));
        g.drawString("INVENTORY", ix + 2, oy + 10);

        int slotSize = barH - 4;
        int slotX = ix;
        int slotY = barY + (barH - slotSize) / 2;
        g.setColor(new Color(15, 12, 10));
        g.fillRoundRect(slotX, slotY, slotSize, slotSize, 4, 4);
        g.setColor(new Color(60, 50, 40));
        g.drawRoundRect(slotX, slotY, slotSize, slotSize, 4, 4);
        if (potionImage != null)
            g.drawImage(potionImage, slotX + 3, slotY + 3, slotSize - 6, slotSize - 6, null);

        g.setFont(new Font("Monospaced", Font.BOLD, 13));
        g.setColor(new Color(220, 200, 180));
        g.drawString("x" + player.potions, slotX + slotSize + 8, slotY + slotSize / 2 + 5);

        // Divider
        int invEnd = healthW + invW;
        g.setColor(new Color(40, 34, 30));
        g.drawLine(invEnd, oy + 4, invEnd, oy + panelH - 4);

        // Score section (right)
        int sx = invEnd + pad;
        g.setFont(new Font("Monospaced", Font.BOLD, 9));
        g.setColor(new Color(200, 180, 160));
        g.drawString("SCORE", sx + 2, oy + 10);

        g.setFont(new Font("Monospaced", Font.BOLD, 20));
        g.setColor(new Color(255, 215, 0));
        String scoreStr = String.valueOf(score);
        fm = g.getFontMetrics();
        g.drawString(scoreStr, sx + 2, oy + panelH - 12);

        // Invincible status
        g.setFont(new Font("Monospaced", Font.PLAIN, 8));
        g.setColor(new Color(80, 75, 70));
        fm = g.getFontMetrics();
        g.drawString("C: invincible", hx + 2, oy + panelH - 4);
        if (player.invincible) {
            g.setFont(new Font("Monospaced", Font.BOLD, 10));
            g.setColor(new Color(180, 150, 20));
            String invText = "INVINCIBLE";
            fm = g.getFontMetrics();
            g.drawString(invText, hx + hw - fm.stringWidth(invText) - 2, oy + panelH - 6);
        }
    }

    // Entry point

    public static void main(String[] args) { new Game(); }
}