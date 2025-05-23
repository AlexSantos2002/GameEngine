import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.awt.Point;

public class GameEngine {

    private static final GameEngine instance = new GameEngine();

    public static GameEngine getInstance() {
        return instance;
    }

    private final List<GameObject> objects = new ArrayList<>();
    private final List<GameObject> enabledObjects = new ArrayList<>();

    public void add(GameObject go) {
        objects.add(go);
        enabledObjects.add(go);
        go.behaviour().onInit();
    }

    public void destroy(GameObject go) {
        objects.remove(go);
        enabledObjects.remove(go);
        go.behaviour().onDestroy();
    }

    public void enable(GameObject go) {
        if (!enabledObjects.contains(go)) {
            enabledObjects.add(go);
            go.behaviour().onEnabled();
        }
    }

    public void disable(GameObject go) {
        if (enabledObjects.remove(go)) {
            go.behaviour().onDisabled();
        }
    }

    public List<GameObject> getEnabled() {
        return new ArrayList<>(enabledObjects);
    }

    public Map<GameObject, List<GameObject>> simulate(int frames, Map<GameObject, double[]> velocities) {
        for (int i = 0; i < frames; i++) {
            for (GameObject go : enabledObjects) {
                double[] v = velocities.get(go);
                go.transform().move(new Point((int) v[0], (int) v[1]), (int) v[2]);
                go.transform().rotate(v[3]);
                go.transform().scale(v[4]);
                go.collider().adjustToTransform();
                go.behaviour().onUpdate();
            }
        }

        Map<GameObject, List<GameObject>> collisions = new LinkedHashMap<>();
        for (int i = 0; i < enabledObjects.size(); i++) {
            GameObject a = enabledObjects.get(i);
            for (int j = i + 1; j < enabledObjects.size(); j++) {
                GameObject b = enabledObjects.get(j);
                if (a.transform().layer() != b.transform().layer()) continue;
                if (detectCollision(a.collider(), b.collider())) {
                    collisions.computeIfAbsent(a, k -> new ArrayList<>()).add(b);
                    collisions.computeIfAbsent(b, k -> new ArrayList<>()).add(a);
                }
            }
        }

        for (Map.Entry<GameObject, List<GameObject>> entry : collisions.entrySet()) {
            for (GameObject other : entry.getValue()) {
                entry.getKey().behaviour().onCollision(other);
            }
        }

        return collisions;
    }

public boolean detectCollision(ICollider a, ICollider b) {
    if (a instanceof CircleCollider && b instanceof CircleCollider) {
        CircleCollider ca = (CircleCollider) a;
        CircleCollider cb = (CircleCollider) b;

        double dx = ca.centroid().x - cb.centroid().x;
        double dy = ca.centroid().y - cb.centroid().y;
        double distanceSq = dx * dx + dy * dy;
        double radiusSum = ca.getRadius() + cb.getRadius();

        return distanceSq <= radiusSum * radiusSum;
    }
    return false;
}
}
