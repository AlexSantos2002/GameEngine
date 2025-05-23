import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.Set;

public class Behaviour implements IBehaviour {

    private GameObject controlledObject;
    private Set<Integer> activeKeys;

    public void setControlledObject(GameObject go) {
        this.controlledObject = go;
    }

    public void setActiveKeys(Set<Integer> keys) {
        this.activeKeys = keys;
    }

    @Override
    public void onInit() {
        // Initialization logic if needed
    }

    @Override
    public void onEnabled() {
        // Logic to execute when enabled
    }

    @Override
    public void onDisabled() {
        // Logic to execute when disabled
    }

    @Override
    public void onDestroy() {
        // Cleanup logic if needed
    }

    @Override
    public void onUpdate() {
        if (controlledObject == null || activeKeys == null) return;

        Point delta = new Point(0, 0);
        int dLayer = 0;
        double dAngle = 0;
        double dScale = 0;

        if (activeKeys.contains(KeyEvent.VK_LEFT))  delta.translate(-5, 0);
        if (activeKeys.contains(KeyEvent.VK_RIGHT)) delta.translate(5, 0);
        if (activeKeys.contains(KeyEvent.VK_UP))    delta.translate(0, -5);
        if (activeKeys.contains(KeyEvent.VK_DOWN))  delta.translate(0, 5);
        if (activeKeys.contains(KeyEvent.VK_E))     dAngle += 5;
        if (activeKeys.contains(KeyEvent.VK_Q))     dAngle -= 5;

        if (delta.x != 0 || delta.y != 0 || dAngle != 0 || dScale != 0) {
            controlledObject.transform().move(delta, dLayer);
            controlledObject.transform().rotate(dAngle);
            controlledObject.transform().scale(dScale);
        }
    }

    @Override
    public void onCollision(GameObject other) {
        // Collision handling logic
    }
}
