package gui;

import component.Unit.Heroes;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import logic.GameEngine;


public class VictoryScene {
    public static void show(Stage stage, GameEngine gameEngine) {

        VBox root = new VBox();
        root.setPadding(new Insets(10));
        GameEngine.setUpgradeHero(null);

        Button backBtn = createButton("Back",100,40);
        backBtn.setOnAction(e -> {
            StartScene.showMenu(stage,gameEngine);
        }); // go back to menu

        root.getChildren().add(backBtn);
        root.setAlignment(Pos.TOP_LEFT);

        Text title = new Text("VICTORY");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        HBox charSelect = new HBox();
        charSelect.setSpacing(10);
        charSelect.setPadding(new Insets(50));
        ToggleGroup group = new ToggleGroup();
        for (Heroes h : GameEngine.getTEAM()){
            VBox slot = new VBox();
            slot.setSpacing(20);
            slot.setAlignment(Pos.CENTER);
            String base = "/Heroes/" + h.getName() + "/";
            Button charBtn = createCharacterButton(
                    base + h.getName() + "Still.PNG",
                    base + h.getName() + "Attack.PNG"
            );
            ToggleButton upgradeBtn = createToggleButton("Select", 150, 40);
            upgradeBtn.setToggleGroup(group);

            upgradeBtn.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                if (isSelected) {
                    gameEngine.setUpgradeHero(h);   // set selected hero
                    upgradeBtn.setText("Unselect");
                    upgradeBtn.setStyle("""
                    -fx-font-size: 18px;
                    -fx-font-weight: bold;
                    -fx-text-fill: white;
                    -fx-background-radius: 30;
                    -fx-background-color: linear-gradient(#ff7a18, #ffb347);
                """);
                } else {
                    gameEngine.setUpgradeHero(null);
                    upgradeBtn.setText("Select");
                    upgradeBtn.setStyle("""
                    -fx-font-size: 18px;
                    -fx-font-weight: bold;
                    -fx-text-fill: white;
                    -fx-background-radius: 30;
                    -fx-background-color: linear-gradient(#11998e, #38ef7d);
                """);
                }
            });
            slot.getChildren().addAll(charBtn,upgradeBtn);
            charSelect.getChildren().add(slot);
        }
        charSelect.setAlignment(Pos.CENTER);

        Button nextBtn = createButton("Next",100,40);
        nextBtn.setAlignment(Pos.CENTER);
        nextBtn.setOnMouseClicked(e -> {
            nextBtnOnClickHandler(stage,gameEngine);
        });

        VBox center = new VBox(title,charSelect,nextBtn);
        center.setAlignment(Pos.CENTER);

        root.getChildren().add(center);

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(root, bounds.getWidth(), bounds.getHeight());
        stage.setScene(scene);
        stage.setMaximized(true);
    }
    private static boolean nextBtnOnClickHandler(Stage stage,GameEngine gameEngine){
        if(gameEngine.getUpgradeHero() == null){
            Alert nullUpgrdeAlert = new Alert(Alert.AlertType.ERROR);
            nullUpgrdeAlert.setTitle("No updated character selected");
            nullUpgrdeAlert.setHeaderText("You are not selecting character to upgrade");
            nullUpgrdeAlert.setContentText("You must select a character to upgrade !!!");

            nullUpgrdeAlert.showAndWait();
            return false;
        }
        else{
            GameEngine.upgradingHero();
            DefeatScene.show(stage,gameEngine);
            return true;
        }
    }

    private static Button createCharacterButton(String imagePath1,String imagePath2) {
        Image img1 = new Image( CharacterSelectionScene.class.getResourceAsStream(imagePath1) );
        ImageView iv1 = new ImageView(img1);
        iv1.setFitWidth(300);
        iv1.setFitHeight(600);
        iv1.setPreserveRatio(true); iv1.setSmooth(true);

        Image img2 = new Image( CharacterSelectionScene.class.getResourceAsStream(imagePath2) );
        ImageView iv2 = new ImageView(img2);
        iv2.setFitWidth(300);
        iv2.setFitHeight(600);
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

    private static Button createButton(String string,int PrefWidth,int PrefHeight) {
        Button btn = new Button(string);

        btn.setPrefWidth(PrefWidth);
        btn.setPrefHeight(PrefHeight);

        btn.setStyle("""
        -fx-font-size: 18px;
        -fx-font-weight: bold;
        -fx-text-fill: white;
        -fx-background-radius: 30;
        -fx-background-color: linear-gradient(#ff7a18, #ffb347);
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
    private static ToggleButton createToggleButton(String text, int w, int h) {
        ToggleButton btn = new ToggleButton(text);

        btn.setPrefWidth(w);
        btn.setPrefHeight(h);

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

        // pressed
        btn.setOnMousePressed(e -> btn.setScaleX(0.95));
        btn.setOnMouseReleased(e -> btn.setScaleX(1.08));

        return btn;
    }


}
