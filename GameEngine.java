import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.*;

/**
 * Classe que representa o motor de jogo, responsavel por gerir GameObjects e simular colisoes.
 * Permite adicionar/remover objetos e simular movimento e colisoes durante multiplas frames.
 * @author
 * Alexandre Santos (71522), Nurio Pereira (72788)
 * @version 10/04/2025
 * @inv Todos os GameObjects tem colisores centrados na sua posicao. Objetos em diferentes layers nao colidem.
 */
public class GameEngine {
    private final List<GameObject> objects = new ArrayList<>();

    public void add(GameObject go) {
        objects.add(go);
    }

    public void destroy(GameObject go) {
        objects.remove(go);
    }

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

    private boolean detectCollision(ICollider a, ICollider b) {
        if (a instanceof CircleCollider && b instanceof CircleCollider) {
            CircleCollider ca = (CircleCollider) a;
            CircleCollider cb = (CircleCollider) b;
            double dx = ca.centroid().x - cb.centroid().x;
            double dy = ca.centroid().y - cb.centroid().y;
            double distanceSq = dx * dx + dy * dy;
            double radiusSum = ca.getRadius() + cb.getRadius();
            return distanceSq <= radiusSum * radiusSum;
        }

        if (a instanceof CircleCollider && b instanceof PolygonCollider) {
            return circlePolygonCollision((CircleCollider) a, (PolygonCollider) b);
        }
        if (b instanceof CircleCollider && a instanceof PolygonCollider) {
            return circlePolygonCollision((CircleCollider) b, (PolygonCollider) a);
        }

        if (a instanceof PolygonCollider && b instanceof PolygonCollider) {
            return polygonPolygonCollision((PolygonCollider) a, (PolygonCollider) b);
        }

        return false;
    }

    private boolean circlePolygonCollision(CircleCollider circle, PolygonCollider poly) {
        Point center = circle.centroid();
        double radius = circle.getRadius();
        String[] verts = poly.toString().split("\\) ");
        List<Point.Double> points = new ArrayList<>();

        for (String v : verts) {
            v = v.replace("(", "").replace(")", "");
            String[] coords = v.split(",");
            double x = Double.parseDouble(coords[0]);
            double y = Double.parseDouble(coords[1]);
            points.add(new Point.Double(x, y));
        }

        // Check vertex inside circle
        for (Point.Double p : points) {
            double dx = p.x - center.getX();
            double dy = p.y - center.getY();
            if (dx * dx + dy * dy <= radius * radius)
                return true;
        }

        // Check circle center inside polygon
        if (pointInPolygon(center, points)) return true;

        // Check edge–circle intersection
        for (int i = 0; i < points.size(); i++) {
            Point.Double p1 = points.get(i);
            Point.Double p2 = points.get((i + 1) % points.size());
            if (lineIntersectsCircle(p1, p2, center, radius)) return true;
        }

        return false;
    }

    private boolean polygonPolygonCollision(PolygonCollider a, PolygonCollider b) {
        String[] va = a.toString().split("\\) ");
        String[] vb = b.toString().split("\\) ");
        List<Point.Double> pa = parsePoints(va);
        List<Point.Double> pb = parsePoints(vb);

        for (Point.Double p : pa)
            if (pointInPolygon(new Point((int) p.x, (int) p.y), pb)) return true;
        for (Point.Double p : pb)
            if (pointInPolygon(new Point((int) p.x, (int) p.y), pa)) return true;

        for (int i = 0; i < pa.size(); i++) {
            Point.Double a1 = pa.get(i);
            Point.Double a2 = pa.get((i + 1) % pa.size());
            for (int j = 0; j < pb.size(); j++) {
                Point.Double b1 = pb.get(j);
                Point.Double b2 = pb.get((j + 1) % pb.size());
                if (Line2D.linesIntersect(a1.x, a1.y, a2.x, a2.y, b1.x, b1.y, b2.x, b2.y))
                    return true;
            }
        }

        return false;
    }

    private List<Point.Double> parsePoints(String[] verts) {
        List<Point.Double> points = new ArrayList<>();
        for (String v : verts) {
            v = v.replace("(", "").replace(")", "");
            String[] coords = v.split(",");
            points.add(new Point.Double(Double.parseDouble(coords[0]), Double.parseDouble(coords[1])));
        }
        return points;
    }

    private boolean pointInPolygon(Point p, List<Point.Double> poly) {
        int count = 0;
        double x = p.getX(), y = p.getY();
        for (int i = 0; i < poly.size(); i++) {
            Point.Double a = poly.get(i);
            Point.Double b = poly.get((i + 1) % poly.size());
            if (((a.y > y) != (b.y > y)) &&
                (x < (b.x - a.x) * (y - a.y) / (b.y - a.y + 1e-9) + a.x)) {
                count++;
            }
        }
        return count % 2 == 1;
    }

    private boolean lineIntersectsCircle(Point.Double a, Point.Double b, Point center, double radius) {
        double ax = a.x - center.x, ay = a.y - center.y;
        double bx = b.x - center.x, by = b.y - center.y;

        double dx = bx - ax, dy = by - ay;
        double a2 = dx * dx + dy * dy;
        double b2 = 2 * (ax * dx + ay * dy);
        double c2 = ax * ax + ay * ay - radius * radius;

        double disc = b2 * b2 - 4 * a2 * c2;
        if (disc < 0) return false;

        double t1 = (-b2 - Math.sqrt(disc)) / (2 * a2);
        double t2 = (-b2 + Math.sqrt(disc)) / (2 * a2);

        return (t1 >= 0 && t1 <= 1) || (t2 >= 0 && t2 <= 1);
    }

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
                    verts.add(new Point.Double(Double.parseDouble(cData[j]), Double.parseDouble(cData[j + 1])));
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