package frc.robot.util.math;

public class SwerveVelocities {
    private Velocity forwardVel;
    private Velocity sideVel;
    private AngularVelocity twistVel;

    public SwerveVelocities setForwardVelocity(Velocity velocity) {
        this.forwardVel = velocity;
        return this;
    }

    public SwerveVelocities setSideVelocity(Velocity velocity) {
        this.sideVel = velocity;
        return this;
    }

    public SwerveVelocities setTwistVelocity(AngularVelocity velocity) {
        this.twistVel = velocity;
        return this;
    }

    public SwerveVelocities(Velocity xVel, Velocity yVel, AngularVelocity twistVel) {
        this.forwardVel = xVel;
        this.sideVel = yVel;
        this.twistVel = twistVel;
    }

    public Velocity getForwardVelocity() { return this.forwardVel; }
    public Velocity getSideVelocity() { return this.sideVel; }
    public AngularVelocity getTwistVelocity() { return this.twistVel; }

    public static SwerveVelocities fromVelocities(Velocity xVel, Velocity yVel, AngularVelocity twistVel) {
        return new SwerveVelocities(xVel, yVel, twistVel);
    }
}
