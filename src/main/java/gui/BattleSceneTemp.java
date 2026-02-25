package gui;

import component.Element;
import component.Unit.Monster;
import component.Unit.heroes.Heroes;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import logic.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.Random;


public class BattleSceneTemp {

    private static Pane heroBox;
    private static Pane monsterBox;
    private static Label logLabel;
    private static Button normalBtn, skillBtn, ultBtn;
    private static Label stageLabel;

    private static BattleEngine engine;
    private static BattleModel model;

    private static final Map<Heroes, Region> heroCardMap = new HashMap<>();
    private static final Map<Monster, Region> monsterCardMap = new HashMap<>();

    private static final DropShadow TURN_GLOW  = new DropShadow(35, Color.GOLD);
    private static final DropShadow HOVER_GLOW = new DropShadow(25, Color.AQUA);

    private static String[] monsterType = new String[3];

    public static void show(Stage stage, GameEngine gameEngine) {

        model = new BattleModel(GameEngine.getHeroTEAM(), GameEngine.getMonsterTeam());
        engine = new BattleEngine(model);
        for (int i = 0; i < 3; i++) {
            Random rand = new Random();
            int number = rand.nextInt(3);
            monsterType[i] = "Type" + (number + 1);
        }

        AnchorPane root = new AnchorPane();
        root.setPadding(new Insets(10));

        // ===== Background =====
        Random rand = new Random();
        int number = rand.nextInt(3);
        Image bg = new Image(application.Main.class.getResource("/Background/BattleStage" + (number + 1) + ".png").toExternalForm());
        BackgroundImage bgImage = new BackgroundImage(
                bg,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(
                        100, 100, true, true, true, true // IMPORTANT: scale background
                )
        );
        root.setBackground(new Background(bgImage));

        // ===== HERO BOX (LEFT) =====
        heroBox = new Pane();
        heroBox.setStyle("""
            -fx-border-color: white;
            -fx-border-width: 3;
        """);

        // left + vertically around middle-lower area
        AnchorPane.setLeftAnchor(heroBox, 200.0);
        AnchorPane.setTopAnchor(heroBox, 140.0);

        // ===== MONSTER BOX (RIGHT) =====
        monsterBox = new Pane();
        monsterBox.setStyle("""
            -fx-border-color: white;
            -fx-border-width: 3;
        """);

        AnchorPane.setRightAnchor(monsterBox, 200.0);
        AnchorPane.setTopAnchor(monsterBox, 140.0);

        // ===== CENTER CONTROLS (BOTTOM CENTER) =====
        VBox controls = new VBox(10);
        controls.setAlignment(Pos.CENTER);

        stageLabel = new Label("Stage: " + engine.getBattleStage());
        stageLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        logLabel = new Label("Battle Start! Choose Hero skill");
        logLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

        normalBtn = new Button("Normal");
        skillBtn  = new Button("Skill");
        ultBtn    = new Button("Ultimate");

        styleButton(normalBtn);
        styleButton(skillBtn);
        styleButton(ultBtn);

        HBox skillBar = new HBox(10, normalBtn, skillBtn, ultBtn);
        skillBar.setAlignment(Pos.CENTER);

        controls.getChildren().addAll(stageLabel, logLabel, skillBar);

        // Anchor controls bottom center (stretch full width so it's centered)
        AnchorPane.setLeftAnchor(controls, 0.0);
        AnchorPane.setRightAnchor(controls, 0.0);
        AnchorPane.setBottomAnchor(controls, 20.0);

        // Add nodes
        root.getChildren().addAll(heroBox, monsterBox,controls);

        // 3) Wire buttons to engine
        normalBtn.setOnAction(e -> engine.onClickHeroSkill(SkillType.NORMAL_ATTACK));
        skillBtn.setOnAction(e -> engine.onClickHeroSkill(SkillType.SKILL));
        ultBtn.setOnAction(e -> engine.onClickHeroSkill(SkillType.ULTIMATE));

        // 4) Set listener (engine -> UI updates)
        engine.setListener(new BattleListener() {
            @Override
            public void onStateChanged(BattleStage stage) {
                stageLabel.setText("Stage: " + stage);
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
        int[][] HeroesCardPos = {{0,165,0},
                {0,100,200}
        };
        int[][] MonstersCardPos = {{165,0,165},
                {0,100,200}
        };

        // HERO cards (also used for ally-target selection)
        for (int i = 0; i < heroes.size(); i++) {
            Heroes h = heroes.get(i);

            VBox card = unitCard(h.getName(), h.getHp(), h.isDead(), h.getElement(), true);

            // ===== POSITION (OVERLAP) =====
            card.setLayoutX(HeroesCardPos[0][i]);   // small horizontal shift
            card.setLayoutY(HeroesCardPos[1][i]);   // vertical shift (stack look)

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

            VBox card = unitCard(monsterType[i], m.getHp(), m.isDead(), m.getElement(), false);

            final int idx = i;

            // ===== POSITION (REVERSE STACK) =====
            card.setLayoutX(MonstersCardPos[0][i]);   // small horizontal shift
            card.setLayoutY(MonstersCardPos[1][i]);   // vertical shift (stack look)

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

    private static VBox unitCard(String name, double hp, boolean dead, Element e,boolean isHero) {

        // ===== IMAGE =====
        String path;
        if(isHero) {
            path = "/Heroes/" + name + "/" + name + "Still.PNG";
        }
        else{
            path = "/Monster/" + name + "/" + e + "/Norm.png";
        }
        Image img = new Image(BattleScene.class.getResourceAsStream(path));
        ImageView iv = new ImageView(img);
        iv.setFitWidth(220);
        iv.setFitHeight(220);
        iv.setPreserveRatio(true);

        Label h = new Label("HP: " + (int) hp);
        h.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        // ===== LAYOUT =====
        VBox box = new VBox(h,iv);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));

        box.setStyle("""
                -fx-border-color: white;
                -fx-border-width: 3;
                """);
        box.setOpacity(dead ? 0.35 : 1.0);

        return box;
    }

    private static void setupHoverGlow(Region node, BooleanSupplier canHover) {
        node.setOnMouseEntered(e -> {
            if (canHover.getAsBoolean() && node.getOpacity() > 0.5) {
                node.setEffect(HOVER_GLOW);
            }
        });
        node.setOnMouseExited(e -> {
            node.setEffect(null);
            refreshTurnGlow(); // restore turn glow if needed
        });
    }

    private static void refreshButtons() {
        // If model not ready yet, skip
        Heroes active = safeGetActiveHero();
        if (active == null) return;

        // Default: enable all; then apply stage/cooldown rules
        normalBtn.setDisable(false);

        // Cooldown rules (only matters during hero choose skill stage)
        if (engine.getBattleStage() == BattleStage.HERO_CHOOSE_SKILL) {
            skillBtn.setDisable(!active.canUseSkill());
            ultBtn.setDisable(!active.canUseUlt());
        } else {
            // outside hero choose skill, interactivity function will control them
            // (keep as-is)
        }
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

        if (stage == BattleStage.HERO_CHOOSE_SKILL) {
            setHeroInputEnabled(true);
            logLabel.setText("Choose Hero skill");
            refreshButtons();
            heroBox.setDisable(false);
            monsterBox.setDisable(false);
        }

        else if (stage == BattleStage.HERO_CHOOSE_TARGET) {
            logLabel.setText("Choose a monster target");
            normalBtn.setDisable(true);
            skillBtn.setDisable(true);
            ultBtn.setDisable(true);

            heroBox.setDisable(true);
            monsterBox.setDisable(false);
        }

        else if (stage == BattleStage.HERO_CHOOSE_ALLY) {
            logLabel.setText("Choose an ally target");
            normalBtn.setDisable(true);
            skillBtn.setDisable(true);
            ultBtn.setDisable(true);

            heroBox.setDisable(false);
            monsterBox.setDisable(true);
        }

        else if (stage == BattleStage.MONSTER_TURN) {
            logLabel.setText("Monster turn... (Wait 2 second)");
            setHeroInputEnabled(false);

            PauseTransition p = new PauseTransition(Duration.millis(2000));
            p.setOnFinished(e -> {
                engine.monsterTurn();
                engine.executeMonsterTurnAndContinue();
            });
            p.play();
        }

        else if (stage == BattleStage.WIN_TURN) {
            setHeroInputEnabled(false);
            logLabel.setText("Victory!");
        }

        else if (stage == BattleStage.LOSE_TURN) {
            setHeroInputEnabled(false);
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

    private static void styleButton(Button btn) {
        btn.setStyle("""
            -fx-background-color: linear-gradient(#ff7a18, #ffb347);
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-font-size: 14px;
            -fx-background-radius: 18;
            -fx-padding: 10 18;
        """);

        btn.setOnMouseEntered(e -> {
            btn.setScaleX(1.08);
            btn.setScaleY(1.08);
        });

        btn.setOnMouseExited(e -> {
            btn.setScaleX(1);
            btn.setScaleY(1);
        });
    }
}