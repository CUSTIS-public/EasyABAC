package custis.easyabac.demo.rest.representation;

import custis.easyabac.demo.model.Order;
import custis.easyabac.demo.model.OrderState;

import java.math.BigDecimal;

public class OrderRepresentation {

    private String id;
    private CustomerRepresentation customer;
    private BigDecimal amount;
    private String branchId;
    private OrderState state;

    public OrderRepresentation(String id, CustomerRepresentation customer, BigDecimal amount, String branchId, OrderState state) {
        this.id = id;
        this.customer = customer;
        this.amount = amount;
        this.branchId = branchId;
        this.state = state;
    }

    public static OrderRepresentation of(Order order) {
        return new OrderRepresentation(order.getId(), CustomerRepresentation.of(order.getCustomer()), order.getAmount(), order.getBranchId(), order.getState());
    }

    public String getId() {
        return id;
    }

    public CustomerRepresentation getCustomer() {
        return customer;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getBranchId() {
        return branchId;
    }

    public OrderState getState() {
        return state;
    }
}
