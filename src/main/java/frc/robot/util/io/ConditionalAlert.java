package frc.robot.util.io;

import java.util.ArrayList;

/**
 * This {@link ConditionalAlert} class represents a notification which can be triggered based
 * on specific conditions. Since a large {@link Object} quantity has the potential to clog the JVM/GC, a
 * <code>Simple Alert</code> can be used instead.
 *
 * @author Eric Gold
 * @since 0.0.1
 */
public class ConditionalAlert {
    private final AlertType type;
    private final String desc;
    private final ArrayList<AlertCondition> conditions;

    private boolean autoRemove, thrownPrev, manEnabled;
    private long enableMillis = -1;

    /**
     * Constructs an Alert with the specified desc, {@link AlertType}, and
     * {@link AlertCondition}s.
     *
     * @param desc The Description of the alert.
     * @param type The {@link AlertType} of the alert.
     */
    ConditionalAlert(String desc, AlertType type) {
        this.desc = desc;
        this.type = type;
        this.thrownPrev = false;
        this.manEnabled = false;
        this.conditions = new ArrayList<>();
    }

    /** The {@link AlertType} of the {@link ConditionalAlert}. */
    public AlertType getType() { return this.type; }

    /** @return The Description of the {@link ConditionalAlert}. */
    public String getName() {
        return this.desc;
    }

    /** @return The Enabled Status of the {@link ConditionalAlert}. */
    public boolean isEnabled() {
        boolean isThrown = manEnabled || conditions.stream().anyMatch(AlertCondition::shouldThrow);
        if (!isThrown)
            enableMillis = -1;
        else if (enableMillis <= 0) {
            // This is the first time throwing; set the millis.
            thrownPrev = true;
            enableMillis = System.currentTimeMillis();
        }
        return isThrown;
    }

    /**
     * Sets the Enabled Status of the {@link ConditionalAlert} <b>independent of any {@link AlertCondition} config</b>.
     * @param enabled The Enabled Status of the {@link ConditionalAlert}.
     */
    public void setEnabled(boolean enabled) {
        this.manEnabled = enabled;
    }

    /**
     * Indicates the {@link IOManager} if this {@link ConditionalAlert} should be removed and destroyed upon
     * becoming disabled AFTER THROWING. Use this functionality if your code contains
     * a loop to add Alerts when necessary.
     *
     * @param autoRemove The auto-remove status.
     * @return           The current {@link ConditionalAlert} instance.
     */
    public ConditionalAlert setAutoRemove(boolean autoRemove) {
        this.autoRemove = autoRemove;
        return this;
    }

    /** @return If the {@link IOManager} should auto-remove this {@link ConditionalAlert} after disabling. */
    public boolean getAutoRemove() { return this.autoRemove; }

    /**
     * @return If the {@link ConditionalAlert} should be removed from the {@link IOManager}. Based upon the
     * {@link #setAutoRemove(boolean)} method.
     */
    public boolean shouldRemove() {
        return !isEnabled() && thrownPrev && autoRemove;
    }

    /** @return The {@link System#currentTimeMillis()} of the enable time. <b>-1 if disabled.</b>*/
    public long getEnableMillis() { return this.enableMillis; }

    /**
     * Adds an {@link AlertCondition} to the {@link ConditionalAlert}
     * @param condition The {@link AlertCondition} to add.
     * @return          The current {@link ConditionalAlert} instance.
     */
    public ConditionalAlert addCondition(AlertCondition condition) {
        conditions.add(condition);
        return this;
    }

    /**
     * Removes a {@link AlertCondition} from the {@link ConditionalAlert}
     * @param condition The {@link AlertCondition} to remove.
     * @return          The current {@link ConditionalAlert} instance.
     */
    public ConditionalAlert removeCondition(AlertCondition condition) {
        conditions.remove(condition);
        return this;
    }

    /**
     * Clears all {@link AlertCondition}s from the {@link ConditionalAlert}.
     * @return The current {@link ConditionalAlert} instance.
     */
    public ConditionalAlert clearAll() {
        conditions.clear();
        return this;
    }

    /** @return All registered {@link AlertCondition} instances. */
    public ArrayList<AlertCondition> getConditions() { return this.conditions; }
}