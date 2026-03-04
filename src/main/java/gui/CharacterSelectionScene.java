package gui;

import component.heroes.Heroes;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
 * The {@code CharacterSelectionScene} class renders the team selection screen.
 *
 * <p>This scene allows the player to:</p>
 * <ul>
 *     <li>View available heroes from {@link GameEngine#getAvailableHeroes()}</li>
 *     <li>Select and deselect heroes into the team (up to 3 members)</li>
 *     <li>View hero preview information (name, stats label, skill, and ultimate description)</li>
 *     <li>Proceed to the next scene when the team is complete</li>
 * </ul>
 *
 * <p>Selection behavior:</p>
 * <ul>
 *     <li>Clicking a hero portrait toggles an information view for that hero</li>
 *     <li>Only one hero information view can be expanded at a time</li>
 *     <li>Clicking the "Choose" button toggles hero membership in the team</li>
 * </ul>
 *
 * <p>This class is designed as a static scene utility with helper methods
 * for building interactive image-based buttons.</p>
 *
 * @author Puttisan
 * @version 1.0
 */
public class CharacterSelectionScene {

    /**
     * A callback used to reset the currently expanded hero portrait, ensuring that
     * only one hero information panel is expanded at a time.
     */
    private static Runnable resetCurrentSelection = null;

    /**
     * Static text data used to display hero details in the portrait scroll overlay.
     *
     * <p>Each row corresponds to one hero, and columns are:</p>
     * <ul>
     *     <li>0: hero name</li>
     *     <li>1: stats placeholder text</li>
     *     <li>2: skill name label</li>
     *     <li>3: skill description</li>
     *     <li>4: ultimate name label</li>
     *     <li>5: ultimate description</li>
     * </ul>
     *
     * <p>Note: values are currently placeholders for HP, ATK, and DEF.</p>
     */
    private static String discription[][] = {
            {"Caster", "HP :\nATK :\nDEF :", "Skill – Arcane Pulse", "Deal light magic damage and empower self.", "Ult – Arcane Cataclysm", "Blast all enemies with devastating magic."},
            {"Archer", "HP :\nATK :\nDEF :", "Skill – Arrow Stock", "Add 1 arrow to the quiver.", "Ult – Arrow Rain", "Unleash all stored arrows, dealing massive AoE damage."},
            {"Tank", "HP :\nATK :\nDEF :", "Skill – Guardian’s Mend", "Restore HP to a selected ally.", "Ult – Aegis Command", "Grant shields and boost all allies."},
            {"Fighter", "HP :\nATK :\nDEF :", "Skill – Lifesteal Strike", "Attack an enemy and recover HP.", "Ult – Execution Breaker", "Ignore all defense and deal massive true damage."}
    };

    /**
     * Displays the character selection scene on the provided stage.
     *
     * <p>This method:</p>
     * <ul>
     *     <li>Sets the game state to {@link GameState#SELECT_TEAM}</li>
     *     <li>Builds the background and main layout</li>
     *     <li>Creates hero portrait buttons and "Choose" buttons for team selection</li>
     *     <li>Creates a start button that proceeds only when the team is full</li>
     * </ul>
     *
     * @param stage the JavaFX stage where this scene is shown
     * @param gameEngine the main game engine instance used for navigation and state
     */
    public static void show(Stage stage, GameEngine gameEngine) {
        GameEngine.setGameState(GameState.SELECT_TEAM);
        VBox root = new VBox();
        root.setPadding(new Insets(10));
        root.setSpacing(130);
        root.setAlignment(Pos.TOP_CENTER);

        // ===== Background =====
        Image bg = new Image(application.Main.class.getResource("/Background/Selection.png").toExternalForm());
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

        // ===== Back Button (top-left) =====
        Button backBtn = createButton("/Button/Back.png");
        backBtn.setOnAction(e -> StartScene.showMenu(stage, gameEngine));

        HBox topBar = new HBox(backBtn);
        topBar.setAlignment(Pos.TOP_LEFT);

        // ===== Character Slots =====
        HBox charSelect = new HBox();
        charSelect.setAlignment(Pos.CENTER);

        int index = 0;
        for (Heroes h : GameEngine.getAvailableHeroes()) {
            VBox slot = new VBox();
            slot.setSpacing(15);
            slot.setAlignment(Pos.CENTER);

            String base = "/Heroes/" + h.getName() + "/";

            Button charBtn = createCharacterButton(
                    base + h.getName() + "Still.PNG",
                    base + h.getName() + "Attack.PNG",
                    index
            );

            Button pickBtn = createButton("/Button/Choose.png");
            pickBtn.setOnAction(e -> pickBtnOnClickHandler(gameEngine, h, pickBtn));

            slot.getChildren().addAll(charBtn, pickBtn);
            charSelect.getChildren().add(slot);

            index++;
        }

        // ===== Start Button =====
        Button startBtn = createButton("/Button/Start.png");
        startBtn.setOnAction(e -> startBtnOnClickHandler(stage, gameEngine));

        VBox center = new VBox(charSelect, startBtn);
        center.setAlignment(Pos.CENTER);

        // Keep center truly centered even when window resized
        StackPane centerWrap = new StackPane(center);
        centerWrap.setAlignment(Pos.CENTER);
        VBox.setVgrow(centerWrap, Priority.ALWAYS);

        // Add to root
        root.getChildren().addAll(topBar, centerWrap);

        Scene scene = new Scene(root, 1280, 720);

        stage.setScene(scene);
        stage.setMaximized(false);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }

    /**
     * Handles the Start button click.
     *
     * <p>If the team is full (exactly 3 heroes), this proceeds to the roll element scene.
     * Otherwise, an error alert is shown to the player.</p>
     *
     * @param stage the current JavaFX stage
     * @param gameEngine the game engine used to validate team size and navigate to the next scene
     */
    private static void startBtnOnClickHandler(Stage stage, GameEngine gameEngine) {
        if (gameEngine.checkFullTeam()) {
            RollElementScene.show(stage, gameEngine);
        } else {
            Alert teamNotFullAlert = new Alert(Alert.AlertType.ERROR);
            teamNotFullAlert.setTitle("Not Enough Member");
            teamNotFullAlert.setHeaderText("Your Team is not Ready");
            teamNotFullAlert.setContentText("You must pick 3 Heroes to go!");
            teamNotFullAlert.showAndWait();
        }
    }

    /**
     * Handles the Choose or Discard button behavior for a specific hero.
     *
     * <p>This method toggles hero membership in the team via
     * {@link GameEngine#toggleTeamMember(Heroes)} and updates the button graphic accordingly.</p>
     *
     * <p>If adding the hero would exceed the maximum team size, an error alert is shown
     * and the selection change is not applied.</p>
     *
     * @param gameEngine the game engine used to manage team membership
     * @param hero the hero to be toggled in or out of the team
     * @param pickBtn the button whose image is updated to reflect current selection state
     */
    private static void pickBtnOnClickHandler(GameEngine gameEngine, Heroes hero, Button pickBtn) {

        boolean checkAddTeamMember = GameEngine.toggleTeamMember(hero);

        if (!checkAddTeamMember) {
            Alert limitAlert = new Alert(Alert.AlertType.ERROR);
            limitAlert.setTitle("Team exceed limit size");
            limitAlert.setHeaderText("Exceed limit team size");
            limitAlert.setContentText("You can pick only up to 3 people !!!");
            limitAlert.showAndWait();
            return;
        }

        ImageView imageView = (ImageView) pickBtn.getGraphic();

        if (gameEngine.isInTeam(hero)) {
            Image unpickImg = new Image(CharacterSelectionScene.class.getResourceAsStream("/Button/Discard.png"));
            imageView.setImage(unpickImg);
            imageView.setFitHeight(130);
            imageView.setPreserveRatio(true);
        } else {
            Image pickImg = new Image(CharacterSelectionScene.class.getResourceAsStream("/Button/Choose.png"));
            imageView.setImage(pickImg);
            imageView.setFitHeight(130);
            imageView.setPreserveRatio(true);
        }
    }

    /**
     * Creates a clickable hero portrait button that can expand into an information panel.
     *
     * <p>Behavior:</p>
     * <ul>
     *     <li>Hover: temporarily swaps to an attack image if the button is not selected</li>
     *     <li>Click: toggles selection and shows a scroll overlay containing hero descriptions</li>
     *     <li>Only one hero button can be expanded at a time using {@link #resetCurrentSelection}</li>
     * </ul>
     *
     * <p>The expanded view uses a scroll image as background and overlays
     * text from {@link #discription} using the provided index.</p>
     *
     * @param imagePath1 resource path for the hero idle image
     * @param imagePath2 resource path for the hero attack image
     * @param index index used to select text from {@link #discription}
     * @return a configured JavaFX {@link Button} representing a hero portrait
     */
    public static Button createCharacterButton(String imagePath1, String imagePath2, int index) {

        DropShadow shadow = new DropShadow();
        shadow.setRadius(40);
        shadow.setSpread(0.4);
        shadow.setOffsetX(0);
        shadow.setOffsetY(15);
        shadow.setColor(Color.rgb(0, 0, 0, 0.85));

        Image img1 = new Image(CharacterSelectionScene.class.getResourceAsStream(imagePath1));
        ImageView iv1 = new ImageView(img1);

        Image img2 = new Image(CharacterSelectionScene.class.getResourceAsStream(imagePath2));
        ImageView iv2 = new ImageView(img2);

        Image img3 = new Image(CharacterSelectionScene.class.getResourceAsStream("/Sign/Scroll.png"));
        ImageView iv3 = new ImageView(img3);

        int[] fit = {350, 350, 300};
        ImageView[] views = {iv1, iv2, iv3};
        for (int i = 0; i < 3; i++) {
            views[i].setFitWidth(fit[i]);
            views[i].setFitHeight(fit[i]);
            views[i].setPreserveRatio(true);
            views[i].setSmooth(true);
            views[i].setEffect(shadow);
        }

        Font font1 = Font.loadFont(CharacterSelectionScene.class.getResource("/Font/Supply_Center.ttf").toExternalForm(), 15);
        Font font2 = Font.loadFont(CharacterSelectionScene.class.getResource("/Font/Supply_Center.ttf").toExternalForm(), 10);
        Font font3 = Font.loadFont(CharacterSelectionScene.class.getResource("/Font/Supply_Center.ttf").toExternalForm(), 8);

        Text name = new Text(discription[index][0]);
        name.setWrappingWidth(fit[2] * 0.65);
        name.setFont(font1);

        Text stat = new Text(discription[index][1]);
        stat.setWrappingWidth(fit[2] * 0.65);
        stat.setFont(font2);
        stat.setLineSpacing(5);

        Text skill = new Text(discription[index][2]);
        skill.setWrappingWidth(fit[2] * 0.65);
        skill.setFont(font2);

        Text skillDis = new Text(discription[index][3]);
        skillDis.setWrappingWidth(fit[2] * 0.65);
        skillDis.setFont(font3);

        Text ultimate = new Text(discription[index][4]);
        ultimate.setWrappingWidth(fit[2] * 0.65);
        ultimate.setFont(font2);

        Text ultimateDis = new Text(discription[index][5]);
        ultimateDis.setWrappingWidth(fit[2] * 0.65);
        ultimateDis.setFont(font3);

        VBox content = new VBox(name, stat, skill, skillDis, ultimate, ultimateDis);
        content.setSpacing(5);

        StackPane back = new StackPane(iv3, content);
        StackPane.setMargin(content, new Insets(70));

        Button btn = new Button();
        btn.setAlignment(Pos.CENTER);
        btn.setStyle("-fx-background-color: transparent;");
        btn.setPrefSize(300, 300);
        btn.setMinSize(300, 300);
        btn.setMaxSize(300, 300);

        StackPane wrapper = new StackPane(iv1);
        wrapper.setPrefSize(350, 350);
        btn.setGraphic(wrapper);

        final boolean[] selected = {false};

        Runnable animateToNormal = () -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), wrapper);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        };

        Runnable animateToBig = () -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), wrapper);
            st.setToX(1.25);
            st.setToY(1.25);
            st.play();
        };

        Runnable resetThisButton = () -> {
            selected[0] = false;
            wrapper.getChildren().setAll(iv1);
            animateToNormal.run();
        };

        btn.setOnMouseEntered(e -> {
            if (!selected[0]) wrapper.getChildren().setAll(iv2);
            btn.setScaleX(1.05);
            btn.setScaleY(1.05);
        });

        btn.setOnMouseExited(e -> {
            if (!selected[0]) wrapper.getChildren().setAll(iv1);
            btn.setScaleX(1.0);
            btn.setScaleY(1.0);
        });

        btn.setOnMouseClicked(e -> {
            if (!selected[0] && resetCurrentSelection != null) {
                resetCurrentSelection.run();
            }

            selected[0] = !selected[0];

            if (selected[0]) {
                wrapper.getChildren().setAll(back);
                animateToBig.run();
                resetCurrentSelection = resetThisButton;
            } else {
                resetThisButton.run();
                if (resetCurrentSelection == resetThisButton) {
                    resetCurrentSelection = null;
                }
            }
        });

        return btn;
    }

    /**
     * Creates a generic image-based button used in the selection UI.
     *
     * <p>This button supports:</p>
     * <ul>
     *     <li>Hover glow with scale-up animation</li>
     *     <li>Press and release translate animation to simulate a click</li>
     * </ul>
     *
     * @param path resource path to the button image
     * @return a styled JavaFX {@link Button} with hover and press animations
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