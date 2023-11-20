// Copyright (c) FIRST and other WPILib contributors.

// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.util.log.*;
import frc.robot.util.loop.LooperManager;
import frc.robot.util.motor.FRCSparkMax;
import org.littletonrobotics.junction.LoggedRobot;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.networktables.NT4Publisher;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static frc.robot.Constants.LooperConfig.*;


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
    public static FRCSparkMax motor;

    /**
     * This method is run when the robot is first started up and should be used for any
     * initialization code.
     */
    @Override
    public void robotInit() {
        // region Initialize AdvantageKit logging. (DO NOT TOUCH)
        Logger.getInstance().recordMetadata("Project Name", BuildConstants.MAVEN_NAME);
        Logger.getInstance().recordMetadata("Build Date", BuildConstants.BUILD_DATE);
        Logger.getInstance().recordMetadata("Git SHA", BuildConstants.GIT_SHA);
        Logger.getInstance().recordMetadata("Git Date", BuildConstants.GIT_DATE);
        Logger.getInstance().recordMetadata("Git Branch", BuildConstants.GIT_BRANCH);

        if (BuildConstants.DIRTY == 1)
            Logger.getInstance().recordMetadata("Git Status", "Uncommitted.");
        else
            Logger.getInstance().recordMetadata("Git Status", "All changes committed.");

        // TODO: setup replay/sim mode!
        Logger.getInstance().addDataReceiver(new NT4Publisher());
        Logger.getInstance().start(); // start logging!
        // endregion

        xbox = new CommandXboxController(0);

        // Add low-priority and periodic instance loops. ALL Runnable(s) will be added to these,
        // preventing a HUGE number of Objects from creating with the RoboRIO's limited RAM/CPU cycles.
        LooperManager.getLoop(PERIODIC_NAME).setInterval(PERIODIC_INTERVAL).start();
        LooperManager.getLoop(LOW_PRIORITY_NAME).setInterval(LOW_PRIORITY_INTERVAL).start();

        BiConsumer<Command, Boolean> logCommandFunction = getCommandActivity();
        CommandScheduler.getInstance().onCommandInitialize(c -> logCommandFunction.accept(c, true));
        CommandScheduler.getInstance().onCommandFinish(c -> logCommandFunction.accept(c, false));
        CommandScheduler.getInstance().onCommandInterrupt(c -> logCommandFunction.accept(c, false));

        // *** IMPORTANT: Call this method at the VERY END of robotInit!!! *** //
        robotContainer = new RobotContainer();
        /////////////////////////////////////////////////////////////////////////
    }

    private static BiConsumer<Command, Boolean> getCommandActivity() {
        Map<String, Integer> commandCounts = new HashMap<>();
        return (Command command, Boolean active) -> {
            String name = command.getName();
            int count = commandCounts.getOrDefault(name, 0) + (active ? 1 : -1);
            commandCounts.put(name, count);
            Logger.getInstance()
                    .recordOutput(
                            "CommandsUnique/" + name + "_" + Integer.toHexString(command.hashCode()), active);
            Logger.getInstance().recordOutput("CommandsAll/" + name, count > 0);
        };
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
        LooperManager.run();
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