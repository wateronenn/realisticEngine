package gui;

import component.Element;
import component.Unit.Heroes;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import logic.GameEngine;
import javafx.stage.Stage;
import logic.RandomElementGenerator;

import java.util.ArrayList;

public class RollElementScene {

    public static void show(Stage stage , GameEngine gameEngine){
// TODO : roll element page with RollElementGenerator.getRandomElement() as ArrayList
        VBox root = new VBox();


        Text title = new Text("Roll element");


        title.setStyle("-fx-font-size: 40px; -fx-font-weight: bold;");

        HBox charSelect = new HBox();
        charSelect.setSpacing(10);
        charSelect.setPadding(new Insets(50));


        charSelect.setAlignment(Pos.CENTER);

        VBox center = new VBox(title,charSelect);
        center.setAlignment(Pos.CENTER);

        root.setSpacing(100);
        root.getChildren().add(center);

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(root, bounds.getWidth(), bounds.getHeight());
        stage.setScene(scene);
        stage.setMaximized(true);
    }

    public void heroElement(){
       ArrayList<Element> element = RandomElementGenerator.getRandomElement();
    }

    public void MonsterElement(){

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
