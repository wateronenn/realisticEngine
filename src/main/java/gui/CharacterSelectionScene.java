package gui;

import Application.Main;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CharacterSelectionScene {
    public static void show(Stage stage) {

        VBox root = new VBox();

        Button backBtn = createButton("← Back");
        backBtn.setOnAction(e -> {
           Main.showMenu(stage);
        }); // go back to menu

        root.getChildren().add(backBtn);
        root.setAlignment(Pos.TOP_LEFT);
        root.setPadding(new Insets(10));

        Text title = new Text("SELECT");
        title.setStyle("-fx-font-size: 40px; -fx-font-weight: bold;");

        HBox charSelect = new HBox();
        charSelect.setSpacing(10);
        charSelect.setPadding(new Insets(50));

        for (int i = 0; i < 4; i++) {
            VBox slot = new VBox();
            slot.setSpacing(20);
            slot.setAlignment(Pos.CENTER);

            Button charBtn = createCharacterButton("/CasterStill.PNG","/CasterAttack.PNG");
            Button pickBtn = createButton("Pick");

            slot.getChildren().addAll(charBtn,pickBtn);
            charSelect.getChildren().add(slot);
        }
        charSelect.setAlignment(Pos.CENTER);

        Button startBtn = createButton("START");
        startBtn.setAlignment(Pos.CENTER);

        VBox center = new VBox(title,charSelect,startBtn);
        center.setAlignment(Pos.CENTER);

        root.setSpacing(100);
        root.getChildren().add(center);

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(root, bounds.getWidth(), bounds.getHeight());
        stage.setScene(scene);
        stage.setMaximized(true);
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
