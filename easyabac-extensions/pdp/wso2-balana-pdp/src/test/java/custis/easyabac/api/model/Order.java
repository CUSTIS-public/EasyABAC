package custis.easyabac.api.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Сущность "Заказ" в системе
 */
@Getter
public class Order {

    private String id;

    private BigDecimal amount;

    Order() {

    }

    public Order(String id, BigDecimal amount) {
        this.id = id;
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", amount=" + amount +
                '}';
    }
}
