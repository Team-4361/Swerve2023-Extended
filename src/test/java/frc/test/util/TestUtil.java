package frc.test.util;

import frc.robot.Robot;
import frc.robot.util.io.IOManager;
import frc.robot.util.io.Looper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestUtil {
    private static Thread ioThread = null;
    private static Robot trackedRobot = null;
    private static boolean isRunning = false;

    /**
     * @return A {@link Robot} instance which can be used for testing purposes.
     */
    public static Robot createTrackedRobot() {
        // Create a new List of IOManager threads if not initialized.
        if (ioThread == null) {
            ioThread = new Thread(() -> {
                while (isRunning) {

                }
            });
        }

        Looper periodicLoop = IOManager.
    }
}
