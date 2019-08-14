package generation.model;

import custis.easyabac.api.attr.annotation.AuthorizationAttribute;
import custis.easyabac.api.attr.annotation.AuthorizationEntity;

@AuthorizationEntity(name = "order")
public class Order {

    /**
     * Authorization attribute "Order ID"
     */
    @AuthorizationAttribute(id = "id")
    private String id;

    /**
     * Authorization attribute "Order Amount"
     */
    @AuthorizationAttribute(id = "amount")
    private Integer amount;

    /**
     * Authorization attribute "Branch ID"
     */
    @AuthorizationAttribute(id = "branchId")
    private String branchId;

    /**
     * Authorization attribute "Client ID"
     */
    @AuthorizationAttribute(id = "customerId")
    private String customerId;

    public Order() {
    }

    public Order(String id, Integer amount, String branchId, String customerId) {
        this.id = id;
        this.amount = amount;
        this.branchId = branchId;
        this.customerId = customerId;
    }

    // Simple getters and setters
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getAmount() {
        return this.amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getBranchId() {
        return this.branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
