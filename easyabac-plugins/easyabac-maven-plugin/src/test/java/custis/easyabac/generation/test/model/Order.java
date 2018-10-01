package custis.easyabac.generation.test.model;

import custis.easyabac.api.AuthorizationAttribute;
import custis.easyabac.api.AuthorizationEntity;

@AuthorizationEntity(name = "Order")
public class Order {

    /**
     * Authorization attribute "Идентификатор"
     */
    @AuthorizationAttribute(id = "id")
    private String id;

    /**
     * Authorization attribute "Название"
     */
    @AuthorizationAttribute(id = "name")
    private String name;

    public Order() {
    }

    public Order(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // Simple getters and setters
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
