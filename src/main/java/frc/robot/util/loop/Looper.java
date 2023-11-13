package frc.robot.util.loop;

import edu.wpi.first.wpilibj2.command.Subsystem;
import frc.robot.Constants;

import java.time.Duration;
import java.util.ArrayList;

import static frc.robot.Constants.Looper.PERIODIC_INTERVAL;

/**
 * This {@link Looper} class is designed to be the bare-minimum for <code>periodic</code>
 * and <code>simulationPeriodic</code> methods. Unlike a {@link Subsystem},
 * this supports conditions such as maximum execution cycles, or
 * delay in-between calls — preventing a {@link Thread#sleep(long)} call.
 * <p></p>
 * <b>NOTICES:</b>
 * <ol>
 *     <li>
 *         You need to register <b>each</b> {@link Looper} in the {@link LooperManager}
 *         class; this allows the system to run using the {@link LooperManager#run()} method. If
 *         the <code>addToManager</code> option is called in the Constructor, this is done <b>automatically.</b>
 *     </li>
 *     <p></p>
 *     <li>
 *         All <code>periodic</code> and <code>simulationPeriodic</code> calls are <b>NOT</b>
 *         multi-threaded! It is <b>NOT</b> recommended to put any {@link Thread#sleep(long)}
 *         calls or equivalent, as the next {@link Looper} will be delayed. As an alternative,
 *         use the "interval" feature in this class.
 *     </li>
 * </ol>
 *
 * @author Eric Gold
 * @since 0.0.0
 */
public class Looper {
    private final ArrayList<Runnable> periodicRunnables;
    private final ArrayList<Runnable> simRunnables;
    private final ArrayList<Runnable> endRunnables;
    private final ArrayList<Runnable> initRunnables;

    private Duration interval;
    private Duration endDelay;
    private boolean running;
    private boolean manFinished;
    private boolean setFinished;
    private long maxCycles;
    private long cycles;

    private long nextMillis;

    /**
     * Starts the {@link Looper} instance <b>when managed by the {@link LooperManager}
     * or equivalent.</b>
     *
     * @see LooperManager#run()
     * @return The started {@link Looper} instance.
     */
    public Looper start() {
        if (running)
            return this;

        this.running = true;
        nextMillis = System.currentTimeMillis() + getInterval().toMillis();

        initRunnables.forEach(Runnable::run);
        return this;
    }

    /**
     * Stops the {@link Looper} instance <b>when managed by the {@link LooperManager}
     * or equivalent.</b> This method also resets the <code>nextMillis</code> to
     * account for a potential end delay.
     *
     * @return The stopped. {@link Looper} instance.
     */
    public Looper stop() {
        manFinished = true;
        return this;
    }

    /** @return If the current {@link Looper} instance is <b>running.</b> */
    public boolean isRunning() { return this.running; }

    /**
     * @return If the current {@link Looper} instance is <b>finished</b>;
     * (ex. maximum execution cycles reached, or {@link #stop()} method is called.)
     */
    public boolean isFinished() {
        return (manFinished || maxCycles > 0 && cycles >= maxCycles);
    }

    /**
     * Adds a new Periodic {@link Runnable} to the {@link Looper} instance.
     * @param runnable The {@link Runnable} to add.
     * @return The current {@link Looper} instance with the updated changes.
     */
    public Looper addPeriodic(Runnable runnable) {
        periodicRunnables.add(runnable);
        return this;
    }

    /**
     * Adds a new Simulation Periodic {@link Runnable} to the {@link Looper} instance.
     * @param runnable The {@link Runnable} to add.
     * @return The current {@link Looper} instance with the updated changes.
     */
    public Looper addSimPeriodic(Runnable runnable) {
        simRunnables.add(runnable);
        return this;
    }

    /**
     * Adds a new Initialize {@link Runnable} to the {@link Looper} instance.
     * @param runnable The {@link Runnable} to add.
     * @return The current {@link Looper} instance with the updated changes.
     */
    public Looper addInit(Runnable runnable) {
        initRunnables.add(runnable);
        return this;
    }

    /**
     * Adds a new {@link Runnable} to the {@link Looper} instance which will be called after execution.
     * @param runnable The {@link Runnable} to add.
     * @return The current {@link Looper} instance with the updated changes.
     */
    public Looper addOnFinished(Runnable runnable) {
        endRunnables.add(runnable);
        return this;
    }

    /**
     * Sets the {@link Duration} interval between <code>periodic</code>/<code>simulationPeriodic</code>
     * {@link Runnable} calls.
     *
     * @param interval The {@link Duration} interval to use; <b>minimum of 20 milliseconds.</b>
     * @return The modified {@link Looper} instance.
     */
    public Looper setInterval(Duration interval) {
        this.interval = Duration.ofMillis(Math.max(20, interval.toMillis()));
        return this;
    }

    /**
     * Sets the maximum cycles before the {@link Looper} is finished. <b><= 0 is INFINITY.
     *
     * @param cycles The {@link Long} value to use.
     * @return The modified {@link Looper} instance.
     */
    public Looper setMaxCycles(long cycles) {
        this.maxCycles = cycles;
        return this;
    }

    /**
     * Sets the {@link Duration} where the <code>end</code> {@link Runnable} will be called after finishing.
     *
     * @param endDelay The end {@link Duration} to use.
     * @return The modified {@link Looper} instance.
     */
    public Looper setEndDelay(Duration endDelay) {
        this.endDelay = endDelay;
        return this;
    }

    /**
     * @return The {@link Duration} interval between
     * <code>periodic</code>/<code>simulationPeriodic</code>{@link Runnable} calls
     */
    public Duration getInterval() { return this.interval; }

    /**
     * @return The {@link Duration} where the <code>end</code> {@link Runnable} will be called after finishing.
     */
    public Duration getEndDelay() { return this.endDelay; }

    /** @return The current periodic {@link Runnable}s. */
    public ArrayList<Runnable> getPeriodicCalls() { return this.periodicRunnables; }

    /** @return The current <b>simulation</b> periodic {@link Runnable}s. */
    public ArrayList<Runnable> getSimPeriodicCalls() { return this.simRunnables; }

    /** @return The current finished {@link Runnable}s. */
    public ArrayList<Runnable> getFinishedCalls() { return this.endRunnables; }

    /** @return The current initialize {@link Runnable}s. */
    public ArrayList<Runnable> getInitCalls() { return this.initRunnables; }

    /** @return The maximum cycles before the {@link Looper} is finished. <b><= 0 is INFINITY.</b> */
    public long getMaxCycles() { return this.maxCycles; }

    /** @return The number of times the {@link Looper} has run. */
    public long getCycles() { return this.cycles; }

    /**
     * Constructs a new {@link Looper} with the input parameters.
     * @param interval     The {@link Duration} between <code>periodic</code>/<code>simulationPeriodic</code>
     *                     {@link Runnable} calls.
     * @param endDelay     The {@link Duration} where the <code>end</code> {@link Runnable} will be called
     *                     after finishing.
     * @param addToManager If this {@link Looper} instance should be <b>automatically</b> added to the
     *                     {@link LooperManager}.
     */
    public Looper(Duration interval, Duration endDelay, boolean addToManager) {
        this.interval = interval;
        this.endDelay = endDelay;
        this.running = false;
        this.manFinished = false;
        this.setFinished = false;
        this.nextMillis = System.currentTimeMillis();

        this.periodicRunnables = new ArrayList<>();
        this.simRunnables = new ArrayList<>();
        this.endRunnables = new ArrayList<>();
        this.initRunnables = new ArrayList<>();

        if (addToManager)
            LooperManager.getInstance().addLoop(this);
    }

    /**
     * Constructs a new {@link Looper} with the input parameters, and is <b>automatically</b>
     * added to the {@link LooperManager}.
     *
     * @param interval     The {@link Duration} between <code>periodic</code>/<code>simulationPeriodic</code>
     *                     {@link Runnable} calls.
     * @param endDelay     The {@link Duration} where the <code>end</code> {@link Runnable} will be called
     *                     after finishing.
     */
    public Looper(Duration interval, Duration endDelay) {
        this(interval, endDelay, true);
    }

    /**
     * Constructs a new {@link Looper} with the interval {@link Duration}, and End Delay of <b>ZERO</b>.
     *
     * @param interval The {@link Duration} between <code>periodic</code>/<code>simulationPeriodic</code>
     *                 @link Runnable} calls.
     */
    public Looper(Duration interval) {
        this(interval, Duration.ZERO);
    }

    /**
     * Constructs a new {@link Looper} with an interval {@link Duration} of
     * {@link Constants.Looper#PERIODIC_INTERVAL}, and End Delay of <b>ZERO</b>.
     */
    public Looper() {
        this(PERIODIC_INTERVAL, Duration.ZERO);
    }

    /**
     * Calls the <code>periodic</code>/<code>simulationPeriodic</code> or <code>end</code> {@link Runnable}
     * based on the current status. This method needs to be called repeatedly upon startup to function
     * correctly.
     */
    public void run() {
        long currentTimeMillis = System.currentTimeMillis();

        if (currentTimeMillis >= nextMillis) {
            if (isFinished()) {
                if (!setFinished) {
                    setFinished = true;
                    nextMillis = System.currentTimeMillis() + endDelay.toMillis();
                    return;
                }
                endRunnables.forEach(Runnable::run);
                running = false;
                return;
            }

            periodicRunnables.forEach(Runnable::run);
            simRunnables.forEach(Runnable::run);

            if (isFinished())
                return; // just in-case.
            nextMillis = currentTimeMillis + interval.toMillis();
            cycles++;
        }
    }
}
