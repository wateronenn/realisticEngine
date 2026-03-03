package gui;

import component.Element;
import component.Monster;
import component.heroes.Archer;
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
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import logic.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BooleanSupplier;

public class BattleScene {

    // ======================= CHANGE IMAGE PATH HERE =======================
    // Panels background (your frame background)
    private static final String SKILL_PANEL_BG_PATH = "/Sign/SkillandDescPanel.png";
    private static final String DESC_PANEL_BG_PATH  = "/Sign/SkillandDescPanel.png";

    // Skill buttons images (CHANGE HERE)
    private static final String NORMAL_BTN_IMG_PATH = "/Button/Normal.png";
    private static final String SKILL_BTN_IMG_PATH  = "/Button/Skill.png";
    private static final String ULT_BTN_IMG_PATH    = "/Button/Ultimate.png";
    private static final String CANCEL_BTN_IMG_PATH = "/Button/Cancel.png"; // square X
    // =====================================================================

    // ======================= NODES =======================
    private static GameEngine GAMEENGINE;
    private static Stage STAGE;
    private static Pane heroBox;
    private static Pane monsterBox;

    // TOP CORNER BACKGROUND PANELS
    private static StackPane heroElementPanelTopLeft;
    private static StackPane monsterElementPanelTopRight;

    private static Label logLabel;
    private static Label stageLabel;

    private static Button normalBtn, skillBtn, ultBtn;
    private static Button cancelBtn;

    // ---- Skill description panel labels ----
    private static Label heroNameLabel;
    private static Label actionNameLabel;
    private static Label actionDescLabel;

    // ---- Panels ----
    private static StackPane skillPanel;
    private static StackPane descPanel;

    // track current hero for description updates
    private static Heroes currentTurnHero = null;

    // which action is selected for description (hover changes this)
    private static SkillType selectedSkillType = SkillType.NORMAL_ATTACK;

    // TOP CORNER ELEMENT ICONS
    private static HBox heroElementBoxTopLeft;
    private static HBox monsterElementBoxTopRight;
    private static final Map<Heroes, ImageView> heroElementIconMap = new HashMap<>();
    private static final Map<Monster, ImageView> monsterElementIconMap = new HashMap<>();

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
    private static final Map<Monster, Animation> monsterTempAnimMap = new HashMap<>();

    // ✅ IMPORTANT: Monster "typeName" mapping (Type1/Type2/Type3) used by your card paths
    private static final Map<Monster, String> monsterTypeMap = new HashMap<>();

    // ======================= TURN TRACKING =======================
    private static BattleStage lastStage = null;
    private static Heroes lastActiveHero = null;          // active hero at last stage
    private static Heroes heroBeforeMonsterTurn = null;   // the hero who ended hero phase (last hero)

    // ======================= EFFECTS =======================
    private static final DropShadow TURN_GLOW  = new DropShadow(35, Color.GOLD);
    private static final DropShadow HOVER_GLOW = new DropShadow(25, Color.AQUA);

    // highlight hovered skill button
    private static final DropShadow BTN_GLOW = new DropShadow(18, Color.BLACK);

    // ======================= DATA =======================
    private static final String[] monsterType = new String[3];

    // TOP CENTER STAGE COUNTER PANEL
    private static StackPane stageCounterPanelTopCenter;
    private static Label stageCounterLabel;

    // =============================================================
    //                  SKILL DESCRIPTION DATA
    // =============================================================
    private static class SkillInfo {
        final String normalName, normalDesc;
        final String skillName,  skillDesc;
        final String ultName,    ultDesc;

        SkillInfo(String nn, String nd, String sn, String sd, String un, String ud) {
            normalName = nn; normalDesc = nd;
            skillName  = sn; skillDesc  = sd;
            ultName    = un; ultDesc    = ud;
        }

        String name(SkillType t) {
            return switch (t) {
                case NORMAL_ATTACK -> normalName;
                case SKILL -> skillName;
                case ULTIMATE -> ultName;
            };
        }
        String desc(SkillType t) {
            return switch (t) {
                case NORMAL_ATTACK -> normalDesc;
                case SKILL -> skillDesc;
                case ULTIMATE -> ultDesc;
            };
        }
    }

    // Key = hero.getName()
    private static final Map<String, SkillInfo> HERO_SKILLS = new HashMap<>();

    private static void initHeroSkillsOnce() {
        if (!HERO_SKILLS.isEmpty()) return;

        HERO_SKILLS.put("Caster", new SkillInfo(
                "Magic Bolt", "Deal damage to 1 enemy.",
                "Arcane Pulse", "Deal light magic damage and empower self.",
                "Arcane Cataclysm", "Blast all enemies with devastating magic."
        ));

        HERO_SKILLS.put("Archer", new SkillInfo(
                "Rapid Shot", "Deal damage to 1 enemy.",
                "Arrow Stock", "Add 1 arrow to the quiver up to 5.",
                "Arrow Rain", "Unleash all stored arrows, dealing massive AoE damage."
        ));

        HERO_SKILLS.put("Tank", new SkillInfo(
                "Shield Bash", "Deal damage to 1 enemy.",
                "Guardian’s Mend", "Restore HP to a selected ally.",
                "Aegis Command", "Grant shields and boost all allies."
        ));

        HERO_SKILLS.put("Fighter", new SkillInfo(
                "Quick Attack", "Deal damage to 1 enemy.",
                "Lifesteal Strike", "Attack an enemy and recover HP.",
                "Execution Breaker", "Ignore all defense and deal massive true damage."
        ));
    }

    // =============================================================
    //                  YOUR IMAGE BUTTON FUNCTION (used for skill/cancel)
    // =============================================================
    private static Button createImageButton(String path, double fitW, double fitH) {

        Image img = new Image(BattleScene.class.getResourceAsStream(path));
        ImageView imageView = new ImageView(img);
        imageView.setFitWidth(fitW);
        imageView.setFitHeight(fitH);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        Button btn = new Button();
        btn.setGraphic(imageView);

        btn.setStyle("""
            -fx-background-color: transparent;
            -fx-padding: 0;
            -fx-cursor: hand;
        """);

        DropShadow glow = new DropShadow();
        glow.setColor(Color.BLACK);
        glow.setRadius(20);

        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(150), btn);
        scaleUp.setToX(1.1);
        scaleUp.setToY(1.1);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(150), btn);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        // IMPORTANT: use addEventHandler so we don't override other handlers you add later
        btn.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_ENTERED, e -> {
            if (!btn.isDisabled()) {
                btn.setEffect(glow);
                scaleUp.playFromStart();
            }
        });

        btn.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_EXITED, e -> {
            btn.setEffect(null);
            scaleDown.playFromStart();
        });

        TranslateTransition press = new TranslateTransition(Duration.millis(100), btn);
        press.setToY(3);

        TranslateTransition release = new TranslateTransition(Duration.millis(100), btn);
        release.setToY(0);

        btn.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED, e -> {
            if (!btn.isDisabled()) press.playFromStart();
        });

        btn.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_RELEASED, e -> release.playFromStart());

        return btn;
    }

    // =============================================================
    //                  UI PANELS (Skill / Description)
    // =============================================================
    private static StackPane buildSkillPanel() {
        ImageView bg = new ImageView(new Image(
                BattleScene.class.getResourceAsStream(SKILL_PANEL_BG_PATH)
        ));

        // ================== CHANGE SKILL PANEL SIZE HERE ==================
        double panelW = 650;
        double panelH = 110;
        // ================================================================

        bg.setFitWidth(panelW);
        bg.setFitHeight(panelH);
        bg.setPreserveRatio(false);
        bg.setSmooth(true);

        HBox bar = new HBox(12, normalBtn, skillBtn, ultBtn, cancelBtn);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(0, 0, 0, 40));

        StackPane panel = new StackPane(bg, bar);
        panel.setPrefSize(panelW, panelH);
        panel.setMinSize(panelW, panelH);
        panel.setMaxSize(panelW, panelH);


        StackPane.setAlignment(bar, Pos.CENTER_LEFT);
        return panel;
    }

    private static StackPane buildSkillDescriptionPanel() {
        heroNameLabel = new Label("Hero: -");
        actionNameLabel = new Label("Action: -");
        actionDescLabel = new Label("Description: -");

        Font font2 = Font.loadFont(CharacterSelectionScene.class.getResource("/Font/Supply_Center.ttf").toExternalForm(),12);
        Font font3 = Font.loadFont(CharacterSelectionScene.class.getResource("/Font/Supply_Center.ttf").toExternalForm(),10);
        heroNameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        actionNameLabel.setStyle("-fx-text-fill: black;");
        actionNameLabel.setFont(font2);
        actionDescLabel.setStyle("-fx-text-fill: black;");
        actionDescLabel.setFont(font3);

        actionDescLabel.setWrapText(true);

        VBox textBox = new VBox(6, actionNameLabel, actionDescLabel);
        textBox.setPadding(new Insets(0, 20, 0, 40)); // adjust text offset
        textBox.setAlignment(Pos.CENTER_LEFT);

        ImageView bg = new ImageView(new Image(
                BattleScene.class.getResourceAsStream(DESC_PANEL_BG_PATH)
        ));

        // ================== CHANGE DESC PANEL SIZE HERE ==================
        double panelW = 420;
        double panelH = 110;
        // ================================================================

        bg.setFitWidth(panelW);
        bg.setFitHeight(panelH);
        bg.setPreserveRatio(false);
        bg.setSmooth(true);

        StackPane panel = new StackPane(bg, textBox);
        panel.setPrefSize(panelW, panelH);
        panel.setMinSize(panelW, panelH);
        panel.setMaxSize(panelW, panelH);

        StackPane.setAlignment(textBox, Pos.CENTER_LEFT);
        return panel;
    }

    private static void updateSkillDescription() {
        if (heroNameLabel == null) return;

        if (currentTurnHero == null) {
            heroNameLabel.setText("Hero: -");
            actionNameLabel.setText("Action: -");
            actionDescLabel.setText("Description: -");
            return;
        }

        initHeroSkillsOnce();

        String heroKey = currentTurnHero.getName();
        SkillInfo info = HERO_SKILLS.get(heroKey);

        heroNameLabel.setText("Hero: " + currentTurnHero.getName());

        if (info == null) {
            actionNameLabel.setText("Action: " + selectedSkillType);
            actionDescLabel.setText("Description: (no data for hero name: " + heroKey + ")");
            return;
        }

        actionNameLabel.setText(selectedSkillType + ": " + info.name(selectedSkillType));
        if(info.name(selectedSkillType) == "Arrow Stock" && currentTurnHero instanceof Archer){
            actionDescLabel.setText(info.desc(selectedSkillType) + " (now : " + ((Archer) currentTurnHero).getBowStack() + ")");
        }
        else{
            actionDescLabel.setText(info.desc(selectedSkillType));
        }
    }

    private static StackPane buildStageCounterPanel(GameEngine gameEngine) {

        // background image (reuse your panel image)
        ImageView bg = new ImageView(new Image(
                BattleScene.class.getResourceAsStream(SKILL_PANEL_BG_PATH)
        ));

        // panel size (adjust as you like)
        double panelW = 200;
        double panelH = 80;

        bg.setFitWidth(panelW);
        bg.setFitHeight(panelH);
        bg.setPreserveRatio(false);
        bg.setSmooth(true);

        stageCounterLabel = new Label();
        stageCounterLabel.setStyle("-fx-text-fill: black;");
        Font f = Font.loadFont(CharacterSelectionScene.class.getResource("/Font/Supply_Center.ttf").toExternalForm(), 18);
        stageCounterLabel.setFont(f);

        StackPane panel = new StackPane(bg, stageCounterLabel);
        panel.setPrefSize(panelW, panelH);
        panel.setMinSize(panelW, panelH);
        panel.setMaxSize(panelW, panelH);

        // center the text
        StackPane.setAlignment(stageCounterLabel, Pos.CENTER);

        updateStageCounter(gameEngine);
        return panel;
    }

    private static void updateStageCounter(GameEngine gameEngine) {
        if (stageCounterLabel == null || gameEngine == null) return;

        // ✅ use your method name here:
        stageCounterLabel.setText("STAGE  " + gameEngine.getStageCounter());
    }

    private static void onHeroTurnStartedForHud(Heroes hero) {
        currentTurnHero = hero;
        selectedSkillType = SkillType.NORMAL_ATTACK;
        updateSkillDescription();

        clearSkillButtonEffects();
        normalBtn.setEffect(BTN_GLOW);
    }

    private static void clearSkillButtonEffects() {
        if (normalBtn != null) normalBtn.setEffect(null);
        if (skillBtn != null)  skillBtn.setEffect(null);
        if (ultBtn != null)    ultBtn.setEffect(null);
    }

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

        StackPane node() {
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

    private static Image heroShieldImg(Heroes h) {
        String name = h.getName();
        return loadImageSafe(
                "/Heroes/" + name + "/" + name + "Shield.PNG",
                "/Heroes/" + name + "/" + name + "Still.PNG"
        );
    }

    private static Image heroDeadImg(Heroes h) {
        String name = h.getName();
        return loadImageSafe(
                "/Heroes/" + name + "/" + name + "IsDead.PNG",
                "/Heroes/" + name + "/" + name + "Still.PNG"
        );
    }

    // ✅ Monster paths should match your card builder:
    // "/Monster/" + typeName + "/" + element + "/Norm.png"
    private static String monsterTypeName(Monster m) {
        return monsterTypeMap.getOrDefault(m, m.getName()); // fallback
    }

    private static Image monsterIdleImg(Monster m) {
        String type = monsterTypeName(m);
        String e = m.getElement().toString();
        return loadImageSafe(
                "/Monster/" + type + "/" + e + "/Norm.png",
                "/Monster/" + type + "/" + e + "/Norm.png"
        );
    }

    private static Image monsterDeadImg(Monster m) {
        String type = monsterTypeName(m);
        String e = m.getElement().toString();
        return loadImageSafe(
                "/Monster/" + type + "/" + e + "/Dead.png",
                "/Monster/" + type + "/" + e + "/Norm.png"
        );
    }

    // ✅ NEW: monster attack image (Attack.png) for 1 sec
    private static Image monsterAttackImg(Monster m) {
        String type = monsterTypeName(m);
        String e = m.getElement().toString();
        return loadImageSafe(
                "/Monster/" + type + "/" + e + "/Attack.png",
                "/Monster/" + type + "/" + e + "/Norm.png"
        );
    }

    private static Image heroBaseImg(Heroes h) {
        if (h == null) return null;
        if (h.isDead()) return heroDeadImg(h);
        if (h.getShield() > 0) return heroShieldImg(h);
        return heroIdleImg(h);
    }

    private static Image monsterBaseImg(Monster m) {
        return m.isDead() ? monsterDeadImg(m) : monsterIdleImg(m);
    }

    // Show Attack.PNG for 1.0s then back to base image
    private static void showAttackFor1_5s(Heroes h) {
        if (h == null || h.isDead()) return;

        ImageView iv = heroImageMap.get(h);
        if (iv == null) return;

        Animation old = heroTempAnimMap.get(h);
        if (old != null) old.stop();

        Image atk = heroAttackImg(h);
        if (atk == null) return;

        iv.setImage(atk);

        PauseTransition back = new PauseTransition(Duration.seconds(1));
        back.setOnFinished(e -> {
            Image base = heroBaseImg(h);
            if (base != null) iv.setImage(base);
        });

        heroTempAnimMap.put(h, back);
        back.play();
    }

    // ✅ NEW: Monster show attack for 1 second then revert
    private static void showMonsterAttackFor1s(Monster m) {
        if (m == null || m.isDead()) return;

        ImageView iv = monsterImageMap.get(m);
        if (iv == null) return;

        Animation old = monsterTempAnimMap.get(m);
        if (old != null) old.stop();

        Image atk = monsterAttackImg(m);
        if (atk != null) iv.setImage(atk);

        PauseTransition back = new PauseTransition(Duration.seconds(1));
        back.setOnFinished(e -> {
            Image base = monsterBaseImg(m);
            if (base != null) iv.setImage(base);
        });

        monsterTempAnimMap.put(m, back);
        back.play();
    }

    // =============================================================
    //                    TOP CORNER ELEMENT ICONS
    // =============================================================
    private static void initTopCornerElementUI(AnchorPane root) {

        heroElementBoxTopLeft = new HBox(12);
        heroElementBoxTopLeft.setPadding(new Insets(5));
        heroElementBoxTopLeft.setAlignment(Pos.CENTER_LEFT);

        monsterElementBoxTopRight = new HBox(12);
        monsterElementBoxTopRight.setPadding(new Insets(5));
        monsterElementBoxTopRight.setAlignment(Pos.CENTER_RIGHT);

        heroElementPanelTopLeft = new StackPane(heroElementBoxTopLeft);
        heroElementPanelTopLeft.setAlignment(Pos.CENTER_LEFT);
        heroElementPanelTopLeft.setPadding(new Insets(6));

        monsterElementPanelTopRight = new StackPane(monsterElementBoxTopRight);
        monsterElementPanelTopRight.setAlignment(Pos.CENTER_RIGHT);
        monsterElementPanelTopRight.setPadding(new Insets(6));

        rebuildTopCornerElementIcons();

        AnchorPane.setTopAnchor(heroElementPanelTopLeft, 12.0);
        AnchorPane.setLeftAnchor(heroElementPanelTopLeft, 12.0);

        AnchorPane.setTopAnchor(monsterElementPanelTopRight, 12.0);
        AnchorPane.setRightAnchor(monsterElementPanelTopRight, 12.0);

        root.getChildren().addAll(heroElementPanelTopLeft, monsterElementPanelTopRight);
    }

    private static void rebuildTopCornerElementIcons() {
        if (heroElementBoxTopLeft != null) heroElementBoxTopLeft.getChildren().clear();
        if (monsterElementBoxTopRight != null) monsterElementBoxTopRight.getChildren().clear();

        heroElementIconMap.clear();
        monsterElementIconMap.clear();

        if (model == null) return;

        for (Heroes h : model.getHERO_TEAM()) {
            StackPane overlay = buildHeroElementOverlay(h);
            heroElementBoxTopLeft.getChildren().add(overlay);
        }

        for (Monster m : model.getMONSTER_TEAM()) {
            StackPane overlay = buildMonsterElementOverlay(m);
            monsterElementBoxTopRight.getChildren().add(overlay);
        }
    }

    private static StackPane buildHeroElementOverlay(Heroes hero) {

        Image elementImg = RandomElementGenerator.getElementImage(hero.getElement());
        ImageView elementView = new ImageView(elementImg);
        elementView.setFitWidth(60);
        elementView.setFitHeight(60);
        elementView.setPreserveRatio(true);

        String name = hero.getName();
        Image heroIcon = loadImageSafe(
                "/Heroes/" + name + "/" + name + "Icon.PNG",
                "/Heroes/" + name + "/" + name + "Still.PNG"
        );
        ImageView heroView = new ImageView(heroIcon);
        heroView.setFitWidth(60);
        heroView.setFitHeight(60);
        heroView.setPreserveRatio(true);
        heroView.setSmooth(true);

        DropShadow shadow = new DropShadow();
        shadow.setRadius(18);
        shadow.setSpread(0.35);
        shadow.setOffsetX(0);
        shadow.setOffsetY(10);
        shadow.setColor(Color.rgb(0, 0, 0, 0.80));
        heroView.setEffect(shadow);

        Image iconImg = new Image(BattleScene.class.getResourceAsStream("/Sign/ElementSlot.png"));
        ImageView iv = new ImageView(iconImg);
        iv.setFitWidth(100);
        iv.setFitHeight(100);

        StackPane overlay = new StackPane(iv, elementView, heroView);
        StackPane.setAlignment(elementView, Pos.TOP_RIGHT);
        StackPane.setMargin(elementView, new Insets(10));
        StackPane.setAlignment(heroView, Pos.BOTTOM_LEFT);
        StackPane.setMargin(heroView, new Insets(12));
        overlay.setPrefSize(100, 100);

        heroElementIconMap.put(hero, elementView);
        return overlay;
    }

    private static StackPane buildMonsterElementOverlay(Monster m) {

        Image elementImg = RandomElementGenerator.getElementImage(m.getElement());
        ImageView elementView = new ImageView(elementImg);
        elementView.setFitWidth(60);
        elementView.setFitHeight(60);
        elementView.setPreserveRatio(true);

        // keep your old overlay monster image (doesn't affect attack animation)
        String normPath = "/Monster/" + m.getName() + "/" + m.getElement() + "/Norm.png";
        Image monsterImg = loadImageSafe(normPath, normPath);

        ImageView monsterView = new ImageView(monsterImg);
        monsterView.setFitWidth(75);
        monsterView.setFitHeight(75);
        monsterView.setPreserveRatio(true);
        monsterView.setSmooth(true);

        DropShadow shadow = new DropShadow();
        shadow.setRadius(18);
        shadow.setSpread(0.35);
        shadow.setOffsetX(0);
        shadow.setOffsetY(10);
        shadow.setColor(Color.rgb(0, 0, 0, 0.80));
        monsterView.setEffect(shadow);

        Image iconImg = new Image(BattleScene.class.getResourceAsStream("/Sign/ElementSlot.png"));
        ImageView iv = new ImageView(iconImg);
        iv.setFitWidth(100);
        iv.setFitHeight(100);

        StackPane overlay = new StackPane(iv, elementView, monsterView);
        StackPane.setAlignment(elementView, Pos.TOP_RIGHT);
        StackPane.setMargin(elementView, new Insets(12));
        StackPane.setAlignment(monsterView, Pos.TOP_LEFT);
        StackPane.setAlignment(iv, Pos.CENTER);
        overlay.setPrefSize(100, 100);

        monsterElementIconMap.put(m, elementView);
        return overlay;
    }

    private static void refreshTopCornerElementIcons() {
        if (model == null) return;

        for (Heroes h : model.getHERO_TEAM()) {
            ImageView iv = heroElementIconMap.get(h);
            if (iv != null) iv.setImage(RandomElementGenerator.getElementImage(h.getElement()));
        }

        for (Monster m : model.getMONSTER_TEAM()) {
            ImageView iv = monsterElementIconMap.get(m);
            if (iv != null) iv.setImage(RandomElementGenerator.getElementImage(m.getElement()));
        }
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

    private static void handleEndTurnAttackImage(BattleStage currentStage) {
        Heroes currentActive = safeGetActiveHero();

        if (lastStage == null) {
            lastStage = currentStage;
            lastActiveHero = currentActive;
            return;
        }

        if (isHeroDecisionStage(lastStage)) {
            heroBeforeMonsterTurn = lastActiveHero;
        }

        // HERO -> HERO end turn
        boolean heroPhaseToHeroPhase = isHeroDecisionStage(lastStage) && isHeroDecisionStage(currentStage);
        if (heroPhaseToHeroPhase && currentActive != null && lastActiveHero != null && currentActive != lastActiveHero) {
            showAttackFor1_5s(lastActiveHero);
        }

        // LAST HERO end turn -> MONSTER
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
        GAMEENGINE = gameEngine;
        STAGE = stage;

        Random rand = new Random();
        for (int i = 0; i < 3; i++) monsterType[i] = "Type" + (rand.nextInt(3) + 1);

        AnchorPane root = new AnchorPane();
        root.setPadding(new Insets(8));

        stageCounterPanelTopCenter = buildStageCounterPanel(gameEngine);

        // top anchor
        AnchorPane.setTopAnchor(stageCounterPanelTopCenter, 10.0);
        AnchorPane.setLeftAnchor(stageCounterPanelTopCenter,540.0);

        root.getChildren().add(stageCounterPanelTopCenter);

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

        initTopCornerElementUI(root);

        heroBox = new Pane();
        AnchorPane.setLeftAnchor(heroBox, 150.0);
        AnchorPane.setTopAnchor(heroBox, 120.0);

        monsterBox = new Pane();
        AnchorPane.setRightAnchor(monsterBox, 150.0);
        AnchorPane.setTopAnchor(monsterBox, 120.0);

        // ===================== BOTTOM HUD =====================
        stageLabel = new Label("Stage: " + engine.getBattleStage());
        stageLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        logLabel = new Label("Battle Start! Choose Hero skill");
        logLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

        // ===================== IMAGE BUTTONS (CHANGE SIZE HERE) =====================
        normalBtn = createImageButton(NORMAL_BTN_IMG_PATH, 150, 70);
        skillBtn  = createImageButton(SKILL_BTN_IMG_PATH, 150, 70);
        ultBtn    = createImageButton(ULT_BTN_IMG_PATH, 150, 70);
        cancelBtn = createImageButton(CANCEL_BTN_IMG_PATH, 70, 70); // square X
        // ==========================================================================

        skillPanel = buildSkillPanel();
        descPanel  = buildSkillDescriptionPanel();

        VBox centerInfo = new VBox(6, stageLabel, logLabel);
        centerInfo.setAlignment(Pos.BOTTOM_CENTER);

        BorderPane hud = new BorderPane();
        hud.setLeft(skillPanel);
        //hud.setCenter(centerInfo);
        hud.setRight(descPanel);
        hud.setPadding(new Insets(10));
        hud.setPickOnBounds(false);

        AnchorPane.setLeftAnchor(hud, 10.0);
        AnchorPane.setRightAnchor(hud, 10.0);
        AnchorPane.setBottomAnchor(hud, 0.0);

        root.getChildren().addAll(heroBox, monsterBox, hud);

        // ===================== HOVER to change description (NOT selection) =====================
        normalBtn.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_ENTERED, e -> {
            selectedSkillType = SkillType.NORMAL_ATTACK;
            updateSkillDescription();
            clearSkillButtonEffects();
            normalBtn.setEffect(BTN_GLOW);
        });

        skillBtn.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_ENTERED, e -> {
            selectedSkillType = SkillType.SKILL;
            updateSkillDescription();
            clearSkillButtonEffects();
            skillBtn.setEffect(BTN_GLOW);
        });

        ultBtn.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_ENTERED, e -> {
            selectedSkillType = SkillType.ULTIMATE;
            updateSkillDescription();
            clearSkillButtonEffects();
            ultBtn.setEffect(BTN_GLOW);
        });

        // leave skill panel -> revert to pending (or normal)
        skillPanel.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_EXITED, e -> {
            SkillType pending = (model != null) ? model.getPendingSkill() : null;
            selectedSkillType = (pending != null) ? pending : SkillType.NORMAL_ATTACK;
            updateSkillDescription();

            clearSkillButtonEffects();
            if (selectedSkillType == SkillType.NORMAL_ATTACK) normalBtn.setEffect(BTN_GLOW);
            else if (selectedSkillType == SkillType.SKILL) skillBtn.setEffect(BTN_GLOW);
            else ultBtn.setEffect(BTN_GLOW);
        });

        // ===================== CLICK to actually pick skill (engine) =====================
        normalBtn.setOnAction(e -> engine.onClickHeroSkill(SkillType.NORMAL_ATTACK));
        skillBtn.setOnAction(e -> engine.onClickHeroSkill(SkillType.SKILL));
        ultBtn.setOnAction(e -> engine.onClickHeroSkill(SkillType.ULTIMATE));
        cancelBtn.setOnAction(e -> engine.onCancelSelection());

        engine.setListener(new BattleListener() {
            @Override
            public void onStateChanged(BattleStage stage) {
                Platform.runLater(() -> {
                    stageLabel.setText("Stage: " + stage);
                    refreshInteractivity(stage);
                    refreshTurnGlow();
                    handleEndTurnAttackImage(stage);

                    if (stage == BattleStage.HERO_CHOOSE_SKILL) {
                        Heroes active = safeGetActiveHero();
                        if (active != null && active != currentTurnHero) onHeroTurnStartedForHud(active);
                        else updateSkillDescription();
                    }
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
                    refreshTopCornerElementIcons();
                    updateSkillDescription();
                });
            }
        });

        buildUnits();
        refreshButtons();
        refreshInteractivity(engine.getBattleStage());

        lastStage = engine.getBattleStage();
        lastActiveHero = safeGetActiveHero();
        heroBeforeMonsterTurn = lastActiveHero;

        onHeroTurnStartedForHud(lastActiveHero);

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
        monsterTempAnimMap.clear();

        monsterTypeMap.clear(); // ✅

        List<Heroes> heroes = model.getHERO_TEAM();
        List<Monster> monsters = model.getMONSTER_TEAM();

        int[][] HeroesCardPos = {{0,165,0},{0,100,200}};
        int[][] MonstersCardPos = {{165,0,165},{0,100,200}};

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

        for (int i = 0; i < monsters.size(); i++) {
            Monster m = monsters.get(i);

            // ✅ store the chosen typeName per monster, so attack/idle/dead use same folder
            String typeName = monsterType[i];
            monsterTypeMap.put(m, typeName);

            VBox card = unitCardWithHpBar_Monster(typeName, m.getHp(), m.isDead(), m.getElement(), m);
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

            if (newHp < oldHp) {
                Platform.runLater(() -> {
                    playHitAnimation(card);
                });
            }
            ui.animateTo(newHp);

            card.setOpacity(h.isDead() ? 0.35 : 1.0);

            Animation temp = heroTempAnimMap.get(h);
            boolean tempRunning = (temp != null && temp.getStatus() == Animation.Status.RUNNING);

            if (!tempRunning) {
                ImageView iv = heroImageMap.get(h);
                if (iv != null) {
                    Image base = heroBaseImg(h);
                    if (base != null) iv.setImage(base);
                }
            }
        }

        for (Monster m : model.getMONSTER_TEAM()) {
            HpBarUI ui = monsterHpMap.get(m);
            Region card = monsterCardMap.get(m);
            if (ui == null || card == null) continue;

            double oldHp = ui.currentHp;
            double newHp = m.getHp();

            if (newHp < oldHp) {
                Platform.runLater(() -> {
                    playHitAnimation(card);
                });
            }
            ui.animateTo(newHp);

            card.setOpacity(m.isDead() ? 0.35 : 1.0);

            Animation temp = monsterTempAnimMap.get(m);
            boolean tempRunning = (temp != null && temp.getStatus() == Animation.Status.RUNNING);

            if (!tempRunning) {
                ImageView iv = monsterImageMap.get(m);
                if (iv != null) iv.setImage(monsterBaseImg(m));
            }
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

        HpBarUI hpUI = new HpBarUI(heroRef.getMaxHp(), hp);
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

        HpBarUI hpUI = new HpBarUI(monsterRef.getMaxHp(), hp);
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
        cancelBtn.setDisable(!(stage == BattleStage.HERO_CHOOSE_TARGET || stage == BattleStage.HERO_CHOOSE_ALLY));

        if (stage == BattleStage.HERO_CHOOSE_SKILL) {

            setHeroInputEnabled(true);
            logLabel.setText("Choose Hero skill");
            refreshButtons();
            heroBox.setDisable(false);
            monsterBox.setDisable(false);

            Heroes active = safeGetActiveHero();
            if (active != null) onHeroTurnStartedForHud(active);

        } else if (stage == BattleStage.HERO_CHOOSE_TARGET) {

            logLabel.setText("Choose a monster target");
            normalBtn.setDisable(true);
            skillBtn.setDisable(true);
            ultBtn.setDisable(true);

            heroBox.setDisable(true);
            monsterBox.setDisable(false);

            SkillType pending = (model != null) ? model.getPendingSkill() : null;
            selectedSkillType = (pending != null) ? pending : SkillType.NORMAL_ATTACK;
            updateSkillDescription();

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

            PauseTransition startDelay = new PauseTransition(Duration.millis(500));
            startDelay.setOnFinished(e -> {
                List<Monster> monsters = model.getMONSTER_TEAM();
                playAllMonstersAttack(
                        monsters,
                        0,
                        engine::executeMonsterTurnAndContinue,
                        engine::monsterAttackOne
                );
            });
            startDelay.play();

        } else if (stage == BattleStage.WIN_TURN) {
            setHeroInputEnabled(false);
            logLabel.setText("Victory!");
            VictoryScene.show(getStage(), getGameEngine());

        } else if (stage == BattleStage.LOSE_TURN) {
            setHeroInputEnabled(false);
            logLabel.setText("Defeat!");
            DefeatScene.show(getStage(), getGameEngine());
        }
    }

    private static void playSingleMonsterAttack(
            Monster m,
            Runnable applyDamageThenUpdateUi,
            Runnable afterDone) {

        if (m == null || m.isDead()) {
            afterDone.run();
            return;
        }

        ImageView iv = monsterImageMap.get(m);
        if (iv == null) {
            applyDamageThenUpdateUi.run();
            afterDone.run();
            return;
        }

        Animation old = monsterTempAnimMap.get(m);
        if (old != null) old.stop();

        // ✅ CHANGE IMAGE TO ATTACK for 1 second
        showMonsterAttackFor1s(m);

        // hit timing
        PauseTransition hitMoment = new PauseTransition(Duration.millis(600));
        PauseTransition finish = new PauseTransition(Duration.millis(400));

        hitMoment.setOnFinished(e -> {
            applyDamageThenUpdateUi.run();
            finish.play();
        });

        finish.setOnFinished(e -> afterDone.run());

        monsterTempAnimMap.put(m, finish);
        hitMoment.play();
    }

    private static void playAllMonstersAttack(List<Monster> monsters, int idx, Runnable onAllDone,
                                              java.util.function.Consumer<Monster> applyDamageForMonster) {
        if (idx >= monsters.size()) {
            onAllDone.run();
            return;
        }

        Monster m = monsters.get(idx);

        playSingleMonsterAttack(
                m,
                () -> {
                    applyDamageForMonster.accept(m);
                    updateUnitsAnimated();
                },
                () -> playAllMonstersAttack(monsters, idx + 1, onAllDone, applyDamageForMonster)
        );
    }

    private static void setHeroInputEnabled(boolean enabled) {
        normalBtn.setDisable(!enabled);
        skillBtn.setDisable(!enabled);
        ultBtn.setDisable(!enabled);
        cancelBtn.setDisable(!enabled);

        heroBox.setDisable(!enabled);
        monsterBox.setDisable(!enabled);
    }

    private static GameEngine getGameEngine() {
        return GAMEENGINE;
    }

    private static Stage getStage() {
        return STAGE;
    }
}