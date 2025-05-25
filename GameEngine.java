import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.awt.Point;

/**
 * Motor principal do jogo que gere o ciclo de vida dos GameObjects.
 * Responsável por adicionar, ativar/desativar, atualizar e destruir objetos,
 * bem como detetar colisões e simular frames de movimento no jogo.
 * Implementado como Singleton, permitindo uma única instância acessível globalmente.
 * 
 * @author 
 * Alexandre Santos (71522), Nurio Pereira (72788)
 * @version 24/05/2025
 * 
 * @inv A instância de GameEngine é única (Singleton).
 * Todos os GameObjects ativos são atualizados a cada frame.
 */

public class GameEngine {

    private static final GameEngine instance = new GameEngine();

    /**
     * Construtor privado para garantir que a classe é um singleton.
     * @return Instância única do GameEngine
     */
    public static GameEngine getInstance() {
        return instance;
    }

    /**
     * Lista de objetos do jogo.
     * Contém todos os objetos, independentemente de estarem ativos ou não.
     */
    private final List<GameObject> objects = new ArrayList<>();

    /**
     * Lista de objetos ativos no jogo.
     */
    private final List<GameObject> enabledObjects = new ArrayList<>();

    /**
     * Adiciona um objeto ao jogo.
     * @param go
     */
    public void add(GameObject go) {
        objects.add(go);
        enabledObjects.add(go);
        go.behaviour().onInit();
    }

    /**
     * Remove um objeto do jogo.
     * Se o objeto estiver ativo, ele será desativado antes de ser removido.
     * @param go Objeto a ser removido
     */
    public void destroy(GameObject go) {
        objects.remove(go);
        enabledObjects.remove(go);
        go.behaviour().onDestroy();
    }

    /**
     * Ativa um objeto do jogo.
     * Se o objeto já estiver ativo, não faz nada.
     * @param go Objeto a ser ativado
     */
    public void enable(GameObject go) {
        if (!enabledObjects.contains(go)) {
            enabledObjects.add(go);
            go.behaviour().onEnabled();
        }
    }

    /**
     * Desativa um objeto do jogo.
     * @param go Objeto a ser desativado
     */
    public void disable(GameObject go) {
        if (enabledObjects.remove(go)) {
            go.behaviour().onDisabled();
        }
    }

    /**
     * Obtém todos os objetos do jogo.
     * @return Lista de objetos do jogo
     */
    public List<GameObject> getEnabled() {
        return new ArrayList<>(enabledObjects);
    }

    /**
     * Simula o movimento dos objetos do jogo por um número de frames.
     * @param frames
     * @param velocities
     * @return
     */
    public Map<GameObject, List<GameObject>> simulate(int frames, Map<GameObject, double[]> velocities) {
        for (int i = 0; i < frames; i++) {
            for (GameObject go : enabledObjects) {
                double[] v = velocities.get(go);
                go.transform().move(new Point((int) v[0], (int) v[1]), (int) v[2]);
                go.transform().rotate(v[3]);
                go.transform().scale(v[4]);
                go.collider().adjustToTransform();
                go.behaviour().onUpdate();
            }
        }

        Map<GameObject, List<GameObject>> collisions = new LinkedHashMap<>();
        for (int i = 0; i < enabledObjects.size(); i++) {
            GameObject a = enabledObjects.get(i);
            for (int j = i + 1; j < enabledObjects.size(); j++) {
                GameObject b = enabledObjects.get(j);
                if (a.transform().layer() != b.transform().layer()) continue;
                if (detectCollision(a.collider(), b.collider())) {
                    collisions.computeIfAbsent(a, _ -> new ArrayList<>()).add(b);
                    collisions.computeIfAbsent(b, _ -> new ArrayList<>()).add(a);
                }
            }
        }

        for (Map.Entry<GameObject, List<GameObject>> entry : collisions.entrySet()) {
            for (GameObject other : entry.getValue()) {
                entry.getKey().behaviour().onCollision(other);
            }
        }

        return collisions;
    }

    /**
     * Deteta colisão entre dois colliders.
     * Atualmente apenas utiliza do CircleCollider
        * @param a Primeiro collider
        * @param b Segundo collider
        * @return Verdadeiro se houver colisão, falso caso contrário
    **/
public boolean detectCollision(ICollider a, ICollider b) {
    if (a instanceof CircleCollider && b instanceof CircleCollider) {
        CircleCollider ca = (CircleCollider) a;
        CircleCollider cb = (CircleCollider) b;

        double dx = ca.centroid().x - cb.centroid().x;
        double dy = ca.centroid().y - cb.centroid().y;
        double distanceSq = dx * dx + dy * dy;
        double radiusSum = ca.getRadius() + cb.getRadius();

        return distanceSq <= radiusSum * radiusSum;
    }
    return false;
}
}
