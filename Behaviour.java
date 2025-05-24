import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.*;
import java.awt.Toolkit;

public class Behaviour implements IBehaviour {

    protected GameObject controlledObject;
    private Set<Integer> activeKeys;
    private long lastFireTime = 0;
    private final long fireCooldown = 1000;

    private final Random random = new Random();
    private final int tankRadius = 30;
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final List<GameObject> currentEnemies = new ArrayList<>();
    private final List<GameObject> currentSoldiers = new ArrayList<>();
    private static final Map<GameObject, String> soldierSprites = new HashMap<>();
    private static int enemyCount = 5;
    private static int soldierCount = 5;

    private static boolean playerHit = false;
    private static int score = 0;

    private long lastShieldTime = 0;
    private final long shieldInterval = 5000;
    private static boolean shieldActive = false;
    private static int shieldHitsLeft = 0;

    public static boolean wasPlayerHit() { return playerHit; }
    public static void resetPlayerHit() { playerHit = false; }
    public static void resetGameState() {
        playerHit = false;
        enemyCount = 2;
        soldierCount = 3;
        score = 0;
        shieldActive = false;
        shieldHitsLeft = 0;
        soldierSprites.clear();
    }

    public static int getScore() { return score; }
    public static void resetScore() { score = 0; }
    public static boolean isShieldActive() { return shieldActive; }

    public static String getSoldierSprite(GameObject soldier) {
        return soldierSprites.getOrDefault(soldier, "Run1");
    }

    public void setControlledObject(GameObject go) { this.controlledObject = go; }
    public GameObject getControlledObject() { return controlledObject; }
    public void setActiveKeys(Set<Integer> keys) { this.activeKeys = keys; }

    @Override public void onInit() {}
    @Override public void onEnabled() {}
    @Override public void onDisabled() {}
    @Override public void onDestroy() {}

    @Override
    public void onUpdate() {
        if (controlledObject == null) return;

        switch (controlledObject.name()) {
            case "Player" -> {
                handlePlayerInput();
                updateEnemySpawner();
                updateSoldierSpawner();
                spawnShieldIfNeeded();
            }
            case "Enemy" -> updateEnemyAI();
            case "Soldier" -> updateSoldierAI();
        }
    }

    private void handlePlayerInput() {
        if (activeKeys == null) return;

        Point delta = new Point(0, 0);
        double dAngle = 0;

        if (activeKeys.contains(KeyEvent.VK_LEFT)) delta.translate(-5, 0);
        if (activeKeys.contains(KeyEvent.VK_RIGHT)) delta.translate(5, 0);
        if (activeKeys.contains(KeyEvent.VK_UP)) delta.translate(0, -5);
        if (activeKeys.contains(KeyEvent.VK_DOWN)) delta.translate(0, 5);
        if (activeKeys.contains(KeyEvent.VK_E)) dAngle += 5;
        if (activeKeys.contains(KeyEvent.VK_Q)) dAngle -= 5;

        long now = System.currentTimeMillis();
        if (activeKeys.contains(KeyEvent.VK_SPACE) && now - lastFireTime >= fireCooldown) {
            fireProjectile();
            lastFireTime = now;
        }

        if (delta.x != 0 || delta.y != 0 || dAngle != 0) {
            controlledObject.transform().move(delta, 0);
            controlledObject.transform().rotate(dAngle);
        }

        Point pos = controlledObject.transform().position();
        int width = screenSize.width;
        int height = screenSize.height;
        if (pos.x < 0) controlledObject.transform().move(new Point(width, 0), 0);
        else if (pos.x > width) controlledObject.transform().move(new Point(-width, 0), 0);
        if (pos.y < 0) controlledObject.transform().move(new Point(0, height), 0);
        else if (pos.y > height) controlledObject.transform().move(new Point(0, -height), 0);
    }

    private void updateEnemySpawner() {
        currentEnemies.removeIf(enemy -> !GameEngine.getInstance().getEnabled().contains(enemy));
        if (currentEnemies.isEmpty()) {
            spawnEnemies(enemyCount);
            enemyCount++;
        }
    }

    private void updateSoldierSpawner() {
        currentSoldiers.removeIf(s -> !GameEngine.getInstance().getEnabled().contains(s));
        if (currentSoldiers.isEmpty()) {
            spawnSoldiers(soldierCount);
            soldierCount++;
        }
    }

    private void spawnEnemies(int count) {
        for (int i = 0; i < count; i++) {
            int x = random.nextInt(screenSize.width);
            int y = random.nextInt(screenSize.height);
            Transform t = new Transform(x, y, 0, 0, 1.0);
            Collider c = CircleCollider.create(t, 0, 0, tankRadius);
            GameObject enemy = new GameObject("Enemy", t, c, new Behaviour());
            ((Behaviour) enemy.behaviour()).setControlledObject(enemy);
            GameEngine.getInstance().add(enemy);
            currentEnemies.add(enemy);
        }
    }

    private void spawnSoldiers(int count) {
        for (int i = 0; i < count; i++) {
            int x = random.nextInt(screenSize.width);
            int y = random.nextInt(screenSize.height);
            Transform t = new Transform(x, y, 0, 0, 1.0);
            Collider c = CircleCollider.create(t, 0, 0, 30);
            GameObject soldier = new GameObject("Soldier", t, c, new Behaviour());
            ((Behaviour) soldier.behaviour()).setControlledObject(soldier);
            GameEngine.getInstance().add(soldier);
            currentSoldiers.add(soldier);
            soldierSprites.put(soldier, "Run1");
        }
    }

    private void updateSoldierAI() {
        GameObject player = GameEngine.getInstance().getEnabled().stream()
            .filter(go -> go.name().equals("Player")).findFirst().orElse(null);
        if (player == null) return;

        Point sp = controlledObject.transform().position();
        Point pp = player.transform().position();
        double dx = pp.x - sp.x;
        double dy = pp.y - sp.y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        long now = System.currentTimeMillis();

        if (dist > 300) {
            double speed = 1.5;
            int mx = (int) (dx / dist * speed);
            int my = (int) (dy / dist * speed);
            controlledObject.transform().move(new Point(mx, my), 0);
            int frame = (int) ((now / 100) % 8) + 1;
            soldierSprites.put(controlledObject, "Run" + frame);
        } else {
            int frame = (int) ((now / 200) % 4) + 1;
            soldierSprites.put(controlledObject, "Shot" + frame);
            if (now - lastFireTime > 2500) {
                fireSoldierProjectile(dx, dy);
                lastFireTime = now;
            }
        }
    }

    private void updateEnemyAI() {
    long now = System.currentTimeMillis();
    GameObject player = GameEngine.getInstance().getEnabled().stream()
            .filter(go -> go.name().equals("Player")).findFirst().orElse(null);
    if (player == null) return;

    Point ep = controlledObject.transform().position();
    Point pp = player.transform().position();
    double dx = pp.x - ep.x;
    double dy = pp.y - ep.y;
    double dist = Math.sqrt(dx * dx + dy * dy);

    if (dist > 1) {
        double speed = 1.5;
        int mx = (int) (dx / dist * speed);
        int my = (int) (dy / dist * speed);
        controlledObject.transform().move(new Point(mx, my), 0);
    }

    double angleRad = Math.atan2(dy, dx);
    double angleDeg = Math.toDegrees(angleRad) + 90;
    controlledObject.transform().rotate(angleDeg - controlledObject.transform().angle());

    if (now - lastFireTime >= 3000) {
        fireProjectile();
        lastFireTime = now;
    }
}

    private void fireSoldierProjectile(double dx, double dy) {
        double dist = Math.sqrt(dx * dx + dy * dy);
        int speed = 8;
        double vx = dx / dist * speed;
        double vy = dy / dist * speed;
        Point pos = controlledObject.transform().position();
        Transform t = new Transform(pos.x, pos.y, 0, 0, 1.0);
        Collider c = CircleCollider.create(t, 0, 0, 5);

        GameObject bullet = new GameObject("Bullet2", t, c, new IBehaviour() {
            private GameObject self;

            @Override public void setControlledObject(GameObject go) { this.self = go; }
            @Override public void onInit() {}
            @Override public void onEnabled() {}
            @Override public void onDisabled() {}
            @Override public void onDestroy() {}
            @Override public void onUpdate() {
                self.transform().move(new Point((int) vx, (int) vy), 0);
                Point pos = self.transform().position();
                if (pos.x < 0 || pos.x > screenSize.width || pos.y < 0 || pos.y > screenSize.height) {
                    GameEngine.getInstance().destroy(self);
                    return;
                }
                for (GameObject target : GameEngine.getInstance().getEnabled()) {
                    if ((target.name().equals("Player") || target.name().equals("Soldier")) &&
                        !target.equals(controlledObject) &&
                        GameEngine.getInstance().detectCollision(self.collider(), target.collider())) {

                        if (target.name().equals("Player")) {
                            if (shieldActive) {
                                shieldHitsLeft--;
                                if (shieldHitsLeft <= 0) shieldActive = false;
                            } else {
                                playerHit = true;
                            }
                        } else {
                            Main.addExplosion(target.collider().centroid());
                            GameEngine.getInstance().destroy(target);
                        }
                        GameEngine.getInstance().destroy(self);
                        break;
                    }
                }
            }
            @Override public void onCollision(GameObject other) {}
        });
        bullet.behaviour().setControlledObject(bullet);
        GameEngine.getInstance().add(bullet);
    }

    private void fireProjectile() {
        ITransform t = controlledObject.transform();
        double angleRad = Math.toRadians(t.angle() - 90);
        double dx = Math.cos(angleRad) * 10;
        double dy = Math.sin(angleRad) * 10;

        Transform bulletT = new Transform(((Transform) t).posX(), ((Transform) t).posY(), t.layer(), t.angle(), 1.0);
        Collider bulletC = CircleCollider.create(bulletT, 0, 0, 5);

        GameObject bullet = new GameObject("Bullet", bulletT, bulletC, new IBehaviour() {
            private GameObject self;
            @Override public void setControlledObject(GameObject go) { this.self = go; }
            @Override public void onInit() {}
            @Override public void onEnabled() {}
            @Override public void onDisabled() {}
            @Override public void onDestroy() {}
            @Override public void onUpdate() {
                self.transform().move(new Point((int) dx, (int) dy), 0);
                Point pos = self.transform().position();
                if (pos.x < 0 || pos.x > screenSize.width || pos.y < 0 || pos.y > screenSize.height) {
                    GameEngine.getInstance().destroy(self);
                    return;
                }

                for (GameObject target : GameEngine.getInstance().getEnabled()) {
                    if ((target.name().equals("Enemy") || target.name().equals("Player") || target.name().startsWith("Soldier")) &&
                        !target.equals(controlledObject) &&
                        GameEngine.getInstance().detectCollision(self.collider(), target.collider())) {

                        if (target.name().equals("Player")) {
                            if (shieldActive) {
                                shieldHitsLeft--;
                                if (shieldHitsLeft <= 0) shieldActive = false;
                            } else {
                                playerHit = true;
                            }
                        } else {
                            Main.addExplosion(target.collider().centroid());
                            GameEngine.getInstance().destroy(target);
                            score++;
                        }

                        GameEngine.getInstance().destroy(self);
                        break;
                    }
                }
            }

            @Override public void onCollision(GameObject other) {}
        });

        bullet.behaviour().setControlledObject(bullet);
        GameEngine.getInstance().add(bullet);
        playShootSound();
    }

    private void spawnShieldIfNeeded() {
        long now = System.currentTimeMillis();
        if (now - lastShieldTime >= shieldInterval) {
            lastShieldTime = now;
            int x = random.nextInt(screenSize.width - 60) + 30;
            int y = random.nextInt(screenSize.height - 60) + 30;

            Transform t = new Transform(x, y, 0, 0, 0.75);
            Collider c = CircleCollider.create(t, 0, 0, 25);

            GameObject shield = new GameObject("Shield", t, c, new IBehaviour() {
                private GameObject self;

                @Override public void setControlledObject(GameObject go) { self = go; }
                @Override public void onInit() {}
                @Override public void onEnabled() {}
                @Override public void onDisabled() {}
                @Override public void onDestroy() {}
                @Override public void onUpdate() {
                    GameObject player = GameEngine.getInstance().getEnabled().stream()
                            .filter(go -> go.name().equals("Player")).findFirst().orElse(null);
                    if (player != null && GameEngine.getInstance().detectCollision(self.collider(), player.collider())) {
                        GameEngine.getInstance().destroy(self);
                        shieldActive = true;
                        shieldHitsLeft = 3;
                    }
                }
                @Override public void onCollision(GameObject other) {}
            });

            shield.behaviour().setControlledObject(shield);
            GameEngine.getInstance().add(shield);
        }
    }

    @Override
    public void onCollision(GameObject other) {
        if (controlledObject.name().equals("Enemy") && other.name().equals("Bullet")) {
            Main.addExplosion(controlledObject.collider().centroid());
            GameEngine.getInstance().destroy(controlledObject);
            score++;
        }
    }

    protected void playShootSound() {}
}
