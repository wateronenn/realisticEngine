package gui;

import component.Unit.Monster;
import component.Unit.heroes.Heroes;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import logic.*;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import java.util.List;
import java.util.function.BooleanSupplier;


public class BattleSceneTemp {

    // UI nodes we need to update
    private static HBox heroBox;
    private static HBox monsterBox;
    private static Label logLabel;
    private static Button normalBtn, skillBtn, ultBtn;
    private static Label stageLabel;

    // keep references
    private static BattleEngine engine;
    private static BattleModel model;
    private static final Map<Heroes, Region> heroCardMap = new HashMap<>();
    private static final Map<Monster, Region> monsterCardMap = new HashMap<>();
    private static final DropShadow TURN_GLOW = new DropShadow(35, Color.GOLD);
    private static final DropShadow HOVER_GLOW = new DropShadow(25, Color.AQUA);

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

        stageLabel = new Label("Stage: " + engine.getBattleStage());
        logLabel = new Label("Battle Start! Choose Hero skill");
        normalBtn = new Button("Normal");
        skillBtn  = new Button("Skill");
        ultBtn    = new Button("Ultimate");

        HBox skillBar = new HBox(10, normalBtn, skillBtn, ultBtn);
        skillBar.setAlignment(Pos.CENTER);

        center.getChildren().addAll(stageLabel,logLabel, skillBar);
        root.setCenter(center);

        // 3) Wire buttons to engine
        normalBtn.setOnAction(e -> engine.onClickHeroSkill(SkillType.NORMAL_ATTACK));
        skillBtn.setOnAction(e -> engine.onClickHeroSkill(SkillType.SKILL));
        ultBtn.setOnAction(e -> engine.onClickHeroSkill(SkillType.ULTIMATE));

        // 4) Set listener (engine -> UI updates)
        engine.setListener(new BattleListener() {
            @Override
            public void onStateChanged(BattleStage stage) {
                stageLabel.setText(stage.toString());
                refreshInteractivity(stage);
                refreshTurnGlow();

            }

            @Override
            public void onLog(String message) {
                logLabel.setText(message);
            }

            @Override
            public void onModelUpdated() {
                refreshUnits();
                refreshButtons();
                refreshTurnGlow();
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
        heroCardMap.clear();
        monsterCardMap.clear();

        List<Heroes> heroes = model.getHERO_TEAM();
        List<Monster> monsters = model.getMONSTER_TEAM();

        // HERO cards (also used for ally-target selection)
        for (Heroes h : heroes) {
            VBox card = unitCard(h.getName(), h.getHp(), h.isDead());

            setupHoverGlow(card, () -> engine.getBattleStage() == BattleStage.HERO_CHOOSE_ALLY);
            card.setOnMouseClicked(e -> {
                if (engine.getBattleStage() == BattleStage.HERO_CHOOSE_ALLY && !h.isDead()) {
                    engine.onClickChoosingAlly(h);
                }
            });

            heroCardMap.put(h, card);
            heroBox.getChildren().add(card);
        }

        // MONSTER cards (index matters for choosing target)
        for (int i = 0; i < monsters.size(); i++) {
            Monster m = monsters.get(i);
            VBox card = unitCard(m.getName(), m.getHp(), m.isDead());

            final int idx = i;

            setupHoverGlow(card, () -> engine.getBattleStage() == BattleStage.HERO_CHOOSE_TARGET);
            card.setOnMouseClicked(e -> {
                if (engine.getBattleStage() == BattleStage.HERO_CHOOSE_TARGET && !m.isDead()) {
                    engine.onClickChoosingTarget(idx);
                }
            });

            monsterCardMap.put(m, card);
            monsterBox.getChildren().add(card);
        }
    }

    private static VBox unitCard(String name, double hp, boolean dead) {
        Label n = new Label(name);
        Label h = new Label("HP: " + hp);

        VBox box = new VBox(6, n, h);
        box.setPadding(new Insets(12));
        box.setAlignment(Pos.CENTER);
        box.setMinWidth(140);

        box.setStyle("""
        -fx-border-color: rgba(255,255,255,0.7);
        -fx-border-radius: 12;
        -fx-background-radius: 12;
        -fx-background-color: rgba(0,0,0,0.35);
    """);

        if (dead) {
            box.setOpacity(0.35);
        } else {
            box.setOpacity(1.0);
        }
        return box;
    }
    private static void setupHoverGlow(Region node, BooleanSupplier canHover) {
        node.setOnMouseEntered(e -> {
            if (canHover.getAsBoolean() && node.getOpacity() > 0.5) {
                node.setEffect(HOVER_GLOW);
            }
        });
        node.setOnMouseExited(e -> {
            // don't remove turn glow here; refreshTurnGlow() will restore it
            node.setEffect(null);
            refreshTurnGlow();
        });
    }


    private static void refreshButtons() {
        Heroes active = engine.getModel().getHERO_TEAM().get(engine.getModel().getActiveHeroIndex());

        // Enable/disable based on cooldown
        // adapt names to your hero API:
        skillBtn.setDisable(engine.getBattleStage() == BattleStage.MONSTER_TURN);
        skillBtn.setDisable(!active.canUseSkill());
        ultBtn.setDisable(!active.canUseUlt());

        // Normal usually always enabled:
        normalBtn.setDisable(false);
    }
    private static void refreshTurnGlow() {
        // clear all effects first (then reapply turn glow if needed)
        for (Region r : heroCardMap.values()) r.setEffect(null);
        for (Region r : monsterCardMap.values()) r.setEffect(null);

        BattleStage s = engine.getBattleStage();

        // Only glow active hero when it is hero's decision time
        boolean heroDecision =
                s == BattleStage.HERO_CHOOSE_SKILL ||
                        s == BattleStage.HERO_CHOOSE_TARGET ||
                        s == BattleStage.HERO_CHOOSE_ALLY;

        if (!heroDecision) return;

        Heroes active = safeGetActiveHero();
        if (active == null) return;

        Region card = heroCardMap.get(active);
        if (card != null && !active.isDead()) {
            card.setEffect(TURN_GLOW);
        }
    }

    private static Heroes safeGetActiveHero() {
        int idx = engine.getModel().getActiveHeroIndex();
        if (idx < 0 || idx >= model.getHERO_TEAM().size()) return null;
        return model.getHERO_TEAM().get(idx);
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
            normalBtn.setDisable(true);
            skillBtn.setDisable(true);
            ultBtn.setDisable(true);

            heroBox.setDisable(true);      // optional
            monsterBox.setDisable(false);
        } else if (stage == BattleStage.HERO_CHOOSE_ALLY) {
            logLabel.setText("Choose an ally target");
            normalBtn.setDisable(true);
            skillBtn.setDisable(true);
            ultBtn.setDisable(true);

            heroBox.setDisable(false);     // allow ally selection
            monsterBox.setDisable(true);
        } else if (stage == BattleStage.MONSTER_TURN) {
            logLabel.setText("Monster turn... (Wait 2 second)");
            System.out.println("Monster turn");
            setHeroInputEnabled(false);

            PauseTransition p = new PauseTransition(Duration.millis(2000));
            p.setOnFinished(e -> {
                engine.monsterTurn();
                engine.executeMonsterTurnAndContinue();
            });
            p.play();

        }
        else if (stage == BattleStage.HERO_CHOOSE_SKILL) {
            setHeroInputEnabled(true);
            logLabel.setText("Chooseeeeeeeeee Hero skill");
        }
        else if (stage == BattleStage.WIN_TURN) {
            logLabel.setText("Victory!");
        } else if (stage == BattleStage.LOSE_TURN) {
            logLabel.setText("Defeat!");
        }
    }
    private static void setHeroInputEnabled(boolean enabled) {
        normalBtn.setDisable(!enabled);
        skillBtn.setDisable(!enabled);
        ultBtn.setDisable(!enabled);

        // Optional: disable clicking on units too
        heroBox.setDisable(!enabled);
        monsterBox.setDisable(!enabled);
    }
}