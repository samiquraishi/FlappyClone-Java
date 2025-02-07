import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Game extends JPanel implements ActionListener, KeyListener {
    private static final int SCREEN_WIDTH = 360;
    private static final int SCREEN_HEIGHT = 640;
    private static final int PIPE_WIDTH = 64;
    private static final int PIPE_HEIGHT = 512;
    private static final int BIRD_WIDTH = 34;
    private static final int BIRD_HEIGHT = 24;
    private static final int PIPE_GAP = SCREEN_HEIGHT / 4;
    private static final int INITIAL_PIPE_SPEED = -5;

    private Image bgImage, birdImage, upperPipeImage, lowerPipeImage;
    private Player player;
    private ArrayList<Obstacle> obstacles;
    private Timer gameTicker, pipeSpawner;
    private boolean isGameOver = false;
    private boolean gameStarted = false;
    private int score = 0;
    private double verticalVelocity = 0;
    private final double gravity = 1.2;
    private final double jumpStrength = -13;
    private int pipeSpeed = INITIAL_PIPE_SPEED;
    private int jumpCount = 0;
    private Random randomizer = new Random();

    public Game() {
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setFocusable(true);
        addKeyListener(this);

        bgImage = new ImageIcon(getClass().getResource("/imgs/flappybirdbg.png")).getImage();
        birdImage = new ImageIcon(getClass().getResource("/imgs/flappybird.png")).getImage();
        upperPipeImage = new ImageIcon(getClass().getResource("/imgs/toppipe.png")).getImage();
        lowerPipeImage = new ImageIcon(getClass().getResource("/imgs/bottompipe.png")).getImage();

        player = new Player();
        obstacles = new ArrayList<>();

        gameTicker = new Timer(1000 / 60, this);
        pipeSpawner = new Timer(1200, e -> spawnObstacles());
    }

    private void spawnObstacles() {
        int pipeOffset = -PIPE_HEIGHT / 3 - randomizer.nextInt(PIPE_HEIGHT / 2);
        int fixedGap = SCREEN_HEIGHT / 4; // Ensure gap remains constant
        obstacles.add(new Obstacle(upperPipeImage, pipeOffset));
        obstacles.add(new Obstacle(lowerPipeImage, pipeOffset + PIPE_HEIGHT + fixedGap));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bgImage, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, null);
        g.drawImage(birdImage, player.x, player.y, BIRD_WIDTH, BIRD_HEIGHT, null);

        for (Obstacle obs : obstacles) {
            g.drawImage(obs.sprite, obs.x, obs.y, PIPE_WIDTH, PIPE_HEIGHT, null);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));

        if (!gameStarted) {
            g.drawString("Tap To Begin", SCREEN_WIDTH / 4, SCREEN_HEIGHT / 2);
        } else if (isGameOver) {
            g.drawString("Game Over: " + score, 10, 35);
            g.drawString("Tap To Begin", SCREEN_WIDTH / 4, SCREEN_HEIGHT / 2);
        } else {
            g.drawString(String.valueOf(score), 10, 35);
        }
    }

    private void updateGame() {
        verticalVelocity += gravity;
        player.y += verticalVelocity;
        player.y = Math.max(player.y, 0);

        for (Obstacle obs : obstacles) {
            obs.x += pipeSpeed;
            if (!obs.counted && player.x > obs.x + PIPE_WIDTH) {
                score++;
                obs.counted = true;
                if (score % 4 == 0) pipeSpeed--; // Increase pipe speed every 4 jumps
            }
            if (player.collidesWith(obs)) {
                isGameOver = true;
            }
        }
        if (player.y > SCREEN_HEIGHT) {
            isGameOver = true;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isGameOver && gameStarted) {
            updateGame();
            repaint();
        } else {
            pipeSpawner.stop();
            gameTicker.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!gameStarted) {
                gameStarted = true;
                gameTicker.start();
                pipeSpawner.start();
            }
            if (!isGameOver) {
                verticalVelocity = jumpStrength;
                jumpCount++;
            } else {
                resetGame();
            }
        }
    }

    private void resetGame() {
        player.y = SCREEN_HEIGHT / 2;
        verticalVelocity = 0;
        obstacles.clear();
        isGameOver = false;
        gameStarted = false;
        score = 0;
        pipeSpeed = INITIAL_PIPE_SPEED;
        jumpCount = 0;
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}

    private class Player {
        int x = SCREEN_WIDTH / 8, y = SCREEN_HEIGHT / 2;
        boolean collidesWith(Obstacle obs) {
            return x < obs.x + PIPE_WIDTH && x + BIRD_WIDTH > obs.x && y < obs.y + PIPE_HEIGHT && y + BIRD_HEIGHT > obs.y;
        }
    }

    private class Obstacle {
        int x = SCREEN_WIDTH, y;
        Image sprite;
        boolean counted = false;
        Obstacle(Image sprite, int y) {
            this.sprite = sprite;
            this.y = y;
        }
    }
}
