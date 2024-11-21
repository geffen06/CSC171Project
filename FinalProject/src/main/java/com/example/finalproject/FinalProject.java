package com.example.finalproject;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Iterator;

public class FinalProject extends Application {

    // Game constants
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int PLAYER_WIDTH = 50;
    private static final int PLAYER_HEIGHT = 30;
    private static final int BULLET_WIDTH = 5;
    private static final int BULLET_HEIGHT = 10;

    // Player and bullets
    private Rectangle player;
    private ArrayList<Rectangle> bullets = new ArrayList<>();
    private boolean moveLeft = false;
    private boolean moveRight = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Basic Shooter Game");

        // Create the root pane and scene
        Pane root = new Pane();
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        // Create the player (a simple rectangle)
        player = new Rectangle(WIDTH / 2 - PLAYER_WIDTH / 2, HEIGHT - PLAYER_HEIGHT - 20, PLAYER_WIDTH, PLAYER_HEIGHT);
        player.setFill(Color.BLUE);

        // Add the player to the root pane
        root.getChildren().add(player);

        // Set up key event handlers for moving the player and shooting
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT) {
                moveLeft = true;
            } else if (e.getCode() == KeyCode.RIGHT) {
                moveRight = true;
            } else if (e.getCode() == KeyCode.SPACE) {
                fireBullet();
            }
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.LEFT) {
                moveLeft = false;
            } else if (e.getCode() == KeyCode.RIGHT) {
                moveRight = false;
            }
        });

        // Set up the game loop
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Move the player
                if (moveLeft && player.getX() > 0) {
                    player.setX(player.getX() - 5);
                }
                if (moveRight && player.getX() < WIDTH - PLAYER_WIDTH) {
                    player.setX(player.getX() + 5);
                }

                // Update the bullets
                updateBullets();

                // Draw the scene
                root.getChildren().clear();
                root.getChildren().add(player);
                root.getChildren().addAll(bullets);
            }
        };

        // Start the game loop
        gameLoop.start();

        // Set the scene and show the stage
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Fire a bullet from the player's current position
    private void fireBullet() {
        Rectangle bullet = new Rectangle(player.getX() + PLAYER_WIDTH / 2 - BULLET_WIDTH / 2, player.getY(), BULLET_WIDTH, BULLET_HEIGHT);
        bullet.setFill(Color.RED);
        bullets.add(bullet);
    }

    // Update the bullets, moving them upward
    private void updateBullets() {
        Iterator<Rectangle> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            Rectangle bullet = iterator.next();
            bullet.setX(bullet.getX() - 10); // Move the bullet upward

            // Remove bullets that are off-screen
            if (bullet.getY() < 0) {
                iterator.remove();
            }
        }
    }
}