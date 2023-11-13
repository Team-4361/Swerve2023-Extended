// Copyright (c) FIRST and other WPILib contributors.

// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.util.log.VerbosityLevel;
import frc.robot.util.loop.LooperManager;
import org.littletonrobotics.junction.LoggedRobot;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.networktables.NT4Publisher;


/**
 * The VM is configured to automatically run this class, and to call the methods corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends LoggedRobot {
    private RobotContainer robotContainer;

    public static VerbosityLevel verbosity = VerbosityLevel.DEBUG;
    public static CommandXboxController xbox;

    /**
     * This method is run when the robot is first started up and should be used for any
     * initialization code.
     */
    @Override
    public void robotInit() {
        // Initialize AdvantageKit logging. (DO NOT TOUCH)
        Logger.getInstance().recordMetadata("Project Name", BuildConstants.MAVEN_NAME);
        Logger.getInstance().recordMetadata("Build Date", BuildConstants.BUILD_DATE);
        Logger.getInstance().recordMetadata("Git SHA", BuildConstants.GIT_SHA);
        Logger.getInstance().recordMetadata("Git Date", BuildConstants.GIT_DATE);
        Logger.getInstance().recordMetadata("Git Branch", BuildConstants.GIT_BRANCH);

        //noinspection RedundantSuppression
        switch (BuildConstants.DIRTY) {
            //noinspection DataFlowIssue
            case 0:
                Logger.getInstance().recordMetadata("Git Status", "All changes committed");
                break;
            //noinspection DataFlowIssue
            case 1:
                Logger.getInstance().recordMetadata("Git Status", "Un-committed changes");
                break;
            //noinspection DataFlowIssue
            default:
                Logger.getInstance().recordMetadata("Git Status", "Unknown");
                break;
        }

        // TODO: setup replay/sim mode!
        Logger.getInstance().addDataReceiver(new NT4Publisher()); // publish to NetworkTables.
        Logger.getInstance().start(); // start logging!

        //////////////////////////////////////////////////////////

        xbox = new CommandXboxController(0);
        //swerveDrive = new SwerveDriveSubsystem(CHASSIS_CONFIG);
        //arm = new ClimberArmSubsystem();
        //wrist = new ClimberWristSubsystem();
        //pump = new VacuumSubsystem();
        //power = new PowerDistribution();

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
        LooperManager.getInstance().run();
    }

    @Override public void disabledInit() { CommandScheduler.getInstance().cancelAll(); }
    @Override public void testInit() { CommandScheduler.getInstance().cancelAll(); }
    @Override public void teleopInit() { CommandScheduler.getInstance().cancelAll(); }

    /**
     * This method is called periodically during operator control.
     */
    @Override
    public void teleopPeriodic() {
        //Robot.arm.getExtension().translateMotor(deadband(-RobotContainer.xbox.getLeftY() / 2, 0.1));
        //Robot.arm.getRotation().translateMotor(deadband(-RobotContainer.xbox.getRightY(), 0.1));
    }
}