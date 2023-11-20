package frc.robot.util.pid;

import com.revrobotics.RelativeEncoder;

import static frc.robot.Constants.ClimberArmValues.WRIST_ROLLOVER_VALUE;

public class RelativeEncoderAdapter {
    private final RelativeEncoder encoder;

    public double getVelocity() {
        return negativeAdjust(encoder.getVelocity(), WRIST_ROLLOVER_VALUE);
    }

    public double getPosition() {
        return negativeAdjust(encoder.getPosition(), WRIST_ROLLOVER_VALUE);
    }

    public void setPosition(double position) {
        encoder.setPosition(position);
    }

    public RelativeEncoderAdapter(RelativeEncoder encoder) {
        this.encoder = encoder;
    }

    public static double negativeAdjust(double val, double threshold) {
        return (val>=threshold?-(Math.abs(threshold-val)):val);
    }
}
