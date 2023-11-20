package frc.robot.util.joystick;

public interface XboxControllerIO {
    class XboxControllerIOInputs {
        public boolean leftBumper;
        public boolean rightBumper;
        public boolean leftStick;
        public boolean rightStick;
        public boolean aBtn;
        public boolean bBtn;
        public boolean xBtn;
        public boolean yBtn;
        public boolean startBtn;
        public boolean backBtn;

        public double ltAxis;
        public double rtAxis;
        public double lxAxis;
        public double lyAxis;
        public double rxAxis;
        public double ryAxis;
    }

    void updateInputs(XboxControllerIOInputs inputs);
}
