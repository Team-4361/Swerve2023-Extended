package frc.test;

import edu.wpi.first.math.geometry.Rotation2d;
import frc.robot.util.math.ExtendedMath;
import frc.robot.util.measurement.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

/**
 * This {@link ConversionTest} verifies the accuracy of Unit Conversion. Test points include:
 * <ul>
 *     <li>Angular Velocity</li>
 *     <li>Linear Velocity</li>
 *     <li>Temperature</li>
 *     <li>Distance</li>
 *     <li>Gear Ratio</li>
 * </ul>
 */
public class ConversionTest {
    private static final int MAX_RUNS = 10;
    private static final Random rand = new Random();


    @Test
    @DisplayName("Linear Velocity")
    public void testLinearVelocity() {
        TestUtil.loopAndDebug("Linear Velocity", MAX_RUNS, () -> {
            double mph = rand.nextInt(10, 20);
            LinearVelocity lv = LinearVelocity.fromMPH(mph);

            Assertions.assertEquals(ExtendedMath.round(mph * 1.609), lv.toKPH(), "KPH test failed!");
            Assertions.assertEquals(mph, lv.toMPH(), "MPH test failed!");
            Assertions.assertEquals(ExtendedMath.round(mph / 2.237), lv.toMPS2(), "MPS^2 test failed!");
        });
    }

    @Test
    @DisplayName("Angular Velocity")
    public void testAngularVelocity() {
        TestUtil.loopAndDebug("Angular Velocity", MAX_RUNS, () -> {
            double rpm = rand.nextInt(500, 5000);
            AngularVelocity av = AngularVelocity.fromRPM(rpm);

            Assertions.assertEquals(ExtendedMath.round(rpm * 0.1047198), av.toRPS(), "RPS test failed!");
            Assertions.assertEquals(rpm, av.toRPM(), "RPM test failed!");
        });
    }

    @Test
    @DisplayName("Distance")
    public void testDistance() {
        TestUtil.loopAndDebug("Distance", MAX_RUNS, () -> {
            double feet = rand.nextDouble(10, 100);
            Distance distance = Distance.fromFeet(feet);

            Assertions.assertEquals(
                    ExtendedMath.round(feet * 12, 2),
                    distance.toInches(),
                    "Distance test failed!"
            );
        });
    }

    @Test
    @DisplayName("Gear Ratio")
    public void testGearRatio() {
        TestUtil.loopAndDebug("Gear Ratio", MAX_RUNS, () -> {
            int gearOne = rand.nextInt(1, 10);
            int gearTwo = rand.nextInt(1, 10);
            int motorRotations = rand.nextInt(5, 25);

            GearRatio ratio = GearRatio.from(gearOne + ":" + gearTwo);

            double actual = ExtendedMath.round(ratio.getFollowerRotation(
                    Rotation2d.fromRotations(motorRotations)
            ).getRotations());
            double expected = ExtendedMath.round(motorRotations * ((double) gearTwo / gearOne), 2);

            Assertions.assertEquals(expected, actual, "GearRatio test failed!");
        });
    }

    @Test
    @DisplayName("Temperature")
    public void testTemperature() {
        TestUtil.loopAndDebug("Temperature", MAX_RUNS, () -> {
            double f = ExtendedMath.round(rand.nextDouble(-100, 100), 2);
            double c = ExtendedMath.round((f - 32) * (5/9f), 2);
            double k = ExtendedMath.round(c + 273.15, 2);

            Temperature temp = Temperature.fromF(f);

            Assertions.assertEquals(f, temp.toF(), "Fahrenheit test failed!");
            Assertions.assertEquals(c, temp.toC(), "Celsius test failed!");
            Assertions.assertEquals(k, temp.toK(), "Kelvin test failed!");
        });
    }
}
