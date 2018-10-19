package generation.model;

import java.util.Arrays;
import java.util.Optional;

public enum OrderAction {

    VIEW("View order details"),
    CREATE("Create new order"),
    APPROVE("Approve existing order"),
    REJECT("Reject existing order");

    private String title;

    OrderAction(String title) {
        this.title = title;
    }

    // Simple getters and setters
    public String getTitle() {
        return title;
    }

    public static OrderAction byId(String id) {
        Optional<OrderAction> optional = Arrays.asList(values()).stream()
                .filter(
                        action -> action.name().toLowerCase().equals(id)
                ).findFirst();
        if (optional.isPresent()) {
            return optional.get();
        }
        throw new IllegalArgumentException(id);
    }
}