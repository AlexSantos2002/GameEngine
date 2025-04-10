/**
 * Classe que representa o motor de jogo, responsavel por gerir GameObjects e simular colisoes.
 * Permite adicionar/remover objetos e simular movimento e colisoes durante multiplas frames.
 * @author
 * Alexandre Santos (71522), Nurio Pereira (72788)
 * @version 10/04/2025
 * @inv Todos os GameObjects tem colisores centrados na sua posicao. Objetos em diferentes layers nao colidem.
 */
import java.awt.Point;
import java.util.*;

public class GameEngine {
    private final List<GameObject> objects = new ArrayList<>();

    /**
     * Adiciona um novo GameObject a lista.
     * @param go GameObject a ser adicionado
     */
    public void add(GameObject go) {
        objects.add(go);
    }

    /**
     * Remove um GameObject da lista.
     * @param go GameObject a ser removido
     */
    public void destroy(GameObject go) {
        objects.remove(go);
    }

    /**
     * Simula a engine durante um numero de frames, atualizando cada GameObject e registando colisoes.
     * @param frames numero de frames a simular
     * @param velocities mapa de GameObjects para vetores de movimento (dx, dy, dLayer, dAngle, dScale)
     * @return mapa com cada GameObject e respetivos objetos em colisao
     */
    public Map<GameObject, List<GameObject>> simulate(int frames, Map<GameObject, double[]> velocities) {
        for (int i = 0; i < frames; i++) {
            for (GameObject go : objects) {
                double[] v = velocities.get(go);
                go.transform().move(new Point((int) v[0], (int) v[1]), (int) v[2]);
                go.transform().rotate(v[3]);
                go.transform().scale(v[4]);
                go.collider().adjustToTransform();
            }
        }

        Map<GameObject, List<GameObject>> collisions = new LinkedHashMap<>();
        for (int i = 0; i < objects.size(); i++) {
            GameObject a = objects.get(i);
            for (int j = i + 1; j < objects.size(); j++) {
                GameObject b = objects.get(j);
                if (a.transform().layer() != b.transform().layer()) continue;
                if (detectCollision(a.collider(), b.collider())) {
                    collisions.computeIfAbsent(a, k -> new ArrayList<>()).add(b);
                    collisions.computeIfAbsent(b, k -> new ArrayList<>()).add(a);
                }
            }
        }
        return collisions;
    }

    /**
     * Detecao simplificada de colisao por sobreposicao de centroides (exemplo: bounding box ou centro).
     * Para este stub, considera-se colisao se centroides forem coincidentes.
     * @param a primeiro colisor
     * @param b segundo colisor
     * @return true se colidirem
     */
    private boolean detectCollision(ICollider a, ICollider b) {
        Point pa = a.centroid();
        Point pb = b.centroid();
        return pa.equals(pb);
    }

    /**
     * Metodo auxiliar para simular e imprimir colisoes como pedido pelo enunciado.
     * @param args argumentos ignorados
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int frames = Integer.parseInt(sc.nextLine());
        int n = Integer.parseInt(sc.nextLine());
        GameEngine engine = new GameEngine();
        Map<GameObject, double[]> velocities = new LinkedHashMap<>();

        for (int i = 0; i < n; i++) {
            String name = sc.nextLine();
            String[] tData = sc.nextLine().split(" ");
            Transform t = new Transform(
                Double.parseDouble(tData[0]), Double.parseDouble(tData[1]),
                Integer.parseInt(tData[2]), Double.parseDouble(tData[3]), Double.parseDouble(tData[4])
            );

            String[] cData = sc.nextLine().split(" ");
            Collider c;
            if (cData.length == 3) {
                c = CircleCollider.create(t, Double.parseDouble(cData[0]), Double.parseDouble(cData[1]), Double.parseDouble(cData[2]));
            } else {
                List<Point.Double> verts = new ArrayList<>();
                for (int j = 0; j < cData.length; j += 2)
                    verts.add(new Point.Double(Double.parseDouble(cData[j]), Double.parseDouble(cData[j+1])));
                c = PolygonCollider.create(t, verts);
            }

            GameObject go = new GameObject(name, t, c);
            engine.add(go);

            String[] vData = sc.nextLine().split(" ");
            velocities.put(go, new double[]{
                Double.parseDouble(vData[0]), Double.parseDouble(vData[1]), Integer.parseInt(vData[2]),
                Double.parseDouble(vData[3]), Double.parseDouble(vData[4])
            });
        }

        Map<GameObject, List<GameObject>> result = engine.simulate(frames, velocities);
        for (GameObject go : engine.objects) {
            if (result.containsKey(go)) {
                System.out.print(go.name());
                result.get(go).stream()
                    .sorted(Comparator.comparing(g -> engine.objects.indexOf(g)))
                    .forEach(g -> System.out.print(" " + g.name()));
                System.out.println();
            }
        }
    }
}
