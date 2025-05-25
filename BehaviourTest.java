import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

public class BehaviourTest {

    private Behaviour behaviour;
    private GameObject player;

    @Before
    public void setUp() {
        Transform t = new Transform(100, 100, 0, 0, 1.0);
        Collider c = CircleCollider.create(t, 0, 0, 30);
        behaviour = new Behaviour();
        player = new GameObject("Player", t, c, behaviour);
        behaviour.setControlledObject(player);
    }

    @Test
    public void testPlayerMovementInput() {
        Set<Integer> keys = new HashSet<>();
        keys.add(KeyEvent.VK_RIGHT);
        keys.add(KeyEvent.VK_DOWN);
        behaviour.setActiveKeys(keys);

        Point before = player.transform().position();
        behaviour.onUpdate();
        Point after = player.transform().position();

        assertTrue(after.x > before.x);
        assertTrue(after.y > before.y);
    }

    @Test
    public void testFireProjectileCooldown() throws InterruptedException {
        Set<Integer> keys = new HashSet<>();
        keys.add(KeyEvent.VK_SPACE);
        behaviour.setActiveKeys(keys);

        behaviour.onUpdate();
        int countAfterFirstShot = GameEngine.getInstance().getEnabled().size();
        behaviour.onUpdate();
        int countAfterSecond = GameEngine.getInstance().getEnabled().size();
        assertEquals(countAfterFirstShot, countAfterSecond);
    }

    @Test
    public void testScoreIncreasesOnEnemyHit() {
        Behaviour.resetScore();
        assertEquals(0, Behaviour.getScore());

        Transform t = new Transform(200, 200, 0, 0, 1.0);
        Collider c = CircleCollider.create(t, 0, 0, 30);
        GameObject enemy = new GameObject("Enemy", t, c, new Behaviour());
        ((Behaviour) enemy.behaviour()).setControlledObject(enemy);

        GameEngine.getInstance().add(enemy);
        GameObject bullet = new GameObject("Bullet", t, c, null);
        enemy.behaviour().onCollision(bullet);

        assertTrue(Behaviour.getScore() >= 1);
    }

    @Test
    public void testResetGameState() {
        Behaviour.resetGameState();
        assertFalse(Behaviour.wasPlayerHit());
        assertEquals(0, Behaviour.getScore());
        assertFalse(Behaviour.isShieldActive());
    }

    @Test
    public void testShieldActivation() {
        Behaviour.resetGameState();
        behaviour.onUpdate();
    }

    @Test
    public void testPlayerWrapsAroundScreen() {
        player.transform().move(new Point(-1000, 0), 0);

        Set<Integer> keys = new HashSet<>();
        keys.add(KeyEvent.VK_LEFT);
        behaviour.setActiveKeys(keys);

        behaviour.onUpdate();
        Point after = player.transform().position();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        assertTrue(after.x >= 0 && after.x <= screenSize.width);
    }
}
