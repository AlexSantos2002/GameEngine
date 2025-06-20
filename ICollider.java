import java.awt.Point;

/**
 * Interface que representa um colisor de GameObject.
 * Deve garantir acesso ao centroide do colisor, necessário para manter alinhamento com a Transform do GameObject.
 * @author Alexandre Santos(71522), Nurio Pereira (72788)
 * @version 1.1 10/04/2025
 * @inv O centroide do colisor deve coincidir com a posição da Transform associada.
 */
public interface ICollider {
    /**
     * Retorna o centroide (centro geométrico) do colisor.
     * @return centroide como Point
     */
    Point centroid();

    /**
     * Aplica a transformação atual (posição, rotação e escala) ao colisor.
     */
    void adjustToTransform();

    /**
     * Verifica se o colisor colide com outro colisor.
     * @param other outro colisor a ser verificado
     * @return true se houver colisão, false caso contrário
     */
    boolean collidesWith(ICollider other);

    /**
     * Verifica se o colisor colide com um ponto.
     * @param point ponto a ser verificado
     * @return true se houver colisão, false caso contrário
     */
    boolean collidesWithCircle(CircleCollider circle);

    /**
     * Verifica se o colisor colide com um polígono.
     * @param polygon polígono a ser verificado
     * @return true se houver colisão, false caso contrário
     */
    boolean collidesWithPolygon(PolygonCollider polygon);
}
