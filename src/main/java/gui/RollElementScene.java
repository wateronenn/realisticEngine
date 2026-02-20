package gui;

import component.Element;
import component.Unit.Monster;
import component.Unit.heroes.Heroes;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import logic.GameEngine;
import logic.RandomElementGenerator;

import java.util.ArrayList;

public class RollElementScene {

    public static void show(Stage stage, GameEngine gameEngine) {

        GameEngine.setCountReroll(0);

        HBox root = new HBox();
        root.setPadding(new Insets(100));
        root.setSpacing(70);
        root.setAlignment(Pos.CENTER);

        // ===== Background (TOP-CENTER pinned like we did before) =====
        Image bg = new Image(application.Main.class.getResource("/Background/RollElement.png").toExternalForm());
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


        // ===== Left Panel =====
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
        monsterElementBox.setSpacing(10);
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
        Text leftRollText = new Text("Roll left : " + leftRoll + "/3");
        leftRollText.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // ===== Hero Elements =====
        HBox heroElementBox = new HBox();
        heroElementBox.setSpacing(10);
        heroElementBox.setPadding(new Insets(5));
        heroElementBox.setAlignment(Pos.CENTER);

        ArrayList<Heroes> heroesTeam = GameEngine.getHeroTEAM();
        ArrayList<Element> heroElement = RandomElementGenerator.getRandomElement(heroesTeam);

        for (Element e : heroElement) {
            Image img = RandomElementGenerator.getElementImage(e);
            ImageView imgView = new ImageView(img);
            imgView.setFitWidth(100);
            imgView.setFitHeight(100);
            imgView.setPreserveRatio(true);
            heroElementBox.getChildren().add(imgView);
        }

        // ===== Buttons =====
        Button rollBtn = createButton("/Button/Roll.png");
        rollBtn.setOnAction(e -> rollBtnHandler(leftRollText, heroElementBox));

        Button startBtn = createButton("/Button/Start.png");
        startBtn.setOnAction(e -> {
            GameEngine.addStageCounter(1);
            BattleScene.show(stage, gameEngine);
        });

        HBox bothBtn = new HBox(rollBtn, startBtn);
        bothBtn.setSpacing(50);
        bothBtn.setAlignment(Pos.CENTER);

        VBox center = new VBox(monsterElementBox, description2, leftRollText, heroElementBox, bothBtn);
        center.setAlignment(Pos.CENTER);
        center.setSpacing(15);

        // ✅ left panel now DOES NOT include title
        rightPanel.getChildren().addAll(description1, center);

        // ===== Right Panel (Title moved here) =====
        VBox leftPanel = new VBox();
        leftPanel.setAlignment(Pos.TOP_CENTER);
        leftPanel.setSpacing(20);

        Image titleImg = new Image(RollElementScene.class.getResourceAsStream("/Sign/RollElement.png"));
        ImageView titleView = new ImageView(titleImg);
        titleView.setFitWidth(500);
        titleView.setPreserveRatio(true);
        titleView.setSmooth(true);

        Image tableImg = new Image(RollElementScene.class.getResourceAsStream("/Test/table.png"));
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

        for (Element e : heroElement) {
            Image img = RandomElementGenerator.getElementImage(e);
            ImageView imgView = new ImageView(img);
            imgView.setFitWidth(100);
            imgView.setFitHeight(100);
            imgView.setPreserveRatio(true);
            heroElementBox.getChildren().add(imgView);
        }

        int leftRoll = Math.max(0, (GameEngine.getMaxReroll() - GameEngine.getCountReroll()));
        leftRollText.setText("Roll left : " + leftRoll + "/3");
    }

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