package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import logic.GameEngine;
import logic.GameState;

public class DefeatScene {
    public static void show(Stage stage,GameEngine gameEngine) {
        GameEngine.setGameState(GameState.GAME_OVER);
        VBox root = new VBox();
        root.setPadding(new Insets(100));
        root.setAlignment(Pos.CENTER);
        root.setSpacing(40);

        // ===== Background (TOP-CENTER pinned) =====
        /*Image bg = new Image(application.Main.class.getResource("/Background/Defeat.png").toExternalForm());
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

        // ===== Title =====
        Text title = new Text("DEFEATED");
        title.setStyle("""
            -fx-font-size: 120px;
            -fx-font-weight: bold;
            -fx-fill: #ff4c4c;
            -fx-stroke: black;
            -fx-stroke-width: 4;
        """);

        // ===== Back Button =====
        Button backBtn = createButton("BACK", 180, 60);
        backBtn.setOnMouseClicked(e -> StartScene.showMenu(stage, gameEngine));

        root.getChildren().addAll(title, backBtn);

        // ===== Window mode (same as other scenes) =====
        Scene scene = new Scene(root, 1280, 720);

        stage.setScene(scene);
        stage.setMaximized(false);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }

    private static Button createButton(String text, int width, int height) {
        Button btn = new Button(text);

        btn.setPrefWidth(width);
        btn.setPrefHeight(height);

        btn.setStyle("""
            -fx-font-size: 18px;
            -fx-font-weight: bold;
            -fx-text-fill: white;
            -fx-background-radius: 30;
            -fx-background-color: linear-gradient(#ff7a18, #ffb347);
            -fx-cursor: hand;
        """);

        // ===== Hover =====
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

        // ===== Press =====
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