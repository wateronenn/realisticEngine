package gui;

import component.Unit.heroes.Heroes;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleGroup;
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

public class VictoryScene {

    public static void show(Stage stage, GameEngine gameEngine) {
        GameEngine.setGameState(GameState.END_TURN);
        VBox root = new VBox();
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.TOP_CENTER);

        GameEngine.setUpgradeHero(null);

        // ===== Background (TOP-CENTER pinned) =====
        Image bg = new Image(application.Main.class.getResource("/Background/Victory.png").toExternalForm());
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

        // ===== Back button (top-left) =====
        Button backBtn = createButton("/Button/Back.png");
        backBtn.setOnAction(e -> StartScene.showMenu(stage, gameEngine));

        HBox topBar = new HBox(backBtn);
        topBar.setAlignment(Pos.TOP_LEFT);

        // ===== Character Select =====
        HBox charSelect = new HBox();
        charSelect.setSpacing(30);
        charSelect.setPadding(new Insets(10));
        charSelect.setAlignment(Pos.CENTER);

        for (Heroes h : GameEngine.getHeroTEAM()) {

            VBox slot = new VBox();
            slot.setAlignment(Pos.CENTER);
            slot.setSpacing(20);

            String base = "/Heroes/" + h.getName() + "/";
            ImageView charImg = createCharacterImage(base + h.getName() + "Icon.PNG");

            Font font = Font.loadFont(CharacterSelectionScene.class.getResource("/Font/Supply_Center.ttf").toExternalForm(),15);
            Text scale = new Text("HP :  before > after\nATK : before > after\nDEF : before > after\n");
            scale.setFont(font);
            scale.setLineSpacing(8);

            slot.getChildren().addAll(charImg,scale);
            charSelect.getChildren().add(slot);
        }

        // ===== Next button =====
        Button nextBtn = createButton("/Button/Next.png");
        nextBtn.setOnMouseClicked(e -> UpgradeScene.show(stage,gameEngine));

        VBox center = new VBox( charSelect, nextBtn);
        center.setAlignment(Pos.CENTER);

        StackPane centerWrap = new StackPane(center);
        centerWrap.setAlignment(Pos.CENTER);
        VBox.setVgrow(centerWrap, Priority.ALWAYS);

        root.getChildren().addAll(topBar, centerWrap);

        // ===== Window mode =====
        Scene scene = new Scene(root, 1280, 720);
        stage.setScene(scene);
        stage.setMaximized(false);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }

    public static ImageView createCharacterImage(String imagePath1) {

        DropShadow shadow = new DropShadow();
        shadow.setRadius(40);
        shadow.setSpread(0.4);
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