package frc.robot.util.loop;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This {@link LooperManager} class manages all {@link Looper} classes; it handles
 * all Custom Delays and the Robot State with the {@link #run()} method.
 *
 * @author Eric Gold
 * @since 0.0.0
 */
public class LooperManager {
    private static final ArrayList<Looper> allLoops = new ArrayList<>();
    private static final List<Looper> removeLoops = new ArrayList<>();

    /**
     * @param loopName The Loop Name to use.
     * @return A {@link Looper} if <code>groupName</code> exists; null otherwise.
     */
    private static Looper getRawLoop(String loopName) {
        return allLoops.stream()
                .filter(o -> o.getName().equals(loopName))
                .findFirst()
                .orElse(null);
    }

    /**
     * @param loopName The Loop Name to use.
     * @return A {@link Looper} associated with the <code>loopName</code> parameter. An instance will
     * <b>automatically</b> be created if invalid.
     */
    public static Looper getLoop(String loopName) {
        if (getRawLoop(loopName) == null) {
            new Looper(loopName); // NOTE: automatically added to LooperManager!
        }
        return getRawLoop(loopName);
    }

    /** @return An {@link ArrayList} of all registered {@link Looper}'s */
    public static ArrayList<Looper> getLoops() {
        return allLoops;
    }

    /**
     * Registers a {@link Looper} with the {@link LooperManager}.
     * @param loop     The {@link Looper} to register.
     */
    public static void addLoop(Looper loop) {
        allLoops.add(loop);
    }

    /** Constructs a new {@link LooperManager}; as this class is only made to contain one Instance, its private. */
    LooperManager() {}

    /**
     * Runs the {@link LooperManager}; expected to be called every <b>20ms</b> by a Robot method.
     * <p></p>
     * <b>IMPORTANT:</b> A {@link Looper}'s lowest delay is the delay in-between this method
     * call. Example: Looper #1 has delay of 15ms, while this method is called every 20ms. As
     * a result, the <b>LOWEST DELAY</b> is 20ms.
     */
    public static synchronized void run() {
        if (!removeLoops.isEmpty()) {
            // Check if the finish method is ready to be called yet.
            Iterator<Looper> it = removeLoops.iterator();
            while (it.hasNext()) {
                allLoops.remove(it.next());
                it.remove();
            }
        }

        for (Looper looper : allLoops) {
            if (!looper.isFinished() && !looper.isRunning()) {
                continue;
            }
            if (looper.isFinished()) {
                removeLoops.add(looper);
                continue;
            }
            looper.run();
        }
    }
}
