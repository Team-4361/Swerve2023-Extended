package frc.robot.commands.auto;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class TimeoutCommand extends ParallelRaceGroup {
    public TimeoutCommand(Command command, double seconds) {
        super(command, new WaitCommand(seconds));
    }
}
