package gui;

import component.Unit.Monster;
import component.Unit.heroes.Heroes;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import logic.*;

import java.util.List;

public class BattleSceneTemp {

    // UI nodes we need to update
    private static HBox heroBox;
    private static HBox monsterBox;
    private static Label logLabel;
    private static Button normalBtn, skillBtn, ultBtn;

    // keep references
    private static BattleEngine engine;
    private static BattleModel model;

    public static void show(Stage stage, GameEngine gameEngine) {

        // 1) Build battle model + engine
        model = new BattleModel(GameEngine.getHeroTEAM(), GameEngine.getMonsterTeam());
        engine = new BattleEngine(model);

        // 2) Build UI layout
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // Monsters (top)
        monsterBox = new HBox(20);
        monsterBox.setAlignment(Pos.CENTER);
        root.setTop(monsterBox);

        // Heroes (bottom)
        heroBox = new HBox(20);
        heroBox.setAlignment(Pos.CENTER);
        root.setBottom(heroBox);

        // Center controls
        VBox center = new VBox(10);
        center.setAlignment(Pos.CENTER);

        logLabel = new Label("Battle Start!");
        normalBtn = new Button("Normal");
        skillBtn  = new Button("Skill");
        ultBtn    = new Button("Ultimate");

        HBox skillBar = new HBox(10, normalBtn, skillBtn, ultBtn);
        skillBar.setAlignment(Pos.CENTER);

        center.getChildren().addAll(logLabel, skillBar);
        root.setCenter(center);

        // 3) Wire buttons to engine
        normalBtn.setOnAction(e -> engine.onClickHeroSkill(SkillType.NORMAL_ATTACK));
        skillBtn.setOnAction(e -> engine.onClickHeroSkill(SkillType.SKILL));
        ultBtn.setOnAction(e -> engine.onClickHeroSkill(SkillType.ULTIMATE));

        // 4) Set listener (engine -> UI updates)
        engine.setListener(new BattleListener() {
            @Override
            public void onStateChanged(BattleStage stage) {
                refreshInteractivity(stage);
            }

            @Override
            public void onLog(String message) {
                logLabel.setText(message);
            }

            @Override
            public void onModelUpdated() {
                refreshUnits();
                refreshButtons();
            }
        });

        // 5) Initial render
        refreshUnits();
        refreshButtons();
        refreshInteractivity(engine.getBattleStage());
        engine.beginBattle();

        Scene scene = new Scene(root, 1280, 720);
        stage.setScene(scene);
        stage.show();
    }

    // ====== UI refresh helpers ======

    private static void refreshUnits() {
        heroBox.getChildren().clear();
        monsterBox.getChildren().clear();

        List<Heroes> heroes = model.getHERO_TEAM();
        List<Monster> monsters = model.getMONSTER_TEAM();

        // Heroes UI
        for (Heroes h : heroes) {
            VBox card = unitCard(h.getName(), h.getHp());
            // ally selection click (only when engine is choosing ally)
            card.setOnMouseClicked(e -> {
                if (engine.getBattleStage() == BattleStage.HERO_CHOOSE_ALLY) {
                    engine.onClickChoosingAlly(h);
                }
            });
            heroBox.getChildren().add(card);
        }

        // Monsters UI (index matters)
        for (int i = 0; i < monsters.size(); i++) {
            Monster m = monsters.get(i);

            VBox card = unitCard(m.getName(), m.getHp());
            final int idx = i;
            card.setOnMouseClicked(e -> {
                if (engine.getBattleStage() == BattleStage.HERO_CHOOSE_TARGET) {
                    engine.onClickChoosingTarget(idx);
                }
            });
            monsterBox.getChildren().add(card);
        }
    }

    private static VBox unitCard(String name, double hp) {
        Label n = new Label(name);
        Label h = new Label("HP: " + hp);
        VBox box = new VBox(5, n, h);
        box.setPadding(new Insets(10));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-border-color: black; -fx-border-radius: 8; -fx-background-radius: 8;");
        return box;
    }

    private static void refreshButtons() {
        Heroes active = engine.getModel().getHERO_TEAM().get(engine.getModel().getActiveHeroIndex());

        // Enable/disable based on cooldown
        // adapt names to your hero API:
        skillBtn.setDisable(!active.canUseSkill());
        ultBtn.setDisable(!active.canUseUlt());

        // Normal usually always enabled:
        normalBtn.setDisable(false);
    }

    private static void refreshInteractivity(BattleStage stage) {
        // Skills clickable only in HERO_CHOOSE_SKILL
        boolean canClickSkills = (stage == BattleStage.HERO_CHOOSE_SKILL);
        normalBtn.setDisable(!canClickSkills);
        // skill/ult will be further disabled by cooldown in refreshButtons()
        if (canClickSkills) refreshButtons();
        else {
            skillBtn.setDisable(true);
            ultBtn.setDisable(true);
        }

        // You can also highlight targets here if you want:
        if (stage == BattleStage.HERO_CHOOSE_TARGET) {
            logLabel.setText("Choose a monster target");
        } else if (stage == BattleStage.HERO_CHOOSE_ALLY) {
            logLabel.setText("Choose an ally target");
        } else if (stage == BattleStage.MONSTER_TURN) {
            logLabel.setText("Monster turn...");
        } else if (stage == BattleStage.WIN_TURN) {
            logLabel.setText("Victory!");
        } else if (stage == BattleStage.LOSE_TURN) {
            logLabel.setText("Defeat!");
        }
    }
}