package frc.robot.util.log;

public enum AlertType {
    WARNING("[WARN]"),
    ERROR("[ERR]");

    private final String prefix;

    AlertType(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return this.prefix;
    }
}
