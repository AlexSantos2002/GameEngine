import java.awt.Point;

/**
 * Interface que define as transformações básicas de um GameObject (posição, rotação, escala).
 * Representa a forma como um objeto é posicionado e orientado no espaço 2D do jogo.
 * @author Alexandre Santos(71522), Nurio Pereira (72788)
 * @version 1.0 25/03/2025
 * @inv A rotação deve estar no intervalo [0, 360). A escala deve ser positiva. A posição é expressa em coordenadas inteiras.
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
    double posX();
    double posY();
}
