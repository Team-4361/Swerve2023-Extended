package frc.robot.swerve.module;

public enum SwerveModuleSide {
    NONE,
    FRONT_LEFT,
    FRONT_RIGHT,
    BACK_LEFT,
    BACK_RIGHT;
    @Override
    public String toString() {
        switch (this) {
            case FRONT_LEFT: return "FL";
            case FRONT_RIGHT: return"FR";
            case BACK_LEFT: return "BL";
            case BACK_RIGHT: return "BR";
            default: return "NONE";
        }
    }
}
