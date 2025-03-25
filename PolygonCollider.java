import java.awt.Point;
import java.util.*;

/**
 * Colisor poligonal que ajusta os vértices com base na Transform: move, rotação e escala.
 * Mantém os vértices originais para aplicar transformações acumuladas com precisão.
 * Garante que a posição da Transform coincide com o novo centroide após transformação.
 * @author Alexandre Santos(71522), Nurio Pereira (72788)
 * @version 1.0 25/03/2025
 * @inv O colisor está sempre centrado na Transform do GameObject. As transformações são aplicadas sobre os vértices originais.
 */

public class PolygonCollider extends Collider {
    private final List<Point.Double> originalVertices;
    private List<Point.Double> transformedVertices;

    private PolygonCollider(Transform t, List<Point.Double> verts) {
        super(t);
        this.originalVertices = new ArrayList<>(verts);
        this.transformedVertices = new ArrayList<>();
    }

    public static PolygonCollider create(Transform t, List<Point.Double> verts) {
        PolygonCollider c = new PolygonCollider(t, verts);
        c.adjustToTransform();
        return c;
    }

    private Point.Double computeCentroid(List<Point.Double> verts) {
        double A = 0, cx = 0, cy = 0;
        int n = verts.size();

        for (int i = 0; i < n; i++) {
            Point.Double p0 = verts.get(i);
            Point.Double p1 = verts.get((i + 1) % n);
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
        Point.Double centroid = computeCentroid(originalVertices);
        List<Point.Double> moved = new ArrayList<>();

        double rad = Math.toRadians(transform.angle());
        double cos = Math.cos(rad), sin = Math.sin(rad);
        double tx = transform.posX(), ty = transform.posY();
        double scale = transform.scale();

        for (Point.Double p : originalVertices) {
            double x = p.x - centroid.x;
            double y = p.y - centroid.y;

            double xr = x * cos - y * sin;
            double yr = x * sin + y * cos;

            xr = xr * scale + tx;
            yr = yr * scale + ty;

            moved.add(new Point.Double(xr, yr));
        }

        this.transformedVertices = moved;
    }

    @Override
    public Point centroid() {
        return new Point((int) transform.posX(), (int) transform.posY());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Point.Double p : transformedVertices) {
            sb.append(String.format("(%.2f,%.2f) ", p.x, p.y));
        }
        return sb.toString().trim();
    }
}
