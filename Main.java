import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Programa principal que cria uma janela gráfica e permite controlar um GameObject com o teclado.
 * Usa teclas para mover, rodar e escalar o objeto em tempo real, com KeyListener.
 * 
 * Teclas:
 * - Setas: mover
 * - R: rodar 15 graus
 * - S: aumentar escala em 0.1
 * 
 * @author 
 * Alexandre Santos (71522), Nurio Pereira (72788)
 * @version 2.1 10/04/2025
 */
public class Main extends JFrame implements KeyListener {

    private static final long serialVersionUID = 1L;

    private transient GameObject go;
    private transient Transform transform;
    private transient Collider collider;
    private JLabel status;

    /**
     * Construtor: inicializa o modelo do GameObject.
     */
    public Main() {
        super("GameObject Controller");

        transform = new Transform(100, 100, 0, 0, 1.0);
        collider = CircleCollider.create(transform, 0, 0, 30);
        go = new GameObject("Player", transform, collider);
    }

    /**
     * Inicializa os componentes gráficos e listeners.
     */
    private void setupUI() {
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        status = new JLabel(go.toString());
        add(status, BorderLayout.SOUTH);

        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        setVisible(true);
    }

    /**
     * Atualiza o estado da UI com os dados do GameObject.
     */
    private void updateState() {
        collider.adjustToTransform();
        status.setText(go.toString());
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        Point delta = new Point(0, 0);
        int dLayer = 0;
        double dAngle = 0;
        double dScale = 0;

        switch (key) {
            case KeyEvent.VK_LEFT:  delta = new Point(-10, 0); break;
            case KeyEvent.VK_RIGHT: delta = new Point(10, 0);  break;
            case KeyEvent.VK_UP:    delta = new Point(0, -10); break;
            case KeyEvent.VK_DOWN:  delta = new Point(0, 10);  break;
            case KeyEvent.VK_R:     dAngle = 15;               break;
            case KeyEvent.VK_S:     dScale = 0.1;              break;
        }

        transform.move(delta, dLayer);
        transform.rotate(dAngle);
        transform.scale(dScale);
        updateState();
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    /**
     * Método principal. Inicializa o jogo.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main app = new Main();
            app.setupUI();
        });
    }
}
