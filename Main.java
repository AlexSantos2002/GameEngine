import java.awt.Point;
import java.util.*;

/**
 * Programa principal que cria o GameObject a partir da entrada.
 */
public class Main {
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            String name = sc.nextLine();
            double x = sc.nextDouble(), y = sc.nextDouble();
            int layer = sc.nextInt();
            double angle = sc.nextDouble(), scale = sc.nextDouble();
            Transform transform = new Transform(x, y, layer, angle, scale);
            sc.nextLine(); // consume newline

            String[] data = sc.nextLine().split(" ");
            Collider collider;

            if (data.length == 3) {
                double cx = Double.parseDouble(data[0]);
                double cy = Double.parseDouble(data[1]);
                double r = Double.parseDouble(data[2]);
                collider = new CircleCollider(transform, cx, cy, r);
            } else {
                List<Point.Double> vertices = new ArrayList<>();
                for (int i = 0; i < data.length; i += 2) {
                    double vx = Double.parseDouble(data[i]);
                    double vy = Double.parseDouble(data[i + 1]);
                    vertices.add(new Point.Double(vx, vy));
                }
                collider = new PolygonCollider(transform, vertices);
            }

            GameObject go = new GameObject(name, transform, collider);
            System.out.println(go);
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
