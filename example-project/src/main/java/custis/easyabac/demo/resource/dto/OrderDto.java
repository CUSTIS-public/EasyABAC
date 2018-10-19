package custis.easyabac.demo.resource.dto;

import custis.easyabac.demo.model.Order;
import custis.easyabac.demo.model.OrderAction;
import custis.easyabac.demo.model.OrderState;

import java.math.BigDecimal;
import java.util.List;

public class OrderDto {

    private String id;
    private CustomerDto customer;
    private BigDecimal amount;
    private String branchId;
    private OrderState state;
    private List<OrderAction> actions;


    public OrderDto(String id, CustomerDto customer, BigDecimal amount, String branchId, OrderState state, List<OrderAction> actions) {
        this.id = id;
        this.customer = customer;
        this.amount = amount;
        this.branchId = branchId;
        this.state = state;
        this.actions = actions;
    }

    public static OrderDto of(Order order, List<OrderAction> actions) {
        return new OrderDto(order.getId(), CustomerDto.of(order.getCustomer()), order.getAmount(), order.getBranchId(), order.getState(), actions);
    }

    public String getId() {
        return id;
    }

    public CustomerDto getCustomer() {
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

    public List<OrderAction> getActions() {
        return actions;
    }
}
