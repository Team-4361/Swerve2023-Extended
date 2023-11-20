package frc.robot.util.log;

import frc.robot.util.loop.Looper;

import java.util.ArrayList;
import java.util.List;

/**
 * This {@link Alert} class represents a notification which can be triggered based on specific conditions.
 *
 * @author Eric Gold
 * @since 0.0.0
 */
public class Alert extends Looper {
    private final AlertType type;
    private final String desc;
    private final ArrayList<AlertCondition> conditions;
    private boolean autoRemove;
    private boolean thrownPrev;

    private long enableMillis = -1;

    /**
     * Constructs an Alert with the specified desc, {@link AlertType}, and
     * {@link AlertCondition}s.
     *
     * @param desc       The Description of the alert.
     * @param type       The {@link AlertType} of the alert.
     * @param conditions The {@link AlertCondition}s to be added to the alert.
     */
    public Alert(String desc, AlertType type, AlertCondition... conditions) {
        super(desc + " Looper"); // init a looper to add actions
        this.desc = desc;
        this.type = type;
        this.thrownPrev = false;
        this.conditions = new ArrayList<>();

        if (conditions != null && conditions.length > 0)
            this.conditions.addAll(List.of(conditions));
    }

    /**
     * Constructs an Alert with the specified desc and {@link AlertType}
     *
     * @param desc The Description of the alert.
     * @param type The {@link AlertType} of the alert.
     */
    public Alert(String desc, AlertType type) {
        this(desc, type, (AlertCondition)null);
    }

    /** The {@link AlertType} of the {@link Alert}. */
    public AlertType getAlertType() { return this.type; }

    /** @return The Description of the {@link Alert}. */
    public String getAlertDescription() {
        return this.desc;
    }

    /** @return The Enabled Status of the {@link Alert}. */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isEnabled() {
        boolean isThrown = conditions.stream().anyMatch(AlertCondition::shouldThrow);
        if (!isThrown) {
            enableMillis = -1;
            stop(); // alert is disabled!
        } else if (enableMillis <= 0) {
            // This is the first time throwing; set the millis.
            thrownPrev = true;
            enableMillis = System.currentTimeMillis();
            start(); // alert is enabled!
        }
        return isThrown;
    }

    /**
     * Indicates the {@link AlertManager} if this {@link Alert} should be removed and destroyed upon
     * becoming disabled AFTER THROWING. Use this functionality if your code contains
     * a loop to add Alerts when necessary.
     *
     * @param autoRemove The auto-remove status.
     * @return           The current {@link Alert} instance.
     */
    public Alert setAutoRemove(boolean autoRemove) {
        this.autoRemove = autoRemove;
        return this;
    }

    /** @return If the {@link AlertManager} should auto-remove this {@link Alert} after disabling. */
    public boolean getAutoRemove() { return this.autoRemove; }

    /**
     * @return If the {@link Alert} should be removed from the {@link AlertManager}. Based upon the
     * {@link #setAutoRemove(boolean)} method.
     */
    public boolean shouldRemove() {
        return !isEnabled() && thrownPrev && autoRemove;
    }

    /** @return The {@link System#currentTimeMillis()} of the enable time. <b>-1 if disabled.</b>*/
    public long getEnableMillis() { return this.enableMillis; }

    /**
     * Adds an {@link AlertCondition} to the {@link Alert}
     * @param condition The {@link AlertCondition} to add.
     * @return          The current {@link Alert} instance.
     */
    public Alert onCondition(AlertCondition condition) {
        conditions.add(condition);
        return this;
    }

    /**
     * Removes a {@link AlertCondition} from the {@link Alert}
     * @param condition The {@link AlertCondition} to remove.
     * @return          The current {@link Alert} instance.
     */
    public Alert removeCondition(AlertCondition condition) {
        conditions.remove(condition);
        return this;
    }

    /**
     * Clears all {@link AlertCondition}s from the {@link Alert}.
     * @return The current {@link Alert} instance.
     */
    public Alert clearAll() {
        conditions.clear();
        return this;
    }

    /** @return All registered {@link AlertCondition} instances. */
    public ArrayList<AlertCondition> getConditions() { return this.conditions; }
}