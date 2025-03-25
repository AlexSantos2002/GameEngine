import java.awt.Point;

/**
 * Interface que define as transformações básicas de um GameObject (posição, rotação, escala).
 */
public interface ITransform {
    /**
     * Move esta Transform por dPos.x(), dPos.y() e dlayer.
     * @param dPos  deslocamento 2D
     * @param dlayer deslocamento da layer
     */
    void move(Point dPos, int dlayer);

    /**
     * Roda esta Transform pelo ângulo indicado (anti-horário).
     * @param dTheta ângulo a adicionar
     */
    void rotate(double dTheta);

    /**
     * Aumenta a escala atual por dScale.
     * @param dScale incremento de escala
     */
    void scale(double dScale);

    /**
     * @return posição (x,y)
     */
    Point position();

    /**
     * @return layer atual
     */
    int layer();

    /**
     * @return ângulo atual (0 <= angle < 360)
     */
    double angle();

    /**
     * @return escala atual
     */
    double scale();
}
