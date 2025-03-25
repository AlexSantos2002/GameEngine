 
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