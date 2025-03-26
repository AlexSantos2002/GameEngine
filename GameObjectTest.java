import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Point;
import java.util.List;
import org.junit.Test;

public class GameObjectTest {

    @Test
    public void testConstructorAndAccessors() {
        Transform t = new Transform(3, 5, 1, 45, 2.0);
        Collider c = PolygonCollider.create(t, List.of(
            new Point.Double(0, 0),
            new Point.Double(0, 2),
            new Point.Double(2, 2),
            new Point.Double(2, 0)
        ));
        GameObject obj = new GameObject("Alien", t, c);

        assertEquals("Alien", obj.name());
        assertEquals(t, obj.transform());
        assertEquals(c, obj.collider());
    }

    @Test
    public void testToStringFormat() {
        Transform t = new Transform(4, 7, 0, 90, 1);
        Collider c = PolygonCollider.create(t, List.of(
            new Point.Double(0, 0),
            new Point.Double(0, 2),
            new Point.Double(2, 2),
            new Point.Double(2, 0)
        ));
        GameObject obj = new GameObject("Player", t, c);

        String result = obj.toString();
        System.out.println("===== DEBUG toString() Output =====");
        System.out.println(result);
        System.out.println("===================================");
    }

       

    @Test
    public void testColliderCentroidMatchesTransformPosition() {
        Transform t = new Transform(6, 6, 0, 0, 1);
        Collider c = PolygonCollider.create(t, List.of(
            new Point.Double(1, 1),
            new Point.Double(1, 3),
            new Point.Double(3, 3),
            new Point.Double(3, 1)
        ));
        GameObject obj = new GameObject("Box", t, c);

        Point expectedCentroid = new Point(6, 6);
        assertEquals(expectedCentroid, obj.collider().centroid());
    }
}
