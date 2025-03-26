import static org.junit.Assert.assertEquals;
import java.awt.Point;
import org.junit.Test;

/**
 * @author Alexandre Santos(71522)
 * @version 26/03/2025
 */
public class TransformTest {

    @Test
    public void testConstructorAndGetters() {
        Transform t = new Transform(5.5, 7.2, 1, 45, 1.5);

        assertEquals(new Point(5, 7), t.position());
        assertEquals(1, t.layer());
        assertEquals(45.0, t.angle(), 0.001);
        assertEquals(1.5, t.scale(), 0.001);
        assertEquals(5.5, t.posX(), 0.001);
        assertEquals(7.2, t.posY(), 0.001);
    }

    @Test
    public void testMove() {
        Transform t = new Transform(1, 2, 0, 0, 1);
        t.move(new Point(3, -1), 2);

        assertEquals(new Point(4, 1), t.position());
        assertEquals(2, t.layer());
    }

    @Test
    public void testRotatePositiveAndOverflow() {
        Transform t = new Transform(0, 0, 0, 350, 1);
        t.rotate(20); // 350 + 20 = 370 % 360 = 10

        assertEquals(10.0, t.angle(), 0.001);
    }

    @Test
    public void testRotateNegative() {
        Transform t = new Transform(0, 0, 0, 10, 1);
        t.rotate(-20); // 10 - 20 = -10 % 360 = 350

        assertEquals(350.0, t.angle(), 0.001);
    }

    @Test
    public void testScale() {
        Transform t = new Transform(0, 0, 0, 0, 2.5);
        t.scale(1.5); // 2.5 + 1.5 = 4.0

        assertEquals(4.0, t.scale(), 0.001);
    }

    @Test
    public void testToStringFormat() {
        Transform t = new Transform(3.456, 7.891, 2, 123.456, 1.234);
        String expected = String.format("(%.2f,%.2f) %d %.2f %.2f", 3.456, 7.891, 2, 123.456 % 360, 1.234);

        assertEquals(expected, t.toString());
    }
}
