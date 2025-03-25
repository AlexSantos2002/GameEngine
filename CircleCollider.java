import java.awt.Point;

/**
 * Colisor circular que se ajusta à posição, rotação e escala da Transform associada.
 * @author Alexandre Santos(71522), Nurio Pereira (72788)
 * @version 1.0 25/03/2025
 * @inv O centro do círculo é sempre a posição definida pela Transform associada. O raio é escalado de acordo com o fator de escala.
 */
public class CircleCollider extends Collider {
    private double x, y, r;

    /**
     * Construtor privado para forçar o uso de factory method
     * @param t Transform associada
     * @param cx coordenada x do centro original
     * @param cy coordenada y do centro original
     * @param r raio original do círculo
     */
    private CircleCollider(Transform t, double cx, double cy, double r) {
        super(t);
        this.x = cx;
        this.y = cy;
        this.r = r;
    }

    /**
     * Método de criação de CircleCollider garantindo transformação inicial
     * @param t Transform associada
     * @param cx coordenada x do centro
     * @param cy coordenada y do centro
     * @param r raio
     * @return nova instância de CircleCollider com transformação aplicada
     */
    public static CircleCollider create(Transform t, double cx, double cy, double r) {
        CircleCollider c = new CircleCollider(t, cx, cy, r);
        c.adjustToTransform();
        return c;
    }

    /**
     * Aplica a transformação atual ao colisor circular
     */
    @Override
    public void adjustToTransform() {
        this.r *= transform.scale();
        this.x = transform.posX();
        this.y = transform.posY();
    }

    /**
     * Retorna o centro atual do colisor circular
     * @return Ponto representando o centro
     */
    @Override
    public Point centroid() {
        return new Point((int) x, (int) y);
    }

    /**
     * Representação textual do colisor
     * @return String no formato "(x,y) raio"
     */
    @Override
    public String toString() {
        return String.format("(%.2f,%.2f) %.2f", x, y, r);
    }
}
