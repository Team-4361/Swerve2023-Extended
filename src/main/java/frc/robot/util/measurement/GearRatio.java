package frc.robot.util.measurement;

import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.util.math.ExtendedMath;

/**
 * This {@link GearRatio} class enables operations with complex gear-ratios. Unlike other measurement classes,
 * we <b>do not</b> extend the {@link BaseConverter} class, as there are no conversions taking place.
 *
 * @author Eric Gold
 * @since 0.0.0
 */
public class GearRatio {
    private double gearOne;
    private double gearTwo;

    /**
     * Constructs a new {@link GearRatio} with a pre-determined Ratio.
     * @param ratio The {@link String}-equivalent ratio (ex. <code>1:5</code> or <code>0.5:2</code>)
     */
    public GearRatio(String ratio) {
        // attempt to split and convert the Ratio.
        try {
            int idx = ratio.indexOf(':');
            if (idx < 0)
                return;
            gearOne = Double.parseDouble(ratio.substring(0, idx).trim());
            gearTwo = Double.parseDouble(ratio.substring(idx + 1).trim());

            // sanity check
            if (gearOne <= 0) {
                gearOne = 1;
            }
            if (gearTwo <= 0) {
                gearTwo = 1;
            }
        } catch (Exception ex) {
            gearOne = 1;
            gearTwo = 1;
        }
    }

    /**
     * Constructs a new {@link GearRatio} with a pre-determined Ratio.
     * @param gearOne The first gear teeth (driving gear)
     * @param gearTwo The second gear teeth (driven gear)
     */
    public GearRatio(double gearOne, double gearTwo) {
        this.gearOne = gearOne;
        this.gearTwo = gearTwo;
    }

    /**
     * Constructs a new {@link GearRatio} with a pre-determined Ratio.
     * @param gearOne The first gear teeth (driving gear)
     * @param gearTwo The second gear teeth (driven gear)
     */
    public static GearRatio from(double gearOne, double gearTwo) { return new GearRatio(gearOne, gearTwo); }

    /**
     * Constructs a new {@link GearRatio} with a pre-determined Ratio.
     * @param ratio The {@link String}-equivalent ratio (ex. <code>1:5</code> or <code>0.5:2</code>)
     */
    public static GearRatio from(String ratio) { return new GearRatio(ratio); }

    /** @return The first gear (driving gear) of the ratio. */
    public double getLeadGear() { return this.gearOne; }

    /** @return The second gear (driven gear) of the ratio. */
    public double getFollower() { return this.gearTwo; }

    /**
     * Combines two {@link GearRatio}s together by <b>multiplying</b> their ratios into a new instance.
     * @param other The {@link GearRatio} to combine.
     * @return A new combined {@link GearRatio}
     */
    public GearRatio add(GearRatio other) {
        double combOne = getLeadGear() * other.getLeadGear();
        double combTwo = getFollower() * other.getFollower();
        return new GearRatio(combOne, combTwo);
    }
    /**
     * Calculates the {@link Rotation2d} of the Follower Gear.
     *
     * @param gearOneRotation The {@link Rotation2d} of the Lead Gear.
     * @return The output {@link Rotation2d}.
     */
    public Rotation2d getFollowerRotation(Rotation2d gearOneRotation) {
        return Rotation2d.fromRotations(
                ExtendedMath.round(gearOneRotation.getRotations() * (gearTwo / gearOne), 2)
        );
    }

    /**
     * Calculates the {@link Rotation2d} of the Lead Gear.
     *
     * @param gearTwoRotation The {@link Rotation2d} of the Follower Gear.
     * @return The output {@link Rotation2d}.
     */
    public Rotation2d getLeadRotation(Rotation2d gearTwoRotation) {
        return Rotation2d.fromRotations(
                ExtendedMath.round(gearTwoRotation.getRotations() * (gearOne / gearTwo), 2)
        );
    }

    /**
     * @return The {@link GearRatio} in {@link String} format (ex. <code>1:5</code>)
     */
    @Override
    public String toString() {
        return gearOne + ":" + gearTwo;
    }
}
