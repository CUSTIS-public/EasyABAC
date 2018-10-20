package custis.easyabac.demo.resource.dto;

import custis.easyabac.demo.model.User;
import custis.easyabac.demo.model.UserRole;

public class UserDto {

    private String id;
    private String branchId;
    private String firstName;
    private String lastName;
    private UserRole role;
    private int maxOrderAmount;

    public UserDto(String id, String branchId, String firstName, String lastName, UserRole role, int maxOrderAmount) {
        this.id = id;
        this.branchId = branchId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.maxOrderAmount = maxOrderAmount;
    }

    public static UserDto of(User user) {
        return new UserDto(user.getId(), user.getBranchId(), user.getFirstName(),
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
