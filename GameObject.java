/**
 * Classe que representa um objeto no jogo, com nome, uma Transform e um Collider associado.
 * É a estrutura principal que combina comportamento posicional com detecção de colisão.
 * @author Alexandre Santos(71522), Nurio Pereira (72788)
 * @version 1.0 25/03/2025
 * @inv A posição do GameObject coincide com o centroide do colisor. A Transform controla posição, rotação e escala do objeto.
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
