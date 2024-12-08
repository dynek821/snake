import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

public class Game extends JPanel implements ActionListener {
    private final int SIZE = 20;
    private final int WIDTH = 600;
    private final int HEIGHT = 400;
    private final ArrayList<Point> snake;
    private Point applePosition;
    private char Direction;
    private boolean GameOver;
    private boolean Paused;
    private final Timer gameTimer;
    private int gameSpeed = 100;

    public Game() {
        snake = new ArrayList<>();
        snake.add(new Point(5, 5)); // Начало
        Direction = getRandomDirection(); // Произвольное направление в начале игры
        spawnApple();
        gameTimer = new Timer(gameSpeed, this);
        gameTimer.start();
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });
    }
    // Обработка нажатий клавиш
    private void handleKeyPress(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                if (Direction != 'D') Direction = 'U';
                break;
            case KeyEvent.VK_S:
                if (Direction != 'U') Direction = 'D';
                break;
            case KeyEvent.VK_A:
                if (Direction != 'R') Direction = 'L';
                break;
            case KeyEvent.VK_D:
                if (Direction != 'L') Direction = 'R';
                break;
            case KeyEvent.VK_P:
                Paused = !Paused;
                break;
            case KeyEvent.VK_R:
                if (GameOver) {
                    restartGame();
                }
                break;
            case KeyEvent.VK_UP:
                increaseGameSpeed();
                break;
            case KeyEvent.VK_DOWN:
                decreaseGameSpeed();
                break;
        }
    }

    // Возвращает случайное направление
    private char getRandomDirection() {
        Random rand = new Random();
        char[] directions = {'U', 'D', 'L', 'R'};
        return directions[rand.nextInt(directions.length)];
    }

    // Рестарт игры
    private void restartGame() {
        snake.clear();
        snake.add(new Point(5, 5));
        Direction = getRandomDirection();
        spawnApple();
        GameOver = false;
        Paused = false;
    }

    // Создает яблоко в пределах поля
    private void spawnApple() {
        Random rand = new Random();
        int x = rand.nextInt(WIDTH / SIZE);  // по оси X
        int y = rand.nextInt(HEIGHT /SIZE); // по оси Y
        applePosition = new Point(x, y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawGameField(g);
        drawSnake(g);
        drawApple(g);
        if (GameOver) {
            drawGameOverMessage(g);
        }
        drawGameInfo(g);
    }

    // Отображение границ игрового поля
    private void drawGameField(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, WIDTH, HEIGHT);
    }

    // Отображение яблока
    private void drawApple(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(applePosition.x * SIZE, applePosition.y * SIZE, SIZE, SIZE);
    }

    // Отображение змейки
    private void drawSnake(Graphics g) {
        for (int i = 0; i < snake.size(); i++) {
            Point segment = snake.get(i);
            g.setColor(i == 0 ? Color.PINK : Color.GREEN);
            g.fillRect(segment.x * SIZE, segment.y * SIZE, SIZE, SIZE);
        }
    }

    // Отображение сообщения об окончании игры
    private void drawGameOverMessage(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawString("Игра окончена, нажмите клавишу R, чтобы начать заново", WIDTH / 2 - 100, HEIGHT / 2);
    }

    // Отображение информации об игре (скорость и управление)
    private void drawGameInfo(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawString("Стрелка вверх - увеличение скорости, стрелка вниз - уменьшение скорости", WIDTH / 2 - 200, HEIGHT - 5);
        g.drawString("Скорость змейки: " + (1000 / gameSpeed), WIDTH / 2 - 50, HEIGHT - 20);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (GameOver || Paused) return;
        moveSnake();
        checkCollisions();
        repaint();
    }

    // Движение змейки
    private void moveSnake() {
        Point head = snake.get(0);
        Point newHead = new Point(head);

        switch (Direction) {
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

        handleWallCollisions(newHead);

        if (newHead.equals(applePosition)) {
            snake.add(0, newHead);
            spawnApple();
        } else {
            snake.add(0, newHead);
            snake.remove(snake.size() - 1);
        }
    }

    // Обработка выхода за границы игрового поля
    private void handleWallCollisions(Point newHead) {
        if (newHead.x < 0) {
            newHead.x = WIDTH / SIZE - 1;
        } else if (newHead.x >= WIDTH / SIZE) {
            newHead.x = 0;
        }
        if (newHead.y < 0) {
            newHead.y = HEIGHT / SIZE - 1;
        } else if (newHead.y >= HEIGHT / SIZE) {
            newHead.y = 0;
        }
    }

    // Проверка на столкновение с телом змейки
    private void checkCollisions() {
        Point head = snake.get(0);
        if (snake.subList(1, snake.size()).contains(head)) {
            GameOver = true;
        }
    }

    // Увеличение скорости игры
    private void increaseGameSpeed() {
        if (gameSpeed > 20) {
            gameSpeed -= 5;
            gameTimer.setDelay(gameSpeed);
        }
    }

    // Уменьшение скорости игры
    private void decreaseGameSpeed() {
        if (gameSpeed < 200) {
            gameSpeed += 5;
            gameTimer.setDelay(gameSpeed);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        Game gamePanel = new Game();
        frame.add(gamePanel);
        frame.setSize(650, 480);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel buttonPanel = new JPanel();
        JButton restartButton = new JButton("Рестарт игры - клавиша R");
        JButton pauseButton = new JButton("Пауза - клавиша P");

        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gamePanel.restartGame();
                gamePanel.requestFocusInWindow();
            }
        });

        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gamePanel.Paused = !gamePanel.Paused;
                gamePanel.requestFocusInWindow();
            }
        });
        buttonPanel.add(restartButton);
        buttonPanel.add(pauseButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
        gamePanel.requestFocusInWindow();
    }
}
