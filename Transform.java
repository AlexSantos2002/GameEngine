import java.awt.Point;

/**
 * Classe que representa a transformação de um GameObject (posição, rotação e escala).
 * @author 
 * @version 25/03/2025
 * @inv A rotação está sempre no intervalo [0, 360)
 */
public class Transform implements ITransform {
    private double x, y, angle, scale;
    private int layer;

    public Transform(double x, double y, int layer, double angle, double scale) {
        this.x = x;
        this.y = y;
        this.layer = layer;
        this.angle = angle % 360;
        this.scale = scale;
    }

    @Override
    public void move(Point dPos, int dlayer) {
        this.x += dPos.getX();
        this.y += dPos.getY();
        this.layer += dlayer;
    }

    @Override
    public void rotate(double dTheta) {
        this.angle = (this.angle + dTheta) % 360;
        if (angle < 0) angle += 360;
    }

    @Override
    public void scale(double dScale) {
        this.scale += dScale;
    }

    @Override
    public Point position() {
        return new Point((int)x, (int)y);
    }

    public double posX() { return x; }
    public double posY() { return y; }

    @Override
    public int layer() {
        return layer;
    }

    @Override
    public double angle() {
        return angle;
    }

    @Override
    public double scale() {
        return scale;
    }

    @Override
    public String toString() {
        return String.format("(%.2f,%.2f) %d %.2f %.2f", x, y, layer, angle, scale);
    }
}
