package com.example.finalproject;

import javafx.animation.TranslateTransition;
import javafx.scene.input.MouseEvent;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;

public class merge extends Application {

    // Game constants
    private static final int width = 800;
    private static final int height = 600;
    private static final int playerWidth = 50;
    private static final int playerHeight = 30;
    private static final int bulletWidth = 5;
    private static final int bulletHeight = 5;

    // Player and bullets
    private Rectangle player;

    private ArrayList<Rectangle> bullets = new ArrayList<>();

    private boolean moveLeft = false;

    private boolean moveRight = false;

    private static final double bulletSpeed = 700;  // Speed of the bullet in pixels per second

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Basic Shooter Game");

        // Create the root pane and scene
        Pane root = new Pane();
        Scene scene = new Scene(root, width, height);

        // Create the player (a simple rectangle)
        player = new Rectangle((double) width / 2 - (double) playerWidth / 2, height - playerHeight - 20, playerWidth, playerHeight);
        player.setFill(Color.BLUE);

        // Add the player to the root pane
        root.getChildren().add(player);

        // Set up key event handlers for moving the player and shooting
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT) {
                moveLeft = true;
            } else if (e.getCode() == KeyCode.RIGHT) {
                moveRight = true;
            }
        });

        scene.setOnMouseClicked(event -> shootGun(event, player, root));

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
                if (moveRight && player.getX() < width - playerWidth) {
                    player.setX(player.getX() + 5);
                }

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

    private void shootGun(MouseEvent event, Rectangle gun, Pane root) {
        // Get the mouse click coordinates
        double targetX = event.getSceneX();
        double targetY = event.getSceneY();

        Rectangle bullet = new Rectangle(player.getX() + (double) playerWidth / 2 - (double) bulletWidth / 2, player.getY(), bulletWidth, bulletHeight);
        bullet.setFill(Color.RED);
        bullets.add(bullet);

        // Add the projectile to the root pane
        root.getChildren().add(bullet);

        // Calculate direction from the gun to the mouse click
        double deltaX = targetX - player.getX() + (double) playerWidth / 2 - (double) bulletWidth / 2;
        double deltaY = targetY - player.getY(), bulletWidth, bulletHeight;

        // Normalize the direction vector to have unit length
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        double directionX = deltaX / distance;
        double directionY = deltaY / distance;

        // Set up the projectile's animation to move in the direction of the click
        TranslateTransition transition = new TranslateTransition();
        transition.setNode(bullet);

        // Move the projectile in the normalized direction
        transition.setByX(directionX * bulletSpeed);  // Move by X in the direction of the click
        transition.setByY(directionY * bulletSpeed);  // Move by Y in the direction of the click

        // Set the duration
        transition.setDuration(Duration.seconds(2));  // Duration depends on the BULLET_SPEED

        transition.play();

        // Remove the projectile once off-screen
        transition.setOnFinished(e -> root.getChildren().remove(bullet));
        }
    }


// make it shoot towards your mouse