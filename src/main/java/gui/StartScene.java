package gui;

import application.Main;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import logic.GameEngine;

public class StartScene {

    public static void showMenu(Stage stage, GameEngine gameEngine) {

        VBox root = new VBox();
        Image bg = new Image(Main.class.getResource("/Background/temp_background.png").toExternalForm());

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

        StackPane stack = new StackPane();
        stack.setPrefSize(1920, 1080);


        ImageView img1 = new ImageView(new Image(Main.class.getResource("/Background/test_char.png").toExternalForm()));

        img1.setFitWidth(1000);
        img1.setFitHeight(200);

        HBox title = new HBox(20, img1);
        title.setAlignment(Pos.CENTER);

        Button startBtn = new Button("START GAME");

        startBtn.setStyle("""
            -fx-font-size: 22px;
            -fx-font-weight: bold;
            -fx-text-fill: white;
            -fx-background-color: linear-gradient(#ff7a18, #ffb347);
            -fx-background-radius: 40;
            -fx-padding: 12 40 12 40;
        """);

        startBtn.setOnMouseEntered(e ->{
            startBtn.setOpacity(0.85);
        });

        startBtn.setOnMouseExited(e ->{
            startBtn.setOpacity(1.0);
        });

        startBtn.setOnAction(e -> {
            CharacterSelectionScene.show(stage,gameEngine);
        });

        VBox content = new VBox(30, title,startBtn);
        content.setAlignment(Pos.CENTER);
        content.setTranslateY(-100);

        stack.getChildren().add(content);
        root.getChildren().add(stack);

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(root, bounds.getWidth(), bounds.getHeight());
        stage.setScene(scene);
        stage.setMaximized(true);


        stage.setTitle("legend of Progmeth!");
        stage.setScene(scene);
    }
}
