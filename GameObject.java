/**
 * Classe que representa um objeto no jogo, com Transform e Collider.
 */
public class GameObject implements IGameObject {
    private String name;
    private Transform transform;
    private Collider collider;

    public GameObject(String name, Transform transform, Collider collider) {
        this.name = name;
        this.transform = transform;
        this.collider = collider;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public ITransform transform() {
        return transform;
    }

    @Override
    public ICollider collider() {
        return collider;
    }

    @Override
    public String toString() {
        return name + "\n" + transform.toString() + "\n" + collider.toString();
    }
}
