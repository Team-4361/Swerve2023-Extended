package frc.robot.util.io;

public enum AlertType {
    WARNING("warnings"),
    ERROR("errors");

    private final String prefix;

    AlertType(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return this.prefix;
    }
}
