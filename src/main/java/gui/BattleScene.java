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

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BooleanSupplier;

/**
 * The {@code BattleScene} class is responsible for rendering and controlling
 * the graphical user interface of the battle phase in the game.
 *
 * <p>This class connects the {@link BattleEngine} and {@link BattleModel}
 * with the JavaFX UI layer. It handles:</p>
 *
 * <ul>
 *     <li>Displaying heroes and monsters</li>
 *     <li>Rendering HP bars with animations</li>
 *     <li>Managing skill selection and descriptions</li>
 *     <li>Handling turn transitions</li>
 *     <li>Playing attack and hit animations</li>
 *     <li>Updating UI when the model changes</li>
 * </ul>
 *
 * <p>The class uses static UI references to maintain a single active battle
 * scene during runtime.</p>
 *
 * <p><b>Architecture Role:</b></p>
 * <ul>
 *     <li>View layer in MVC structure</li>
 *     <li>Listens to {@link BattleListener} callbacks</li>
 *     <li>Updates UI based on {@link BattleStage}</li>
 * </ul>
 *
 * @author Puttisan
 * @version 1.0
 */
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
    /** Reference to the shared game engine instance used for stage counter and team state. */
    private static GameEngine GAMEENGINE;

    /** JavaFX stage currently showing the battle UI. */
    private static Stage STAGE;

    /** Container holding hero cards and interactions. */
    private static Pane heroBox;

    /** Container holding monster cards and interactions. */
    private static Pane monsterBox;

    // TOP CORNER BACKGROUND PANELS
    /** Top-left panel holding hero element icons. */
    private static StackPane heroElementPanelTopLeft;

    /** Top-right panel holding monster element icons. */
    private static StackPane monsterElementPanelTopRight;

    /** Text label for displaying battle log or guidance messages. */
    private static Label logLabel;

    /** Text label for displaying current {@link BattleStage}. */
    private static Label stageLabel;

    /** Action buttons for the hero turn. */
    private static Button normalBtn, skillBtn, ultBtn;

    /** Cancel button for exiting target or ally selection. */
    private static Button cancelBtn;

    // ---- Skill description panel labels ----
    /** Label showing current hero name for the skill description HUD. */
    private static Label heroNameLabel;

    /** Label showing the selected action name. */
    private static Label actionNameLabel;

    /** Label showing action description text. */
    private static Label actionDescLabel;

    // ---- Panels ----
    /** HUD panel holding skill buttons. */
    private static StackPane skillPanel;

    /** HUD panel holding skill descriptions. */
    private static StackPane descPanel;

    // track current hero for description updates
    /** Currently active hero during the player's decision stage. */
    private static Heroes currentTurnHero = null;

    // which action is selected for description (hover changes this)
    /** Skill type currently displayed in the description panel (hover-based). */
    private static SkillType selectedSkillType = SkillType.NORMAL_ATTACK;

    // TOP CORNER ELEMENT ICONS
    /** Top-left icon row for hero elements. */
    private static HBox heroElementBoxTopLeft;

    /** Top-right icon row for monster elements. */
    private static HBox monsterElementBoxTopRight;

    /** Map from hero to its displayed element icon image view. */
    private static final Map<Heroes, ImageView> heroElementIconMap = new HashMap<>();

    /** Map from monster to its displayed element icon image view. */
    private static final Map<Monster, ImageView> monsterElementIconMap = new HashMap<>();

    // ======================= ENGINE / MODEL =======================
    /** Battle engine controlling battle flow. */
    private static BattleEngine engine;

    /** Battle model providing teams and indexes. */
    private static BattleModel model;

    // ======================= UI MAPS =======================
    /** Map from hero to its UI card node. */
    private static final Map<Heroes, Region> heroCardMap = new HashMap<>();

    /** Map from monster to its UI card node. */
    private static final Map<Monster, Region> monsterCardMap = new HashMap<>();

    /** Map from hero to its HP bar UI wrapper. */
    private static final Map<Heroes, HpBarUI> heroHpMap = new HashMap<>();

    /** Map from monster to its HP bar UI wrapper. */
    private static final Map<Monster, HpBarUI> monsterHpMap = new HashMap<>();

    /** Map from hero to its image view used for sprite swaps (idle, attack, dead, shield). */
    private static final Map<Heroes, ImageView> heroImageMap = new HashMap<>();

    /** Map from monster to its image view used for sprite swaps (idle, attack, dead). */
    private static final Map<Monster, ImageView> monsterImageMap = new HashMap<>();

    /** Temporary per-hero animations (used to revert attack image back to base). */
    private static final Map<Heroes, Animation> heroTempAnimMap = new HashMap<>();

    /** Temporary per-monster animations (used to revert attack image back to base). */
    private static final Map<Monster, Animation> monsterTempAnimMap = new HashMap<>();

    /** Monster type folder mapping used to resolve correct resource paths for each monster card. */
    private static final Map<Monster, String> monsterTypeMap = new HashMap<>();

    // ======================= TURN TRACKING =======================
    /** Previous stage used to detect stage transitions and play end-turn animations. */
    private static BattleStage lastStage = null;

    /** The active hero at the previous stage. */
    private static Heroes lastActiveHero = null;

    /** The hero that ended the hero phase before the monster turn begins. */
    private static Heroes heroBeforeMonsterTurn = null;

    // ======================= EFFECTS =======================
    /** Glow effect for currently active hero. */
    private static final DropShadow TURN_GLOW  = new DropShadow(35, Color.GOLD);

    /** Glow effect for hovered selectable cards during target selection. */
    private static final DropShadow HOVER_GLOW = new DropShadow(25, Color.AQUA);

    /** Glow effect for hovered skill buttons. */
    private static final DropShadow BTN_GLOW = new DropShadow(18, Color.BLACK);

    // TOP CENTER STAGE COUNTER PANEL
    /** Top-center panel showing stage counter. */
    private static StackPane stageCounterPanelTopCenter;

    /** Label showing current stage number. */
    private static Label stageCounterLabel;

    /**
     * Holds display name and description for each action type for a single hero.
     *
     * <p>This is used only for UI text display and does not affect gameplay logic.
     */
    private static class SkillInfo {
        final String normalName, normalDesc;
        final String skillName,  skillDesc;
        final String ultName,    ultDesc;

        /**
         * Creates a skill info bundle for one hero.
         *
         * @param nn normal attack name
         * @param nd normal attack description
         * @param sn skill name
         * @param sd skill description
         * @param un ultimate name
         * @param ud ultimate description
         */
        SkillInfo(String nn, String nd, String sn, String sd, String un, String ud) {
            normalName = nn; normalDesc = nd;
            skillName  = sn; skillDesc  = sd;
            ultName    = un; ultDesc    = ud;
        }

        /**
         * Returns the display name for a given skill type.
         *
         * @param t skill type
         * @return name string to show in the HUD
         */
        String name(SkillType t) {
            return switch (t) {
                case NORMAL_ATTACK -> normalName;
                case SKILL -> skillName;
                case ULTIMATE -> ultName;
            };
        }

        /**
         * Returns the display description for a given skill type.
         *
         * @param t skill type
         * @return description string to show in the HUD
         */
        String desc(SkillType t) {
            return switch (t) {
                case NORMAL_ATTACK -> normalDesc;
                case SKILL -> skillDesc;
                case ULTIMATE -> ultDesc;
            };
        }
    }

    /** Map of hero name to skill description data used by the HUD. */
    private static final Map<String, SkillInfo> HERO_SKILLS = new HashMap<>();

    /**
     * Initializes the hero skill description map if it has not been initialized yet.
     *
     * <p>This method is idempotent and will only populate data once.
     */
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

    /**
     * Creates an image-based JavaFX button with hover and press animations.
     *
     * <p>The button uses an {@link ImageView} as its graphic and applies:
     * scale animation on hover, drop shadow glow effect, and press translation animation.
     *
     * <p>The button background is fully transparent.
     *
     * @param path resource path of the image
     * @param fitW width of the image
     * @param fitH height of the image
     * @return a styled animated button
     */
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

    /**
     * Builds the bottom-left skill button panel containing:
     * normal, skill, ultimate, and cancel buttons.
     *
     * @return skill panel node
     */
    private static StackPane buildSkillPanel() {
        ImageView bg = new ImageView(new Image(
                BattleScene.class.getResourceAsStream(SKILL_PANEL_BG_PATH)
        ));

        double panelW = 650;
        double panelH = 110;

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

    /**
     * Builds the bottom-right description panel that shows action name and description
     * based on the currently active hero and the selected skill type.
     *
     * @return description panel node
     */
    private static StackPane buildSkillDescriptionPanel() {
        heroNameLabel = new Label("Hero: -");
        actionNameLabel = new Label("Action: -");
        actionDescLabel = new Label("Description: -");

        Font font2 = Font.loadFont(CharacterSelectionScene.class.getResource("/Font/Supply_Center.ttf").toExternalForm(), 12);
        Font font3 = Font.loadFont(CharacterSelectionScene.class.getResource("/Font/Supply_Center.ttf").toExternalForm(), 10);
        heroNameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        actionNameLabel.setStyle("-fx-text-fill: black;");
        actionNameLabel.setFont(font2);
        actionDescLabel.setStyle("-fx-text-fill: black;");
        actionDescLabel.setFont(font3);

        actionDescLabel.setWrapText(true);

        VBox textBox = new VBox(6, actionNameLabel, actionDescLabel);
        textBox.setPadding(new Insets(0, 20, 0, 40));
        textBox.setAlignment(Pos.CENTER_LEFT);

        ImageView bg = new ImageView(new Image(
                BattleScene.class.getResourceAsStream(DESC_PANEL_BG_PATH)
        ));

        double panelW = 420;
        double panelH = 110;

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

    /**
     * Updates the skill description panel based on:
     * the currently active hero and {@link #selectedSkillType}.
     *
     * <p>If hero data is missing from the skill info map, a fallback message is shown.
     */
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

        if (info.name(selectedSkillType) == "Arrow Stock" && currentTurnHero instanceof Archer) {
            actionDescLabel.setText(info.desc(selectedSkillType) + " (now : " + ((Archer) currentTurnHero).getBowStack() + ")");
        } else {
            actionDescLabel.setText(info.desc(selectedSkillType));
        }
    }

    /**
     * Builds a top-center panel that displays the stage counter.
     *
     * @param gameEngine game engine providing the stage counter value
     * @return stage counter panel node
     */
    private static StackPane buildStageCounterPanel(GameEngine gameEngine) {

        ImageView bg = new ImageView(new Image(
                BattleScene.class.getResourceAsStream(SKILL_PANEL_BG_PATH)
        ));

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

        StackPane.setAlignment(stageCounterLabel, Pos.CENTER);

        updateStageCounter(gameEngine);
        return panel;
    }

    /**
     * Updates the top-center stage counter text from the game engine.
     *
     * @param gameEngine game engine used as the data source
     */
    private static void updateStageCounter(GameEngine gameEngine) {
        if (stageCounterLabel == null || gameEngine == null) return;
        stageCounterLabel.setText("STAGE  " + gameEngine.getStageCounter());
    }

    /**
     * Called when a hero turn starts to update HUD state and default description selection.
     *
     * @param hero active hero starting the turn
     */
    private static void onHeroTurnStartedForHud(Heroes hero) {
        currentTurnHero = hero;
        selectedSkillType = SkillType.NORMAL_ATTACK;
        updateSkillDescription();

        clearSkillButtonEffects();
        normalBtn.setEffect(BTN_GLOW);
    }

    /**
     * Clears glow effects from skill buttons.
     */
    private static void clearSkillButtonEffects() {
        if (normalBtn != null) normalBtn.setEffect(null);
        if (skillBtn != null)  skillBtn.setEffect(null);
        if (ultBtn != null)    ultBtn.setEffect(null);
    }

    /**
     * Represents a visual HP bar component for a unit.
     *
     * <p>This class supports instant updates and animated transitions with
     * a damage lag effect.
     */
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

        /**
         * Creates an HP bar UI for a unit.
         *
         * @param maxHp max HP used for percentage calculation
         * @param currentHp current HP used for initial rendering
         */
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

        /**
         * Builds the JavaFX node representing this HP bar, including the text overlay.
         *
         * @return node for embedding into unit cards
         */
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

        /**
         * Sets the HP bar immediately without animation.
         *
         * @param hp new HP value
         */
        void setInstant(double hp) {
            currentHp = hp;
            double ratio = clamp01(hp / maxHp);

            fill.setWidth(barW * ratio);
            dmg.setWidth(barW * ratio);

            text.setText((int) hp + "/" + (int) maxHp);
            updateColor(ratio);
        }

        /**
         * Animates the HP bar to a new HP value.
         *
         * <p>If HP decreases, a delayed red damage bar effect is shown.
         * If HP increases, the bar updates immediately.
         *
         * @param newHp the new HP value
         */
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

        /**
         * Updates the fill color based on current HP percentage.
         *
         * @param ratio hp ratio in range 0 to 1
         */
        private void updateColor(double ratio) {
            if (ratio > 0.6) fill.setFill(Color.web("#35d04f"));
            else if (ratio > 0.3) fill.setFill(Color.web("#f2c94c"));
            else fill.setFill(Color.web("#ff4d4d"));
        }

        /**
         * Clamps a double to the range 0 to 1.
         *
         * @param v input value
         * @return clamped value
         */
        private static double clamp01(double v) {
            return Math.max(0, Math.min(1, v));
        }
    }

    /**
     * Plays a short hit animation (shake) on a unit card.
     *
     * @param card unit card UI node
     */
    private static void playHitAnimation(Region card) {
        TranslateTransition shake = new TranslateTransition(Duration.millis(60), card);
        shake.setByX(10);
        shake.setCycleCount(4);
        shake.setAutoReverse(true);
        shake.play();
    }

    /**
     * Attempts to load an image from a resource path. If loading fails, the fallback path is tried.
     *
     * @param path primary resource path
     * @param fallbackPath fallback resource path used if primary fails
     * @return loaded image, or null if both paths fail
     */
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

    /**
     * Returns the idle sprite image for a hero.
     *
     * @param h hero reference
     * @return idle image or fallback image, or null if resources missing
     */
    private static Image heroIdleImg(Heroes h) {
        String name = h.getName();
        return loadImageSafe(
                "/Heroes/" + name + "/" + name + "Still.PNG",
                "/Heroes/" + name + "/" + name + "Still.PNG"
        );
    }

    /**
     * Returns the attack sprite image for a hero.
     *
     * @param h hero reference
     * @return attack image or fallback image
     */
    private static Image heroAttackImg(Heroes h) {
        String name = h.getName();
        return loadImageSafe(
                "/Heroes/" + name + "/" + name + "Attack.PNG",
                "/Heroes/" + name + "/" + name + "Still.PNG"
        );
    }

    /**
     * Returns the shield sprite image for a hero.
     *
     * @param h hero reference
     * @return shield image or fallback image
     */
    private static Image heroShieldImg(Heroes h) {
        String name = h.getName();
        return loadImageSafe(
                "/Heroes/" + name + "/" + name + "Shield.PNG",
                "/Heroes/" + name + "/" + name + "Still.PNG"
        );
    }

    /**
     * Returns the dead sprite image for a hero.
     *
     * @param h hero reference
     * @return dead image or fallback image
     */
    private static Image heroDeadImg(Heroes h) {
        String name = h.getName();
        return loadImageSafe(
                "/Heroes/" + name + "/" + name + "IsDead.PNG",
                "/Heroes/" + name + "/" + name + "Still.PNG"
        );
    }

    /**
     * Returns the resolved monster type folder name for a monster.
     *
     * <p>This is used to ensure the same resource folder is used consistently
     * for idle, attack, and dead sprites.
     *
     * @param m monster reference
     * @return type folder name
     */
    private static String monsterTypeName(Monster m) {
        return monsterTypeMap.getOrDefault(m, m.getName());
    }

    /**
     * Returns the idle sprite image for a monster.
     *
     * @param m monster reference
     * @return idle image or fallback image
     */
    private static Image monsterIdleImg(Monster m) {
        String type = monsterTypeName(m);
        String e = m.getElement().toString();
        return loadImageSafe(
                "/Monster/" + type + "/" + e + "/Norm.png",
                "/Monster/" + type + "/" + e + "/Norm.png"
        );
    }

    /**
     * Returns the dead sprite image for a monster.
     *
     * @param m monster reference
     * @return dead image or fallback image
     */
    private static Image monsterDeadImg(Monster m) {
        String type = monsterTypeName(m);
        String e = m.getElement().toString();
        return loadImageSafe(
                "/Monster/" + type + "/" + e + "/Dead.png",
                "/Monster/" + type + "/" + e + "/Norm.png"
        );
    }

    /**
     * Returns the attack sprite image for a monster.
     *
     * @param m monster reference
     * @return attack image or fallback image
     */
    private static Image monsterAttackImg(Monster m) {
        String type = monsterTypeName(m);
        String e = m.getElement().toString();
        return loadImageSafe(
                "/Monster/" + type + "/" + e + "/Attack.png",
                "/Monster/" + type + "/" + e + "/Norm.png"
        );
    }

    /**
     * Returns the base hero image appropriate for current hero state:
     * dead, shielded, or idle.
     *
     * @param h hero reference
     * @return base image or null if resources missing
     */
    private static Image heroBaseImg(Heroes h) {
        if (h == null) return null;
        if (h.isDead()) return heroDeadImg(h);
        if (h.getShield() > 0) return heroShieldImg(h);
        return heroIdleImg(h);
    }

    /**
     * Returns the base monster image appropriate for current monster state:
     * dead or idle.
     *
     * @param m monster reference
     * @return base image or null if resources missing
     */
    private static Image monsterBaseImg(Monster m) {
        return m.isDead() ? monsterDeadImg(m) : monsterIdleImg(m);
    }

    /**
     * Temporarily shows the hero attack sprite for about 1 second and then reverts to base image.
     *
     * @param h hero reference
     */
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

    /**
     * Temporarily shows the monster attack sprite for about 1 second and then reverts to base image.
     *
     * @param m monster reference
     */
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

    /**
     * Initializes the top-corner element UI panels and places them in the root anchor pane.
     *
     * @param root root container for the scene
     */
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

    /**
     * Rebuilds element icon overlays for both heroes and monsters in the top corners.
     *
     * <p>This clears all existing icon nodes and creates fresh overlays based on the current model.
     */
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

    /**
     * Creates a top-left overlay icon for a hero showing:
     * element icon and hero icon.
     *
     * @param hero hero reference
     * @return overlay node representing the hero element display
     */
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

    /**
     * Creates a top-right overlay icon for a monster showing:
     * element icon and monster sprite icon.
     *
     * @param m monster reference
     * @return overlay node representing the monster element display
     */
    private static StackPane buildMonsterElementOverlay(Monster m) {

        Image elementImg = RandomElementGenerator.getElementImage(m.getElement());
        ImageView elementView = new ImageView(elementImg);
        elementView.setFitWidth(60);
        elementView.setFitHeight(60);
        elementView.setPreserveRatio(true);

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

    /**
     * Refreshes only the element icon images in the top corners based on current model elements.
     */
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

    /**
     * Returns true if the stage is a hero decision stage where player input is expected.
     *
     * @param s stage to check
     * @return true if stage is HERO_CHOOSE_SKILL, HERO_CHOOSE_TARGET, or HERO_CHOOSE_ALLY
     */
    private static boolean isHeroDecisionStage(BattleStage s) {
        return s == BattleStage.HERO_CHOOSE_SKILL
                || s == BattleStage.HERO_CHOOSE_TARGET
                || s == BattleStage.HERO_CHOOSE_ALLY;
    }

    /**
     * Safely gets the active hero from the model using the engine index.
     *
     * @return active hero or null if index invalid
     */
    private static Heroes safeGetActiveHero() {
        int idx = engine.getModel().getActiveHeroIndex();
        if (idx < 0 || idx >= model.getHERO_TEAM().size()) return null;
        return model.getHERO_TEAM().get(idx);
    }

    /**
     * Handles attack image transitions when a turn ends.
     *
     * <p>This method uses stage transitions to decide when to show the previous hero's
     * attack sprite briefly.
     *
     * @param currentStage the current battle stage
     */
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

        boolean heroPhaseToHeroPhase = isHeroDecisionStage(lastStage) && isHeroDecisionStage(currentStage);
        if (heroPhaseToHeroPhase && currentActive != null && lastActiveHero != null && currentActive != lastActiveHero) {
            showAttackFor1_5s(lastActiveHero);
        }

        boolean enteringMonsterTurn = currentStage == BattleStage.MONSTER_TURN && lastStage != BattleStage.MONSTER_TURN;
        if (enteringMonsterTurn) {
            showAttackFor1_5s(heroBeforeMonsterTurn);
        }

        lastStage = currentStage;
        lastActiveHero = currentActive;
    }

    /**
     * Initializes and displays the battle scene.
     *
     * <p>This method creates the {@link BattleModel} and {@link BattleEngine},
     * constructs UI nodes, attaches event handlers, registers a {@link BattleListener},
     * builds unit cards, and starts the battle.
     *
     * @param stage the primary JavaFX stage used to render the scene
     * @param gameEngine the main game engine containing stage counter and team data
     */
    public static void show(Stage stage, GameEngine gameEngine) {

        model = new BattleModel(GameEngine.getHeroTEAM(), GameEngine.getMonsterTeam());
        engine = new BattleEngine(model);
        GAMEENGINE = gameEngine;
        STAGE = stage;
        MusicPlayer.playMusic(GameState.BATTLE);

        AnchorPane root = new AnchorPane();
        root.setPadding(new Insets(8));

        stageCounterPanelTopCenter = buildStageCounterPanel(gameEngine);

        AnchorPane.setTopAnchor(stageCounterPanelTopCenter, 10.0);
        AnchorPane.setLeftAnchor(stageCounterPanelTopCenter, 540.0);

        root.getChildren().add(stageCounterPanelTopCenter);

        Random rand = new Random();
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

        stageLabel = new Label("Stage: " + engine.getBattleStage());
        stageLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        logLabel = new Label("Battle Start! Choose Hero skill");
        logLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

        normalBtn = createImageButton(NORMAL_BTN_IMG_PATH, 150, 70);
        skillBtn  = createImageButton(SKILL_BTN_IMG_PATH, 150, 70);
        ultBtn    = createImageButton(ULT_BTN_IMG_PATH, 150, 70);
        cancelBtn = createImageButton(CANCEL_BTN_IMG_PATH, 70, 70);

        skillPanel = buildSkillPanel();
        descPanel  = buildSkillDescriptionPanel();

        VBox centerInfo = new VBox(6, stageLabel, logLabel);
        centerInfo.setAlignment(Pos.BOTTOM_CENTER);

        BorderPane hud = new BorderPane();
        hud.setLeft(skillPanel);
        hud.setRight(descPanel);
        hud.setPadding(new Insets(10));
        hud.setPickOnBounds(false);

        AnchorPane.setLeftAnchor(hud, 10.0);
        AnchorPane.setRightAnchor(hud, 10.0);
        AnchorPane.setBottomAnchor(hud, 0.0);

        root.getChildren().addAll(heroBox, monsterBox, hud);

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

        skillPanel.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_EXITED, e -> {
            SkillType pending = (model != null) ? model.getPendingSkill() : null;
            selectedSkillType = (pending != null) ? pending : SkillType.NORMAL_ATTACK;
            updateSkillDescription();

            clearSkillButtonEffects();
            if (selectedSkillType == SkillType.NORMAL_ATTACK) normalBtn.setEffect(BTN_GLOW);
            else if (selectedSkillType == SkillType.SKILL) skillBtn.setEffect(BTN_GLOW);
            else ultBtn.setEffect(BTN_GLOW);
        });

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

    /**
     * Builds all unit cards for heroes and monsters one time.
     *
     * <p>This clears existing UI maps and containers before creating new card nodes.
     * Cards also register hover and click handlers based on the active battle stage.
     */
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

        monsterTypeMap.clear();

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

            String typeName = monsters.get(i).getName();
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

    /**
     * Updates unit HP bars and card visuals without rebuilding cards.
     *
     * <p>This method:
     * <ul>
     *     <li>Animates HP bar changes</li>
     *     <li>Plays hit animation when damage is taken</li>
     *     <li>Updates opacity when a unit dies</li>
     *     <li>Resets sprite to base image if no temporary animation is active</li>
     * </ul>
     */
    private static void updateUnitsAnimated() {

        for (Heroes h : model.getHERO_TEAM()) {
            HpBarUI ui = heroHpMap.get(h);
            Region card = heroCardMap.get(h);
            if (ui == null || card == null) continue;

            double oldHp = ui.currentHp;
            double newHp = h.getHp();

            if (newHp < oldHp) {
                Platform.runLater(() -> playHitAnimation(card));
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
                Platform.runLater(() -> playHitAnimation(card));
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

    /**
     * Builds a hero card containing an HP bar and the hero sprite image.
     *
     * @param hp initial hp
     * @param dead whether the hero is dead at creation time
     * @param heroRef hero reference for maps and sprite lookup
     * @return hero card node
     */
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

    /**
     * Builds a monster card containing an HP bar and the monster sprite image.
     *
     * @param typeName monster folder type name used for resource lookup
     * @param hp initial hp
     * @param dead whether the monster is dead at creation time
     * @param e monster element used for resource lookup
     * @param monsterRef monster reference for maps and sprite lookup
     * @return monster card node
     */
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

    /**
     * Adds hover glow behavior to a node while a condition is true.
     *
     * <p>This is used to indicate selectable targets during target selection stages.
     *
     * @param node node to apply hover effect to
     * @param canHover condition that must be true to show hover glow
     */
    private static void setupHoverGlow(Region node, BooleanSupplier canHover) {
        node.setOnMouseEntered(e -> {
            if (canHover.getAsBoolean() && node.getOpacity() > 0.5) node.setEffect(HOVER_GLOW);
        });

        node.setOnMouseExited(e -> {
            node.setEffect(null);
            refreshTurnGlow();
        });
    }

    /**
     * Refreshes the enabled and disabled state of action buttons based on current stage and cooldowns.
     */
    private static void refreshButtons() {
        Heroes active = safeGetActiveHero();
        if (active == null) return;

        normalBtn.setDisable(false);

        if (engine.getBattleStage() == BattleStage.HERO_CHOOSE_SKILL) {
            skillBtn.setDisable(!active.canUseSkill());
            ultBtn.setDisable(!active.canUseUlt());
        }
    }

    /**
     * Refreshes the glow highlight for the currently active hero during hero decision stages.
     */
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

    /**
     * Updates UI interactivity and triggers monster animations based on the battle stage.
     *
     * <p>This method controls which UI inputs are enabled and starts the monster attack sequence
     * during {@link BattleStage#MONSTER_TURN}.
     *
     * @param stage current battle stage
     */
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

    /**
     * Plays a single monster attack sequence:
     * set attack sprite, wait until hit timing, apply damage, update UI, then continue.
     *
     * @param m monster performing the attack
     * @param applyDamageThenUpdateUi callback to apply damage and update UI
     * @param afterDone callback to continue to the next monster
     */
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

        showMonsterAttackFor1s(m);

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

    /**
     * Plays attack animations for all monsters sequentially.
     *
     * <p>This ensures monsters attack one at a time rather than simultaneously.
     *
     * @param monsters list of monsters
     * @param idx current monster index
     * @param onAllDone callback when all monsters finish attacking
     * @param applyDamageForMonster logic to apply damage for each monster
     */
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

    /**
     * Enables or disables all hero-related input controls and unit boxes.
     *
     * @param enabled true to enable input, false to disable input
     */
    private static void setHeroInputEnabled(boolean enabled) {
        normalBtn.setDisable(!enabled);
        skillBtn.setDisable(!enabled);
        ultBtn.setDisable(!enabled);
        cancelBtn.setDisable(!enabled);

        heroBox.setDisable(!enabled);
        monsterBox.setDisable(!enabled);
    }

    /**
     * Returns the current game engine reference for this battle scene.
     *
     * @return game engine reference
     */
    private static GameEngine getGameEngine() {
        return GAMEENGINE;
    }

    /**
     * Returns the current stage reference for this battle scene.
     *
     * @return JavaFX stage hosting the battle scene
     */
    private static Stage getStage() {
        return STAGE;
    }
}