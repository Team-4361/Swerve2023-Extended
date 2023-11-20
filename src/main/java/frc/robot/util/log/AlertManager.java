package frc.robot.util.log;

import com.revrobotics.REVLibError;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Robot;
import frc.robot.util.loop.LooperManager;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static frc.robot.Constants.LooperConfig.LOW_PRIORITY_NAME;

public class AlertManager {
    private static final RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
    private static final ArrayList<AlertManager> managers = new ArrayList<>();

    private final ArrayList<Alert> alerts;
    private final HashMap<String, String[]> cache;
    private final String groupName;

    //region static methods
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
        return "[" + type + name + formatTime(rb.getUptime()) + "] ";
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
     *
     * @param text The {@link String} to broadcast.
     */
    public static void debug(String text) { debug(null, text); }

    /**
     * Broadcasts an INFO message to the RIOLOG with a null sender.
     *
     * @param text The {@link String} to broadcast.
     */
    public static void info(String text) { info(null, text); }

    public static void logAlert(Alert alert) {
        if (!alert.isEnabled())
            return; // only log enabled alerts!
        switch (alert.getAlertType()) {
            case ERROR -> error(alert.getAlertDescription());
        }
    }

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
     * @param groupName The Group Name to use.
     * @return An {@link AlertManager} if <code>groupName</code> exists; null otherwise.
     */
    private static AlertManager getRawGroup(String groupName) {
        return managers.stream()
                .filter(manager -> manager.groupName.equals(groupName))
                .findFirst()
                .orElse(null);
    }

    /**
     * @param groupName The Group Name to use.
     * @return An {@link AlertManager} associated with the <code>groupName</code> parameter. An instance will
     * <b>automatically</b> be created if invalid.
     */
    public static AlertManager getGroup(String groupName) {
        if (getRawGroup(groupName) == null) {
            managers.add(new AlertManager(groupName));
        }
        return getRawGroup(groupName);
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    //endregion

    /**
     * This method is <b>NOT INTENDED</b> to be called by the user. Please use {@link #getGroup(String)} instead.
     */
    AlertManager(String groupName) {
        this.groupName = groupName;
        this.alerts = new ArrayList<>();
        this.cache = new HashMap<>();

        LooperManager.getLoop(LOW_PRIORITY_NAME).addPeriodic(this::update);
    }

    /**
     * Adds an {@link Alert} to the {@link AlertManager}
     * @param alert     The {@link Alert} to add.
     * @return          The current {@link AlertManager} instance.
     */
    public AlertManager addAlert(Alert alert) {
        alerts.add(alert);
        return this;
    }

    /**
     * Removes an {@link Alert} from the {@link AlertManager}
     * @param alert     The {@link Alert} to remove.
     * @return          The current {@link AlertManager} instance.
     */
    public AlertManager removeAlert(Alert alert) {
        alerts.remove(alert);
        return this;
    }

    /**
     * Clears all {@link Alert} instances from the {@link AlertManager}.
     * @return The current {@link AlertManager} instance.
     */
    public AlertManager clearAll() {
        alerts.clear();
        return this;
    }

    /** @return All registered {@link Alert} instances. */
    public ArrayList<Alert> getAlerts() { return this.alerts; }

    public String[] getEnabledAlerts(AlertType type) {
        Iterator<Alert> it = alerts.iterator();
        ArrayList<String> output = new ArrayList<>();
        while (it.hasNext()) {
            Alert a = it.next();
            if (a.shouldRemove()) {
                it.remove();
                continue;
            }
            if (!a.isEnabled() || a.getAlertType() != type) {
                continue;
            }
            output.add(a.getAlertType().toString() + ": " + a.getAlertDescription());
        }
        return output.toArray(String[]::new);
    }

    public void update() {
        Sendable sender = builder -> {
            builder.clearProperties();

            // Rebuild the cache.
            cache.clear();
            for (AlertType type : AlertType.values()) {
                String[] alerts = getEnabledAlerts(type);
                cache.put(type.toString(), alerts);

                // For each element in the cache, add the String Property
                for (int i=0; i<alerts.length; i++) {
                    final int fIdx = i; // prevents the idx from changing; only accepted way.
                    builder.addStringArrayProperty(type + " #" + i,
                            () -> (new String[]{cache.get(type.toString())[fIdx]}),
                            null
                    );
                }
            }
        };
        SmartDashboard.putData(groupName, sender);
    }
}
