package frc.robot.swerve.parser.json;

import frc.robot.swerve.parser.PIDFConfig;

/**
 * {@link frc.robot.swerve.SwerveModule} PID with Feedforward for the drive motor and angle motor.
 */
public class PIDFPropertiesJson
{

  /**
   * The PIDF with Integral Zone used for the drive motor.
   */
  public PIDFConfig drive;
  /**
   * The PIDF with Integral Zone used for the angle motor.
   */
  public PIDFConfig angle;
}
