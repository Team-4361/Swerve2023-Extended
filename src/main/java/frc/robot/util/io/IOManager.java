package frc.robot.util.io;

import com.revrobotics.REVLibError;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Robot;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This {@link IOManager} class is designed to control ALL {@link Looper} and {@link ConditionalAlert} instances. Since
 * the original two managers were lightweight and operated in a similar fashion, the decision was made to combine them.
 *
 * @author Eric Gold
 * @since 0.0.1
 */
public class IOManager {
    private static final List<Looper> LOOPS = new ArrayList<>();
    private static final List<ConditionalAlert> CONDITIONAL_ALERTS = new ArrayList<>();
    private static final AlertType[] ALERT_ORDER = new AlertType[] { AlertType.ERROR, AlertType.WARNING };
    private static final RuntimeMXBean MX_BEAN = ManagementFactory.getRuntimeMXBean();
    private static final HashMap<String, ArrayList<String>> SIMPLE_ALERTS = new HashMap<>();

    private static long lastLoopUpdate = System.currentTimeMillis();
    private static long lastAlertUpdate = System.currentTimeMillis();

    static {
        for (AlertType type : ALERT_ORDER) {
            SIMPLE_ALERTS.put(type.getPrefix(), new ArrayList<>());
        }
    }

    /**
     * Throws an on-demand <code>simple Alert</code> to the Dashboard.
     *
     * @param type The {@link AlertType} to use.
     * @param name The name to display.
     * @return     Successful if the Alert is un-displayed; false otherwise.
     */
    public static boolean throwAlert(AlertType type, String name) {
        ArrayList<String> list = SIMPLE_ALERTS.get(type.getPrefix());
        if (list.contains(name))
            return false;
        return list.add(name);
    }

    public static void throwAlert(AlertType type, String name, Supplier<Boolean> condition, boolean persist) {
        if (condition.get()) {
            throwAlert(type, name);
        } else if (!persist) {
            hideAlert(type, name);
        }
    }

    public static void throwAlert(AlertType type, String name, Supplier<Boolean> condition) {
        throwAlert(type, name, condition, false);
    }

    /**
     * Removes an on-demand <code>simple Alert</code> from the Dashboard.
     *
     * @param type The {@link AlertType} to hide.
     * @param name The name to hide.
     * @return     Successfully if the Alert is found and displayed; false otherwise.
     */
    public static boolean hideAlert(AlertType type, String name) {
        ArrayList<String> list = SIMPLE_ALERTS.get(type.getPrefix());
        if (!list.contains(name))
            return false;
        return list.remove(name);
    }

    public static List<Looper> getLoops() { return LOOPS; }

    public static List<ConditionalAlert> getAutoAlerts() { return CONDITIONAL_ALERTS; }

    public static Looper getLoop(String loopName) {
        Looper loop = LOOPS.stream()
                .filter(o -> o.getName().equals(loopName))
                .findFirst()
                .orElseGet(() -> new Looper(loopName));
        if (!LOOPS.contains(loop))
            LOOPS.add(loop);
        return loop;
    }

    public static ConditionalAlert getConditionalAlert(String alertName, AlertType type) {
        ConditionalAlert alert = CONDITIONAL_ALERTS.stream()
                .filter(o -> o.getName().equals(alertName) && o.getType() == type)
                .findFirst()
                .orElseGet(() -> new ConditionalAlert(alertName, type));
        if (!CONDITIONAL_ALERTS.contains(alert))
            CONDITIONAL_ALERTS.add(alert);
        return alert;
    }

    /**
     * Generates the log header for a log message.
     *
     * @param sender The originating {@link Object}.
     * @param type   The type of log message.
     * @return The log header.
     */
    private static String getLogHeader(Object sender, String type) {
        String name = " ";
        try {
            if (sender != null)
                name = " " + sender.getClass().getName() + " ";
        } catch (Exception ignored) {
        }
        return "[" + type + name + formatTime(MX_BEAN.getUptime()) + "] ";
    }

    /**
     * Broadcasts a DEBUG message to the RIOLOG.
     *
     * @param sender The originating {@link Object}.
     * @param text   The {@link String} to broadcast.
     */
    public static void debug(Object sender, String text) {
        if (Robot.verbosity != VerbosityLevel.DEBUG)
            return;
        System.out.println(getLogHeader(sender, "DEBUG") + text);
    }

    /**
     * Broadcasts a WARN message to the RIOLOG
     * @param sender The originating {@link Object}
     * @param text The {@link String} to broadcast.
     */
    public static void warn(Object sender, String text) {
        DriverStation.reportWarning(getLogHeader(sender, "WARN") + text, false);
    }

    /**
     * Broadcasts an ERROR message to the RIOLOG
     *
     * @param sender The originating {@link Object}
     * @param text The {@link String} to broadcast.
     */
    public static void error(Object sender, String text) {
        DriverStation.reportError(getLogHeader(sender, "ERROR") + text, false);
    }

    /**
     * Broadcasts an INFO message to the RIOLOG.
     *
     * @param sender The originating {@link Object}.
     * @param text   The {@link String} to broadcast.
     */
    public static void info(Object sender, String text) {
        System.out.println(getLogHeader(sender, "INFO") + text);
    }

    /**
     * Broadcasts a DEBUG message to the RIOLOG with a null sender.
     * @param text The {@link String} to broadcast.
     */
    public static void debug(String text) { debug(null, text); }

    /**
     * Broadcasts an INFO message to the RIOLOG with a null sender.
     * @param text The {@link String} to broadcast.
     */
    public static void info(String text) { info(null, text); }


    /**
     * Formats the uptime as a string.
     *
     * @param uptime The uptime in milliseconds.
     * @return The formatted uptime string.
     */
    private static String formatTime(long uptime) {
        long totalSeconds = uptime / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        long millis = uptime % 1000;

        return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, millis);
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

    public static void warn(String text) { warn(null, text); }
    public static void error(String text) { error(null, text); }

    private static final Function<AlertType, String[]> getEnabledAlerts = type -> {
        ArrayList<String> output = new ArrayList<>();

        // First, check for any conditional alerts which may be thrown.
        if (!CONDITIONAL_ALERTS.isEmpty()) {
            Iterator<ConditionalAlert> it = CONDITIONAL_ALERTS.iterator();
            while (it.hasNext()) {
                ConditionalAlert a = it.next();
                if (a.shouldRemove()) {
                    it.remove();
                    continue;
                }
                if (!a.isEnabled() || a.getType() != type)
                    continue;
                output.add(a.getName());
            }
        }

        // Next, check for simple alerts which are always thrown unless disabled.
        try {
            output.addAll(SIMPLE_ALERTS.get(type.getPrefix()));
        } catch (Exception ex) {
            warn("Simple Alert threw Exception; attempting creation...");
            output.add(type.getPrefix());
        }

        return output.toArray(String[]::new);
    };

    public static synchronized void run() {
        if (System.currentTimeMillis() - lastLoopUpdate < 20)
            return;

        Iterator<Looper> it = LOOPS.iterator();
        while (it.hasNext()) {
            Looper looper = it.next();
            if (!looper.isFinished() && !looper.isRunning()) {
                looper.start();
                debug("Starting loop!");
                continue;
            }
            if (looper.isFinished()) {
                debug("Removing loop!");
                it.remove();
                continue;
            }
            looper.run();
        }

        lastLoopUpdate = System.currentTimeMillis();

        if (System.currentTimeMillis() - lastAlertUpdate <= (DriverStation.isEnabled() ? 1000 : 1000))
            return;

        debug("Updating alerts!");

        Sendable alertData = builder -> {
            builder.setSmartDashboardType("Alerts");
            for (AlertType type : ALERT_ORDER) {
                builder.addStringArrayProperty(type.getPrefix(), () -> getEnabledAlerts.apply(type), null);
            }
        };

        SmartDashboard.putData("Alerts", alertData);
        lastAlertUpdate = System.currentTimeMillis();
    }
}
