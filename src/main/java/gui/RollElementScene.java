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
import javafx.scene.layout.StackPane;
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
        HBox root = new HBox();
        root.setPadding(new Insets(50));
        root.setSpacing(300);
        root.setAlignment(Pos.CENTER);

        VBox leftPanel = new VBox();
        Image titleImg = new Image(
                RollElementScene.class.getResourceAsStream("/Sign/RollElement.png")
        );
        ImageView titleView = new ImageView(titleImg);
        titleView.setFitWidth(600);     // adjust size
        titleView.setPreserveRatio(true);
        titleView.setSmooth(true);

        Text description1 = new Text("Monster's Element");
        description1.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");

        Text description2 = new Text("Hero's Element");
        description2.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");

        HBox monsterElementBox = new HBox();
        monsterElementBox.setSpacing(10);
        monsterElementBox.setPadding(new Insets(5));
        monsterElementBox.setAlignment(Pos.CENTER);

        GameEngine.setMonsterTeam();
        ArrayList<Monster> monstersTeam = GameEngine.getMonsterTeam();
        ArrayList<Element> monsterElement = RandomElementGenerator.getRandomElement(monstersTeam);
        for(Element e : monsterElement){
            Image img = RandomElementGenerator.getElementImage(e);
            ImageView imgView = new ImageView(img);
            imgView.setFitWidth(200);
            imgView.setFitHeight(200);
            imgView.setPreserveRatio(true);
            monsterElementBox.getChildren().add(imgView);
        }

        monsterElementBox.setAlignment(Pos.CENTER);
        int leftRoll = Math.max(0,(GameEngine.getMaxReroll() - GameEngine.getCountReroll()));
        Text leftRollText = new Text("Roll left : " + leftRoll + "/3");
        leftRollText.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        HBox heroElementBox = new HBox();
        heroElementBox.setSpacing(10);
        heroElementBox.setPadding(new Insets(5));
        heroElementBox.setAlignment(Pos.CENTER);
        ArrayList<Heroes> heroesTeam = GameEngine.getHeroTEAM();
        ArrayList<Element> heroElement = RandomElementGenerator.getRandomElement(heroesTeam);

        for(Element e : heroElement){
            Image img = RandomElementGenerator.getElementImage(e);
            ImageView imgView = new ImageView(img);
            imgView.setFitWidth(200);
            imgView.setFitHeight(200);
            imgView.setPreserveRatio(true);
            heroElementBox.getChildren().add(imgView);
        }
        Button rollBtn = createButton("Roll");
        rollBtn.setOnAction(e->rollBtnHandler(leftRollText,heroElementBox));

        heroElementBox.setAlignment(Pos.CENTER);

        Button startBtn = createButton("Start");
        startBtn.setOnAction(e -> {
            GameEngine.addStageCounter(1);
            BattleScene.show(stage,gameEngine);
        });

        HBox bothBtn = new HBox();
        bothBtn.setSpacing(50);
        bothBtn.setAlignment(Pos.CENTER);
        bothBtn.getChildren().addAll(rollBtn,startBtn);

        VBox center = new VBox(monsterElementBox,description2,leftRollText,heroElementBox,bothBtn);
        center.setSpacing(10);
        center.setAlignment(Pos.CENTER);

        Image img = new Image(RollElementScene.class.getResourceAsStream("/Element/ElementTable.png"));
        ImageView imageView = new ImageView(img);

        imageView.setFitWidth(500);
        imageView.setFitHeight(800);
        imageView.setPreserveRatio(true);

        leftPanel.setSpacing(10);
        leftPanel.getChildren().addAll(titleView,description1,center);
        leftPanel.setAlignment(Pos.TOP_CENTER);

        root.getChildren().addAll(leftPanel,imageView);

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(root, bounds.getWidth(), bounds.getHeight());
        stage.setScene(scene);
        stage.setMaximized(true);
    }

    private static void rollBtnHandler(Text leftRollText,HBox heroElementBox){
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
            imgView.setFitHeight(200);
            imgView.setFitHeight(200);
            imgView.setPreserveRatio(true);

            heroElementBox.getChildren().add(imgView);
        }
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
