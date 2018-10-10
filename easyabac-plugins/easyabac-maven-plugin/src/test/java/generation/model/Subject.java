package generation.model;

import custis.easyabac.api.AuthorizationAttribute;
import custis.easyabac.api.AuthorizationEntity;

@AuthorizationEntity(name = "subject")
public class Subject {

    /**
     * Authorization attribute "null"
     */
    @AuthorizationAttribute(id = "id")
    private String id;

    /**
     * Authorization attribute "Роль сотрудника "Менеджер" / Операционист"
     */
    @AuthorizationAttribute(id = "role")
    private String role;

    /**
     * Authorization attribute "ИД филиала"
     */
    @AuthorizationAttribute(id = "branchId")
    private String branchId;

    /**
     * Authorization attribute "Максимальный заказ"
     */
    @AuthorizationAttribute(id = "maxOrderAmount")
    private int maxOrderAmount;

    public Subject() {
    }

    public Subject(String id, String role, String branchId, int maxOrderAmount) {
        this.id = id;
        this.role = role;
        this.branchId = branchId;
        this.maxOrderAmount = maxOrderAmount;
    }

    // Simple getters and setters
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getBranchId() {
        return this.branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public int getMaxOrderAmount() {
        return this.maxOrderAmount;
    }

    public void setMaxOrderAmount(int maxOrderAmount) {
        this.maxOrderAmount = maxOrderAmount;
    }
}
