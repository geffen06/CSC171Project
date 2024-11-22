package com.example.gamefinal;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;

public class GameFinal extends Application {
    private int width;
    private int height;

    private Vector2D gravity;
    private Player player;

    private ArrayList<Platform> platforms;
    void createPlatforms(int width, int height) {
        platforms = new ArrayList<>();
        platforms.add(new Platform((double) width / 2, (double) height / 2, 200));
    }

    void displayPlatforms(Group root) {
        for (Platform platform : platforms) {
            platform.display(root);
        }
    }

    @Override
    public void start(Stage stage) {
        // Create the root group and scene
        Group root = new Group();

        width = 800;
        height = 600;
        Scene scene = new Scene(root, width, height, Color.LIGHTGRAY);

        gravity = new Vector2D(0, 0.2);
        player = new Player((double) width / 2, 0, 50, 50);

        createPlatforms(width, height);

        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                width = (int)scene.getWidth();
                height = (int)scene.getHeight();

                player.addForce(gravity);
                player.update(platforms);
                player.bounce(height);

                root.getChildren().clear();
                displayPlatforms(root);
                player.display(root);
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

class Player {
    Vector2D position;
    Vector2D velocity;
    Vector2D acceleration;
    Vector2D bottomPosition;
    double width;
    double height;
    Rectangle displayObject;
    Player(double x, double y, double width, double height) {
        this.position = new Vector2D(x, y);
        this.velocity = new Vector2D(0, 0);
        this.acceleration = new Vector2D(0, 0);

        this.width = width;
        this.height = height;

        this.bottomPosition = new Vector2D(this.position);
        this.bottomPosition.add(0, this.height / 2);

        displayObject = new Rectangle(x, y, width, height);
        displayObject.setFill(Color.BLUE);
    }

    public void addForce(Vector2D force) {
        acceleration.add(force);
    }

    public void bounce(int windowHeight) {
        if (position.y > windowHeight - this.height) {
            velocity.y = -velocity.y;
            position.y = windowHeight - this.height;
        }
    }

    public Platform onPlatform(ArrayList<Platform> platforms) {
        this.bottomPosition = new Vector2D(this.position);
        this.bottomPosition.add(0, this.height / 2);

        for (Platform platform : platforms) {
            if (
                    Vector2D.xDist(this.position, platform.position) < (this.width + platform.width) / 2 &&
                            Vector2D.yDist(this.bottomPosition, platform.position) < this.velocity.y &&
                            this.velocity.y > 0
            ) {
                return platform;
            }
        }
        return null;
    }

    public void update(ArrayList<Platform> platforms) {
        Platform platform = this.onPlatform(platforms);
        if (platform == null) {
            this.velocity.add(this.acceleration);
            this.position.add(this.velocity);
            this.acceleration.set(0, 0);
        } else {
            this.velocity.y = 0;
            this.position.y = platform.position.y - height / 2;
        }
    }

    public void display(Group root) {
        displayObject.setX(position.x - width / 2);
        displayObject.setY(position.y - height / 2);

        root.getChildren().add(displayObject);
    }
}

class Platform {
    Vector2D position;
    double height;
    double width;
    Rectangle displayObject;
    Platform(double x, double y, double width) {
        this.position = new Vector2D(x, y);
        this.width = width;
        this.height = 10;

        displayObject = new Rectangle(x, y, width, height);
        displayObject.setFill(Color.BLACK);
    }

    public void display(Group root) {
        displayObject.setX(position.x - width / 2);
        displayObject.setY(position.y);

        root.getChildren().add(displayObject);
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

    public void add(Vector2D vector) {
        this.x += vector.x;
        this.y += vector.y;
    }
    public void add(double x, double y) {
        this.x += x;
        this.y += y;
    }

    public void set(Vector2D vector) {
        this.x = vector.x;
        this.y = vector.y;
    }
    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static double xDist(Vector2D vector1, Vector2D vector2) {
        return Math.abs(vector1.x - vector2.x);
    }
    public static double yDist(Vector2D vector1, Vector2D vector2) {
        return Math.abs(vector1.y - vector2.y);
    }
}