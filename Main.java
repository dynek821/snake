import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

public class Main extends JPanel implements ActionListener {
    private final int TILE_SIZE = 20;
    private final int WIDTH = 600;
    private final int HEIGHT = 400;
    private final ArrayList<Point> snake;
    private Point apple;
    private char direction;
    private boolean gameOver;
    private final Timer timer;

    public Main() {
        snake = new ArrayList<>();
        snake.add(new Point(5, 5)); // Начальная позиция змейки
        direction = 'R'; // Направление по умолчанию (вправо)
        spawnApple();
        timer = new Timer(100, this);
        timer.start();
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        if (direction != 'D') direction = 'U';
                        break;
                    case KeyEvent.VK_DOWN:
                        if (direction != 'U') direction = 'D';
                        break;
                    case KeyEvent.VK_LEFT:
                        if (direction != 'R') direction = 'L';
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (direction != 'L') direction = 'R';
                        break;
                }
            }
        });
    }

    private void spawnApple() {
        Random rand = new Random();
        int x = rand.nextInt(WIDTH / TILE_SIZE);
        int y = rand.nextInt(HEIGHT / TILE_SIZE);
        apple = new Point(x, y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.RED);
        g.fillRect(apple.x * TILE_SIZE, apple.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        g.setColor(Color.GREEN);
        for (Point p : snake) {
            g.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }

        if (gameOver) {
            g.setColor(Color.BLACK);
            g.drawString("Game Over!", WIDTH / 2 - 30, HEIGHT / 2);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver) return;

        Point head = snake.get(0);
        Point newHead = new Point(head);

        switch (direction) {
            case 'U':
                newHead.y--;
                break;
            case 'D':
                newHead.y++;
                break;
            case 'L':
                newHead.x--;
                break;
            case 'R':
                newHead.x++;
                break;
        }

        if (newHead.equals(apple)) {
            snake.add(0, newHead);
            spawnApple();
        } else {
            snake.add(0, newHead);
            snake.remove(snake.size() - 1); // Удалить последний сегмент
        }

        if (newHead.x < 0 || newHead.x >= WIDTH / TILE_SIZE || newHead.y < 0 || newHead.y >= HEIGHT / TILE_SIZE || snake.subList(1, snake.size()).contains(newHead)) {
            gameOver = true;
        }

        repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        Main game = new Main();
        frame.add(game);
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        game.requestFocus();
    }
}
