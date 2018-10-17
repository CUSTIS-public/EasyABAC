package custis.easyabac.api.model;

public enum OrderAction {
    CREATE("CREATE", "Создать заказ"),
    VIEW("VIEW", "Посмотреть заказ"),
    APPROVE("APPROVE", "Подтвердить заказ"),
    REJECT("REJECT", "Отклонить заказ");

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
