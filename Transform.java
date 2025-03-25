import java.awt.Point;

/**
 * Classe que representa a transformação de um GameObject (posição, rotação e escala).
 * Responsável por armazenar e aplicar translações, rotações e alterações de escala
 * sobre os objetos do jogo. Inclui também a camada (layer) em que o objeto se encontra.
 * 
 * @author Alexandre Santos(71522), Nurio Pereira (72788)
 * @version 25/03/2025
 * 
 * @inv A rotação está sempre no intervalo [0, 360)
 */
public class Transform implements ITransform {
    private double x, y, angle, scale;
    private int layer;

    /**
     * Construtor da classe Transform.
     * @param x coordenada x da posição
     * @param y coordenada y da posição
     * @param layer camada do objeto
     * @param angle rotação inicial em graus
     * @param scale fator de escala inicial
     */
    public Transform(double x, double y, int layer, double angle, double scale) {
        this.x = x;
        this.y = y;
        this.layer = layer;
        this.angle = angle % 360;
        this.scale = scale;
    }

    /**
     * Aplica uma translação à Transform e altera a camada.
     * @param dPos deslocamento em x e y
     * @param dlayer incremento da camada
     */
    @Override
    public void move(Point dPos, int dlayer) {
        this.x += dPos.getX();
        this.y += dPos.getY();
        this.layer += dlayer;
    }

    /**
     * Aplica uma rotação incremental à Transform.
     * @param dTheta ângulo a adicionar (em graus)
     */
    @Override
    public void rotate(double dTheta) {
        this.angle = (this.angle + dTheta) % 360;
        if (angle < 0) angle += 360;
    }

    /**
     * Altera o fator de escala da Transform.
     * @param dScale incremento de escala
     */
    @Override
    public void scale(double dScale) {
        this.scale += dScale;
    }

    /**
     * Retorna a posição atual da Transform em coordenadas inteiras.
     * @return Ponto com as coordenadas atuais (x, y)
     */
    @Override
    public Point position() {
        return new Point((int)x, (int)y);
    }

    /**
     * Retorna o valor da coordenada x.
     * @return valor de x (posição horizontal)
     */
    public double posX() { return x; }

    /**
     * Retorna o valor da coordenada y.
     * @return valor de y (posição vertical)
     */
    public double posY() { return y; }

    /**
     * Retorna o número da camada atual.
     * @return valor inteiro da layer
     */
    @Override
    public int layer() {
        return layer;
    }

    /**
     * Retorna o ângulo atual de rotação da Transform.
     * @return valor do ângulo entre 0 e 360
     */
    @Override
    public double angle() {
        return angle;
    }

    /**
     * Retorna o fator de escala atual da Transform.
     * @return valor da escala
     */
    @Override
    public double scale() {
        return scale;
    }

    /**
     * Representação textual da Transform.
     * @return String formatada com (x, y) layer ângulo escala
     */
    @Override
    public String toString() {
        return String.format("(%.2f,%.2f) %d %.2f %.2f", x, y, layer, angle, scale);
    }
}
