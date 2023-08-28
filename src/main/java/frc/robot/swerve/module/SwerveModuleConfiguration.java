package frc.robot.swerve.module;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import frc.robot.util.math.*;

public class SwerveModuleConfiguration {
    private final CANSparkMax driveMotor;
    private final CANSparkMax turnMotor;

    private final DutyCycleEncoder turnEncoder;

    private final GearRatio driveRatio;
    private final GearRatio turnRatio;

    private final Rotation2d offset;
    private final Velocity maxSpeed;

    private final AngularVelocity maxTurnSpeed;

    private final Distance wheelDiameter;

    private final PIDController turnController;
    private final PIDController driveController;

    private final SwerveModuleSide moduleSide;

    private final boolean driveInverted, turnInverted;
    public SwerveModuleConfiguration(
            int turnMotorID,
            int driveMotorID,
            int turnEncoderID,
            GearRatio driveRatio,
            GearRatio turnRatio,
            Rotation2d offset,
            PIDController driveController,
            PIDController turnController,
            Distance wheelDiameter,
            SwerveModuleSide moduleSide,
            SwerveVelocities maxVelocities,
            boolean driveInverted,
            boolean turnInverted
    ) {
        this.driveRatio = driveRatio;
        this.turnRatio = turnRatio;
        this.turnMotor = new CANSparkMax(turnMotorID, MotorType.kBrushless);
        this.driveMotor = new CANSparkMax(driveMotorID, MotorType.kBrushless);
        this.turnEncoder = new DutyCycleEncoder(turnEncoderID);
        this.offset = offset;
        this.driveController = driveController;
        this.turnController = turnController;
        this.wheelDiameter = wheelDiameter;
        this.moduleSide = moduleSide;
        this.maxSpeed = maxVelocities.getForwardVelocity();
        this.maxTurnSpeed = maxVelocities.getTwistVelocity();
        this.driveInverted = driveInverted;
        this.turnInverted = turnInverted;

        driveMotor.setInverted(driveInverted);
        turnMotor.setInverted(turnInverted);
    }

    public SwerveModuleConfiguration(
            int turnMotorID,
            int driveMotorID,
            int turnEncoderID,
            GearRatio driveRatio,
            GearRatio turnRatio,
            Rotation2d offset,
            PIDController driveController,
            PIDController turnController,
            Distance wheelDiameter,
            SwerveModuleSide moduleSide,
            SwerveVelocities maxVelocities
    ) {
        this(
                turnMotorID,
                driveMotorID,
                turnEncoderID,
                driveRatio,
                turnRatio,
                offset,
                driveController,
                turnController,
                wheelDiameter,
                moduleSide,
                maxVelocities,
                false,
                false
        );
    }

    public CANSparkMax getDriveMotor() { return this.driveMotor; }
    public CANSparkMax getTurnMotor() { return this.turnMotor; }
    public DutyCycleEncoder getTurnEncoder() { return this.turnEncoder; }
    public GearRatio getDriveRatio() { return this.driveRatio; }
    public GearRatio getTurnRatio() { return this.turnRatio; }
    public Rotation2d getOffset() { return this.offset; }
    public PIDController getDriveController() { return this.driveController; }
    public PIDController getTurnController() { return this.turnController; }
    public Distance getWheelDiameter() { return this.wheelDiameter; }
    public Velocity getMaxVelocity() { return this.maxSpeed; }
    public SwerveModuleSide getModuleSide() { return this.moduleSide; }
    public AngularVelocity getMaxTurnVelocity() { return this.maxTurnSpeed; }
    public RelativeEncoder getDriveEncoder() { return driveMotor.getEncoder(); }

    public boolean isDriveInverted() { return this.driveInverted; }
    public boolean isTurnInverted() { return this.turnInverted; }
}
