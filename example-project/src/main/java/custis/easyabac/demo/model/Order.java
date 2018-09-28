package custis.easyabac.demo.model;

import custis.easyabac.api.AuthorizationAttribute;
import custis.easyabac.api.AuthorizationEntity;
import lombok.Getter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Сущность "Заказ" в системе
 */
@Entity
@Getter
@Table(name = "order")
@AuthorizationEntity(name = "order")
public class Order {

    @Id
    @AttributeOverride(name = "value", column = @Column(name = "id"))
    @AuthorizationAttribute(id = "id")
    private OrderId id;

    @AttributeOverride(name = "value", column = @Column(name = "customer_id"))
    private CustomerId customerId;

    @ManyToOne
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private Customer customer;

    @Column(name = "amount")
    @AuthorizationAttribute
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "branch_id", insertable = false, updatable = false)
    private Branch branch;

    @AttributeOverride(name = "value", column = @Column(name = "branch_id"))
    private BranchId branchId;

    @Enumerated(EnumType.STRING)
    private OrderState state;

    Order() {

    }

    public Order(Customer customer, Branch branch, BigDecimal amount) {
        this.id = OrderId.newId();
        this.customerId = customer.getId();
        this.branchId = branch.getId();
        this.amount = amount;
        this.state = OrderState.NEW;
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

    public void approve() {
        this.state = OrderState.APPROVED;
    }

    public void reject() {
        this.state = OrderState.REJECTED;
    }
}
