package custis.easyabac.demo.model;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(String message) {
        super(message);
    }

    public OrderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrderNotFoundException(Throwable cause) {
        super(cause);
    }

    public OrderNotFoundException(OrderId id) {
        this("Не найден заказ с ID[" + id.toString() + "]");
    }
}
