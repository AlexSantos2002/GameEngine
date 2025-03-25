import java.awt.Point;

/**
 * Colisor circular que se ajusta à posição, escala da Transform.
 */
public class CircleCollider extends Collider {
    private double x, y, r;

    public CircleCollider(Transform t, double cx, double cy, double r) {
        super(t);
        this.x = cx;
        this.y = cy;
        this.r = r;
        adjustToTransform();
    }

    @Override
    public void adjustToTransform() {
        this.r *= transform.scale();
        this.x = transform.posX();
        this.y = transform.posY();
    }

    @Override
    public Point centroid() {
        return new Point((int)x, (int)y);
    }

    @Override
    public String toString() {
        return String.format("(%.2f,%.2f) %.2f", x, y, r);
    }
}
