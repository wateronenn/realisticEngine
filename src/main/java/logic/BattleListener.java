package logic;

/**
 * Listener interface used by {@link BattleEngine} to communicate with the UI layer.
 *
 * <p>This interface allows the engine to notify external systems (typically the GUI)
 * about important battle events such as stage changes, log messages, and model updates.
 *
 * <p>Typical usage:
 * <ul>
 *     <li>The UI implements this interface</li>
 *     <li>The UI registers itself using {@code BattleEngine.setListener()}</li>
 *     <li>The engine calls these methods whenever the battle state changes</li>
 * </ul>
 */
public interface BattleListener {

    /**
     * Called when the battle stage changes.
     *
     * <p>This allows the UI to react to transitions such as:
     * <ul>
     *     <li>Hero selecting a skill</li>
     *     <li>Choosing a target</li>
     *     <li>Monster turn starting</li>
     *     <li>Victory or defeat</li>
     * </ul>
     *
     * @param stage the new {@link BattleStage} representing the current phase of battle
     */
    void onStateChanged(BattleStage stage);

    /**
     * Called when the engine emits a log message.
     *
     * <p>This is typically used to display messages in the UI battle log,
     * such as skill usage, cooldown warnings, or combat events.
     *
     * @param message text message describing the event
     */
    void onLog(String message);

    /**
     * Called when the underlying battle model has been updated.
     *
     * <p>The UI should refresh displayed information such as:
     * <ul>
     *     <li>Hero HP</li>
     *     <li>Monster HP</li>
     *     <li>Shields or buffs</li>
     *     <li>Cooldown indicators</li>
     * </ul>
     */
    void onModelUpdated();
}