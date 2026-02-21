package gui;

import component.Unit.heroes.Heroes;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
import logic.GameState;

import java.util.Objects;

public class CharacterSelectionScene {

    private static Runnable resetCurrentSelection = null;

    public static void show(Stage stage,GameEngine gameEngine) {
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
                        100, 100, true, true, true, true // IMPORTANT: scale background
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

        for (Heroes h : gameEngine.getAvailableHeroes()) {

        for (Heroes h: GameEngine.getAvailableHeroes()) {
            VBox slot = new VBox();
            slot.setSpacing(15);
            slot.setAlignment(Pos.CENTER);

            String base = "/Heroes/" + h.getName() + "/";

            Button charBtn = createCharacterButton(
                    base + h.getName() + "Still.PNG",
                    base + h.getName() + "Attack.PNG",
                    "/Test/testscroll.png"
            );

            Button pickBtn = createButton("/Button/Choose.png");
            pickBtn.setOnAction(e -> pickBtnOnClickHandler(gameEngine, h, pickBtn));

            slot.getChildren().addAll(charBtn, pickBtn);
            charSelect.getChildren().add(slot);
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
    private static void startBtnOnClickHandler(Stage stage,GameEngine gameEngine){
        if(GameEngine.checkFullTeam()){
            RollElementScene.show(stage,gameEngine);
        }
        else{
            Alert teamNotFullAlert = new Alert(Alert.AlertType.ERROR);
            teamNotFullAlert.setTitle("Not Enough Member");
            teamNotFullAlert.setHeaderText("Your Team is not Ready");
            teamNotFullAlert.setContentText("You must pick 3 Heroes to go!");
            teamNotFullAlert.showAndWait();
        }
    }

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
        } else {
            Image pickImg = new Image(CharacterSelectionScene.class.getResourceAsStream("/Button/Choose.png"));
            imageView.setImage(pickImg);
        }
    }

    public static Button createCharacterButton(String imagePath1, String imagePath2, String imagePath3) {

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

        Image img3 = new Image(CharacterSelectionScene.class.getResourceAsStream(imagePath3));
        ImageView iv3 = new ImageView(img3);

        int[] fit = {350,350,300};
        ImageView[] views = {iv1, iv2, iv3};
        for (int i = 0; i < 3; i++) {
            views[i].setFitWidth(fit[i]);
            views[i].setFitHeight(fit[i]);
            views[i].setPreserveRatio(true);
            views[i].setSmooth(true);
            views[i].setEffect(shadow);
        }

        Button btn = new Button();
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
                wrapper.getChildren().setAll(iv3);
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