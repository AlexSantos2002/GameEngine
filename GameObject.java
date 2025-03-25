/**
 * Classe que representa um objeto no jogo, com nome, uma Transform e um Collider associado.
 * É a estrutura principal que combina comportamento posicional com detecção de colisão.
 * Cada GameObject tem uma posição, orientação e escala definida pela sua Transform, 
 * e uma forma definida por um Collider, que deve manter o centroide na mesma posição.
 * 
 * @author 
 * Alexandre Santos (71522), Nurio Pereira (72788)
 * @version 1.0 25/03/2025
 * 
 * @inv A posição do GameObject coincide com o centroide do colisor. 
 * A Transform controla posição, rotação e escala do objeto.
 */
public class GameObject implements IGameObject {
    private String name;
    private Transform transform;
    private Collider collider;

    /**
     * Construtor da classe GameObject.
     * @param name nome do objeto
     * @param transform instância da Transform associada (posição, rotação e escala)
     * @param collider colisor associado ao GameObject, centrado na Transform
     */
    public GameObject(String name, Transform transform, Collider collider) {
        this.name = name;
        this.transform = transform;
        this.collider = collider;
    }

    /**
     * Retorna o nome do GameObject.
     * @return nome do objeto
     */
    @Override
    public String name() {
        return name;
    }

    /**
     * Retorna a Transform associada a este GameObject.
     * @return objeto Transform com informação de posição, rotação e escala
     */
    @Override
    public ITransform transform() {
        return transform;
    }

    /**
     * Retorna o Collider associado a este GameObject.
     * @return objeto Collider com forma geométrica do objeto
     */
    @Override
    public ICollider collider() {
        return collider;
    }

    /**
     * Representação textual completa do GameObject.
     * @return string com nome, transformações e colisor formatado
     */
    @Override
    public String toString() {
        return name + "\n" + transform.toString() + "\n" + collider.toString();
    }
}
