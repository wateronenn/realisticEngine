package gui;

import application.Main;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import logic.GameEngine;

public class StartScene {

    public static void showMenu(Stage stage,GameEngine gameEngine) {
        gameEngine.newGame();

        VBox root = new VBox();

        // ===== BACKGROUND =====
        Image bg = new Image(Main.class.getResource("/Background/Mainmenu.png").toExternalForm());

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

        // ===== CONTENT =====
        StackPane stack = new StackPane();

        ImageView img1 = new ImageView(
                new Image(Main.class.getResource("/Sign/Logo.png").toExternalForm())
        );

        img1.setFitWidth(750);
        img1.setPreserveRatio(true);

        Button startBtn = createStartButton();
        startBtn.setOnAction(e -> CharacterSelectionScene.show(stage, gameEngine));

        VBox content = new VBox( img1, startBtn);
        content.setAlignment(Pos.CENTER);

        stack.getChildren().add(content);

        root.getChildren().add(stack);
        root.setAlignment(Pos.TOP_CENTER);

        Scene scene = new Scene(root, 1280, 720); // << fixed size

        stage.setTitle("legend of Progmeth!");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static Button createStartButton() {

        // Load Image
        Image img = new Image(
                StartScene.class.getResourceAsStream("/Button/Start.png")
        );

        ImageView imageView = new ImageView(img);
        imageView.setFitWidth(250);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        // Create button with image
        Button startBtn = new Button();
        startBtn.setGraphic(imageView);

        // Remove default button style
        startBtn.setStyle("""
            -fx-background-color: transparent;
            -fx-padding: 0;
            -fx-cursor: hand;
            """);

        // Glow effect
        DropShadow glow = new DropShadow();
        glow.setColor(Color.BLACK);
        glow.setRadius(20);

        // Hover Animation
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(150), startBtn);
        scaleUp.setToX(1.1);
        scaleUp.setToY(1.1);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(150), startBtn);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        startBtn.setOnMouseEntered(e -> {
            startBtn.setEffect(glow);
            scaleUp.playFromStart();
        });

        startBtn.setOnMouseExited(e -> {
            startBtn.setEffect(null);
            scaleDown.playFromStart();
        });

        // Press Effect
        TranslateTransition pressDown = new TranslateTransition(Duration.millis(100), startBtn);
        pressDown.setToY(3);

        TranslateTransition release = new TranslateTransition(Duration.millis(100), startBtn);
        release.setToY(0);

        startBtn.setOnMousePressed(e -> pressDown.playFromStart());
        startBtn.setOnMouseReleased(e -> release.playFromStart());

        return startBtn;
    }
}
