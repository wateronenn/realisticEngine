package gui;

import component.Element;
import component.Monster;
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
import logic.MusicPlayer;
import logic.RandomElementGenerator;

import java.util.ArrayList;

/**
 * The {@code RollElementScene} class renders the element rolling screen shown before a battle.
 *
 * <p>This scene is responsible for:</p>
 * <ul>
 *     <li>Advancing the stage counter (battle stage progression)</li>
 *     <li>Spawning a new monster team using {@link GameEngine#setMonsterTeam()}</li>
 *     <li>Assigning random elements to monsters and heroes via {@link RandomElementGenerator}</li>
 *     <li>Allowing the player to re-roll hero elements up to a fixed limit</li>
 *     <li>Providing a start button to enter the battle scene</li>
 * </ul>
 *
 * <p>Reroll behavior:</p>
 * <ul>
 *     <li>At the start of this scene, reroll count is reset to zero</li>
 *     <li>The player can reroll hero elements up to {@link GameEngine#getMaxReroll()}</li>
 *     <li>Rerolling updates the hero element display and the remaining roll counter</li>
 * </ul>
 *
 * <p>UI notes:</p>
 * <ul>
 *     <li>The left panel shows the roll title and the element effectiveness table</li>
 *     <li>The right panel shows monster elements, hero elements, and action buttons</li>
 * </ul>
 *
 * @author Puttisan
 * @version 1.0
 */
public class RollElementScene {

    /**
     * Displays the roll element scene on the provided stage.
     *
     * <p>This method performs game-state setup, creates UI panels, rolls initial elements,
     * and shows the screen at 1280 by 720 resolution.</p>
     *
     * <p>Side effects:</p>
     * <ul>
     *     <li>Increases stage counter by 1</li>
     *     <li>Resets reroll count to 0</li>
     *     <li>Sets game state to {@link GameState#ROLL_ELEMENT}</li>
     *     <li>Spawns a new monster team</li>
     *     <li>Assigns random elements to both teams</li>
     * </ul>
     *
     * @param stage the JavaFX stage where this scene is shown
     * @param gameEngine the main game engine instance used for navigation and state
     */
    public static void show(Stage stage, GameEngine gameEngine) {
        GameEngine.addStageCounter(1);
        GameEngine.setCountReroll(0);
        GameEngine.setGameState(GameState.ROLL_ELEMENT);
        MusicPlayer.playMusic(GameState.START_GAME);
        HBox root = new HBox();
        root.setPadding(new Insets(100));
        root.setSpacing(30);
        root.setAlignment(Pos.CENTER);

        // ===== Background =====
        Image bg = new Image(application.Main.class.getResource("/Background/RollElement.png").toExternalForm());
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

        // ===== Left Panel (rightPanel variable name kept as original code) =====
        VBox rightPanel = new VBox();
        rightPanel.setAlignment(Pos.CENTER);
        rightPanel.setSpacing(15);

        Image img1 = new Image(RollElementScene.class.getResourceAsStream("/Sign/Monster's_Element.png"));
        ImageView description1 = new ImageView(img1);
        description1.setFitWidth(450);
        description1.setPreserveRatio(true);

        Image img2 = new Image(RollElementScene.class.getResourceAsStream("/Sign/Hero's_Element.png"));
        ImageView description2 = new ImageView(img2);
        description2.setFitWidth(450);
        description2.setPreserveRatio(true);

        // ===== Monster Elements =====
        HBox monsterElementBox = new HBox();
        monsterElementBox.setSpacing(50);
        monsterElementBox.setPadding(new Insets(5));
        monsterElementBox.setAlignment(Pos.CENTER);

        GameEngine.setMonsterTeam();
        ArrayList<Monster> monstersTeam = GameEngine.getMonsterTeam();
        ArrayList<Element> monsterElement = RandomElementGenerator.getRandomElement(monstersTeam);

        for (Element e : monsterElement) {
            Image img = RandomElementGenerator.getElementImage(e);
            ImageView imgView = new ImageView(img);
            imgView.setFitWidth(100);
            imgView.setFitHeight(100);
            imgView.setPreserveRatio(true);
            monsterElementBox.getChildren().add(imgView);
        }

        int leftRoll = Math.max(0, (GameEngine.getMaxReroll() - GameEngine.getCountReroll()));
        Font font = Font.loadFont(CharacterSelectionScene.class.getResource("/Font/Supply_Center.ttf").toExternalForm(), 15);
        Text leftRollText = new Text("Roll  left : " + leftRoll + " / 3");
        leftRollText.setFont(font);
        leftRollText.setStyle("-fx-fill: #4d2c12;");

        // ===== Hero Elements =====
        HBox heroElementBox = new HBox();
        heroElementBox.setPadding(new Insets(5));
        heroElementBox.setAlignment(Pos.CENTER);

        ArrayList<Heroes> heroesTeam = GameEngine.getHeroTEAM();
        ArrayList<Element> heroElement = RandomElementGenerator.getRandomElement(heroesTeam);

        int index = 0;
        for (Element e : heroElement) {

            Image img = RandomElementGenerator.getElementImage(e);
            ImageView imgView = new ImageView(img);
            imgView.setFitWidth(100);
            imgView.setFitHeight(100);
            imgView.setPreserveRatio(true);

            DropShadow shadow = new DropShadow();
            shadow.setRadius(20);
            shadow.setSpread(0.4);
            shadow.setOffsetX(0);
            shadow.setOffsetY(15);
            shadow.setColor(Color.rgb(0, 0, 0, 0.85));

            Image iconImg = new Image(CharacterSelectionScene.class.getResourceAsStream(
                    "/Heroes/" + heroesTeam.get(index).getName() + "/" + heroesTeam.get(index).getName() + "Icon.PNG"
            ));
            ImageView iv = new ImageView(iconImg);

            iv.setFitWidth(100);
            iv.setFitHeight(100);
            iv.setPreserveRatio(true);
            iv.setSmooth(true);
            iv.setEffect(shadow);

            StackPane overlay = new StackPane(imgView, iv);
            StackPane.setAlignment(imgView, Pos.TOP_RIGHT);
            overlay.setPrefSize(175, 175);

            heroElementBox.getChildren().add(overlay);
            index++;
        }

        // ===== Buttons =====
        Button rollBtn = createButton("/Button/Roll.png");
        rollBtn.setOnAction(e -> rollBtnHandler(leftRollText, heroElementBox));

        Button startBtn = createButton("/Button/Start.png");
        startBtn.setOnAction(e -> BattleScene.show(stage, gameEngine));

        HBox bothBtn = new HBox(rollBtn, startBtn);
        bothBtn.setSpacing(50);
        bothBtn.setAlignment(Pos.CENTER);

        VBox center = new VBox(monsterElementBox, description2, leftRollText, heroElementBox, bothBtn);
        center.setAlignment(Pos.CENTER);
        center.setSpacing(15);

        rightPanel.getChildren().addAll(description1, center);

        // ===== Right Panel (leftPanel variable name kept as original code) =====
        VBox leftPanel = new VBox();
        leftPanel.setAlignment(Pos.TOP_CENTER);
        leftPanel.setSpacing(20);

        Image titleImg = new Image(RollElementScene.class.getResourceAsStream("/Sign/RollElement.png"));
        ImageView titleView = new ImageView(titleImg);
        titleView.setFitWidth(500);
        titleView.setPreserveRatio(true);
        titleView.setSmooth(true);

        Image tableImg = new Image(RollElementScene.class.getResourceAsStream("/Element/Table.png"));
        ImageView tableView = new ImageView(tableImg);
        tableView.setFitWidth(500);
        tableView.setFitHeight(500);
        tableView.setPreserveRatio(true);
        tableView.setSmooth(true);

        leftPanel.getChildren().addAll(titleView, tableView);

        // ===== Add Panels =====
        root.getChildren().addAll(leftPanel, rightPanel);

        // ===== Window =====
        Scene scene = new Scene(root, 1280, 720);
        stage.setScene(scene);
        stage.setMaximized(false);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }

    /**
     * Handles the reroll button logic for hero elements.
     *
     * <p>This method:</p>
     * <ul>
     *     <li>Prevents reroll when reroll count has reached the maximum limit</li>
     *     <li>Increments reroll count by 1</li>
     *     <li>Clears the current hero element UI</li>
     *     <li>Assigns new random elements to all heroes</li>
     *     <li>Rebuilds the hero element UI overlays</li>
     *     <li>Updates the remaining roll text</li>
     * </ul>
     *
     * @param leftRollText text node showing remaining rolls
     * @param heroElementBox container holding hero element overlays
     */
    private static void rollBtnHandler(Text leftRollText, HBox heroElementBox) {

        if (GameEngine.getCountReroll() >= GameEngine.getMaxReroll()) {
            Alert limitRollAlert = new Alert(Alert.AlertType.ERROR);
            limitRollAlert.setTitle("Exceed Roll limit");
            limitRollAlert.setHeaderText("Exceed Roll limit at 3");
            limitRollAlert.setContentText("You can only roll just 3 times!");
            limitRollAlert.showAndWait();
            return;
        }

        GameEngine.addCountReroll(1);
        heroElementBox.getChildren().clear();

        ArrayList<Heroes> heroesTeam = GameEngine.getHeroTEAM();
        ArrayList<Element> heroElement = RandomElementGenerator.getRandomElement(heroesTeam);

        int index = 0;
        for (Element e : heroElement) {

            Image img = RandomElementGenerator.getElementImage(e);
            ImageView imgView = new ImageView(img);
            imgView.setFitWidth(100);
            imgView.setFitHeight(100);
            imgView.setPreserveRatio(true);

            DropShadow shadow = new DropShadow();
            shadow.setRadius(20);
            shadow.setSpread(0.4);
            shadow.setOffsetX(0);
            shadow.setOffsetY(15);
            shadow.setColor(Color.rgb(0, 0, 0, 0.85));

            Image iconImg = new Image(CharacterSelectionScene.class.getResourceAsStream(
                    "/Heroes/" + heroesTeam.get(index).getName() + "/" + heroesTeam.get(index).getName() + "Icon.PNG"
            ));
            ImageView iv = new ImageView(iconImg);

            iv.setFitWidth(100);
            iv.setFitHeight(100);
            iv.setPreserveRatio(true);
            iv.setSmooth(true);
            iv.setEffect(shadow);

            StackPane overlay = new StackPane(imgView, iv);
            StackPane.setAlignment(imgView, Pos.TOP_RIGHT);
            overlay.setPrefSize(175, 175);

            heroElementBox.getChildren().add(overlay);
            index++;
        }

        int leftRoll = Math.max(0, (GameEngine.getMaxReroll() - GameEngine.getCountReroll()));
        leftRollText.setText("Roll  left : " + leftRoll + " / 3");
    }

    /**
     * Creates an image-based JavaFX button with hover glow, hover scale,
     * and press translation animations.
     *
     * <p>The returned button is transparent and uses an image as its graphic.</p>
     *
     * @param path resource path to the button image
     * @return a configured JavaFX {@link Button}
     */
    private static Button createButton(String path) {

        Image pickImg = new Image(
                CharacterSelectionScene.class.getResourceAsStream(path)
        );

        ImageView imageView = new ImageView(pickImg);
        imageView.setFitWidth(150);
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