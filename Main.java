// Main.java atualizado com suporte para Shield e Bubble
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class Main extends JFrame implements KeyListener {

    private static final long serialVersionUID = 1L;

    private transient GameObject go;
    private JLabel status;
    private BufferedImage background, tankImage, bulletImage, enemyImage, heartImage, shieldImage, bubbleImage;
    private BufferedImage[] explosionFrames;
    private GamePanel gamePanel;

    private final Set<Integer> activeKeys = new HashSet<>();
    private Timer movementTimer;
    private Clip backgroundClip;
    private final File shootSoundFile = new File("Audio/Shoot.wav");

    private int playerLives = 3;
    private boolean isPaused = false;
    private boolean gameOver = false;

    public static final List<Explosion> explosions = new ArrayList<>();

    public static class Explosion {
        public final Point position;
        public final long startTime;

        public Explosion(Point position) {
            this.position = position;
            this.startTime = System.currentTimeMillis();
        }
    }

    public static void addExplosion(Point pos) {
        explosions.add(new Explosion(pos));
        try {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(new File("Audio/Explosion.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInput);
            clip.start();
        } catch (Exception e) {
            System.err.println("Erro ao tocar som de explosão: " + e.getMessage());
        }
    }

    public Main() {
        super("GameObject Controller");

        Transform transform = new Transform(100, 100, 0, 90, 1.0);
        Collider collider = CircleCollider.create(transform, 0, 0, 30);

        Behaviour behaviour = new Behaviour() {
            @Override
            protected void playShootSound() {
                try {
                    AudioInputStream shootInput = AudioSystem.getAudioInputStream(shootSoundFile);
                    Clip shootClip = AudioSystem.getClip();
                    shootClip.open(shootInput);
                    shootClip.start();
                } catch (Exception e) {
                    System.err.println("Erro ao tocar som de disparo: " + e.getMessage());
                }
            }
        };

        behaviour.setActiveKeys(activeKeys);
        go = new GameObject("Player", transform, collider, behaviour);
        behaviour.setControlledObject(go);
        GameEngine.getInstance().add(go);

        loadAssets();
        setupUI();
    }

    private void loadAssets() {
        try {
            background = ImageIO.read(new File("Sprites/Background.png"));
            tankImage = ImageIO.read(new File("Sprites/Tank.png"));
            bulletImage = ImageIO.read(new File("Sprites/Bullet.png"));
            enemyImage = ImageIO.read(new File("Sprites/Enemy.png"));
            heartImage = ImageIO.read(new File("Sprites/Heart.png"));
            shieldImage = ImageIO.read(new File("Sprites/Shield.png"));
            bubbleImage = ImageIO.read(new File("Sprites/Bubble.png"));

            explosionFrames = new BufferedImage[10];
            for (int i = 0; i < 10; i++) {
                explosionFrames[i] = ImageIO.read(new File("Sprites/Explosion_" + (i + 1) + ".png"));
            }

        } catch (IOException e) {
            System.err.println("Erro ao carregar imagens: " + e.getMessage());
        }

        try {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(new File("Audio/Music.wav"));
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(audioInput);
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            System.err.println("Erro ao carregar áudio: " + e.getMessage());
        }
    }

    private void restartGame() {
        for (GameObject obj : GameEngine.getInstance().getEnabled()) {
            GameEngine.getInstance().destroy(obj);
        }

        Behaviour.resetGameState();
        Behaviour.resetScore();
        explosions.clear();

        Transform transform = new Transform(100, 100, 0, 90, 1.0);
        Collider collider = CircleCollider.create(transform, 0, 0, 30);

        Behaviour behaviour = new Behaviour() {
            @Override
            protected void playShootSound() {
                try {
                    AudioInputStream shootInput = AudioSystem.getAudioInputStream(shootSoundFile);
                    Clip shootClip = AudioSystem.getClip();
                    shootClip.open(shootInput);
                    shootClip.start();
                } catch (Exception e) {
                    System.err.println("Erro ao tocar som de disparo: " + e.getMessage());
                }
            }
        };

        behaviour.setActiveKeys(activeKeys);
        go = new GameObject("Player", transform, collider, behaviour);
        behaviour.setControlledObject(go);
        GameEngine.getInstance().add(go);

        playerLives = 3;
    }

    private void setupUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);

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
        for (GameObject obj : GameEngine.getInstance().getEnabled()) {
            obj.collider().adjustToTransform();
        }

        if (Behaviour.wasPlayerHit()) {
            playerLives--;
            Behaviour.resetPlayerHit();
            if (playerLives <= 0) {
                isPaused = true;
                gameOver = true;
            }
        }

        status.setText(go.toString());
    }

    private void startMovementLoop() {
        movementTimer = new Timer(16, _ -> {
            if (!isPaused) {
                for (GameObject obj : GameEngine.getInstance().getEnabled()) {
                    obj.behaviour().onUpdate();
                }
                updateState();
            }
            gamePanel.repaint();
        });
        movementTimer.start();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver && e.getKeyCode() == KeyEvent.VK_ENTER) {
            gameOver = false;
            isPaused = false;
            restartGame();
            return;
        }

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

            for (GameObject obj : GameEngine.getInstance().getEnabled()) {
                Graphics2D g2d = (Graphics2D) g.create();
                Point center = obj.collider().centroid();
                double angle = Math.toRadians(obj.transform().angle());
                double scale;
                if (obj.name().startsWith("Soldier")) {
                    scale = obj.transform().scale() * 0.85;
                } else {
                    scale = obj.transform().scale() * 0.25;
                }

                BufferedImage sprite = null;

                try {
                    switch (obj.name()) {
                        case "Player" -> sprite = tankImage;
                        case "Enemy" -> sprite = enemyImage;
                        case "Bullet" -> sprite = bulletImage;
                        case "Shield" -> sprite = shieldImage;
                        case "Bullet2" -> sprite = ImageIO.read(new File("Sprites/Bullet2.png"));
                        case "Soldier" -> {
                            String state = Behaviour.getSoldierSprite(obj);
                            sprite = ImageIO.read(new File("Sprites/" + state + ".png"));
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Erro ao carregar sprite: " + e.getMessage());
                }

                if (sprite != null) {
                    int imgWidth = sprite.getWidth();
                    int imgHeight = sprite.getHeight();
                    int drawWidth = (int) (imgWidth * scale);
                    int drawHeight = (int) (imgHeight * scale);

                    g2d.translate(center.x, center.y);
                    g2d.rotate(angle);
                    g2d.drawImage(sprite, -drawWidth / 2, -drawHeight / 2, drawWidth, drawHeight, null);
                    g2d.dispose();
                }
            }

            // Bubble (escudo ativo)
            if (Behaviour.isShieldActive()) {
                Graphics2D g2d = (Graphics2D) g.create();
                Point center = go.collider().centroid();
                double angle = Math.toRadians(go.transform().angle());
                double scale = go.transform().scale() * 3.5;

                int imgWidth = bubbleImage.getWidth();
                int imgHeight = bubbleImage.getHeight();
                int drawWidth = (int) (imgWidth * scale);
                int drawHeight = (int) (imgHeight * scale);

                g2d.translate(center.x, center.y);
                g2d.rotate(angle);
                g2d.drawImage(bubbleImage, -drawWidth / 2, -drawHeight / 2, drawWidth, drawHeight, null);
                g2d.dispose();
            }

            // Corações (vidas)
            for (int i = 0; i < playerLives; i++) {
                g.drawImage(heartImage, 20 + i * 40, 20, 32, 32, null);
            }

            // Pontuação
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("Score: " + Behaviour.getScore(), getWidth() - 150, 40);

            // Explosões
            long now = System.currentTimeMillis();
            int duration = 1500;
            int frameCount = explosionFrames.length;
            int frameTime = duration / frameCount;
            explosions.removeIf(exp -> now - exp.startTime >= duration);

            for (Explosion exp : explosions) {
                int frame = (int) ((now - exp.startTime) / frameTime);
                if (frame >= 0 && frame < explosionFrames.length) {
                    BufferedImage img = explosionFrames[frame];
                    g.drawImage(img, exp.position.x - 48, exp.position.y - 48, 96, 96, null);
                }
            }

            if (gameOver) {
                g.setColor(Color.RED);
                g.setFont(new Font("Arial", Font.BOLD, 36));
                String msg = "Pressione ENTER para jogar novamente!";
                int msgWidth = g.getFontMetrics().stringWidth(msg);
                g.drawString(msg, (getWidth() - msgWidth) / 2, getHeight() / 2);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
