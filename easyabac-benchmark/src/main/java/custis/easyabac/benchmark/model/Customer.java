package custis.easyabac.benchmark.model;

import custis.easyabac.api.attr.annotation.AuthorizationAttribute;
import custis.easyabac.api.attr.annotation.AuthorizationEntity;

@AuthorizationEntity(name = "customer")
public class Customer {

    /**
     * Authorization attribute "Client ID"
     */
    @AuthorizationAttribute(id = "id")
    private String id;

    /**
     * Authorization attribute "Branch ID"
     */
    @AuthorizationAttribute(id = "branchId")
    private String branchId;

    public Customer() {
    }

    public Customer(String id, String branchId) {
        this.id = id;
        this.branchId = branchId;
    }

    // Simple getters and setters
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBranchId() {
        return this.branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }
}
