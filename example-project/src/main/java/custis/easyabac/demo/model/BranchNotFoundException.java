package custis.easyabac.demo.model;

public class BranchNotFoundException extends RuntimeException {

    public BranchNotFoundException(String message) {
        super(message);
    }

    public BranchNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public BranchNotFoundException(Throwable cause) {
        super(cause);
    }

    public BranchNotFoundException(BranchId id) {
        this("Не найден филиал с ID[" + id.toString() + "]");
    }
}
