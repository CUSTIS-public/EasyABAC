package custis.easyabac.autogen.model;

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
    @AuthorizationAttribute(id = "roles")
    private String roles;

    /**
     * Authorization attribute "ИД филиала"
     */
    @AuthorizationAttribute(id = "branchId")
    private String branchId;

    public Subject() {
    }

    public Subject(String id, String roles, String branchId) {
        this.id = id;
        this.roles = roles;
        this.branchId = branchId;
    }

    // Simple getters and setters
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoles() {
        return this.roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getBranchId() {
        return this.branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }
}
