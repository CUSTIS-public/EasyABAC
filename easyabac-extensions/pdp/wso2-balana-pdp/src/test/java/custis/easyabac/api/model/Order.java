package custis.easyabac.api.model;

import custis.easyabac.api.attr.annotation.AuthorizationAttribute;
import custis.easyabac.api.attr.annotation.AuthorizationEntity;
import lombok.Getter;

import java.util.Objects;

/**
 * Entity "Order" in the system
 */
@Getter
@AuthorizationEntity(name = "order")
public class Order {

    @AuthorizationAttribute
    private String id;

    @AuthorizationAttribute
    private int amount;

    Order() {

    }

    public Order(String id, int amount) {
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
