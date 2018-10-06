package custis.easyabac.api.model;

import custis.easyabac.api.AuthorizationAction;
import custis.easyabac.api.AuthorizationActionId;

@AuthorizationAction(entity = "order")
public enum OrderAction {
    CREATE("CREATE", "Создать заказ"),
    VIEW("VIEW", "Посмотреть заказ"),
    APPROVE("APPROVE", "Подтвердить заказ"),
    REJECT("REJECT", "Отклонить заказ");

    @AuthorizationActionId
    private final String id;

    private final String description;

    OrderAction(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
}
