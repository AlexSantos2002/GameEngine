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

    /**
     * Construtor privado da classe PolygonCollider.
     * @param t Transform associada ao GameObject
     * @param verts Lista de vértices originais do polígono (sentido horário)
     */
    private PolygonCollider(Transform t, List<Point.Double> verts) {
        super(t);
        this.originalVertices = new ArrayList<>(verts);
        this.transformedVertices = new ArrayList<>();
    }

    /**
     * Método de criação do PolygonCollider com transformação inicial aplicada.
     * @param t Transform associada
     * @param verts lista de vértices originais (sentido horário)
     * @return nova instância de PolygonCollider com os vértices transformados
     */
    public static PolygonCollider create(Transform t, List<Point.Double> verts) {
        PolygonCollider c = new PolygonCollider(t, verts);
        c.adjustToTransform();
        return c;
    }

    /**
     * Calcula o centroide do polígono original com base na fórmula do centroide de polígono irregular.
     * @param verts lista de vértices a partir dos quais calcular o centroide
     * @return ponto que representa o centro geométrico (centroide) do polígono
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

    /**
     * Aplica as transformações de rotação, escala e deslocamento aos vértices originais,
     * armazenando os vértices resultantes em `transformedVertices`.
     */
    @Override
    public void adjustToTransform() {
        Point.Double centroid = computeCentroid(originalVertices);
        List<Point.Double> moved = new ArrayList<>(); // tirar as imutabilidades

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

    /**
     * Retorna o centroide atual do polígono transformado, que coincide com a posição da Transform.
     * @return ponto (x,y) representando o centro atual do polígono
     */
    @Override
    public Point centroid() {
        return new Point((int) transform.posX(), (int) transform.posY());
    }

    /**
     * Representação textual dos vértices transformados do polígono.
     * @return string com coordenadas formatadas dos vértices no formato (x,y)
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Point.Double p : transformedVertices) {
            sb.append(String.format("(%.2f,%.2f) ", p.x, p.y));
        }
        return sb.toString().trim();
    }
}
