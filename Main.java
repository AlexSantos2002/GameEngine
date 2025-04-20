import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.imageio.ImageIO;

/**
 * Jogo com nave controlada por teclado.
 * 
 * Teclas:
 * - Setas: mover
 * - Q: rodar para a esquerda
 * - E: rodar para a direita
 * 
 * A nave é representada por Ship.png e o fundo por Background.png (pasta Sprites/).
 * O jogo abre maximizado e faz wrap-around nos limites da janela.
 */
public class Main extends JFrame implements KeyListener {

    private static final long serialVersionUID = 1L;

    private transient GameObject go;
    private transient Transform transform;
    private transient Collider collider;
    private JLabel status;
    private BufferedImage background;
    private BufferedImage shipImage;
    private GamePanel gamePanel;

    private final Set<Integer> activeKeys = new HashSet<>();
    private Timer movementTimer;

    public Main() {
        super("GameObject Controller");

        transform = new Transform(100, 100, 0, 0, 1.0);
        collider = CircleCollider.create(transform, 0, 0, 30);
        go = new GameObject("Player", transform, collider);

        try {
            background = ImageIO.read(new File("Sprites/Background.png"));
            shipImage = ImageIO.read(new File("Sprites/Ship.png"));
        } catch (IOException e) {
            System.err.println("Erro ao carregar imagens: " + e.getMessage());
        }
    }

    private void setupUI() {
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Começa maximizado
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        gamePanel = new GamePanel();
        add(gamePanel, BorderLayout.CENTER);

        status = new JLabel(go.toString());
        add(status, BorderLayout.SOUTH);

        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        setVisible(true);

        startMovementLoop();
    }

    private void updateState() {
        collider.adjustToTransform();
        Point pos = transform.position();
        int width = gamePanel.getWidth();
        int height = gamePanel.getHeight();

        int x = pos.x;
        int y = pos.y;

        // Wrap-around horizontal
        if (x < 0) transform.move(new Point(width, 0), 0);
        else if (x > width) transform.move(new Point(-width, 0), 0);

        // Wrap-around vertical
        if (y < 0) transform.move(new Point(0, height), 0);
        else if (y > height) transform.move(new Point(0, -height), 0);

        status.setText(go.toString());
        gamePanel.repaint();
    }

    private void startMovementLoop() {
        movementTimer = new Timer(16, e -> {
            Point delta = new Point(0, 0);
            int dLayer = 0;
            double dAngle = 0;
            double dScale = 0;

            if (activeKeys.contains(KeyEvent.VK_LEFT))  delta.translate(-5, 0);
            if (activeKeys.contains(KeyEvent.VK_RIGHT)) delta.translate(5, 0);
            if (activeKeys.contains(KeyEvent.VK_UP))    delta.translate(0, -5);
            if (activeKeys.contains(KeyEvent.VK_DOWN))  delta.translate(0, 5);
            if (activeKeys.contains(KeyEvent.VK_E))     dAngle += 5;
            if (activeKeys.contains(KeyEvent.VK_Q))     dAngle -= 5;

            if (delta.x != 0 || delta.y != 0 || dAngle != 0 || dScale != 0) {
                transform.move(delta, dLayer);
                transform.rotate(dAngle);
                transform.scale(dScale);
                updateState();
            }
        });
        movementTimer.start();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        activeKeys.add(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        activeKeys.remove(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    private class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (background != null) {
                g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
            }

            if (go.collider() instanceof CircleCollider c && shipImage != null) {
                Graphics2D g2d = (Graphics2D) g.create();

                Point center = c.centroid();
                double angle = Math.toRadians(transform.angle());
                double scale = transform.scale() * 0.25;

                int imgWidth = shipImage.getWidth();
                int imgHeight = shipImage.getHeight();

                int drawWidth = (int) (imgWidth * scale);
                int drawHeight = (int) (imgHeight * scale);

                g2d.translate(center.x, center.y);
                g2d.rotate(angle);
                g2d.drawImage(shipImage, -drawWidth / 2, -drawHeight / 2, drawWidth, drawHeight, null);

                g2d.dispose();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main app = new Main();
            app.setupUI();
        });
    }
}
