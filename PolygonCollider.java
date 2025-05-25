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

    /**
     * Lista de vértices originais do polígono.
     * Os vértices são armazenados como pontos com coordenadas de ponto flutuante para maior precisão.
     */
    private final List<Point.Double> originalVertices;

    /**
     * Lista de vértices transformados do polígono.
     * Estes vértices são calculados com base na Transform atual e podem ser ajustados dinamicamente.
     */
    private List<Point.Double> transformedVertices;

    /**
     * Construtor privado para criar um PolygonCollider com os vértices originais.
     * @param t Transform associada ao colisor
     * @param verts Lista de vértices do polígono
     */
    private PolygonCollider(Transform t, List<Point.Double> verts) {
        super(t);
        this.originalVertices = new ArrayList<>(verts);
        this.transformedVertices = new ArrayList<>();
    }

    /**
     * Método estático para criar um PolygonCollider com os vértices originais e aplicar a transformação inicial.
     * @param t Transform associada ao colisor
     * @param verts Lista de vértices do polígono
     * @return Uma instância de PolygonCollider com os vértices transformados
     */
    public static PolygonCollider create(Transform t, List<Point.Double> verts) {
        PolygonCollider c = new PolygonCollider(t, verts);
        c.adjustToTransform();
        return c;
    }

    /**
     * Método estático para criar um PolygonCollider com os vértices originais e aplicar a transformação inicial.
     * @param t Transform associada ao colisor
     * @param verts Lista de vértices do polígono
     * @return Uma instância de PolygonCollider com os vértices transformados
     */
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
    /**
     * Aplica a transformação atual ao colisor, ajustando os vértices transformados.
     */
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
    /**
     * Retorna o centroide do colisor, que é o ponto médio dos vértices transformados.
     * @return Centroide como Point
     */
    public Point centroid() {
        return new Point((int) transform.posX(), (int) transform.posY());
    }

    @Override
    /**
     * Retorna uma representação em string dos vértices transformados do polígono.
     * @return String representando os vértices transformados
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Point.Double p : transformedVertices) {
            sb.append(String.format("(%.2f,%.2f) ", p.x, p.y));
        }
        return sb.toString().trim();
    }

    // Métodos de colisão
    @Override
    /**
     * Verifica se este colisor colide com outro colisor genérico.
     * @param other Outro colisor a ser verificado
     * @return true se houver colisão, false caso contrário
     */
    public boolean collidesWith(ICollider other) {
        return other.collidesWithPolygon(this);
    }

    @Override
    /**
     * Verifica se este colisor colide com um CircleCollider.
     * @param circle CircleCollider a ser verificado
     * @return true se houver colisão, false caso contrário
     */
    public boolean collidesWithCircle(CircleCollider circle) {
        // Lógica de colisão polígono-círculo não implementada
        return false;
    }

    @Override
    /**
     * Verifica se este colisor colide com um PolygonCollider.
     * @param polygon PolygonCollider a ser verificado
     * @return true se houver colisão, false caso contrário
     */
    public boolean collidesWithPolygon(PolygonCollider polygon) {
        // Lógica de colisão polígono-polígono não implementada
        return false;
    }
}
