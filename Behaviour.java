import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.Set;
import java.awt.Dimension;
import java.awt.Toolkit;

public class Behaviour implements IBehaviour {

    private GameObject controlledObject;
    private Set<Integer> activeKeys;
    private long lastFireTime = 0;
    private final long fireCooldown = 1000;

    public void setControlledObject(GameObject go) {
        this.controlledObject = go;
    }

    public void setActiveKeys(Set<Integer> keys) {
        this.activeKeys = keys;
    }

    @Override
    public void onInit() {}

    @Override
    public void onEnabled() {}

    @Override
    public void onDisabled() {}

    @Override
    public void onDestroy() {}

    @Override
    public void onUpdate() {
        if (controlledObject == null || activeKeys == null) return;

        Point delta = new Point(0, 0);
        int dLayer = 0;
        double dAngle = 0;
        double dScale = 0;

        // Movimento e rotação
        if (activeKeys.contains(KeyEvent.VK_LEFT))  delta.translate(-5, 0);
        if (activeKeys.contains(KeyEvent.VK_RIGHT)) delta.translate(5, 0);
        if (activeKeys.contains(KeyEvent.VK_UP))    delta.translate(0, -5);
        if (activeKeys.contains(KeyEvent.VK_DOWN))  delta.translate(0, 5);
        if (activeKeys.contains(KeyEvent.VK_E))     dAngle += 5;
        if (activeKeys.contains(KeyEvent.VK_Q))     dAngle -= 5;

        // Disparo com espaço (1 por segundo)
        long now = System.currentTimeMillis();
        if (activeKeys.contains(KeyEvent.VK_SPACE) && now - lastFireTime >= fireCooldown) {
            fireProjectile();
            lastFireTime = now;
        }

        if (delta.x != 0 || delta.y != 0 || dAngle != 0 || dScale != 0) {
            controlledObject.transform().move(delta, dLayer);
            controlledObject.transform().rotate(dAngle);
            controlledObject.transform().scale(dScale);
        }
    }

    private void fireProjectile() {
        Transform t = (Transform) controlledObject.transform();
        double angleRad = Math.toRadians(t.angle() - 90); // Corrigir direção visual
        double dx = Math.cos(angleRad) * 10;
        double dy = Math.sin(angleRad) * 10;

        Transform bulletT = new Transform(t.posX(), t.posY(), t.layer(), t.angle(), 1.0);
        Collider bulletC = CircleCollider.create(bulletT, 0, 0, 5);

        GameObject bullet = new GameObject("Bullet", bulletT, bulletC, new IBehaviour() {
            private GameObject self;

            @Override
            public void onInit() {}

            @Override
            public void onEnabled() {}

            @Override
            public void onDisabled() {}

            @Override
            public void onDestroy() {}

            @Override
            public void onUpdate() {
                if (self == null) return;
                self.transform().move(new Point((int) dx, (int) dy), 0);
                Point pos = self.transform().position();
                Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
                if (pos.x < 0 || pos.x > screen.width || pos.y < 0 || pos.y > screen.height) {
                    GameEngine.getInstance().destroy(self);
                }
            }

            @Override
            public void onCollision(GameObject other) {
                GameEngine.getInstance().destroy(self);
            }

            @Override
            public void setControlledObject(GameObject go) {
                this.self = go;
            }
        });

        bullet.behaviour().setControlledObject(bullet);
        GameEngine.getInstance().add(bullet);
        playShootSound();
    }

    @Override
    public void onCollision(GameObject other) {
    }

    protected void playShootSound() {
    }
}
