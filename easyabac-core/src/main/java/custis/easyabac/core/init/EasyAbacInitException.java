package custis.easyabac.core.init;

public class EasyAbacInitException extends Exception {
    public EasyAbacInitException(String message) {
        super(message);
    }

    public EasyAbacInitException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
