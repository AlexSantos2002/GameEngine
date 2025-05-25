import java.awt.Point;
/**
 * Colisor circular que se ajusta à posição, rotação e escala da Transform associada.
 * @author Alexandre Santos(71522), Nurio Pereira (72788)
 * @version 1.2 24/05/2025
 * @inv O centro do círculo é sempre a posição definida pela Transform associada. O raio é escalado de acordo com o fator de escala.
 */
public class CircleCollider extends Collider {
    /**
     * Centro do círculo (coordenadas x e y) e raio.
     */
    private double x, y, r;

    /**
     * Construtor privado para criar um CircleCollider com centro e raio especificados.
     * @param t Transform associada ao colisor
     * @param cx Coordenada x do centro do círculo
     * @param cy Coordenada y do centro do círculo
     * @param r Raio do círculo
     */
    private CircleCollider(Transform t, double cx, double cy, double r) {
        super(t);
        this.x = cx;
        this.y = cy;
        this.r = r;
    }

    /**
     * Método estático para criar um CircleCollider com centro e raio especificados,
     * aplicando a transformação inicial.
     * @param t Transform associada ao colisor
     * @param cx Coordenada x do centro do círculo
     * @param cy Coordenada y do centro do círculo
     * @param r Raio do círculo
     * @return Uma instância de CircleCollider com a transformação aplicada
     */
    public static CircleCollider create(Transform t, double cx, double cy, double r) {
        CircleCollider c = new CircleCollider(t, cx, cy, r);
        c.adjustToTransform();
        return c;
    }

    @Override
    /**
     * Aplica a transformação associada ao colisor, ajustando a posição e o raio
     */
    public void adjustToTransform() {
        this.r *= transform.scale();
        this.x = transform.posX();
        this.y = transform.posY();
    }

    @Override
    /**
     * Retorna o centroide do círculo como um ponto.
     * @return Centroide do círculo como Point
     */
    public Point centroid() {
        return new Point((int) x, (int) y);
    }

    /**
     * Retorna o raio do círculo.
     * @return Raio do círculo
     */
    public double getRadius() {
        return r;
    }

    @Override
    /**
     * Representação textual do colisor circular.
     * @return String formatada com as coordenadas do centro e o raio
     */
    public String toString() {
        return String.format("(%.2f,%.2f) %.2f", x, y, r);
    }

    // Métodos de colisão
    @Override
    /**
     * Verifica se este colisor circular colide com outro colisor.
     * @param other Outro colisor a ser verificado
     * @return true se houver colisão, false caso contrário
     */
    public boolean collidesWith(ICollider other) {
        return other.collidesWithCircle(this);
    }

    @Override
    /**
     * Verifica se este colisor circular colide com outro círculo.
     * @param circle Outro círculo a ser verificado
     * @return true se houver colisão, false caso contrário
     */
    public boolean collidesWithCircle(CircleCollider circle) {
        double dx = this.x - circle.x;
        double dy = this.y - circle.y;
        double distanceSq = dx * dx + dy * dy;
        double radiusSum = this.r + circle.r;
        return distanceSq <= radiusSum * radiusSum;
    }

    @Override
    /**
     * Verifica se este colisor circular colide com um PolygonCollider.
     * @param polygon PolygonCollider a ser verificado
     * @return true se houver colisão, false caso contrário
     */
    public boolean collidesWithPolygon(PolygonCollider polygon) {
        // Lógica de colisão círculo-polígono (não implementada aqui)
        return false;
    }
}
