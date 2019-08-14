package custis.easyabac.demo.model;

public enum OrderAction {
    EDIT("Edit order details"),
    VIEW("View order details"),
    CREATE("Create order"),
    APPROVE("Approve order"),
    REJECT("Reject order");

    private String title;

    OrderAction(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
