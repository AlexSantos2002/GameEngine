/**
 * Interface que representa um GameObject no sistema.
 * Expõe os métodos essenciais para aceder ao nome, Transform e Collider do objeto de jogo.
 * @author Alexandre Santos(71522), Nurio Pereira (72788)
 * @version 1.0 25/03/2025
 * @inv O centroide do Collider deve coincidir com a posição da Transform.
 */
public interface IGameObject { 
 
    /** 
     * @return the name of the GameObject 
     */ 
    String name(); 
 
    /** 
     * @return the Transform of the GameObject 
     */ 
    ITransform transform(); 
 
    /** 
     * @return the Collider of the GameObject with its centroid at this.transform().position() 
     */ 
    ICollider collider(); 

    String toString();
}