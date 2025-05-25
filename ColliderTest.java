import static org.junit.Assert.*;
import org.junit.Test;

public class ColliderTest {

    @Test
    public void testTransformReferenceIsStored() {
        Transform t = new Transform(10, 20, 0, 0, 1.0);
        Collider c = CircleCollider.create(t, 0, 0, 5);
        assertSame(t, c.transform);
    }

    @Test
    public void testAdjustToTransformAffectsCollider() {
        Transform t = new Transform(0, 0, 0, 0, 1.0);
        CircleCollider c = CircleCollider.create(t, 0, 0, 5);
        
        t.scale(2.0);
        c.adjustToTransform();
        assertEquals(15.0, c.getRadius(), 0.01);
    }

    @Test
    public void testToStringReturnsExpectedFormat() {
        Transform t = new Transform(3, 4, 0, 0, 1.0);
        CircleCollider c = CircleCollider.create(t, 0, 0, 5);
        String result = c.toString();

        assertTrue(result.startsWith("("));
        assertTrue(result.contains("5.00"));
    }
}
