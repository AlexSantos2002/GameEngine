import java.awt.Point;

/**
 * Colisor circular que se ajusta à posição, rotação e escala da Transform associada.
 * @author Alexandre Santos(71522), Nurio Pereira (72788)
 * @version 1.0 25/03/2025
 * @inv O centro do círculo é sempre a posição definida pela Transform associada. O raio é escalado de acordo com o fator de escala.
 */

public class CircleCollider extends Collider {
    private double x, y, r;

    // Construtor privado para forçar o uso de factory method
    private CircleCollider(Transform t, double cx, double cy, double r) {
        super(t);
        this.x = cx;
        this.y = cy;
        this.r = r;
    }

    // Factory method para criação segura
    public static CircleCollider create(Transform t, double cx, double cy, double r) {
        CircleCollider c = new CircleCollider(t, cx, cy, r);
        c.adjustToTransform(); // só depois da construção
        return c;
    }

    @Override
    public void adjustToTransform() {
        this.r *= transform.scale();
        this.x = transform.posX();
        this.y = transform.posY();
    }

    @Override
    public Point centroid() {
        return new Point((int) x, (int) y);
    }

    @Override
    public String toString() {
        return String.format("(%.2f,%.2f) %.2f", x, y, r);
    }
}
