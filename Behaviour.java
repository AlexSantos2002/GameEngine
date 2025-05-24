import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.Toolkit;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Random;


public class Behaviour implements IBehaviour {

    protected GameObject controlledObject;
    private Set<Integer> activeKeys;
    private long lastFireTime = 0;
    private final long fireCooldown = 1000;

    private final Random random = new Random();
    private final int tankRadius = 30;
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final List<GameObject> currentEnemies = new ArrayList<>();
    private static int enemyCount = 5;

    private static boolean playerHit = false;
    private static int score = 0;

    public static boolean wasPlayerHit() {
        return playerHit;
    }

    public static void resetPlayerHit() {
        playerHit = false;
    }

    public static void resetGameState() {
        playerHit = false;
        enemyCount = 5;
        score = 0;
    }

    public static int getScore() {
        return score;
    }

    public static void resetScore() {
        score = 0;
    }

    public void setControlledObject(GameObject go) {
        this.controlledObject = go;
    }

    public GameObject getControlledObject() {
        return controlledObject;
    }

    public void setActiveKeys(Set<Integer> keys) {
        this.activeKeys = keys;
    }

    @Override public void onInit() {}
    @Override public void onEnabled() {}
    @Override public void onDisabled() {}
    @Override public void onDestroy() {}

    @Override
    public void onUpdate() {
        if (controlledObject == null) return;

        if (controlledObject.name().equals("Player")) {
            handlePlayerInput();
            updateEnemySpawner();
        } else if (controlledObject.name().equals("Enemy")) {
            updateEnemyAI();
        }
    }

    private void handlePlayerInput() {
        if (activeKeys == null) return;

        Point delta = new Point(0, 0);
        double dAngle = 0;

        if (activeKeys.contains(KeyEvent.VK_LEFT))  delta.translate(-5, 0);
        if (activeKeys.contains(KeyEvent.VK_RIGHT)) delta.translate(5, 0);
        if (activeKeys.contains(KeyEvent.VK_UP))    delta.translate(0, -5);
        if (activeKeys.contains(KeyEvent.VK_DOWN))  delta.translate(0, 5);
        if (activeKeys.contains(KeyEvent.VK_E))     dAngle += 5;
        if (activeKeys.contains(KeyEvent.VK_Q))     dAngle -= 5;

        long now = System.currentTimeMillis();
        if (activeKeys.contains(KeyEvent.VK_SPACE) && now - lastFireTime >= fireCooldown) {
            fireProjectile();
            lastFireTime = now;
        }

        if (delta.x != 0 || delta.y != 0 || dAngle != 0) {
            controlledObject.transform().move(delta, 0);
            controlledObject.transform().rotate(dAngle);
        }

        // Wrap-around
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

    private void spawnEnemies(int count) {
        List<Point> takenSpots = new ArrayList<>();
        Point playerPos = controlledObject.transform().position();

        for (int i = 0; i < count; i++) {
            Point pos;
            int attempts = 0;
            do {
                int x = random.nextInt(screenSize.width - 2 * tankRadius) + tankRadius;
                int y = random.nextInt(screenSize.height - 2 * tankRadius) + tankRadius;
                pos = new Point(x, y);
                attempts++;
            } while ((pos.distance(playerPos) < 3 * tankRadius || overlaps(pos, takenSpots)) && attempts < 100);

            Transform transform = new Transform(pos.x, pos.y, 0, 0, 1.0);
            Collider collider = CircleCollider.create(transform, 0, 0, tankRadius);

            GameObject enemy = new GameObject("Enemy", transform, collider, new Behaviour());
            ((Behaviour) enemy.behaviour()).setControlledObject(enemy);
            GameEngine.getInstance().add(enemy);
            currentEnemies.add(enemy);
            takenSpots.add(pos);
        }
    }

    private boolean overlaps(Point p, List<Point> others) {
        for (Point other : others) {
            if (p.distance(other) < 2 * tankRadius) return true;
        }
        return false;
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

    private void fireProjectile() {
        ITransform t = controlledObject.transform();
        double angleRad = Math.toRadians(t.angle() - 90);
        double dx = Math.cos(angleRad) * 10;
        double dy = Math.sin(angleRad) * 10;

        Transform bulletT = new Transform(((Transform) t).posX(), ((Transform) t).posY(), t.layer(), t.angle(), 1.0);
        Collider bulletC = CircleCollider.create(bulletT, 0, 0, 5);

        GameObject bullet = new GameObject("Bullet", bulletT, bulletC, new IBehaviour() {
            private GameObject self;

            @Override public void onInit() {}
            @Override public void onEnabled() {}
            @Override public void onDisabled() {}
            @Override public void onDestroy() {}

            @Override
            public void onUpdate() {
                if (self == null) return;
                self.transform().move(new Point((int) dx, (int) dy), 0);
                Point pos = self.transform().position();

                if (pos.x < 0 || pos.x > screenSize.width || pos.y < 0 || pos.y > screenSize.height) {
                    GameEngine.getInstance().destroy(self);
                    return;
                }

                for (GameObject target : GameEngine.getInstance().getEnabled()) {
                    if ((target.name().equals("Enemy") || target.name().equals("Player")) &&
                            !target.equals(controlledObject) &&
                            GameEngine.getInstance().detectCollision(self.collider(), target.collider())) {

                        if (target.name().equals("Player")) {
                            playerHit = true;
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
            @Override public void setControlledObject(GameObject go) { this.self = go; }
        });

        bullet.behaviour().setControlledObject(bullet);
        GameEngine.getInstance().add(bullet);
        playShootSound();
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
