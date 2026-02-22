package gui;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import logic.GameEngine;
import logic.GameState;

public class DefeatScene {
    public static void show(Stage stage,GameEngine gameEngine) {
        GameEngine.setGameState(GameState.DEFEAT);
        VBox root = new VBox();
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.BOTTOM_CENTER);

        // ===== Background (TOP-CENTER pinned) =====
        Image bg = new Image(application.Main.class.getResource("/Background/Defeated.png").toExternalForm());
        BackgroundImage bgImage = new BackgroundImage(
                bg,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(
                        100, 100, true, true, true, true // IMPORTANT: scale background
                )
        );
        root.setBackground(new Background(bgImage));


        // ===== Back Button =====
        Button backBtn = createButton("/Button/Restart.png");
        backBtn.setOnMouseClicked(e -> StartScene.showMenu(stage, gameEngine));

        root.getChildren().addAll(backBtn);

        // ===== Window mode (same as other scenes) =====
        Scene scene = new Scene(root, 1280, 720);

        stage.setScene(scene);
        stage.setMaximized(false);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }

    private static Button createButton (String path){

        Image img = new Image(CharacterSelectionScene.class.getResourceAsStream(path));
        ImageView imageView = new ImageView(img);
        imageView.setFitWidth(250);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        Button btn = new Button();
        btn.setGraphic(imageView);

        btn.setStyle("""
                        -fx-background-color: transparent;
                        -fx-padding: 0;
                        -fx-cursor: hand;
                    """);

        DropShadow glow = new DropShadow();
        glow.setColor(Color.BLACK);
        glow.setRadius(20);

        btn.setEffect(glow);

        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(150), btn);
        scaleUp.setToX(1.1);
        scaleUp.setToY(1.1);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(150), btn);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        btn.setOnMouseEntered(e -> {
            scaleUp.playFromStart();
        });

        btn.setOnMouseExited(e -> {
            scaleDown.playFromStart();
        });

        TranslateTransition press = new TranslateTransition(Duration.millis(100), btn);
        press.setToY(3);

        TranslateTransition release = new TranslateTransition(Duration.millis(100), btn);
        release.setToY(0);

        btn.setOnMousePressed(e -> press.playFromStart());
        btn.setOnMouseReleased(e -> release.playFromStart());

        return btn;
    }
}