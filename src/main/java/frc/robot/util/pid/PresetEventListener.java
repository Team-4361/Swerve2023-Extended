package frc.robot.util.pid;

public interface PresetEventListener<T> {
    void onPresetAdjust(T value);
}