package custis.easyabac.generation.test.model;

import custis.easyabac.api.AuthorizationAttribute;
import custis.easyabac.api.AuthorizationEntity;

@AuthorizationEntity(name = "Subject")
public class Subject {

    /**
     * Authorization attribute "null"
     */
    @AuthorizationAttribute(id = "id")
    private String id;

    /**
     * Authorization attribute "категории доступные субъекту"
     */
    @AuthorizationAttribute(id = "allowed-categories")
    private String allowed_categories;

    public Subject() {
    }

    public Subject(String id, String allowed_categories) {
        this.id = id;
        this.allowed_categories = allowed_categories;
    }

    // Simple getters and setters
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAllowed_categories() {
        return this.allowed_categories;
    }

    public void setAllowed_categories(String allowed_categories) {
        this.allowed_categories = allowed_categories;
    }
}
