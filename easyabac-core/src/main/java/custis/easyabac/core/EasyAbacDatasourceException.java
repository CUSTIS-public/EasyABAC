package custis.easyabac.core;

public class EasyAbacDatasourceException extends RuntimeException {
    public EasyAbacDatasourceException(String message) {
        super(message);
    }

    public EasyAbacDatasourceException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
