public class GameObject implements IGameObject {
    private String name;
    private Transform transform;
    private Collider collider;
    private IBehaviour behaviour;

    public GameObject(String name, Transform transform, Collider collider, IBehaviour behaviour) {
        this.name = name;
        this.transform = transform;
        this.collider = collider;
        this.behaviour = behaviour;
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

    public IBehaviour behaviour() {
        return behaviour;
    }

    @Override
    public String toString() {
        return name + "\n" + transform.toString() + "\n" + collider.toString();
    }
}