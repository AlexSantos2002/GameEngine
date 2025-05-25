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

/**
 * Função main em que é responsavel por fazer a parte de GUI separada do motor de jogo
 * @version 1.2 24/05/2025
 * @author Alexandre Santos (71522), Nurio Pereira (72788)
 */
public class Main extends JFrame implements KeyListener {

    /**
     *  serve como ponto de entrada para o jogo, inicializando o motor de jogo e a interface gráfica.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Representa o objeto do jogo controlado pelo jogador.
     * Contém informações sobre a posição, ângulo, escala e comportamento do objeto.
     */
    private transient GameObject go;

    /**
     * Representa o painel de jogo onde os objetos são desenhados.
     * É responsável por renderizar o fundo, os objetos do jogo e as explosões.
     */
    private JLabel status;

    /**
     * Imagens usadas no jogo, incluindo fundo, tanque, projéteis, inimigos e efeitos visuais.
     * As imagens são carregadas a partir de arquivos no sistema de arquivos.
     */
    private BufferedImage background, tankImage, bulletImage, enemyImage, heartImage, shieldImage, bubbleImage;

    /**
     * Frames de explosão usados para animar explosões no jogo.
     * Cada frame é uma imagem que representa um estado da explosão.
     */
    private BufferedImage[] explosionFrames;

    /**
     * Painel de jogo que exibe os objetos e o estado do jogo.
     * É atualizado a cada frame para refletir as mudanças no estado do jogo.
     */
    private GamePanel gamePanel;

    /**
     * Conjunto de teclas ativas que controlam o comportamento do objeto do jogo.
     * Permite que o jogador mova o objeto e execute ações como disparar projéteis.
     */
    private final Set<Integer> activeKeys = new HashSet<>();

    /**
     * Timer que controla o loop de movimento do jogo.
     * Atualiza o estado do jogo e redesenha o painel a cada 16 milissegundos (aproximadamente 60 FPS).
     */
    private Timer movementTimer;

    /**
     * Clip de áudio que toca a música de fundo do jogo.
     * É iniciado quando o jogo é carregado e toca continuamente até que o jogo seja encerrado.
     */
    private Clip backgroundClip;

    /**
     * Arquivo de áudio que contém o som do disparo do jogador.
     * É tocado sempre que o jogador dispara um projétil.
     */
    private final File shootSoundFile = new File("Audio/Shoot.wav");

    /**
     * Variáveis de estado do jogo, incluindo vidas do jogador, pausa e fim de jogo.
     * Controlam o fluxo do jogo e determinam quando o jogo deve ser reiniciado ou pausado.
     */
    private int playerLives = 3;
    private boolean isPaused = false;
    private boolean gameOver = false;

    /**
     * Lista de explosões ativas no jogo.
     * Cada explosão é representada por uma posição e um tempo de início.
     * As explosões são desenhadas no painel de jogo e removidas após um certo tempo.
     */
    public static final List<Explosion> explosions = new ArrayList<>();

    /**
     * Classe interna que representa uma explosão no jogo.
     * Contém a posição da explosão e o tempo em que ela começou.
     */
    public static class Explosion {
        public final Point position;
        public final long startTime;

        public Explosion(Point position) {
            this.position = position;
            this.startTime = System.currentTimeMillis();
        }
    }

    /**
     * Método estático para adicionar uma explosão ao jogo.
     * Cria uma nova explosão na posição especificada e toca o som de explosão.
     *
     * @param pos Posição onde a explosão deve ocorrer
     */
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

    /**
     * Construtor da classe Main que inicializa o jogo.
     * Configura o objeto do jogo, carrega os ativos (imagens e sons) e configura a interface do usuário.
     */
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

    /**
     * Carrega os ativos necessários para o jogo, incluindo imagens e sons.
     * As imagens são carregadas a partir de arquivos no sistema de arquivos e os sons são carregados em um Clip.
     * Se ocorrer um erro ao carregar os ativos, uma mensagem de erro é exibida no console.
     */
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

    /**
     * Reinicia o jogo, destruindo todos os objetos existentes e criando um novo objeto do jogador.
     * Reseta o estado do jogo, as explosões e as vidas do jogador.
     * O som de disparo é configurado novamente para o novo objeto do jogador.
     */
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

    /**
     * Configura a interface do usuário do jogo, incluindo o painel de jogo e o status do jogo.
     * Define o layout, adiciona componentes e inicia o loop de movimento.
     * Também configura o listener de teclado para capturar entradas do jogador.
     */
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

    /**
     * Atualiza o estado do jogo, ajustando os colliders dos objetos e verificando se o jogador foi atingido.
     * Se o jogador perder todas as vidas, o jogo é pausado e a variável gameOver é definida como verdadeira.
     * O status do jogo é atualizado para refletir o estado atual.
     */
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

    /**
     * Inicia o loop de movimento do jogo, que atualiza o estado dos objetos e redesenha o painel a cada 16 milissegundos.
     * Se o jogo estiver pausado, o loop não atualiza o estado dos objetos.
     * O painel de jogo é repintado a cada iteração do loop.
     */
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
    /**
     * Método chamado quando uma tecla é pressionada.
     * Se o jogo estiver em estado de game over e a tecla pressionada for ENTER, o jogo é reiniciado.
     * Caso contrário, a tecla pressionada é adicionada ao conjunto de teclas ativas.
     *
     * @param e Evento de teclado
     */
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
    /**
     * Método chamado quando uma tecla é liberada.
     * Remove a tecla liberada do conjunto de teclas ativas, permitindo que o objeto do jogo pare de se mover ou realizar ações associadas a essa tecla.
     *
     * @param e Evento de teclado
     */
    public void keyReleased(KeyEvent e) {
        activeKeys.remove(e.getKeyCode());
    }

    @Override
    /**
     * Método chamado quando uma tecla é digitada.
     * Neste caso, não há ação associada a este evento, mas é necessário implementar o método devido à interface KeyListener.
     *
     * @param e Evento de teclado
     */
    public void keyTyped(KeyEvent e) {}

    /**
     * Classe interna que representa o painel de jogo.
     * É responsável por desenhar o fundo, os objetos do jogo e as explosões.
     * Também exibe informações como vidas do jogador e pontuação.
     */
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

            for (int i = 0; i < playerLives; i++) {
                g.drawImage(heartImage, 20 + i * 40, 20, 32, 32, null);
            }

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("Score: " + Behaviour.getScore(), getWidth() - 150, 40);

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
