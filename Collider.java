/**
 * Classe abstrata para colisores que guarda referência à Transform do GameObject a que pertence.
 * Fornece a estrutura comum a todos os tipos de colisor.
 * @author Alexandre Santos(71522), Nurio Pereira (72788)
 * @version 1.0 25/03/2025
 * @inv Todos os colisores devem estar centrados na Transform do GameObject. Métodos de transformação devem ser invocados após cada alteração de estado.
 */

public abstract class Collider implements ICollider {
    protected Transform transform;

    public Collider(Transform t) {
        this.transform = t;
    }

    public abstract void adjustToTransform();
    public abstract String toString();
}
