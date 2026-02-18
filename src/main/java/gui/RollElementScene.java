package gui;

import component.Element;
import component.Unit.Monster;
import component.Unit.heroes.Heroes;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import logic.GameEngine;
import javafx.stage.Stage;
import logic.RandomElementGenerator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;

public class RollElementScene {

    public static void show(Stage stage , GameEngine gameEngine){
        GameEngine.setCountReroll(0);
        VBox root = new VBox();
        Text title = new Text("Roll element");
        title.setStyle("-fx-font-size: 40px; -fx-font-weight: bold;");

        HBox monsterElementBox = new HBox();
        monsterElementBox.setSpacing(10);
        monsterElementBox.setPadding(new Insets(50));
        monsterElementBox.setAlignment(Pos.CENTER);
        GameEngine.setMonsterTeam();
        ArrayList<Monster> monstersTeam = GameEngine.getMonsterTeam();
        ArrayList<Element> monsterElement = RandomElementGenerator.getRandomElement(monstersTeam);
        for(Element e : monsterElement){
            Image img = RandomElementGenerator.getElementImage(e);
            ImageView imgView = new ImageView(img);
            imgView.setFitWidth(80);
            imgView.setFitHeight(80);
            imgView.setPreserveRatio(true);

            monsterElementBox.getChildren().add(imgView);
        }

        monsterElementBox.setAlignment(Pos.CENTER);
        int leftRoll = Math.max(0,(GameEngine.getMaxReroll() - GameEngine.getCountReroll()));
        Text leftRollText = new Text("Roll left : " + leftRoll + "/3");

        HBox heroElementBox = new HBox();
        heroElementBox.setSpacing(10);
        heroElementBox.setPadding(new Insets(50));
        heroElementBox.setAlignment(Pos.CENTER);
        ArrayList<Heroes> heroesTeam = GameEngine.getHeroTEAM();
        ArrayList<Element> heroElement = RandomElementGenerator.getRandomElement(heroesTeam);

        for(Element e : heroElement){
            Image img = RandomElementGenerator.getElementImage(e);
            ImageView imgView = new ImageView(img);
            imgView.setFitWidth(80);
            imgView.setFitHeight(80);
            imgView.setPreserveRatio(true);
            heroElementBox.getChildren().add(imgView);
        }
        Button rollBtn = createButton("Roll");
        rollBtn.setOnAction(e->rollBtnHandler(rollBtn,leftRollText,heroElementBox));

        heroElementBox.getChildren().add(rollBtn);
        heroElementBox.setAlignment(Pos.CENTER);

        Button startBtn = createButton("Start");
        startBtn.setOnAction(e -> {
            GameEngine.addStageCounter(1);
            BattleScene.show(stage,gameEngine);
        }); // go back to menu

        VBox center = new VBox(title, monsterElementBox,leftRollText,heroElementBox,startBtn);
        center.setAlignment(Pos.CENTER);

        root.setSpacing(100);
        root.getChildren().add(center);

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(root, bounds.getWidth(), bounds.getHeight());
        stage.setScene(scene);
        stage.setMaximized(true);
    }

    private static void rollBtnHandler(Button rollBtn,Text leftRollText,HBox heroElementBox){
        if(GameEngine.getCountReroll() >= GameEngine.getMaxReroll()){
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
        for(Element e : heroElement){
            Image img = RandomElementGenerator.getElementImage(e);
            ImageView imgView = new ImageView(img);
            imgView.setFitHeight(80);
            imgView.setFitHeight(80);
            imgView.setPreserveRatio(true);

            heroElementBox.getChildren().add(imgView);
        }
        heroElementBox.getChildren().add(rollBtn);
        int leftRoll = Math.max(0,(GameEngine.getMaxReroll() - GameEngine.getCountReroll()));
        leftRollText.setText("Roll left : " + leftRoll + "/3");;
    }

    private static Button createButton(String string) {
        Button btn = new Button(string);

        btn.setPrefWidth(100);
        btn.setPrefHeight(40);

        btn.setStyle("""
        -fx-font-size: 18px;
        -fx-font-weight: bold;
        -fx-text-fill: white;
        -fx-background-radius: 30;
        -fx-background-color: linear-gradient(#11998e, #38ef7d);
    """);

        // hover
        btn.setOnMouseEntered(e -> {
            btn.setScaleX(1.08);
            btn.setScaleY(1.08);
            btn.setOpacity(0.9);
        });

        btn.setOnMouseExited(e -> {
            btn.setScaleX(1.0);
            btn.setScaleY(1.0);
            btn.setOpacity(1.0);
        });

        // click press effect
        btn.setOnMousePressed(e -> btn.setScaleX(0.95));
        btn.setOnMouseReleased(e -> btn.setScaleX(1.08));
        return btn;
    }
}
