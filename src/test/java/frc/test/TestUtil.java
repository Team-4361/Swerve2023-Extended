package frc.test;


/**
 * This {@link TestUtil} class contains Utilities which benefit JUnit Testing.
 */
public class TestUtil {
    public interface OperationCallback {
        void call();
    }

    public static void loopAndDebug(String name, int count, OperationCallback callback) {
        for (int i=0; i<count; i++) {
            //AlertManager.debug("Processing " + name + " | (" + i + "/" + count + ")");
            callback.call();
        }
    }
}
