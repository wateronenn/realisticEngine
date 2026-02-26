package gui;

import component.Element;
import component.Monster;
import component.heroes.Heroes;
import javafx.animation.*;
import javafx.application.Platform;
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
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import logic.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BooleanSupplier;

public class BattleSceneTemp {

    // ======================= NODES =======================
    private static Pane heroBox;
    private static Pane monsterBox;

    private static Label logLabel;
    private static Label stageLabel;

    private static Button normalBtn, skillBtn, ultBtn;

    // ======================= ENGINE / MODEL =======================
    private static BattleEngine engine;
    private static BattleModel model;

    // ======================= UI MAPS =======================
    private static final Map<Heroes, Region> heroCardMap = new HashMap<>();
    private static final Map<Monster, Region> monsterCardMap = new HashMap<>();

    private static final Map<Heroes, HpBarUI> heroHpMap = new HashMap<>();
    private static final Map<Monster, HpBarUI> monsterHpMap = new HashMap<>();

    private static final Map<Heroes, ImageView> heroImageMap = new HashMap<>();
    private static final Map<Monster, ImageView> monsterImageMap = new HashMap<>();

    // per-hero timer (attack image back to idle)
    private static final Map<Heroes, Animation> heroTempAnimMap = new HashMap<>();

    // ======================= TURN TRACKING =======================
    private static BattleStage lastStage = null;
    private static Heroes lastActiveHero = null;          // active hero at last stage
    private static Heroes heroBeforeMonsterTurn = null;   // the hero who ended hero phase (last hero)

    // ======================= EFFECTS =======================
    private static final DropShadow TURN_GLOW  = new DropShadow(35, Color.GOLD);
    private static final DropShadow HOVER_GLOW = new DropShadow(25, Color.AQUA);

    // ======================= DATA =======================
    private static final String[] monsterType = new String[3];

    // =============================================================
    //                         HP BAR UI
    // =============================================================
    private static class HpBarUI {
        final double barW = 250;
        final double barH = 20;

        final Rectangle bg = new Rectangle(barW, barH);
        final Rectangle border = new Rectangle(barW, barH);
        final Rectangle fill = new Rectangle(barW, barH);
        final Rectangle dmg = new Rectangle(barW, barH);

        final Label text = new Label();

        final double maxHp;
        double currentHp;

        HpBarUI(double maxHp, double currentHp) {
            this.maxHp = Math.max(1, maxHp);
            this.currentHp = currentHp;

            bg.setFill(Color.rgb(0, 0, 0, 0.40));

            border.setFill(Color.TRANSPARENT);
            border.setStroke(Color.WHITE);
            border.setStrokeWidth(2);

            dmg.setFill(Color.web("#ff4d4d"));
            fill.setFill(Color.web("#35d04f"));

            text.setStyle("""
                -fx-text-fill: white;
                -fx-font-size: 13px;
                -fx-font-weight: bold;
                -fx-effect: dropshadow(gaussian, black, 4, 0.6, 0, 1);
            """);

            setInstant(currentHp);
        }

        private void round(Rectangle r) {
            r.setArcWidth(18);
            r.setArcHeight(18);
        }

        StackPane node() {
            // NOTE: your original code never called round(), keeping same behavior.
            StackPane barLayer = new StackPane(bg, dmg, fill, border);
            barLayer.setAlignment(Pos.CENTER_LEFT);

            StackPane.setAlignment(bg, Pos.CENTER_LEFT);
            StackPane.setAlignment(dmg, Pos.CENTER_LEFT);
            StackPane.setAlignment(fill, Pos.CENTER_LEFT);
            StackPane.setAlignment(border, Pos.CENTER_LEFT);

            StackPane textLayer = new StackPane(text);
            textLayer.setAlignment(Pos.CENTER);

            return new StackPane(barLayer, textLayer);
        }

        void setInstant(double hp) {
            currentHp = hp;
            double ratio = clamp01(hp / maxHp);

            fill.setWidth(barW * ratio);
            dmg.setWidth(barW * ratio);

            text.setText((int) hp + "/" + (int) maxHp);
            updateColor(ratio);
        }

        void animateTo(double newHp) {
            double oldRatio = clamp01(currentHp / maxHp);
            double newRatio = clamp01(newHp / maxHp);
            boolean tookDamage = newHp < currentHp;

            currentHp = newHp;
            text.setText((int) newHp + "/" + (int) maxHp);

            Timeline main = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(fill.widthProperty(), barW * oldRatio)
                    ),
                    new KeyFrame(Duration.millis(250),
                            new KeyValue(fill.widthProperty(), barW * newRatio, Interpolator.EASE_BOTH)
                    )
            );
            main.play();

            if (tookDamage) {
                Timeline lag = new Timeline(
                        new KeyFrame(Duration.ZERO,
                                new KeyValue(dmg.widthProperty(), barW * oldRatio)
                        ),
                        new KeyFrame(Duration.millis(450),
                                new KeyValue(dmg.widthProperty(), barW * newRatio, Interpolator.EASE_OUT)
                        )
                );

                PauseTransition delay = new PauseTransition(Duration.millis(120));
                delay.setOnFinished(e -> lag.play());
                delay.play();
            } else {
                dmg.setWidth(barW * newRatio);
            }

            updateColor(newRatio);
        }

        private void updateColor(double ratio) {
            if (ratio > 0.6) fill.setFill(Color.web("#35d04f"));
            else if (ratio > 0.3) fill.setFill(Color.web("#f2c94c"));
            else fill.setFill(Color.web("#ff4d4d"));
        }

        private static double clamp01(double v) {
            return Math.max(0, Math.min(1, v));
        }
    }

    // =============================================================
    //                        ANIMATIONS
    // =============================================================
    private static void playHitAnimation(Region card) {
        TranslateTransition shake = new TranslateTransition(Duration.millis(60), card);
        shake.setByX(10);
        shake.setCycleCount(4);
        shake.setAutoReverse(true);
        shake.play();
    }

    // =============================================================
    //                      IMAGE HELPERS
    // =============================================================
    private static Image loadImageSafe(String path, String fallbackPath) {
        try {
            var s = BattleScene.class.getResourceAsStream(path);
            if (s != null) return new Image(s);
        } catch (Exception ignored) {}

        try {
            var s2 = BattleScene.class.getResourceAsStream(fallbackPath);
            if (s2 != null) return new Image(s2);
        } catch (Exception ignored) {}

        return null;
    }

    private static Image heroIdleImg(Heroes h) {
        String name = h.getName();
        return loadImageSafe(
                "/Heroes/" + name + "/" + name + "Still.PNG",
                "/Heroes/" + name + "/" + name + "Still.PNG"
        );
    }

    private static Image heroAttackImg(Heroes h) {
        String name = h.getName();
        return loadImageSafe(
                "/Heroes/" + name + "/" + name + "Attack.PNG",
                "/Heroes/" + name + "/" + name + "Still.PNG"
        );
    }

    // Show Attack.PNG for 1.5s then back to Still.PNG
    private static void showAttackFor1_5s(Heroes h) {
        if (h == null || h.isDead()) return;

        ImageView iv = heroImageMap.get(h);
        if (iv == null) return;

        Animation old = heroTempAnimMap.get(h);
        if (old != null) old.stop();

        Image atk = heroAttackImg(h);
        Image idle = heroIdleImg(h);
        if (atk == null || idle == null) return;

        iv.setImage(atk);

        PauseTransition back = new PauseTransition(Duration.seconds(1));
        back.setOnFinished(e -> iv.setImage(idle));
        heroTempAnimMap.put(h, back);
        back.play();
    }

    // =============================================================
    //                       TURN HELPERS
    // =============================================================
    private static boolean isHeroDecisionStage(BattleStage s) {
        return s == BattleStage.HERO_CHOOSE_SKILL
                || s == BattleStage.HERO_CHOOSE_TARGET
                || s == BattleStage.HERO_CHOOSE_ALLY;
    }

    private static Heroes safeGetActiveHero() {
        int idx = engine.getModel().getActiveHeroIndex();
        if (idx < 0 || idx >= model.getHERO_TEAM().size()) return null;
        return model.getHERO_TEAM().get(idx);
    }

    /**
     * Core logic:
     * 1) Hero->Hero: when active hero changes during hero decision stages -> previous hero ended turn => show attack on previous hero
     * 2) Last hero: when stage transitions into MONSTER_TURN -> last hero ended hero phase => show attack on heroBeforeMonsterTurn
     * 3) Do NOT trigger after monster turn ends (MONSTER_TURN -> HERO_CHOOSE_...) because we never fire there.
     */
    private static void handleEndTurnAttackImage(BattleStage currentStage) {
        Heroes currentActive = safeGetActiveHero();

        if (lastStage == null) {
            lastStage = currentStage;
            lastActiveHero = currentActive;
            return;
        }

        // track who is the "last hero" right before monster turn
        if (isHeroDecisionStage(lastStage)) {
            heroBeforeMonsterTurn = lastActiveHero;
        }

        // (1) HERO -> HERO end turn
        boolean heroPhaseToHeroPhase = isHeroDecisionStage(lastStage) && isHeroDecisionStage(currentStage);
        if (heroPhaseToHeroPhase && currentActive != null && lastActiveHero != null && currentActive != lastActiveHero) {
            showAttackFor1_5s(lastActiveHero);
        }

        // (2) LAST HERO end turn (entering MONSTER_TURN)
        boolean enteringMonsterTurn = currentStage == BattleStage.MONSTER_TURN && lastStage != BattleStage.MONSTER_TURN;
        if (enteringMonsterTurn) {
            showAttackFor1_5s(heroBeforeMonsterTurn);
        }

        lastStage = currentStage;
        lastActiveHero = currentActive;
    }

    // =============================================================
    //                           MAIN
    // =============================================================
    public static void show(Stage stage, GameEngine gameEngine) {

        model = new BattleModel(GameEngine.getHeroTEAM(), GameEngine.getMonsterTeam());
        engine = new BattleEngine(model);

        Random rand = new Random();
        for (int i = 0; i < 3; i++) monsterType[i] = "Type" + (rand.nextInt(3) + 1);

        AnchorPane root = new AnchorPane();
        root.setPadding(new Insets(10));

        // Background
        int number = rand.nextInt(3) + 1;
        Image bg = new Image(application.Main.class
                .getResource("/Background/BattleStage" + number + ".png")
                .toExternalForm()
        );
        root.setBackground(new Background(new BackgroundImage(
                bg,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, true, true)
        )));

        // HERO BOX (LEFT)
        heroBox = new Pane();
        AnchorPane.setLeftAnchor(heroBox, 150.0);
        AnchorPane.setTopAnchor(heroBox, 140.0);

        // MONSTER BOX (RIGHT)
        monsterBox = new Pane();
        AnchorPane.setRightAnchor(monsterBox, 150.0);
        AnchorPane.setTopAnchor(monsterBox, 140.0);

        // CONTROLS
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
        AnchorPane.setLeftAnchor(controls, 0.0);
        AnchorPane.setRightAnchor(controls, 0.0);
        AnchorPane.setBottomAnchor(controls, 20.0);

        root.getChildren().addAll(heroBox, monsterBox, controls);

        // Buttons
        normalBtn.setOnAction(e -> engine.onClickHeroSkill(SkillType.NORMAL_ATTACK));
        skillBtn.setOnAction(e -> engine.onClickHeroSkill(SkillType.SKILL));
        ultBtn.setOnAction(e -> engine.onClickHeroSkill(SkillType.ULTIMATE));

        engine.setListener(new BattleListener() {
            @Override
            public void onStateChanged(BattleStage stage) {
                Platform.runLater(() -> {
                    stageLabel.setText("Stage: " + stage);
                    refreshInteractivity(stage);
                    refreshTurnGlow();
                    handleEndTurnAttackImage(stage);
                });
            }

            @Override
            public void onLog(String message) {
                Platform.runLater(() -> logLabel.setText(message));
            }

            @Override
            public void onModelUpdated() {
                Platform.runLater(() -> {
                    updateUnitsAnimated();
                    refreshButtons();
                    refreshTurnGlow();
                });
            }
        });

        buildUnits();
        refreshButtons();
        refreshInteractivity(engine.getBattleStage());

        // init tracking before battle starts
        lastStage = engine.getBattleStage();
        lastActiveHero = safeGetActiveHero();
        heroBeforeMonsterTurn = lastActiveHero;

        engine.beginBattle();

        stage.setScene(new Scene(root, 1280, 720));
        stage.show();
    }

    // =============================================================
    //                    BUILD UNITS (ONCE)
    // =============================================================
    private static void buildUnits() {

        heroBox.getChildren().clear();
        monsterBox.getChildren().clear();

        heroCardMap.clear();
        monsterCardMap.clear();

        heroHpMap.clear();
        monsterHpMap.clear();

        heroImageMap.clear();
        monsterImageMap.clear();

        heroTempAnimMap.clear();

        List<Heroes> heroes = model.getHERO_TEAM();
        List<Monster> monsters = model.getMONSTER_TEAM();

        int[][] HeroesCardPos = {{0,165,0},{0,100,200}};
        int[][] MonstersCardPos = {{165,0,165},{0,100,200}};

        // HERO cards
        for (int i = 0; i < heroes.size(); i++) {
            Heroes h = heroes.get(i);

            VBox card = unitCardWithHpBar_Hero(h.getHp(), h.isDead(), h);
            card.setLayoutX(HeroesCardPos[0][i]);
            card.setLayoutY(HeroesCardPos[1][i]);

            setupHoverGlow(card, () -> engine.getBattleStage() == BattleStage.HERO_CHOOSE_ALLY);
            card.setOnMouseClicked(e -> {
                if (engine.getBattleStage() == BattleStage.HERO_CHOOSE_ALLY && !h.isDead()) {
                    engine.onClickChoosingAlly(h);
                }
            });

            heroCardMap.put(h, card);
            heroBox.getChildren().add(card);
        }

        // MONSTER cards
        for (int i = 0; i < monsters.size(); i++) {
            Monster m = monsters.get(i);

            VBox card = unitCardWithHpBar_Monster(monsterType[i], m.getHp(), m.isDead(), m.getElement(), m);

            final int idx = i;

            card.setLayoutX(MonstersCardPos[0][i]);
            card.setLayoutY(MonstersCardPos[1][i]);

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

    // =============================================================
    //                 UPDATE HP (NO REBUILD)
    // =============================================================
    private static void updateUnitsAnimated() {

        for (Heroes h : model.getHERO_TEAM()) {
            HpBarUI ui = heroHpMap.get(h);
            Region card = heroCardMap.get(h);
            if (ui == null || card == null) continue;

            double oldHp = ui.currentHp;
            double newHp = h.getHp();

            if (newHp < oldHp) playHitAnimation(card);
            ui.animateTo(newHp);

            card.setOpacity(h.isDead() ? 0.35 : 1.0);
        }

        for (Monster m : model.getMONSTER_TEAM()) {
            HpBarUI ui = monsterHpMap.get(m);
            Region card = monsterCardMap.get(m);
            if (ui == null || card == null) continue;

            double oldHp = ui.currentHp;
            double newHp = m.getHp();

            if (newHp < oldHp) playHitAnimation(card);
            ui.animateTo(newHp);

            card.setOpacity(m.isDead() ? 0.35 : 1.0);
        }
    }

    // =============================================================
    //                       CARD BUILDERS
    // =============================================================
    private static VBox unitCardWithHpBar_Hero(double hp, boolean dead, Heroes heroRef) {

        ImageView iv = new ImageView(heroIdleImg(heroRef));
        iv.setFitWidth(220);
        iv.setFitHeight(220);
        iv.setPreserveRatio(true);

        heroImageMap.put(heroRef, iv);

        HpBarUI hpUI = new HpBarUI(hp, hp);
        heroHpMap.put(heroRef, hpUI);

        VBox box = new VBox(8, hpUI.node(), iv);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));
        box.setOpacity(dead ? 0.35 : 1.0);

        return box;
    }

    private static VBox unitCardWithHpBar_Monster(String typeName, double hp, boolean dead, Element e, Monster monsterRef) {

        String idlePath = "/Monster/" + typeName + "/" + e + "/Norm.png";

        ImageView iv = new ImageView(loadImageSafe(idlePath, idlePath));
        iv.setFitWidth(220);
        iv.setFitHeight(220);
        iv.setPreserveRatio(true);

        monsterImageMap.put(monsterRef, iv);

        HpBarUI hpUI = new HpBarUI(hp, hp);
        monsterHpMap.put(monsterRef, hpUI);

        VBox box = new VBox(8, hpUI.node(), iv);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));
        box.setOpacity(dead ? 0.35 : 1.0);

        return box;
    }

    // =============================================================
    //                         UI HELPERS
    // =============================================================
    private static void setupHoverGlow(Region node, BooleanSupplier canHover) {
        node.setOnMouseEntered(e -> {
            if (canHover.getAsBoolean() && node.getOpacity() > 0.5) node.setEffect(HOVER_GLOW);
        });

        node.setOnMouseExited(e -> {
            node.setEffect(null);
            refreshTurnGlow();
        });
    }

    private static void refreshButtons() {
        Heroes active = safeGetActiveHero();
        if (active == null) return;

        normalBtn.setDisable(false);

        if (engine.getBattleStage() == BattleStage.HERO_CHOOSE_SKILL) {
            skillBtn.setDisable(!active.canUseSkill());
            ultBtn.setDisable(!active.canUseUlt());
        }
    }

    private static void refreshTurnGlow() {
        heroCardMap.values().forEach(r -> r.setEffect(null));
        monsterCardMap.values().forEach(r -> r.setEffect(null));

        BattleStage s = engine.getBattleStage();
        if (!isHeroDecisionStage(s)) return;

        Heroes active = safeGetActiveHero();
        if (active == null || active.isDead()) return;

        Region card = heroCardMap.get(active);
        if (card != null) card.setEffect(TURN_GLOW);
    }

    private static void refreshInteractivity(BattleStage stage) {

        if (stage == BattleStage.HERO_CHOOSE_SKILL) {

            setHeroInputEnabled(true);
            logLabel.setText("Choose Hero skill");
            refreshButtons();
            heroBox.setDisable(false);
            monsterBox.setDisable(false);

        } else if (stage == BattleStage.HERO_CHOOSE_TARGET) {

            logLabel.setText("Choose a monster target");
            normalBtn.setDisable(true);
            skillBtn.setDisable(true);
            ultBtn.setDisable(true);

            heroBox.setDisable(true);
            monsterBox.setDisable(false);

        } else if (stage == BattleStage.HERO_CHOOSE_ALLY) {

            logLabel.setText("Choose an ally target");
            normalBtn.setDisable(true);
            skillBtn.setDisable(true);
            ultBtn.setDisable(true);

            heroBox.setDisable(false);
            monsterBox.setDisable(true);

        } else if (stage == BattleStage.MONSTER_TURN) {

            logLabel.setText("Monster turn...");
            setHeroInputEnabled(false);

            PauseTransition p = new PauseTransition(Duration.millis(2000));
            p.setOnFinished(e -> {
                //engine.monsterTurn();
                engine.executeMonsterTurnAndContinue();
            });
            p.play();

        } else if (stage == BattleStage.WIN_TURN) {

            setHeroInputEnabled(false);
            logLabel.setText("Victory!");

        } else if (stage == BattleStage.LOSE_TURN) {

            setHeroInputEnabled(false);
            logLabel.setText("Defeat!");
        }
    }

    private static void setHeroInputEnabled(boolean enabled) {
        normalBtn.setDisable(!enabled);
        skillBtn.setDisable(!enabled);
        ultBtn.setDisable(!enabled);

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