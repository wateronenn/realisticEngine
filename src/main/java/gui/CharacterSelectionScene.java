package gui;

import component.Unit.Heroes;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import logic.GameEngine;

public class CharacterSelectionScene {
    public static void show(Stage stage,GameEngine gameEngine) {

        VBox root = new VBox();

        Button backBtn = createButton("← Back");
        backBtn.setOnAction(e -> {
           StartScene.showMenu(stage,gameEngine);
        }); // go back to menu

        root.getChildren().add(backBtn);
        root.setAlignment(Pos.TOP_LEFT);
        root.setPadding(new Insets(10));

        Text title = new Text("SELECT 3 TEAM MEMBERS");


        title.setStyle("-fx-font-size: 40px; -fx-font-weight: bold;");

        HBox charSelect = new HBox();
        charSelect.setSpacing(10);
        charSelect.setPadding(new Insets(50));

        for (Heroes h: gameEngine.getAvailableHeroes()) {
            VBox slot = new VBox();
            slot.setSpacing(20);
            slot.setAlignment(Pos.CENTER);

            String base = "/Heroes/" + h.getName() + "/";
            Button charBtn = createCharacterButton(
                    base + h.getName() + "Still.PNG",
                    base + h.getName() + "Attack.PNG"
            );

            Button pickBtn = createButton("Pick");
            pickBtn.setOnAction(e -> pickBtnOnClickHandler(gameEngine,h,pickBtn));
            slot.getChildren().addAll(charBtn,pickBtn);
            charSelect.getChildren().add(slot);
        }
        charSelect.setAlignment(Pos.CENTER);

        Button startBtn = createButton("START");
        startBtn.setAlignment(Pos.CENTER);
        startBtn.setOnAction(e -> startBtnOnClickHandler(stage,gameEngine));
        VBox center = new VBox(title,charSelect,startBtn);
        center.setAlignment(Pos.CENTER);

        root.setSpacing(100);
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

    private static void pickBtnOnClickHandler(GameEngine gameEngine,Heroes hero, Button pickBtn){
        boolean checkAddTeamMember = GameEngine.toggleTeamMember(hero);
        if(!checkAddTeamMember){
            Alert limitAlert = new Alert(Alert.AlertType.ERROR);
            limitAlert.setTitle("Team exceed limit size");
            limitAlert.setHeaderText("Exceed limit team size");
            limitAlert.setContentText("You can pick only up to 3 people !!!");

            limitAlert.showAndWait();
            return;
        }
        if(gameEngine.isInTeam(hero)){
            pickBtn.setText("UnPick");
            pickBtn.setOpacity(0.85);
            pickBtn.setStyle("""
            -fx-font-size: 18px;
            -fx-font-weight: bold;
            -fx-text-fill: white;
            -fx-background-radius: 30;
            -fx-background-color: linear-gradient(#ff7a18, #ffb347);
        """);
        }
        else{
            pickBtn.setText("Pick");
            pickBtn.setOpacity(1);
            pickBtn.setStyle("""
            -fx-font-size: 18px;
            -fx-font-weight: bold;
            -fx-text-fill: white;
            -fx-background-radius: 30;
            -fx-background-color: linear-gradient(#11998e, #38ef7d);
        """);
        }

    }

    private static Button createCharacterButton(String imagePath1,String imagePath2) {
        Image img1 = new Image( CharacterSelectionScene.class.getResourceAsStream(imagePath1) );
        ImageView iv1 = new ImageView(img1);
        iv1.setFitWidth(250); iv1.setFitHeight(500);
        iv1.setPreserveRatio(true); iv1.setSmooth(true);

        Image img2 = new Image( CharacterSelectionScene.class.getResourceAsStream(imagePath2) );
        ImageView iv2 = new ImageView(img2);
        iv2.setFitWidth(250);
        iv2.setFitHeight(500);
        iv2.setPreserveRatio(true);
        iv2.setSmooth(true);

        Button btn = new Button();
        btn.setGraphic(iv1);
        btn.setStyle(""" 
                -fx-background-color: transparent; -fx-padding: 10; 
        """); // hover animation
        btn.setOnMouseEntered(e -> {
            btn.setGraphic(iv2);
            btn.setScaleX(1.05);
            btn.setScaleY(1.05);
        });

        btn.setOnMouseExited(e -> {
            btn.setGraphic(iv1); btn.setScaleX(1.0);
            btn.setScaleY(1.0);
        });
        return btn;
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
