package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

        Button backBtn = createButton("Back",100,40);
        backBtn.setOnAction(e -> {
            StartScene.showMenu(stage,gameEngine);
        }); // go back to menu

        root.getChildren().add(backBtn);
        root.setAlignment(Pos.TOP_LEFT);

        Text title = new Text("VICTORY");
        title.setStyle("-fx-font-size: 200px; -fx-font-weight: bold;");

        HBox charSelect = new HBox();
        charSelect.setSpacing(10);
        charSelect.setPadding(new Insets(50));

        for (int i = 0; i < 3; i++) {
            VBox slot = new VBox();
            slot.setSpacing(20);
            slot.setAlignment(Pos.CENTER);

            Button charBtn = createCharacterButton("/Heroes/Caster/CasterStill.PNG","/Heroes/Caster/CasterAttack.PNG");
            Button upgradeBtn = createButton("Upgrade",150,40);

            slot.getChildren().addAll(charBtn,upgradeBtn);
            charSelect.getChildren().add(slot);
        }
        charSelect.setAlignment(Pos.CENTER);

        Button nextBtn = createButton("Next",100,40);
        nextBtn.setAlignment(Pos.CENTER);
        nextBtn.setOnMouseClicked(e -> {
            DefeatScene.show(stage,gameEngine);
        });

        VBox center = new VBox(title,charSelect,nextBtn);
        center.setAlignment(Pos.CENTER);

        root.getChildren().add(center);

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(root, bounds.getWidth(), bounds.getHeight());
        stage.setScene(scene);
        stage.setMaximized(true);
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
}
