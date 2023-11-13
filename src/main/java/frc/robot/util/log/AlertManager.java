package frc.robot.util.log;

import com.revrobotics.REVLibError;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID;
import frc.robot.Robot;
import frc.robot.util.loop.Looper;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.Duration;
import java.util.concurrent.ConcurrentLinkedQueue;

import static frc.robot.util.math.ExtendedMath.formatTime;

/**
 * This {@link AlertManager} class enables attention-grabbing functionality; this includes color-coded logs,
 * HID-vibration, and more.
 *
 * @author Eric Gold
 * @since 0.0.0
 */
public class AlertManager {
    /**
     * This {@link VibrationLooper} is designed to control {@link GenericHID} vibrations using special
     * {@link Looper} properties and additional features.
     *
     * @author Eric Gold
     * @since 0.0.0
     */
    private static class VibrationLooper extends Looper {
        private static final ConcurrentLinkedQueue<GenericHID> activeLoops = new ConcurrentLinkedQueue<>();
        private boolean isVibrating = false;

        /**
         * @param hid The {@link GenericHID} to test.
         * @return If the specified {@link GenericHID} is vibrating and not available.
         */
        public static boolean isVibrating(GenericHID hid) { return activeLoops.contains(hid); }

        /**
         * Constructs a new {@link VibrationLooper} with the {@link GenericHID} HID.
         * @param hid The {@link GenericHID} to use.
         * @throws InstantiationException If the specified {@link GenericHID} is vibrating and not available.
         */
        public VibrationLooper(GenericHID hid) throws InstantiationException {
            if (activeLoops.contains(hid))
                throw new InstantiationException("HID is already vibrating!");

            setInterval(Duration.ofMillis(500));
            setEndDelay(Duration.ofSeconds(1)); // prevents re-vibrating with-in this duration.
            setMaxCycles(2); // MUST be divisible by 2.

            addInit(() -> activeLoops.add(hid));
            addPeriodic(() -> {
                hid.setRumble(GenericHID.RumbleType.kLeftRumble, isVibrating?0:1);
                hid.setRumble(GenericHID.RumbleType.kRightRumble, isVibrating?0:1);
                isVibrating = !isVibrating;
                AlertManager.debug(this, "Called it!");
            });
            addOnFinished(() -> {
                hid.setRumble(GenericHID.RumbleType.kLeftRumble, 0);
                hid.setRumble(GenericHID.RumbleType.kRightRumble, 0);
                activeLoops.remove(hid);
            });
        }
    }

    public static RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();


    /**
     * Vibrates the {@link GenericHID} controller in a "message-like" configuration;
     * as in twice with 100ms interval. No repeat calls (same Controller) are allowed with-in 2 seconds.
     *
     * @param controller The {@link GenericHID} controller to vibrate.
     */
    public static void vibrate(GenericHID controller) {
        if (VibrationLooper.isVibrating(controller)) {
            AlertManager.debug("Controller is already vibrating; not constructing.");
            return;
        }
        try {
            new VibrationLooper(controller);
        } catch (InstantiationException ex) {
            AlertManager.warn("Failed to construct Controller Vibrator!");
        }
    }

    public AlertManager() throws InstantiationException {
        throw new InstantiationException("AlertManager is a utility class!");
    }

    private static String getLogHeader(Object sender, String type) {
        String name = " ";
        try {
            if (sender != null)
                name = " " + sender.getClass().getName() + " ";
        } catch (Exception ignored) {}
        return "[" + type + name + formatTime(rb.getUptime()) + " ]: ";
    }

    /**
     * Broadcasts a DEBUG message to the RIOLOG
     * @param sender The originating {@link Object}
     * @param text The {@link String} to broadcast.
     */
    public static void debug(Object sender, String text) {
        if (Robot.verbosity != VerbosityLevel.DEBUG)
            return;
        System.out.println(getLogHeader(sender, "DEBUG") + text);
    }

    /**
     * Broadcasts an INFO message to the RIOLOG
     * @param sender The originating {@link Object}
     * @param text The {@link String} to broadcast.
     */
    public static void info(Object sender, String text) {
        System.out.println(getLogHeader(sender, "INFO") + text);
    }

    /**
     * Broadcasts a WARN message to the RIOLOG
     * @param sender The originating {@link Object}
     * @param text The {@link String} to broadcast.
     */
    public static void warn(Object sender, String text) {
        DriverStation.reportWarning(getLogHeader(sender, "WARN") + text, false);
    }

    public static void error(GenericHID hid, Object sender, String text) {
        if (hid != null) {
            // Create a looper to manage this; expire after 10 seconds.

        }
    }

    /**
     * Broadcasts an ERROR message to the RIOLOG
     * @param sender The originating {@link Object}
     * @param text The {@link String} to broadcast.
     */
    public static void error(Object sender, String text) {
        DriverStation.reportError(getLogHeader(sender, "WARN") + text, false);
    }

    /**
     * Broadcasts an ERROR message to the RIOLOG if {@link REVLibError} is not OK.
     * @param sender The originating {@link Object}
     */
    public static void errorOnFail(Object sender, REVLibError error, String message) {
        if (error != REVLibError.kOk)
            if (message != null && !message.isEmpty())
                error(sender, message + " | " + error.name());
            else
                error(sender, error.name());
    }

    /**
     * Broadcasts an WARN message to the RIOLOG if {@link REVLibError} is not OK.
     * @param sender The originating {@link Object}
     */
    public static void warnOnFail(Object sender, REVLibError error, String message) {
        if (error != REVLibError.kOk)
            if (message != null && !message.isEmpty())
                warn(sender, message + " | " + error.name());
            else
                warn(sender, error.name());
    }

    public static void errorOnFail(REVLibError error, String message) { errorOnFail(null, error, message); }
    public static void warnOnFail(REVLibError error, String message) { warnOnFail(null, error, message); }
    public static void errorOnFail(REVLibError error) { errorOnFail(null, error, ""); }
    public static void warnOnFail(REVLibError error) { warnOnFail(null, error, ""); }

    public static void debug(String text) { debug(null, text); }
    public static void info(String text) { info(null, text); }
    public static void warn(String text) { warn(null, text); }
    public static void error(String text) { error(null, text); }
}
