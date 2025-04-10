
import static org.junit.Assert.*;
import org.junit.Test;
import java.awt.Point;
import java.util.*;


/**
 * @author Nurio Pereira (72788)
 * @version 1.0 10/04/2025
 */

public class GameEngineTest {

    @Test
    public void testAdd() {
        GameEngine engine = new GameEngine();
        GameObject go = new GameObject("Obj", new Transform(0,0,0,0,1),
            CircleCollider.create(new Transform(0,0,0,0,1), 0, 0, 1));
        engine.add(go);
        assertTrue(engine.simulate(1, Map.of(go, new double[]{0,0,0,0,0})).isEmpty());
    }

    @Test
    public void testDestroy() {
        GameEngine engine = new GameEngine();
        GameObject go = new GameObject("Obj", new Transform(0,0,0,0,1),
            CircleCollider.create(new Transform(0,0,0,0,1), 0, 0, 1));
        engine.add(go);
        engine.destroy(go);
        assertTrue(engine.simulate(1, Map.of(go, new double[]{0,0,0,0,0})).isEmpty());
    }

    @Test
    public void testCircleCircleCollision() {
        GameEngine engine = new GameEngine();
        Transform t1 = new Transform(0, 0, 0, 0, 1);
        Transform t2 = new Transform(1, 0, 0, 0, 1);
        GameObject g1 = new GameObject("A", t1, CircleCollider.create(t1, 0, 0, 1));
        GameObject g2 = new GameObject("B", t2, CircleCollider.create(t2, 0, 0, 1));
        engine.add(g1); engine.add(g2);
        Map<GameObject, double[]> vels = Map.of(g1, new double[]{0,0,0,0,0}, g2, new double[]{0,0,0,0,0});
        Map<GameObject, List<GameObject>> r = engine.simulate(1, vels);
        assertTrue(r.get(g1).contains(g2) && r.get(g2).contains(g1));
    }

    @Test
    public void testCirclePolygonCollision() {
        GameEngine engine = new GameEngine();
        Transform tc = new Transform(1, 1, 0, 0, 1);
        Transform tp = new Transform(1, 1, 0, 0, 1);
        GameObject g1 = new GameObject("Circle", tc, CircleCollider.create(tc, 0, 0, 1));
        List<Point.Double> verts = List.of(
            new Point.Double(0,0), new Point.Double(2,0), new Point.Double(2,2), new Point.Double(0,2));
        GameObject g2 = new GameObject("Polygon", tp, PolygonCollider.create(tp, verts));
        engine.add(g1); engine.add(g2);
        Map<GameObject, double[]> vels = Map.of(g1, new double[]{0,0,0,0,0}, g2, new double[]{0,0,0,0,0});
        Map<GameObject, List<GameObject>> r = engine.simulate(1, vels);
        assertTrue(r.get(g1).contains(g2));
    }

    @Test
    public void testPolygonPolygonCollision() {
        GameEngine engine = new GameEngine();
        Transform t1 = new Transform(1, 1, 0, 0, 1);
        Transform t2 = new Transform(1, 1, 0, 0, 1);
        List<Point.Double> verts = List.of(
            new Point.Double(0,0), new Point.Double(2,0), new Point.Double(2,2), new Point.Double(0,2));
        GameObject g1 = new GameObject("P1", t1, PolygonCollider.create(t1, verts));
        GameObject g2 = new GameObject("P2", t2, PolygonCollider.create(t2, verts));
        engine.add(g1); engine.add(g2);
        Map<GameObject, double[]> vels = Map.of(g1, new double[]{0,0,0,0,0}, g2, new double[]{0,0,0,0,0});
        Map<GameObject, List<GameObject>> r = engine.simulate(1, vels);
        assertTrue(r.get(g1).contains(g2));
    }

    @Test
    public void testNoCollisionDifferentLayers() {
        GameEngine engine = new GameEngine();
        Transform t1 = new Transform(0, 0, 0, 0, 1);
        Transform t2 = new Transform(0, 0, 1, 0, 1);
        GameObject g1 = new GameObject("Top", t1, CircleCollider.create(t1, 0, 0, 1));
        GameObject g2 = new GameObject("Bottom", t2, CircleCollider.create(t2, 0, 0, 1));
        engine.add(g1); engine.add(g2);
        Map<GameObject, double[]> vels = Map.of(g1, new double[]{0,0,0,0,0}, g2, new double[]{0,0,0,0,0});
        Map<GameObject, List<GameObject>> r = engine.simulate(1, vels);
        assertNull(r.get(g1));
        assertNull(r.get(g2));
    }
}
