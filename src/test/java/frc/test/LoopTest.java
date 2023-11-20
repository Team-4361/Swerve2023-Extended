package frc.test;

import frc.robot.util.loop.Looper;
import frc.robot.util.loop.LooperManager;

/**
 * This {@link LoopTest} class is designed to test {@link Looper} and
 * {@link LooperManager}. The mentioned objects are heavily used through-out this
 * Robot program, justifying an entire Test class.
 *
 * @author Eric Gold
 * @since 0.0.0
 */
public class LoopTest {

    /**
     * This {@link TrackedLooper} class is designed to keep track of the {@link Looper}'s performance.
     *
     * @author Eric Gold
     * @since 0.0.0
     */

    /*private static class TrackedLooper extends Looper {
        private long lastPeriodic;
        private final ArrayList<Long> periodicIntervals;

        *//**
         * Starts the {@link Looper} instance <b>when managed by the {@link LooperManager} or equivalent.</b>
         *
         * @return The started {@link Looper} instance.
         * @see LooperManager#run()
         *//*
        @Override
        public Looper start() {
            super.start();
            this.lastPeriodic = System.currentTimeMillis();
            return this;
        }

        *//**
         * Constructs a new {@link TrackedLooper}
         *//*
        public TrackedLooper() {
            this.periodicIntervals = new ArrayList<>();

            // Create random intervals and durations.
            Random rand = new Random();

            long intervalMs = rand.nextLong(50, 200);
            long endDelayMs = rand.nextLong(50, 200);
            long cycles     = rand.nextLong(1, 5);

            setInterval(Duration.ofMillis(intervalMs));
            setEndDelay(Duration.ofMillis(endDelayMs));
            setMaxCycles(cycles);

            addPeriodic(() -> {
                long millis = System.currentTimeMillis();
                periodicIntervals.add(millis - lastPeriodic);
                lastPeriodic = millis;
            });

            addOnFinished(() -> {
                long delayMs = System.currentTimeMillis() - lastPeriodic;
                double periodicMs = (long)ExtendedMath.average(periodicIntervals);

                Assertions.assertTrue(
                        ExtendedMath.inTolerance(getEndDelay().toMillis(), delayMs, 250),
                        "End delay not in tolerance! (" + delayMs + " ms)"
                );

                Assertions.assertTrue(
                        ExtendedMath.inTolerance(periodicMs, getInterval().toMillis(), 250),
                        "Periodic interval not in tolerance! (" + periodicMs + " ms)"
                );

                Assertions.assertEquals(cycles, getCycles(), "Cycles not reached!");
            });
        }
    }

    *//**
     * Runs the first test to check the loop's integrity and time-verification.
     *//*
    @Test
    @DisplayName("Method Call Test")
    public void testMethodCalls() {
        // Make sure all methods are getting called correctly.
        for (int i=25; i<=100; i+=25) {
            AlertManager.debug("Testing " + i + " loop(s)...");

            for (int j=0; j<i; j++) {
                TrackedLooper looper = new TrackedLooper();
                looper.start();
            }

            while (!LooperManager.getInstance().getLoops().isEmpty()) {
                LooperManager.getInstance().run();
            }

            AlertManager.debug(i + " loop(s) successful.");
        }
    }*/
}
