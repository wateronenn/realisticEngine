package gui;

import component.heroes.Heroes;
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
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import logic.GameEngine;
import logic.GameState;

/**
 * The {@code VictoryScene} class displays the victory screen after the player wins a battle.
 *
 * <p>This scene serves as a transition screen that shows the current hero team and then allows
 * the player to proceed to the upgrade screen.</p>
 *
 * <p>Main responsibilities:</p>
 * <ul>
 *     <li>Set the game state to {@link GameState#VICTORY}</li>
 *     <li>Clear any previously selected upgrade hero via {@link GameEngine#setUpgradeHero(Heroes)}</li>
 *     <li>Render a victory background and a title</li>
 *     <li>Display the current hero team icons from {@link GameEngine#getHeroTEAM()}</li>
 *     <li>Provide a "Next" button to navigate to {@link UpgradeScene#show(Stage, GameEngine)}</li>
 * </ul>
 *
 * <p>UI notes:</p>
 * <ul>
 *     <li>This scene uses a fixed window size of 1280 by 720 and disables resizing</li>
 *     <li>Hero icons are displayed with a drop shadow for depth</li>
 * </ul>
 *
 * @author Puttisan
 * @version 1.0
 */
public class VictoryScene {

    /**
     * Shows the victory screen.
     *
     * <p>This method:</p>
     * <ul>
     *     <li>Updates the global game state to {@link GameState#VICTORY}</li>
     *     <li>Resets the selected upgrade hero to {@code null}</li>
     *     <li>Builds and displays the JavaFX UI</li>
     * </ul>
     *
     * <p>Navigation:</p>
     * <ul>
     *     <li>Clicking "Next" opens {@link UpgradeScene}</li>
     * </ul>
     *
     * @param stage the primary JavaFX stage used to show the scene
     * @param gameEngine the main game engine instance used for navigation and global state
     */
    public static void show(Stage stage, GameEngine gameEngine) {
        GameEngine.setGameState(GameState.VICTORY);

        VBox root = new VBox();
        root.setPadding(new Insets(133, 0, 0, 0));
        root.setAlignment(Pos.TOP_CENTER);

        GameEngine.setUpgradeHero(null);

        // Background
        Image bg = new Image(application.Main.class.getResource("/Background/Victory.png").toExternalForm());
        BackgroundImage bgImage = new BackgroundImage(
                bg,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, true, true)
        );
        root.setBackground(new Background(bgImage));

        // Title
        Font font1 = Font.loadFont(
                CharacterSelectionScene.class.getResource("/Font/Supply_Center.ttf").toExternalForm(),
                30
        );
        Text title = new Text("Level  up");
        title.setFont(font1);

        // Hero team display
        HBox charSelect = new HBox();
        charSelect.setSpacing(60);
        charSelect.setPadding(new Insets(10));
        charSelect.setAlignment(Pos.CENTER);

        for (Heroes h : GameEngine.getHeroTEAM()) {

            VBox slot = new VBox();
            slot.setAlignment(Pos.CENTER);
            slot.setSpacing(20);

            String base = "/Heroes/" + h.getName() + "/";
            ImageView charImg = createCharacterImage(base + h.getName() + "Icon.PNG");

            Font font2 = Font.loadFont(
                    CharacterSelectionScene.class.getResource("/Font/Supply_Center.ttf").toExternalForm(),
                    15
            );
            Text scale = new Text("HP :  before > after\nATK : before > after\nDEF : before > after\n");
            scale.setFont(font2);
            scale.setLineSpacing(8);

            slot.getChildren().addAll(charImg, scale);
            charSelect.getChildren().add(slot);
        }

        // Next button
        Button nextBtn = createButton("/Button/Next.png");
        nextBtn.setOnMouseClicked(e -> UpgradeScene.show(stage, gameEngine));

        VBox center = new VBox(charSelect, nextBtn);
        center.setAlignment(Pos.CENTER);

        VBox centerWrap = new VBox(title, center);
        centerWrap.setAlignment(Pos.CENTER);
        VBox.setVgrow(centerWrap, Priority.ALWAYS);

        root.getChildren().addAll(centerWrap);

        // Window mode
        Scene scene = new Scene(root, 1280, 720);
        stage.setScene(scene);
        stage.setMaximized(false);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }

    /**
     * Creates a hero icon {@link ImageView} for display on the victory screen.
     *
     * <p>The icon is styled with a drop shadow and fixed sizing.</p>
     *
     * @param imagePath1 resource path to the hero icon image
     * @return an {@link ImageView} configured for this scene
     */
    public static ImageView createCharacterImage(String imagePath1) {

        DropShadow shadow = new DropShadow();
        shadow.setRadius(40);
        shadow.setSpread(0.2);
        shadow.setOffsetX(0);
        shadow.setOffsetY(15);
        shadow.setColor(Color.rgb(0, 0, 0, 0.85));

        Image img1 = new Image(CharacterSelectionScene.class.getResourceAsStream(imagePath1));
        ImageView iv1 = new ImageView(img1);

        iv1.setFitWidth(150);
        iv1.setFitHeight(150);
        iv1.setPreserveRatio(true);
        iv1.setSmooth(true);
        iv1.setEffect(shadow);

        return iv1;
    }

    /**
     * Creates an image-based button with hover scaling, glow effect, and press animation.
     *
     * <p>This helper is used for the "Next" button.</p>
     *
     * @param path resource path of the button image
     * @return a styled JavaFX {@link Button}
     */
    private static Button createButton(String path) {

        Image img = new Image(CharacterSelectionScene.class.getResourceAsStream(path));
        ImageView imageView = new ImageView(img);
        imageView.setFitWidth(130);
        imageView.setFitHeight(100);
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

        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(150), btn);
        scaleUp.setToX(1.1);
        scaleUp.setToY(1.1);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(150), btn);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        btn.setOnMouseEntered(e -> {
            btn.setEffect(glow);
            scaleUp.playFromStart();
        });

        btn.setOnMouseExited(e -> {
            btn.setEffect(null);
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