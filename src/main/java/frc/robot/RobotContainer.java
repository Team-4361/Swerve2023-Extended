// Copyright (c) FIRST and other WPILib contributors.

// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;


import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.util.joystick.DriveJoystick;
import frc.robot.util.joystick.DriveXboxController;

import static frc.robot.Constants.ClimberPresets.*;
import static frc.robot.Constants.Control.*;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
   // public static final DriveJoystick xyStick = new DriveJoystick(LEFT_STICK_ID);
   // public static final DriveJoystick zStick = new DriveJoystick(RIGHT_STICK_ID);
    public static final DriveXboxController xbox = new DriveXboxController(XBOX_CONTROLLER_ID);

    /**
     * The container for the robot. Contains subsystems, OI devices, and commands.
     */
    public RobotContainer() {
        // Configure the trigger bindings
        configureBindings();

        Robot.swerveDrive.setDefaultCommand(Robot.swerveDrive.runEnd(() -> {
            Robot.swerveDrive.drive(
                    Robot.swerveDrive.getTargetPose(xbox),
                    true,
                    false
            );
        }, () -> Robot.swerveDrive.stop()));
    }

    /**
     * Use this method to define your trigger->command mappings. Triggers can be created via the
     * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with an arbitrary
     * predicate, or via the named factories in {@link
     * edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for {@link
     * CommandXboxController Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller
     * PS4} controllers or {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight
     * joysticks}.
     */
    private void configureBindings() {
        //xyStick.button(12).onTrue(Commands.runOnce(() -> Robot.swerveDrive.zeroGyro()));

        ///////////////////////////////// XBOX CONTROLS

        xbox.a().onTrue(Commands.runOnce(() -> CLIMBER_PRESET_GROUP.setPreset(ZERO_POSITION_NAME)));
        xbox.b().onTrue(Commands.runOnce(() -> CLIMBER_PRESET_GROUP.setPreset(FLOOR_CUBE_NAME)));
        xbox.y().onTrue(Commands.runOnce(() -> CLIMBER_PRESET_GROUP.setPreset(HUMAN_STATION_NAME)));
        xbox.x().onTrue(Commands.runOnce(() -> CLIMBER_PRESET_GROUP.setPreset(MID_CONE_NAME)));

        xbox.povDown().onTrue(Commands.runOnce(() -> CLIMBER_PRESET_GROUP.setPreset(FLOOR_CONE_NAME)));
        xbox.povLeft().onTrue(Commands.runOnce(() -> CLIMBER_PRESET_GROUP.setPreset(MANUAL_STATION_NAME)));
        xbox.povUp().onTrue(Robot.pump.openVacuumCommand());

        xbox.rightBumper().onTrue(Commands.runOnce(() -> CLIMBER_PRESET_GROUP.setPreset(HIGH_CONE_NAME)));

        xbox.rightTrigger().whileTrue(Commands.runEnd(
                () -> Robot.wrist.translateMotor(-xbox.getRightTriggerAxis()/2),
                () -> Robot.wrist.translateMotor(0)
        ));

        xbox.leftTrigger().whileTrue(Commands.runEnd(
                () -> Robot.wrist.translateMotor(xbox.getLeftTriggerAxis()/2),
                () -> Robot.wrist.translateMotor(0)
        ));

        xbox.leftStick().onTrue(Commands.runOnce(() -> {
            Robot.wrist.resetEncoder();
            Robot.arm.getRotation().resetEncoder();
            Robot.arm.getExtension().resetEncoder();
        }));

        xbox.leftBumper().onTrue(Commands.runOnce(() -> Robot.pump.toggleVacuum()));
    }
}