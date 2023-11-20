package frc.robot.util.log;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * This {@link AlertCondition} class represents an Alert Condition.
 * <p></p>
 * The conditions include an enable supplier, a disable supplier (optional),
 * a disable delay, and an auto-disable flag. The class provides methods to set
 * and retrieve these conditions, as well as a method to check whether the alert
 * should be thrown based on the configured conditions.
 *
 * @author Eric Gold
 * @since 0.0.0
 */
public class AlertCondition {
    private Supplier<Boolean> enableSupplier;
    private Supplier<Boolean> disableSupplier;
    private Duration disableDelay;
    private Duration enableDelay;
    private boolean autoDisable;
    private boolean persistTrue;
    private long disabledTime = -1;
    private long enabledTime = -1;

    /**
     * Gets the enable condition supplier.
     *
     * @return The enable condition supplier.
     */
    public Supplier<Boolean> getEnableCondition() {
        return this.enableSupplier;
    }

    /**
     * Gets the disable condition supplier, if present.
     *
     * @return Optional containing the disable condition supplier, or empty if not present.
     */
    public Optional<Supplier<Boolean>> getDisableCondition() {
        return Optional.ofNullable(this.disableSupplier);
    }

    /**
     * Gets the disable delay.
     *
     * @return The disable delay.
     */
    public Duration getDisableDelay() {
        return this.disableDelay;
    }

    /**
     * Gets the enable delay.
     *
     * @return The enable delay.
     */
    public Duration getEnableDelay() {
        return this.enableDelay;
    }

    /**
     * Checks if auto-disable is enabled.
     *
     * @return True if auto-disable is enabled, false otherwise.
     */
    public boolean isAutoDisable() {
        return this.autoDisable;
    }

    /**
     * Sets the enable condition supplier.
     *
     * @param supplier The enable condition supplier.
     * @return The current AlertCondition instance for method chaining.
     */
    public AlertCondition setEnableCondition(Supplier<Boolean> supplier) {
        this.enableSupplier = supplier;
        return this;
    }

    /**
     * Sets the disable condition supplier.
     *
     * @param supplier The disable condition supplier.
     * @return The current AlertCondition instance for method chaining.
     */
    public AlertCondition setDisableCondition(Supplier<Boolean> supplier) {
        this.disableSupplier = supplier;
        return this;
    }

    /**
     * Sets the disable delay.
     *
     * @param delay The disable delay.
     * @return The current AlertCondition instance for method chaining.
     */
    public AlertCondition setDisableDelay(Duration delay) {
        this.disableDelay = delay;
        return this;
    }

    /**
     * Sets the enable delay.
     *
     * @param delay The enable delay.
     * @return The current AlertCondition instance for method chaining.
     */
    public AlertCondition setEnableDelay(Duration delay) {
        this.enableDelay = delay;
        return this;
    }

    /**
     * Sets the auto-disable flag.
     *
     * @param autoDisable True to enable auto-disable, false otherwise.
     * @return The current AlertCondition instance for method chaining.
     */
    public AlertCondition setAutoDisable(boolean autoDisable) {
        this.autoDisable = autoDisable;
        return this;
    }

    /**
     * Constructs an AlertCondition with the specified enable supplier,
     * disable supplier, and auto-disable flag.
     *
     * @param alertEnable  The enable supplier.
     * @param alertDisable The disable supplier (optional).
     * @param autoDisable  True to enable auto-disable, false otherwise.
     */
    public AlertCondition(Supplier<Boolean> alertEnable, Supplier<Boolean> alertDisable, boolean autoDisable) {
        this.disableSupplier = alertDisable;
        this.enableSupplier = alertEnable;
        this.autoDisable = autoDisable;
        this.disableDelay = Duration.ZERO;
    }

    /**
     * Constructs an AlertCondition with the specified enable supplier.
     * @param alertEnable The enable supplier.
     */
    public AlertCondition(Supplier<Boolean> alertEnable) {
        this(alertEnable, null, true);
    }

    /**
     * Checks whether the alert should be thrown based on the configured conditions.
     *
     * @return True if the alert should be thrown, false otherwise.
     */
    public boolean shouldThrow() {
        boolean enableCondition = enableSupplier.get();
        if (enableCondition) {
            // Make sure the condition should be registered as enabled first.
            if (enabledTime <= 0) {
                enabledTime = System.currentTimeMillis() + enableDelay.toMillis();
            }
            enableCondition = System.currentTimeMillis() < enabledTime;
        } else {
            enabledTime = -1; // reset the enabled time.
        }

        if (!autoDisable) {
            if (enableCondition) {
                persistTrue = true;
            }
            return persistTrue;
        } else if (enableCondition) {
            disabledTime = -1; // reset the disabled time.
            return true;
        } else {
            if (disableSupplier != null && !disableSupplier.get())
                return true;
            // At this point, the condition is disabled. Check the disabled time and see if we can return false.
            if (disabledTime <= 0) {
                disabledTime = System.currentTimeMillis() + disableDelay.toMillis();
            }
            return System.currentTimeMillis() < disabledTime;
        }
    }
}