package com.example.gamefinal;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
// import javafx.geometry.Point2D;

public class GameFinal extends Application {
    private Rectangle player;

    @Override
    public void start(Stage stage) {
        // Create the root group and scene
        Group root = new Group();
        Scene scene = new Scene(root, 800, 600, Color.LIGHTGRAY);

        player = new Rectangle(400, 500, 50, 50);
        player.setFill(Color.BLUE);

        root.getChildren().add(player);

        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                player.setX(player.getX() + 5);

                root.getChildren().clear();
                root.getChildren().add(player);
            }
        };

        gameLoop.start();

        // Set the stage
        stage.setTitle("JavaFX Game");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}