package gui;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import logic.GameEngine;
import logic.GameState;
import logic.MusicPlayer;

/**
 * The {@code DefeatScene} class renders the defeat screen shown when the player loses a battle.
 *
 * <p>This scene:</p>
 * <ul>
 *     <li>Sets the global game state to {@link GameState#DEFEAT}</li>
 *     <li>Shows a defeat background image</li>
 *     <li>Provides a restart button that returns the player to the main menu</li>
 * </ul>
 *
 * <p>The UI uses image-based buttons with hover and press animations.</p>
 *
 * @author Puttisan
 * @version 1.0
 */
public class DefeatScene {

    /**
     * Displays the defeat scene on the provided stage.
     *
     * <p>This method:</p>
     * <ul>
     *     <li>Updates game state to {@link GameState#DEFEAT}</li>
     *     <li>Builds a full-screen background using {@code /Background/Defeated.png}</li>
     *     <li>Creates a restart button that navigates back to {@code StartScene}</li>
     *     <li>Sets window size to 1280 by 720 and disables resizing</li>
     * </ul>
     *
     * @param stage the JavaFX stage where the scene will be displayed
     * @param gameEngine the main game engine instance used for navigation
     */
    public static void show(Stage stage, GameEngine gameEngine) {
        GameEngine.setGameState(GameState.DEFEAT);
        MusicPlayer.playMusic(GameState.DEFEAT);
        VBox root = new VBox();
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.BOTTOM_CENTER);

        // ===== Background =====
        Image bg = new Image(application.Main.class.getResource("/Background/Defeated.png").toExternalForm());
        BackgroundImage bgImage = new BackgroundImage(
                bg,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(
                        100, 100, true, true, true, true
                )
        );
        root.setBackground(new Background(bgImage));

        // ===== Restart Button =====
        Button backBtn = createButton("/Button/Restart.png");
        backBtn.setOnMouseClicked(e -> StartScene.showMenu(stage, gameEngine));

        root.getChildren().addAll(backBtn);

        // ===== Window mode =====
        Scene scene = new Scene(root, 1280, 720);

        stage.setScene(scene);
        stage.setMaximized(false);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }

    /**
     * Creates an image-based button with hover scaling and press translation animations.
     *
     * <p>The created button:</p>
     * <ul>
     *     <li>Uses the provided image resource as its graphic</li>
     *     <li>Applies a constant drop shadow glow effect</li>
     *     <li>Scales up slightly when hovered</li>
     *     <li>Moves down slightly on press and returns on release</li>
     * </ul>
     *
     * @param path resource path to the image used for the button graphic
     * @return a configured JavaFX {@link Button}
     */
    private static Button createButton(String path) {

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

        // Always-on glow
        btn.setEffect(glow);

        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(150), btn);
        scaleUp.setToX(1.1);
        scaleUp.setToY(1.1);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(150), btn);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        btn.setOnMouseEntered(e -> scaleUp.playFromStart());
        btn.setOnMouseExited(e -> scaleDown.playFromStart());

        TranslateTransition press = new TranslateTransition(Duration.millis(100), btn);
        press.setToY(3);

        TranslateTransition release = new TranslateTransition(Duration.millis(100), btn);
        release.setToY(0);

        btn.setOnMousePressed(e -> press.playFromStart());
        btn.setOnMouseReleased(e -> release.playFromStart());

        return btn;
    }
}