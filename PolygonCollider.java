import java.awt.Point;
import java.util.*;

/**
 * Colisor poligonal que ajusta os vértices com rotação, escala e translação da Transform.
 */
public class PolygonCollider extends Collider {
    private List<Point.Double> vertices;

    // Construtor privado
    private PolygonCollider(Transform t, List<Point.Double> verts) {
        super(t);
        this.vertices = new ArrayList<>(verts);
    }

    // Factory method
    public static PolygonCollider create(Transform t, List<Point.Double> verts) {
        PolygonCollider c = new PolygonCollider(t, verts);
        c.adjustToTransform(); // só depois de tudo estar pronto
        return c;
    }

    private Point.Double computeCentroid() {
        double A = 0, cx = 0, cy = 0;
        int n = vertices.size();

        for (int i = 0; i < n; i++) {
            Point.Double p0 = vertices.get(i);
            Point.Double p1 = vertices.get((i + 1) % n);
            double cross = p0.x * p1.y - p1.x * p0.y;
            A += cross;
            cx += (p0.x + p1.x) * cross;
            cy += (p0.y + p1.y) * cross;
        }

        A *= 0.5;
        cx /= (6 * A);
        cy /= (6 * A);

        return new Point.Double(cx, cy);
    }

    @Override
    public void adjustToTransform() {
        Point.Double centroid = computeCentroid();
        List<Point.Double> moved = new ArrayList<>();

        double rad = Math.toRadians(transform.angle());
        double cos = Math.cos(rad), sin = Math.sin(rad);
        double tx = transform.posX(), ty = transform.posY();
        double scale = transform.scale();

        for (Point.Double p : vertices) {
            double x = p.x - centroid.x;
            double y = p.y - centroid.y;

            double xr = x * cos - y * sin;
            double yr = x * sin + y * cos;

            xr = xr * scale + tx;
            yr = yr * scale + ty;

            moved.add(new Point.Double(xr, yr));
        }

        this.vertices = moved;
    }

    @Override
    public Point centroid() {
        return new Point((int) transform.posX(), (int) transform.posY());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Point.Double p : vertices) {
            sb.append(String.format("(%.2f,%.2f) ", p.x, p.y));
        }
        return sb.toString().trim();
    }
}
