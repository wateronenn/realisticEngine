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
import javafx.stage.Stage;
import javafx.util.Duration;
import logic.GameEngine;
import logic.GameState;

public class VictoryScene {

    public static void show(Stage stage, GameEngine gameEngine) {

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

            String base = "/Heroes/" + h.getName() + "/";

            Button charBtn = createCharacterButton(
                    base + h.getName() + "Icon.PNG",
                    base + h.getName() + "Icon.PNG"
            );

            slot.getChildren().addAll(charBtn);
            charSelect.getChildren().add(slot);
        }

        // ===== Next button =====
        Button nextBtn = createButton("/Button/Next.png");
        nextBtn.setOnMouseClicked(e -> nextBtnOnClickHandler(stage, gameEngine));

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

    private static boolean nextBtnOnClickHandler(Stage stage, GameEngine gameEngine) {
        if (gameEngine.getUpgradeHero() == null) {
            Alert nullUpgrdeAlert = new Alert(Alert.AlertType.ERROR);
            nullUpgrdeAlert.setTitle("No updated character selected");
            nullUpgrdeAlert.setHeaderText("You are not selecting character to upgrade");
            nullUpgrdeAlert.setContentText("You must select a character to upgrade !!!");
            nullUpgrdeAlert.showAndWait();
            return false;
        } else {
            GameEngine.upgradingHero();
            RollElementScene.show(stage, gameEngine);
            return true;
        }
    }

    public static Button createCharacterButton(String imagePath1, String imagePath2) {

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

        int[] fit = {150,150};
        ImageView[] views = {iv1, iv2};
        for (int i = 0; i < 2; i++) {
            views[i].setFitWidth(fit[i]);
            views[i].setFitHeight(fit[i]);
            views[i].setPreserveRatio(true);
            views[i].setSmooth(true);
            views[i].setEffect(shadow);
        }

        Button btn = new Button();
        btn.setStyle("-fx-background-color: transparent;");
        btn.setPrefSize(150, 150);
        btn.setMinSize(150, 150);
        btn.setMaxSize(150, 150);

        StackPane wrapper = new StackPane(iv1);
        wrapper.setPrefSize(350, 350);
        btn.setGraphic(wrapper);

        btn.setOnMouseEntered(e -> {
             wrapper.getChildren().setAll(iv2);
            btn.setScaleX(1.05);
            btn.setScaleY(1.05);
        });

        btn.setOnMouseExited(e -> {
            wrapper.getChildren().setAll(iv1);
            btn.setScaleX(1.0);
            btn.setScaleY(1.0);
        });

        return btn;
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