package custis.easyabac.demo.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Сущность "Заказ" в системе
 */
@Entity
@Table(name = "t_order")
public class Order {

    @Id
    private String id;

    @Column(name = "customer_id")
    private String customerId;

    @ManyToOne
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private Customer customer;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column
    private String branchId;

    @Enumerated(EnumType.STRING)
    private OrderState state;

    Order() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public OrderState getState() {
        return state;
    }

    public void setState(OrderState state) {
        this.state = state;
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
