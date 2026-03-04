package gui;

import application.Main;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
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

/**
 * The {@code StartScene} class renders the main menu of the game.
 *
 * <p>This scene is the entry UI of the application. It is responsible for:</p>
 * <ul>
 *     <li>Resetting the game using {@link GameEngine#newGame()}</li>
 *     <li>Setting the game state to {@link GameState#START_GAME}</li>
 *     <li>Displaying the main menu background and logo</li>
 *     <li>Providing a start button that navigates to {@link CharacterSelectionScene}</li>
 * </ul>
 *
 * <p>UI behavior:</p>
 * <ul>
 *     <li>The start button is an image-based JavaFX button</li>
 *     <li>Hovering applies glow and scale-up animation</li>
 *     <li>Pressing applies a small "push down" translation</li>
 * </ul>
 *
 * @author Puttisan
 * @version 1.0
 */
public class StartScene {

    /**
     * Displays the main menu on the provided JavaFX stage.
     *
     * <p>This method resets and initializes the game state and constructs a fixed-size
     * scene (1280 by 720) with a background image, logo, and start button.</p>
     *
     * <p>Navigation:</p>
     * <ul>
     *     <li>Start button opens {@link CharacterSelectionScene#show(Stage, GameEngine)}</li>
     * </ul>
     *
     * @param stage the JavaFX stage where the menu scene will be displayed
     * @param gameEngine the main game engine instance used for state and navigation
     */
    public static void showMenu(Stage stage, GameEngine gameEngine) {
        gameEngine.newGame();
        GameEngine.setGameState(GameState.START_GAME);

        VBox root = new VBox();

        // ===== BACKGROUND =====
        Image bg = new Image(Main.class.getResource("/Background/Mainmenu.png").toExternalForm());
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

        // ===== CONTENT =====
        StackPane stack = new StackPane();

        ImageView logoView = new ImageView(
                new Image(Main.class.getResource("/Sign/Logo.png").toExternalForm())
        );
        logoView.setFitWidth(750);
        logoView.setPreserveRatio(true);

        Button startBtn = createStartButton();
        startBtn.setOnAction(e -> CharacterSelectionScene.show(stage, gameEngine));

        VBox content = new VBox(logoView, startBtn);
        content.setAlignment(Pos.CENTER);

        stack.getChildren().add(content);

        root.getChildren().add(stack);
        root.setAlignment(Pos.TOP_CENTER);

        Scene scene = new Scene(root, 1280, 720);

        stage.setTitle("legend of Progmeth!");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Creates the main menu "Start" button as an image-based button with animations.
     *
     * <p>The returned button uses {@code /Button/Start.png} and applies:</p>
     * <ul>
     *     <li>Glow effect and scale animation on hover</li>
     *     <li>Small translation on mouse press and release</li>
     * </ul>
     *
     * @return a configured JavaFX {@link Button} that visually represents the start button
     */
    public static Button createStartButton() {

        Image img = new Image(
                StartScene.class.getResourceAsStream("/Button/Start.png")
        );

        ImageView imageView = new ImageView(img);
        imageView.setFitWidth(250);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        Button startBtn = new Button();
        startBtn.setGraphic(imageView);

        startBtn.setStyle("""
            -fx-background-color: transparent;
            -fx-padding: 0;
            -fx-cursor: hand;
        """);

        DropShadow glow = new DropShadow();
        glow.setColor(Color.BLACK);
        glow.setRadius(20);

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

        TranslateTransition pressDown = new TranslateTransition(Duration.millis(100), startBtn);
        pressDown.setToY(3);

        TranslateTransition release = new TranslateTransition(Duration.millis(100), startBtn);
        release.setToY(0);

        startBtn.setOnMousePressed(e -> pressDown.playFromStart());
        startBtn.setOnMouseReleased(e -> release.playFromStart());

        return startBtn;
    }
}