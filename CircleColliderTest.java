import static org.junit.Assert.assertEquals;
import java.awt.Point;
import org.junit.Test;

/**
 * Testes unitários para a classe CircleCollider.
 * Valida a criação, transformação e formatação do colisor circular.
 * @author Alexandre Santos (71522)
 * @version 1.0 26/03/2025
 */
public class CircleColliderTest {

    @Test
    public void testCreateAndCentroid() {
        Transform t = new Transform(5, 8, 0, 0, 1.0);
        CircleCollider c = CircleCollider.create(t, 0, 0, 3);
        assertEquals(new Point(5, 8), c.centroid());
    }

    @Test
    public void testRadiusIsScaled() {
        Transform t = new Transform(2, 2, 0, 0, 2.0);
        CircleCollider c = CircleCollider.create(t, 0, 0, 4);

        String str = c.toString();
        System.out.println("DEBUG: toString() = " + str);

        String[] parts = str.split(" ");
        assertEquals("(2,00,2,00)", parts[0]);
        double raio = Double.parseDouble(parts[1].replace(",", "."));

        assertEquals(8.0, raio, 0.01);
    }

    @Test
    public void testToStringFormat() {
        Transform t = new Transform(1.2, 3.4, 0, 0, 1.5);
        CircleCollider c = CircleCollider.create(t, 0, 0, 2.0);

        String result = c.toString();
        assertEquals("(1,20,3,40) 3,00", result);
    }
}