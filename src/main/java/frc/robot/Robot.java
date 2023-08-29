// Copyright (c) FIRST and other WPILib contributors.

// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.subsystems.climber.ClimberArmSubsystem;
import frc.robot.subsystems.climber.ClimberWristSubsystem;
import frc.robot.subsystems.vacuum.VacuumSubsystem;
import frc.robot.swerve.chassis.SwerveDriveSubsystem;

import static frc.robot.Constants.Chassis.CHASSIS_CONFIG;
import static frc.robot.util.math.ExtendedMath.deadband;

/**
 * The VM is configured to automatically run this class, and to call the methods corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
    private RobotContainer robotContainer;

    public static SwerveDriveSubsystem swerveDrive;
    public static ClimberArmSubsystem arm;
    public static ClimberWristSubsystem wrist;
    public static VacuumSubsystem pump;
    public static PowerDistribution power;

    public static boolean pidControlEnabled = true; //true;
    public static boolean limitSwitchBypass = false; //false;

    public static SendableChooser<Integer> autoMode = new SendableChooser<>();

    /**
     * This method is run when the robot is first started up and should be used for any
     * initialization code.
     */
    @Override
    public void robotInit() {
        swerveDrive = new SwerveDriveSubsystem(CHASSIS_CONFIG);
        arm = new ClimberArmSubsystem();
        wrist = new ClimberWristSubsystem();
        pump = new VacuumSubsystem();
        power = new PowerDistribution();

        // *** IMPORTANT: Call this method at the VERY END of robotInit!!! *** //
        robotContainer = new RobotContainer();
    }

    /**
     * This method is called every 20 ms, no matter the mode. Use this for items like diagnostics
     * that you want ran during disabled, autonomous, teleoperated and test.
     *
     * <p>This runs after the mode specific periodic methods, but before LiveWindow and
     * SmartDashboard integrated updating.
     */
    @Override
    public void robotPeriodic() {
        // Runs the Scheduler.  This is responsible for polling buttons, adding newly-scheduled
        // commands, running already-scheduled commands, removing finished or interrupted commands,
        // and running subsystem periodic() methods.  This must be called from the robot's periodic
        // block in order for anything in the Command-based framework to work.
        CommandScheduler.getInstance().run();
    }

    @Override public void disabledInit() { CommandScheduler.getInstance().cancelAll(); }
    @Override public void testInit() { CommandScheduler.getInstance().cancelAll(); }
    @Override public void teleopInit() { CommandScheduler.getInstance().cancelAll(); }

    /**
     * This method is called periodically during operator control.
     */
    @Override
    public void teleopPeriodic() {
        Robot.arm.getExtension().translateMotor(deadband(-RobotContainer.xbox.getLeftY() / 2, 0.1));
        Robot.arm.getRotation().translateMotor(deadband(-RobotContainer.xbox.getRightY(), 0.1));
    }
}