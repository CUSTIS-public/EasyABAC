package custis.easyabac.model;

public class EasyAbacInitException extends Exception {
    public EasyAbacInitException(String message) {
        super(message);
    }

    public EasyAbacInitException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
