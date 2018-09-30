package custis.easyabac.api.model;

import custis.easyabac.api.AuthorizationAttribute;
import custis.easyabac.api.AuthorizationEntity;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Сущность "Заказ" в системе
 */
@Getter
@AuthorizationEntity(name = "order")
public class Order {

    @AuthorizationAttribute(id = "id")
    private String id;

    @AuthorizationAttribute
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
