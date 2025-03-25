/**
 * Classe abstrata para colisores que guarda referência à Transform do GameObject a que pertence.
 * Fornece a estrutura comum a todos os tipos de colisor.
 * Subclasses devem implementar os métodos de transformação e representação textual.
 * 
 * @author 
 * Alexandre Santos (71522), Nurio Pereira (72788)
 * @version 1.0 25/03/2025
 * 
 * @inv Todos os colisores devem estar centrados na Transform do GameObject. 
 * Métodos de transformação devem ser invocados após cada alteração de estado.
 */
public abstract class Collider implements ICollider {
    protected Transform transform;

    /**
     * Construtor da classe Collider.
     * @param t Transform associada ao colisor (posição, rotação e escala).
     */
    public Collider(Transform t) {
        this.transform = t;
    }

    /**
     * Aplica a Transform associada ao colisor, ajustando posição, rotação e escala
     * com base nos dados atuais do GameObject.
     */
    public abstract void adjustToTransform();

    /**
     * Representação textual do colisor.
     * @return String formatada com os dados do colisor (pode variar consoante o tipo).
     */
    public abstract String toString();
}
