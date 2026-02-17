package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import logic.GameEngine;

public class DefeatScene {
    public static void show(Stage stage,GameEngine gameEngine) {
        VBox root = new VBox();
        root.setPadding(new Insets(100));
        root.setAlignment(Pos.CENTER);

        Text title = new Text("DEFEATED");
        title.setStyle("-fx-font-size: 200px; -fx-font-weight: bold;");

        Button backBtn = createButton("back",100,40);
        backBtn.setOnMouseClicked(e -> {
            StartScene.showMenu(stage, gameEngine);
        });

        root.getChildren().addAll(title,backBtn);

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(root, bounds.getWidth(), bounds.getHeight());
        stage.setScene(scene);
        stage.setMaximized(true);

    }

    private static Button createButton(String string, int PrefWidth, int PrefHeight) {
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
