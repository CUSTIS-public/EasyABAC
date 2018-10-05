package custis.easyabac.autogen.model;

import custis.easyabac.api.AuthorizationAttribute;
import custis.easyabac.api.AuthorizationEntity;

@AuthorizationEntity(name = "order")
public class Order {

    /**
     * Authorization attribute "ИД заказа"
     */
    @AuthorizationAttribute(id = "id")
    private String id;

    /**
     * Authorization attribute "Сумма заказа"
     */
    @AuthorizationAttribute(id = "amount")
    private String amount;

    /**
     * Authorization attribute "ИД филиала"
     */
    @AuthorizationAttribute(id = "branchId")
    private String branchId;

    /**
     * Authorization attribute "ИД клиента"
     */
    @AuthorizationAttribute(id = "customerId")
    private String customerId;

    public Order() {
    }

    public Order(String id, String amount, String branchId, String customerId) {
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

    public String getAmount() {
        return this.amount;
    }

    public void setAmount(String amount) {
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
