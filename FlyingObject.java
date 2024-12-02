package com.example.csc171_game;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FlyingObject extends Application {

    private static final double WIDTH = 800;
    private static final double HEIGHT = 600;

    private static final double PLAYER_SIZE = 40;
    private static final double OBSTACLE_WIDTH = 50;
    private static final double OBSTACLE_HEIGHT = 30;
    private static final double OBSTACLE_SPEED = 3;

    private Rectangle player;
    private List<Rectangle> obstacles = new ArrayList<>();
    private Random random = new Random();
    private boolean gameOver = false;

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        // Create the player (blue box)
        player = new Rectangle(PLAYER_SIZE, PLAYER_SIZE, Color.BLUE);
        player.setTranslateX(WIDTH / 4); // Starting position
        player.setTranslateY(HEIGHT / 2);

        root.getChildren().add(player);

        // Generate flying obstacles
        AnimationTimer obstacleGenerator = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now % 60 == 0) { // Spawn obstacles periodically
                    createObstacle(root);
                }
            }
        };
        obstacleGenerator.start();

        // Move obstacles and check for collisions
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (gameOver) {
                    this.stop();
                    obstacleGenerator.stop();
                    System.out.println("Game Over!");
                    return;
                }

                // Move obstacles
                for (Rectangle obstacle : obstacles) {
                    obstacle.setTranslateX(obstacle.getTranslateX() - OBSTACLE_SPEED);
                }

                // Check for collisions
                for (Rectangle obstacle : obstacles) {
                    if (player.getBoundsInParent().intersects(obstacle.getBoundsInParent())) {
                        gameOver = true;
                    }
                }

                // Remove obstacles that have moved off-screen
                obstacles.removeIf(obstacle -> obstacle.getTranslateX() + OBSTACLE_WIDTH < 0);
                root.getChildren().removeIf(node -> node instanceof Rectangle && node.getTranslateX() + OBSTACLE_WIDTH < 0);
            }
        };
        gameLoop.start();

        // Player movement
        scene.setOnKeyPressed(event -> {
            if (gameOver) return;

            double x = player.getTranslateX();
            double y = player.getTranslateY();

            if (event.getCode() == KeyCode.UP && y > 0) {
                player.setTranslateY(y - 10);
            } else if (event.getCode() == KeyCode.DOWN && y + PLAYER_SIZE < HEIGHT) {
                player.setTranslateY(y + 10);
            } else if (event.getCode() == KeyCode.LEFT && x > 0) {
                player.setTranslateX(x - 10);
            } else if (event.getCode() == KeyCode.RIGHT && x + PLAYER_SIZE < WIDTH) {
                player.setTranslateX(x + 10);
            }
        });

        primaryStage.setTitle("Flying Obstacle Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void createObstacle(Pane root) {
        Rectangle obstacle = new Rectangle(OBSTACLE_WIDTH, OBSTACLE_HEIGHT, Color.RED);
        double randomY = random.nextDouble() * (HEIGHT - OBSTACLE_HEIGHT);
        obstacle.setTranslateX(WIDTH);
        obstacle.setTranslateY(randomY);

        obstacles.add(obstacle);
        root.getChildren().add(obstacle);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
