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

public class CharacterSelectionScene {
    private static Runnable resetCurrentSelection = null;

    public static void show(Stage stage,GameEngine gameEngine) {

        VBox root = new VBox();
        root.setPadding(new Insets(10));
        root.setSpacing(200);

        Image bg = new Image(application.Main.class.getResource("/Background/Selection.png").toExternalForm());
        BackgroundImage bgImage = new BackgroundImage(
                bg,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(
                        BackgroundSize.AUTO,
                        BackgroundSize.AUTO,
                        false,
                        false,
                        true,
                        true
                )
        );
        root.setBackground(new Background(bgImage));

        Button backBtn = createButton("/Button/Back.png"); // back
        backBtn.setOnAction(e -> {
           StartScene.showMenu(stage,gameEngine);
        }); // go back to menu

        root.getChildren().add(backBtn);
        root.setAlignment(Pos.TOP_LEFT);

        HBox charSelect = new HBox();

        for (Heroes h: gameEngine.getAvailableHeroes()) {
            VBox slot = new VBox();
            slot.setSpacing(15);
            slot.setAlignment(Pos.CENTER);

            String base = "/Heroes/" + h.getName() + "/";
            Button charBtn = createCharacterButton(
                    base + h.getName() + "Still.PNG",
                    base + h.getName() + "Attack.PNG",
                    "/Test/testscroll.png"
            );

            Button pickBtn = createButton("/Button/Choose.png"); // pick
            pickBtn.setOnAction(e -> pickBtnOnClickHandler(gameEngine,h,pickBtn));
            slot.getChildren().addAll(charBtn,pickBtn);
            charSelect.getChildren().add(slot);
        }
        charSelect.setAlignment(Pos.CENTER);

        Button startBtn = createButton("/Button/Start.png"); // start
        startBtn.setAlignment(Pos.CENTER);
        startBtn.setOnAction(e -> startBtnOnClickHandler(stage,gameEngine));

        VBox center = new VBox(charSelect,startBtn);
        center.setAlignment(Pos.CENTER);

        root.getChildren().add(center);

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(root, bounds.getWidth(), bounds.getHeight());
        stage.setScene(scene);
        stage.setMaximized(true);
    }
    private static void startBtnOnClickHandler(Stage stage,GameEngine gameEngine){
        if(gameEngine.checkFullTeam()){
            RollElementScene.show(stage,gameEngine);
        }
        else{
            Alert teamNotFullAlert = new Alert(Alert.AlertType.ERROR);
            teamNotFullAlert.setTitle("Not Enough Member");
            teamNotFullAlert.setHeaderText("Your Team is not Ready");
            teamNotFullAlert.setContentText("You must pick 3 Heroes to go!");

            teamNotFullAlert.showAndWait();
            return;
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
            Image unpickImg = new Image(
                    CharacterSelectionScene.class.getResourceAsStream("/Button/Discard.png")
            );
            imageView.setImage(unpickImg);
        } else {
            Image pickImg = new Image(
                    CharacterSelectionScene.class.getResourceAsStream("/Button/Choose.png")
            );
            imageView.setImage(pickImg);
        }
    }

    public static Button createCharacterButton(String imagePath1, String imagePath2, String imagePath3) {

        Image img1 = new Image(CharacterSelectionScene.class.getResourceAsStream(imagePath1));
        ImageView iv1 = new ImageView(img1);

        Image img2 = new Image(CharacterSelectionScene.class.getResourceAsStream(imagePath2));
        ImageView iv2 = new ImageView(img2);

        Image img3 = new Image(CharacterSelectionScene.class.getResourceAsStream(imagePath3));
        ImageView iv3 = new ImageView(img3);

        DropShadow shadow = new DropShadow();
        shadow.setRadius(40);
        shadow.setSpread(0.4);
        shadow.setOffsetX(0);
        shadow.setOffsetY(15);
        shadow.setColor(Color.rgb(0, 0, 0, 0.85));

        ImageView[] views = {iv1, iv2, iv3};
        for (ImageView iv : views) {
            iv.setFitWidth(350);
            iv.setFitHeight(350);
            iv.setPreserveRatio(true);
            iv.setSmooth(true);
            iv.setEffect(shadow);
        }

        Button btn = new Button();
        btn.setStyle("-fx-background-color: transparent;");
        btn.setPrefSize(350, 350);
        btn.setMinSize(350, 350);
        btn.setMaxSize(350, 350);

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

        Image pickImg = new Image(
                CharacterSelectionScene.class.getResourceAsStream(path)
        );

        ImageView imageView = new ImageView(pickImg);
        imageView.setFitWidth(200);
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

        // ===== Glow Effect =====
        DropShadow glow = new DropShadow();
        glow.setColor(Color.BLACK);
        glow.setRadius(20);

        // ===== Hover Scale =====
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

        // ===== Press Effect =====
        TranslateTransition press = new TranslateTransition(Duration.millis(100), btn);
        press.setToY(3);

        TranslateTransition release = new TranslateTransition(Duration.millis(100), btn);
        release.setToY(0);

        btn.setOnMousePressed(e -> press.playFromStart());
        btn.setOnMouseReleased(e -> release.playFromStart());

        return btn;
    }
}
