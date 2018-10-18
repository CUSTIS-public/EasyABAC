package custis.easyabac.demo.rest.representation;

import custis.easyabac.demo.model.User;
import custis.easyabac.demo.model.UserRole;

public class UserRepresentation {

    private String id;
    private String branchId;
    private String firstName;
    private String lastName;
    private UserRole role;
    private int maxOrderAmount;

    public UserRepresentation(String id, String branchId, String firstName, String lastName, UserRole role, int maxOrderAmount) {
        this.id = id;
        this.branchId = branchId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.maxOrderAmount = maxOrderAmount;
    }

    public static UserRepresentation of(User user) {
        return new UserRepresentation(user.getId(), user.getBranchId(), user.getFirstName(),
                user.getLastName(), user.getRole(), user.getMaxOrderAmount());
    }

    public String getId() {
        return id;
    }

    public String getBranchId() {
        return branchId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public UserRole getRole() {
        return role;
    }

    public int getMaxOrderAmount() {
        return maxOrderAmount;
    }
}
