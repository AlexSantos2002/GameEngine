import static org.junit.Assert.*;

import java.awt.Point;
import java.util.*;
import org.junit.Test;

public class PolygonColliderTest {

    @Test
    public void testCreateAndCentroid() {
        Transform t = new Transform(5, 5, 1, 0, 1);
        List<Point.Double> verts = Arrays.asList(
            new Point.Double(0, 0),
            new Point.Double(2, 0),
            new Point.Double(1, 2)
        );
        PolygonCollider collider = PolygonCollider.create(t, verts);
        
        assertEquals(new Point(5, 5), collider.centroid());
    }

    @Test
    public void testAdjustToTransform_Move() {
        Transform t = new Transform(3, 4, 1, 0, 1);
        List<Point.Double> verts = Arrays.asList(
            new Point.Double(0, 0),
            new Point.Double(2, 0),
            new Point.Double(1, 2)
        );
        PolygonCollider collider = PolygonCollider.create(t, verts);
        
        t.move(new Point(2, -1), 0);
        collider.adjustToTransform();
        
        assertEquals(new Point(5, 3), collider.centroid());
    }

    @Test
    public void testAdjustToTransform_Rotate() {
        Transform t = new Transform(0, 0, 1, 90, 1);
        List<Point.Double> verts = Arrays.asList(
            new Point.Double(1, 0),
            new Point.Double(1, 1),
            new Point.Double(0, 1)
        );
        PolygonCollider collider = PolygonCollider.create(t, verts);
        collider.adjustToTransform();
        
        String expected = "(0,67,0,33) (-0,33,0,33) (-0,33,-0,67)";
        assertEquals(expected, collider.toString());
    }

    @Test
    public void testAdjustToTransform_Scale() {
        Transform t = new Transform(0, 0, 1, 0, 2);
        List<Point.Double> verts = Arrays.asList(
            new Point.Double(1, 1),
            new Point.Double(-1, 1),
            new Point.Double(-1, -1),
            new Point.Double(1, -1)
        );
        PolygonCollider collider = PolygonCollider.create(t, verts);
        collider.adjustToTransform();
        
        String expected = "(2,00,2,00) (-2,00,2,00) (-2,00,-2,00) (2,00,-2,00)";
        assertEquals(expected, collider.toString());
    }

    @Test
    public void testToStringFormat() {
        Transform t = new Transform(1.5, 2.5, 1, 0, 1);
        List<Point.Double> verts = Arrays.asList(
            new Point.Double(0, 0),
            new Point.Double(1, 0),
            new Point.Double(0, 1)
        );
        PolygonCollider collider = PolygonCollider.create(t, verts);
        collider.adjustToTransform();
        
        String expected = "(1,17,2,17) (2,17,2,17) (1,17,3,17)";
        assertEquals(expected, collider.toString());
    }
}
