import java.awt.Point;

public class CircleCollider extends Collider {
    private double x, y, r;

    private CircleCollider(Transform t, double cx, double cy, double r) {
        super(t);
        this.x = cx;
        this.y = cy;
        this.r = r;
    }

    public static CircleCollider create(Transform t, double cx, double cy, double r) {
        CircleCollider c = new CircleCollider(t, cx, cy, r);
        c.adjustToTransform();
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

    public double getRadius() {
        return r;
    }

    @Override
    public String toString() {
        return String.format("(%.2f,%.2f) %.2f", x, y, r);
    }

    // Métodos de colisão
    @Override
    public boolean collidesWith(ICollider other) {
        return other.collidesWithCircle(this);
    }

    @Override
    public boolean collidesWithCircle(CircleCollider circle) {
        double dx = this.x - circle.x;
        double dy = this.y - circle.y;
        double distanceSq = dx * dx + dy * dy;
        double radiusSum = this.r + circle.r;
        return distanceSq <= radiusSum * radiusSum;
    }

    @Override
    public boolean collidesWithPolygon(PolygonCollider polygon) {
        // Lógica de colisão círculo-polígono (não implementada aqui)
        return false;
    }
}
