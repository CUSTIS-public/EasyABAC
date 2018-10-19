package custis.easyabac.demo.resource.dto;

import custis.easyabac.demo.model.Order;
import custis.easyabac.demo.model.OrderState;

import java.math.BigDecimal;

public class OrderDto {

    private String id;
    private CustomerDto customer;
    private BigDecimal amount;
    private String branchId;
    private OrderState state;


    public OrderDto(String id, CustomerDto customer, BigDecimal amount, String branchId, OrderState state) {
        this.id = id;
        this.customer = customer;
        this.amount = amount;
        this.branchId = branchId;
        this.state = state;
    }

    public static OrderDto of(Order order) {
        return new OrderDto(order.getId(), CustomerDto.of(order.getCustomer()), order.getAmount(), order.getBranchId(), order.getState());
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

}
