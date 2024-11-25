package com.example.finalproject;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class shooter extends Application {

    private static final double gunRadius = 20;  // Size of the gun (circle)
    private static final double bulletRadius = 5; // Bullet size
    private static final double bulletSpeed = 500;  // Speed of the bullet in pixels per second

    @Override
    public void start(Stage primaryStage) {
        // Create a basic gun
        Circle gun = new Circle(300, 300, gunRadius, Color.GRAY);

        // Create a pane to hold the gun and projectiles
        Pane root = new Pane();
        root.getChildren().add(gun);

        // Set up the scene
        Scene scene = new Scene(root, 600, 600);
        primaryStage.setTitle("Gun Shooter");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Add mouse click event handler
        scene.setOnMouseClicked(event -> shootGun(event, gun, root));
    }

    private void shootGun(MouseEvent event, Circle gun, Pane root) {
        // Get the mouse click coordinates
        double targetX = event.getSceneX();
        double targetY = event.getSceneY();

        // Create a projectile
        Circle projectile = new Circle(gun.getCenterX(), gun.getCenterY(), bulletRadius, Color.RED);

        // Add the projectile to the root pane
        root.getChildren().add(projectile);

        // Calculate direction from the gun to the mouse click
        double deltaX = targetX - gun.getCenterX();
        double deltaY = targetY - gun.getCenterY();

        // Normalize the direction vector to have unit length
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        double directionX = deltaX / distance;
        double directionY = deltaY / distance;

        // Set up the projectile's animation to move in the direction of the click
        TranslateTransition transition = new TranslateTransition();
        transition.setNode(projectile);

        // Move the projectile in the normalized direction
        transition.setByX(directionX * bulletSpeed);  // Move by X in the direction of the click
        transition.setByY(directionY * bulletSpeed);  // Move by Y in the direction of the click

        // Set the duration
        transition.setDuration(Duration.seconds(2));  // Duration depends on the BULLET_SPEED

        transition.play();

        // Remove the projectile once off-screen
        transition.setOnFinished(e -> root.getChildren().remove(projectile));
    }

    public static void main(String[] args) {
        launch(args);
    }
}

// need to merge the two so it moves and shoots in direction of a click