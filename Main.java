import java.awt.Point;
import java.util.*;

/**
 * Programa principal que cria o GameObject e aplica transformações conforme o problema M.
 */
public class Main {
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            // Lê GameObject
            String name = sc.nextLine();
            double x = sc.nextDouble(), y = sc.nextDouble();
            int layer = sc.nextInt();
            double angle = sc.nextDouble(), scale = sc.nextDouble();
            Transform transform = new Transform(x, y, layer, angle, scale);
            sc.nextLine(); // consumir fim da linha

            // Lê Collider
            String[] data = sc.nextLine().trim().split(" ");
            Collider collider;

            if (data.length == 3) {
                double cx = Double.parseDouble(data[0]);
                double cy = Double.parseDouble(data[1]);
                double r = Double.parseDouble(data[2]);
                collider = CircleCollider.create(transform, cx, cy, r);
            } else {
                List<Point.Double> vertices = new ArrayList<>();
                for (int i = 0; i < data.length; i += 2) {
                    double vx = Double.parseDouble(data[i]);
                    double vy = Double.parseDouble(data[i + 1]);
                    vertices.add(new Point.Double(vx, vy));
                }
                collider = PolygonCollider.create(transform, vertices);
            }

            // Cria GameObject
            GameObject go = new GameObject(name, transform, collider);

            // Aplica transformações subsequentes
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(" ");
                switch (parts[0]) {
                    case "move":
                        double dx = Double.parseDouble(parts[1]);
                        double dy = Double.parseDouble(parts[2]);
                        int dlayer = Integer.parseInt(parts[3]);
                        transform.move(new Point((int) dx, (int) dy), dlayer);
                        break;
                    case "rotate":
                        double dTheta = Double.parseDouble(parts[1]);
                        transform.rotate(dTheta);
                        break;
                    case "scale":
                        double dScale = Double.parseDouble(parts[1]);
                        transform.scale(dScale);
                        break;
                }

                // Reajusta o colisor após transformação
                collider.adjustToTransform();
            }

            // Imprime GameObject final
            System.out.println(go);
        }
    }
}
