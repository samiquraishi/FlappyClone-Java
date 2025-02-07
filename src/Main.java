import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        final int WIDTH = 360;
        final int HEIGHT = 640;

        JFrame window = new JFrame("Flappy Clone");
        window.setSize(WIDTH, HEIGHT);
        window.setLocationRelativeTo(null);
        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Game gamePanel = new Game();
        window.add(gamePanel);
        window.pack();
        gamePanel.requestFocus();
        window.setVisible(true);
    }
}

