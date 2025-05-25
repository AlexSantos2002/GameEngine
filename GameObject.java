/**
 * 
 */
public class GameObject implements IGameObject {
    private String name;
    private Transform transform;
    private Collider collider;
    private IBehaviour behaviour;

    /**
     * Construtor para criar um GameObject com nome, transform, collider e comportamento.
     *
     * @param name      Nome do objeto
     * @param transform Transform do objeto
     * @param collider  Collider do objeto
     * @param behaviour Comportamento do objeto
     */
    public GameObject(String name, Transform transform, Collider collider, IBehaviour behaviour) {
        this.name = name;
        this.transform = transform;
        this.collider = collider;
        this.behaviour = behaviour;
    }

    @Override
    /**
     * Obtém o nome do objeto.
     * @return Nome do objeto
     */
    public String name() {
        return name;
    }

    @Override
    /**
     * Obtém o transform do objeto.
     * @return Transform do objeto
     */
    public ITransform transform() {
        return transform;
    }

    @Override
    /**
     * Obtém o collider do objeto.
     * @return Collider do objeto
     */
    public ICollider collider() {
        return collider;
    }

    /**
     * Obtém o comportamento do objeto.
     * @return Comportamento do objeto
     */
    public IBehaviour behaviour() {
        return behaviour;
    }

    @Override
    /**
     * Define o comportamento do objeto.
     * @param behaviour Comportamento a ser definido
     */
    public String toString() {
        return name + "\n" + transform.toString() + "\n" + collider.toString();
    }
}