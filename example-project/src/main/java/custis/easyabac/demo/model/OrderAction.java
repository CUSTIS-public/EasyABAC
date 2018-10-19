package custis.easyabac.demo.model;

public enum OrderAction {
    VIEW("View order details"),
    CREATE("Create order"),
    APPROVE("Approve order"),
    REJECT("Reject oreder");

    private String title;

    OrderAction(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
