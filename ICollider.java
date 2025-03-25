import java.awt.Point;

/**
 * Interface que representa um colisor de GameObject.
 */
public interface ICollider {
    /**
     * Retorna o centroide (centro geométrico) do colisor.
     * @return centroide como Point
     */
    Point centroid();
}
