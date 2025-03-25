import java.awt.Point;

/**
 * Interface que representa um colisor de GameObject.
 * Deve garantir acesso ao centroide do colisor, necessário para manter alinhamento com a Transform do GameObject.
 * @author Alexandre Santos(71522), Nurio Pereira (72788)
 * @version 1.0 25/03/2025
 * @inv O centroide do colisor deve coincidir com a posição da Transform associada.
 */

public interface ICollider {
    /**
     * Retorna o centroide (centro geométrico) do colisor.
     * @return centroide como Point
     */
    Point centroid();
}
