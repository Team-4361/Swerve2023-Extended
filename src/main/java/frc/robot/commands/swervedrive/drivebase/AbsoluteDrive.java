// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.swervedrive.drivebase;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.Robot;
import frc.robot.swerve.SwerveController;
import frc.robot.swerve.math.SwerveMath;

import java.util.List;
import java.util.function.DoubleSupplier;

/**
 * An example command that uses an example subsystem.
 */
public class AbsoluteDrive extends CommandBase {
    private final DoubleSupplier vX, vY;
    private final DoubleSupplier headingHorizontal, headingVertical;
    private final boolean isOpenLoop;

    /**
     * Used to drive a Robot.swerveDrive robot in full field-centric mode.  vX and vY supply translation inputs, where x is
     * towards/away from alliance wall and y is left/right. headingHorizontal and headingVertical are the Cartesian
     * coordinates from which the robot's angle will be derived— they will be converted to a polar angle, which the robot
     * will rotate to.
     *
     * @param vX                DoubleSupplier that supplies the x-translation joystick input.  Should be in the range -1
     *                          to 1 with deadband already accounted for.  Positive X is away from the alliance wall.
     * @param vY                DoubleSupplier that supplies the y-translation joystick input.  Should be in the range -1
     *                          to 1 with deadband already accounted for.  Positive Y is towards the left wall when
     *                          looking through the driver station glass.
     * @param headingHorizontal DoubleSupplier that supplies the horizontal component of the robot's heading angle. In the
     *                          robot coordinate system, this is along the same axis as vY. Should range from -1 to 1 with
     *                          no deadband.  Positive is towards the left wall when looking through the driver station
     *                          glass.
     * @param headingVertical   DoubleSupplier that supplies the vertical component of the robot's heading angle.  In the
     *                          robot coordinate system, this is along the same axis as vX.  Should range from -1 to 1
     *                          with no deadband. Positive is away from the alliance wall.
     */
    public AbsoluteDrive(DoubleSupplier vX, DoubleSupplier vY, DoubleSupplier headingHorizontal,
                         DoubleSupplier headingVertical, boolean isOpenLoop) {
        this.vX = vX;
        this.vY = vY;
        this.headingHorizontal = headingHorizontal;
        this.headingVertical = headingVertical;
        this.isOpenLoop = isOpenLoop;

        addRequirements(Robot.swerveDrive);
    }

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {

        // Get the desired chassis speeds based on a 2 joystick module.

        ChassisSpeeds desiredSpeeds = Robot.swerveDrive.getTargetSpeeds(vX.getAsDouble(), vY.getAsDouble(),
                headingHorizontal.getAsDouble(),
                headingVertical.getAsDouble());
        
        Translation2d translation = SwerveController.getTranslation2d(desiredSpeeds);
        translation = SwerveMath.limitVelocity(translation, Robot.swerveDrive.getFieldVelocity(), Robot.swerveDrive.getPose(),
                0.13, Constants.Chassis.ROBOT_MASS_KG, List.of(Constants.Chassis.MATTER),
                Robot.swerveDrive.getSwerveDriveConfiguration());
        SmartDashboard.putNumber("LimitedTranslation", translation.getX());
        SmartDashboard.putString("Translation", translation.toString());

        // Make the robot move
        Robot.swerveDrive.drive(translation, desiredSpeeds.omegaRadiansPerSecond, true, isOpenLoop);

    }

    // Returns true when the command should end.
    @Override
    public boolean isFinished() {
        return false;
    }
}
