package application;

import gui.CharacterSelectionScene;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import logic.GameEngine;

import static gui.StartScene.showMenu;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        GameEngine gameEngine = new GameEngine();
        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });

        showMenu(primaryStage,gameEngine);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
