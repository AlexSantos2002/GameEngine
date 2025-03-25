import java.awt.Point;

/**
 * Colisor circular que se ajusta à posição, rotação e escala da Transform associada.
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
