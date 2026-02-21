package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import logic.GameEngine;
import logic.GameState;

public class BattleScene {

    public static void show(Stage stage, GameEngine gameEngine) {
        GameEngine.setGameState(GameState.BATTLE);
        VBox root = new VBox();
        root.setPadding(new Insets(100));
        root.setAlignment(Pos.CENTER);
        root.setSpacing(30);

        // ===== Background (TOP-CENTER pinned) =====
        /*Image bg = new Image(application.Main.class.getResource("/Background/Battle.png").toExternalForm());
        BackgroundImage bgImage = new BackgroundImage(
                bg,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(
                        100, 100, true, true, true, true // IMPORTANT: scale background
                )
        );
        root.setBackground(new Background(bgImage));*/

        Text title = new Text("Battle");
        title.setStyle("""
            -fx-font-size: 80px;
            -fx-font-weight: bold;
            -fx-fill: white;
            -fx-stroke: black;
            -fx-stroke-width: 3;
        """);

        Button winBtn = createButton("win", 180, 60);
        winBtn.setOnMouseClicked(e -> VictoryScene.show(stage, gameEngine));

        Button loseBtn = createButton("lose", 180, 60);
        loseBtn.setOnMouseClicked(e -> DefeatScene.show(stage, gameEngine));

        root.getChildren().addAll(title, winBtn, loseBtn);

        // ===== Window mode (same as your other scenes) =====
        Scene scene = new Scene(root, 1280, 720);
        stage.setScene(scene);
        stage.setMaximized(false);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }

    private static Button createButton(String string, int prefWidth, int prefHeight) {
        Button btn = new Button(string);

        btn.setPrefWidth(prefWidth);
        btn.setPrefHeight(prefHeight);

        btn.setStyle("""
            -fx-font-size: 18px;
            -fx-font-weight: bold;
            -fx-text-fill: white;
            -fx-background-radius: 30;
            -fx-background-color: linear-gradient(#ff7a18, #ffb347);
            -fx-cursor: hand;
        """);

        // hover
        btn.setOnMouseEntered(e -> {
            btn.setScaleX(1.08);
            btn.setScaleY(1.08);
            btn.setOpacity(0.9);
        });

        btn.setOnMouseExited(e -> {
            btn.setScaleX(1.0);
            btn.setScaleY(1.0);
            btn.setOpacity(1.0);
        });

        // click press effect
        btn.setOnMousePressed(e -> {
            btn.setScaleX(0.95);
            btn.setScaleY(0.95);
        });
        btn.setOnMouseReleased(e -> {
            btn.setScaleX(1.08);
            btn.setScaleY(1.08);
        });

        return btn;
    }
}