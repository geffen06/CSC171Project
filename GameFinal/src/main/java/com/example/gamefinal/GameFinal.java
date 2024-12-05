package com.example.gamefinal;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.ArrayList;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GameFinal extends Application {
    Group root;
    private int width;
    private int height;

    private boolean running = false;
    final long[] delay = {(long) (1.5 * 1000000000), 1000000000};
    final long[] time = {System.nanoTime(), System.nanoTime()};
    private int timer = 0;
    private Text timerText;

    private int enemiesKilled = 0;

    private Vector2D gravity;
    private Player player;

    private ArrayList<Enemy> enemies;

    private ArrayList<Platform> platforms;
    void createPlatforms(int width, int height) {
        platforms = new ArrayList<>();
        platforms.add(new Platform((double) width * 0.5, (double) height * 0.8, (double) width * 0.8, root));
        platforms.add(new Platform((double) width * 0.15, (double) height * 0.6, (double) width * 0.2, root));
        platforms.add(new Platform((double) width * 0.85, (double) height * 0.6, (double) width * 0.2, root));
        platforms.add(new Platform((double) width * 0.5, (double) height * 0.4, (double) width * 0.2, root));
    }

    void displayPlatforms() {
        for (Platform platform : platforms) {
            platform.display();
        }
    }

    void spawnEnemy() {
        Vector2D position = new Vector2D();
        int value = (int) Math.floor(Math.random() * 4);
        switch (value) {
            case 0:
                position.x = Math.random() * width;
                break;

            case 1:
                position.x = width;
                position.y = Math.random() * height;
                break;

            case 2:
                position.x = Math.random() * width;
                position.y = height;
                break;

            default:
                position.y = Math.random() * height;
                break;
        }

        int health = (int) Math.floor(Math.random() * 3) + 1;
        enemies.add(new Enemy(position.x, position.y, 25 * health, 25 * health, health, (double) 1 / health, root));
    }

    void enemyLoop() {
        for (int i = enemies.size()-1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            enemy.update(player);
            enemy.display();

            if (enemy.isDead(root)) {
                enemies.remove(i);
                enemiesKilled++;
            }
        }
    }

    void startingScreen() {
        Image backgroundImage = new Image("file:src/main/background.jpg"); // Replace with your image path
        ImageView backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.setFitWidth(width);
        backgroundImageView.setFitHeight(height);

        Text titleText = new Text();
        titleText.setText("Title of Game");
        titleText.setFill(Color.BLUE);
        titleText.setFont(Font.font("Roboto", 96));
        titleText.setX(width * 0.5 - titleText.getBoundsInLocal().getWidth() / 2);
        titleText.setY(height * 0.25);

        Button startButton = new Button("Start");
        startButton.setOnAction(e -> startGame());
        startButton.setScaleX(2);
        startButton.setScaleY(2);
        startButton.setTranslateX(width * 0.5 - 20);
        startButton.setTranslateY(height * 0.75);

        root.getChildren().addAll(backgroundImageView, titleText, startButton);
    }

    void GameOver(Scene scene) {
        running = false;

        root.getChildren().clear();
        scene.setFill(Color.BLACK);

        Text gameOverText = new Text();
        gameOverText.setText("Game Over");
        gameOverText.setFill(Color.RED);
        gameOverText.setFont(Font.font("Roboto", 96));
        gameOverText.setX(width * 0.5 - gameOverText.getBoundsInLocal().getWidth() / 2);
        gameOverText.setY(height * 0.25);

        int highScore = getHighScore();
        if (timer > highScore) {
            storeHighScore(timer);
            highScore = timer;
        }
        Text timerText = new Text();
        // timerText.setText("You lived for " + timer + " seconds and killed " + enemiesKilled + " enemies.");
        timerText.setText("Time Survived: " + timer + "\nEnemies Killed: " + enemiesKilled + "\nHigh Score Time: " + highScore);
        timerText.setFill(Color.WHITE);
        timerText.setFont(Font.font("Roboto", 56));
        timerText.setX(width * 0.5 - timerText.getBoundsInLocal().getWidth() / 2);
        timerText.setY(height * 0.4);

        Button restartButton = new Button("Restart");
        restartButton.setOnAction(e -> startGame());
        restartButton.setScaleX(2);
        restartButton.setScaleY(2);
        restartButton.setTranslateX(width * 0.5 - 20);
        restartButton.setTranslateY(height * 0.75);

        root.getChildren().addAll(gameOverText, timerText, restartButton);
    }

    int getHighScore() {
        try {
            String content = new String(Files.readAllBytes(Paths.get("src/main/highscore")));
            return Integer.parseInt(content);
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            return -1; // Or some default value or error code
        }
    }

    public static void storeHighScore(int score) {
        try {
            Files.write(Paths.get("src/main/highscore"), String.valueOf(score).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void startGame() {
        root.getChildren().clear();

        Image backgroundImage = new Image("file:src/main/background.jpg"); // Replace with your image path
        ImageView backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.setFitWidth(width);
        backgroundImageView.setFitHeight(height);
        root.getChildren().add(backgroundImageView);

        gravity = new Vector2D(0, 0.6);
        player = new Player((double) width / 2, 0, 50, 50, root);

        enemies = new ArrayList<>();
        delay[0] = (long) (1.5 * 1000000000);
        Enemy.speed = 2;
        createPlatforms(width, height);

        timer = 0;
        enemiesKilled = 0;

        timerText = new Text();
        timerText.setX(width * 0.5);
        timerText.setY(50);
        timerText.setFill(Color.BLACK);
        timerText.setFont(Font.font("Roboto", 36));
        root.getChildren().add(timerText);

        running = true;
    }

    @Override
    public void start(Stage stage) {
        // Create the root group and scene
        root = new Group();

        width = 1280;
        height = 720;
        Scene scene = new Scene(root, width, height, Color.LIGHTGRAY);

        startingScreen();

        scene.setOnKeyPressed(event -> {
            KeyCode key = event.getCode();
            player.keyPressed(key);
        });
        scene.setOnKeyReleased(event -> {
            KeyCode key = event.getCode();
            player.keyReleased(key);
        });

        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                width = (int)scene.getWidth();
                height = (int)scene.getHeight();

                if (running) {
                    if (now - time[0] > delay[0]) {
                        time[0] = now;
                        spawnEnemy();
                    }
                    if (now - time[1] > delay[1]) {
                        time[1] = now;
                        timer++;
                        if (timer % 5 == 0) {
                            delay[0] -= (long) (0.1 * 1000000000);
                            Enemy.increaseSpeed(0.2);
                        }
                    }
                    timerText.setText(String.valueOf(timer));

                    enemyLoop();

                    player.addForce(gravity);
                    player.move(platforms);
                    player.update(platforms);
                    player.bulletLoop(root, enemies);
                    player.checkCollision(enemies);
                    if (player.isDead()) {
                        GameOver(scene);
                    }

                    displayPlatforms();
                    player.display();
                }
            }
        };

        scene.setOnMouseClicked(event -> player.shoot(event, running, root));

        gameLoop.start();

        // Set the stage
        stage.setTitle("Game Title");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

class Player {
    // Movement variables
    public Vector2D position;
    public Vector2D velocity;
    public Vector2D acceleration;
    private final boolean[] keyz;
    private final double movementSpeed;
    private final double jumpForce;
    public final double width;
    public final double height;
    private Vector2D bottomPosition;

    // Shooting variables
    private final ArrayList<Bullet> bullets;
    private final double bulletSpeed;

    // Health variables
    public int health;

    // Display variables
    private final ImageView displayObject;
    Player(double x, double y, double width, double height, Group root) {
        // Movement
        this.position = new Vector2D(x, y);
        this.velocity = new Vector2D(0, 0);
        this.acceleration = new Vector2D(0, 0);

        this.keyz = new boolean[3];
        this.movementSpeed = 6;
        this.jumpForce = 15;

        this.width = width;
        this.height = height;

        this.bottomPosition = new Vector2D(this.position);
        this.bottomPosition.add(0, this.height / 2);

        // Shooting
        this.bullets = new ArrayList<>();
        this.bulletSpeed = 15;

        // Health
        this.health = 1;

        // Display
        displayObject = new ImageView(new Image("file:src/main/puppy.png"));
        displayObject.setFitWidth(width * 1.6);
        displayObject.setFitHeight(height * 1.6);
        root.getChildren().add(displayObject);
    }

    public void addForce(Vector2D force) {
        acceleration.add(force);
    }

    public Platform onPlatform(ArrayList<Platform> platforms) {
        this.bottomPosition = new Vector2D(this.position);
        this.bottomPosition.add(0, this.height / 2);

        for (Platform platform : platforms) {
            if (
                    Vector2D.xDist(this.position, platform.position) < (this.width + platform.width) / 2 &&
                            Vector2D.yDist(this.bottomPosition, platform.position) < this.velocity.y + 0.4 &&
                            this.velocity.y >= 0
            ) {
                return platform;
            }
        }
        return null;
    }

    public void keyPressed(KeyCode key) {
        if (key == KeyCode.D || key == KeyCode.RIGHT) keyz[0] = true;
        if (key == KeyCode.A || key == KeyCode.LEFT) keyz[1] = true;
        if (key == KeyCode.W || key == KeyCode.UP) keyz[2] = true;
    }
    public void keyReleased(KeyCode key) {
        if (key == KeyCode.D || key == KeyCode.RIGHT) keyz[0] = false;
        if (key == KeyCode.A || key == KeyCode.LEFT) keyz[1] = false;
        if (key == KeyCode.W || key == KeyCode.UP) keyz[2] = false;
    }

    public void move(ArrayList<Platform> platforms) {
        if (keyz[0]) {
            this.position.x += this.movementSpeed;
            displayObject.setScaleX(-1);
        }

        if (keyz[1]) {
            this.position.x -= this.movementSpeed;
            displayObject.setScaleX(1);
        }

        if (keyz[2]) {
            boolean grounded = (this.onPlatform(platforms) != null);
            if (grounded) {
                this.velocity.y = -this.jumpForce;
                // System.out.println("Jump!");
            }
        }
    }

    public void shoot(MouseEvent event, boolean running, Group root) {
        if (running) {
            Vector2D mouse = new Vector2D(event.getSceneX(), event.getSceneY());
            Vector2D difference = Vector2D.sub(mouse, this.position);
            difference.setMag(this.bulletSpeed);
            bullets.add(new Bullet(this.position.copy(), difference, root));
        }
    }

    public void bulletLoop(Group root, ArrayList<Enemy> enemies) {
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.update();
            bullet.checkCollision(enemies);
            bullet.display();

            if (bullet.isDead(root)) {
                bullets.remove(i);
            }
        }
    }

    public boolean isDead() {
        return (this.health <= 0);
    }

    public void checkCollision(ArrayList<Enemy> enemies) {
        for (Enemy enemy : enemies) {
            if (this.colliding(enemy)) {
                this.health--;
            }
        }
    }

    public boolean colliding(Enemy enemy) {
        double xDist = Vector2D.xDist(this.position, enemy.position);
        double yDist = Vector2D.yDist(this.position, enemy.position);
        return (xDist < (this.width + enemy.width) / 2 && yDist < (this.height + enemy.height) / 2);
    }

    public void update(ArrayList<Platform> platforms) {
        Platform platform = this.onPlatform(platforms);
        if (platform == null) {
            this.velocity.add(this.acceleration);
            this.position.add(this.velocity);
        } else {
            this.velocity.y = 0;
            this.position.y = platform.position.y - height / 2;
        }

        this.acceleration.set(0, 0);
    }

    public void display() {
        displayObject.setX(position.x - width * 0.8);
        displayObject.setY(position.y - height * 0.9);
    }
}

class Bullet {
    private final Vector2D position;
    private final Vector2D velocity;
    private final double width;
    private int lifetime;
    private final Rectangle displayObject;
    Bullet(Vector2D position, Vector2D velocity, Group root) {
        this.position = position;
        this.velocity = velocity;

        this.lifetime = 100;

        this.width = 10;
        this.displayObject = new Rectangle(this.position.x, this.position.y, this.width, this.width);
        this.displayObject.setFill(Color.YELLOW);
        root.getChildren().add(this.displayObject);
    }

    boolean isDead(Group root) {
        if (lifetime <= 0) {
            root.getChildren().remove(this.displayObject);
            return true;
        }
        return false;
    }

    void checkCollision(ArrayList<Enemy> enemies) {
        for (Enemy enemy : enemies) {
            if (this.colliding(enemy)) {
                this.lifetime = 0;
                enemy.hit();
            }
        }
    }

    boolean colliding(Enemy enemy) {
        return (enemy.position.x - enemy.width / 2 <= this.position.x &&
                this.position.x <= enemy.position.x + enemy.width / 2 &&
                enemy.position.y - enemy.height / 2 <= this.position.y &&
                this.position.y <= enemy.position.y + enemy.height / 2);
    }

    void update() {
        position.add(velocity);

        lifetime--;
    }

    void display() {
        displayObject.setX(position.x - width / 2);
        displayObject.setY(position.y - width / 2);
    }
}

class Enemy {
    Vector2D position;
    double width;
    double height;
    static double speed = 2;
    double speedMulty;
    int health;
    private final ImageView displayObject;
    Enemy(double x, double y, double width, double height, int health, double speedMulty, Group root) {
        this.position = new Vector2D(x, y);
        this.width = width;
        this.height = height;

        this.health = health;
        this.speedMulty = speedMulty;

        displayObject = new ImageView(new Image("file:src/main/bat.png"));
        displayObject.setFitWidth(width * 2);
        displayObject.setFitHeight(height * 2);
        root.getChildren().add(displayObject);
    }

    public void hit() {
        this.health--;
    }

    public boolean isDead(Group root) {
        if (health <= 0) {
            root.getChildren().remove(this.displayObject);
            return true;
        }
        return false;
    }

    public static void increaseSpeed(double increase) {
        speed += increase;
    }

    public void update(Player player) {
        Vector2D difference = Vector2D.sub(player.position, this.position);
        difference.setMag(speed * this.speedMulty);

        this.position.add(difference);
    }

    public void display() {
        displayObject.setX(position.x - width);
        displayObject.setY(position.y - height);
    }
}

class Platform {
    Vector2D position;
    double height;
    double width;
    Rectangle displayObject;
    Platform(double x, double y, double width, Group root) {
        this.position = new Vector2D(x, y);
        this.width = width;
        this.height = 10;

        displayObject = new Rectangle(x, y, width, height);
        displayObject.setFill(Color.WHITE);
        root.getChildren().add(displayObject);
    }

    public void display() {
        displayObject.setX(position.x - width / 2);
        displayObject.setY(position.y);
    }
}

class Vector2D {
    public double x;
    public double y;
    Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }
    Vector2D(Vector2D vector) {
        this.x = vector.x;
        this.y = vector.y;
    }
    Vector2D() {
        this.x = 0;
        this.y = 0;
    }

    public void add(Vector2D vector) {
        this.x += vector.x;
        this.y += vector.y;
    }
    public void add(double x, double y) {
        this.x += x;
        this.y += y;
    }

    public static Vector2D sub(Vector2D v1, Vector2D v2) {
        return new Vector2D(v1.x - v2.x, v1.y - v2.y);
    }

    public void setMag(double amount) {
        this.div(this.mag());
        this.mult(amount);
    }
    public double mag() {
        return Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2));
    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void mult(double multiplier) {
        this.x *= multiplier;
        this.y *= multiplier;
    }
    public void div(double divider) {
        this.x /= divider;
        this.y /= divider;
    }

    Vector2D copy() {
        return new Vector2D(this.x, this.y);
    }

    public static double xDist(Vector2D vector1, Vector2D vector2) {
        return Math.abs(vector1.x - vector2.x);
    }
    public static double yDist(Vector2D vector1, Vector2D vector2) {
        return Math.abs(vector1.y - vector2.y);
    }
}