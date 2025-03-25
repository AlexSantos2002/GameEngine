/**
 * Classe abstrata para colisores que guarda referência à Transform.
 */
public abstract class Collider implements ICollider {
    protected Transform transform;

    public Collider(Transform t) {
        this.transform = t;
    }

    public abstract void adjustToTransform();
    public abstract String toString();
}
