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
    private static final LooperManager instance = new LooperManager();

    /** @return The <b>global</b> {@link LooperManager} instance used. */
    public static LooperManager getInstance() { return instance; }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private final ArrayList<Looper> allLoops = new ArrayList<>();
    private final List<Looper> removeLoops = new ArrayList<>();

    /** @return An {@link ArrayList} of all registered {@link Looper}'s */
    public ArrayList<Looper> getLoops() {
        return allLoops;
    }

    /**
     * Registers a {@link Looper} with the {@link LooperManager}.
     * @param loop     The {@link Looper} to register.
     * @return         The current {@link LooperManager} with the Registered {@link Looper}.
     */
    @SuppressWarnings("UnusedReturnValue")
    public LooperManager addLoop(Looper loop) {
        allLoops.add(loop);
        return this;
    }

    /**
     * Runs the {@link LooperManager}; expected to be called every <b>20ms</b> by a Robot method.
     * <p></p>
     * <b>IMPORTANT:</b> A {@link Looper}'s lowest delay is the delay in-between this method
     * call. Example: Looper #1 has delay of 15ms, while this method is called every 20ms. As
     * a result, the <b>LOWEST DELAY</b> is 20ms.
     */
    public synchronized void run() {
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
                // start the looper; it's not done or running.
                looper.start();
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
