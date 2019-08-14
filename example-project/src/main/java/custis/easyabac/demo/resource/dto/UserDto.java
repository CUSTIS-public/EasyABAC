package custis.easyabac.demo.resource.dto;

import custis.easyabac.demo.model.User;
import custis.easyabac.demo.model.UserRole;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class UserDto {

    private String id;
    private Set<String> branchIds;
    private String firstName;
    private String lastName;
    private UserRole role;
    private int maxOrderAmount;

    public UserDto(String id, Set<String> branchId, String firstName, String lastName, UserRole role, int maxOrderAmount) {
        this.id = id;
        this.branchIds = branchId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.maxOrderAmount = maxOrderAmount;
    }

    public static UserDto of(User user) {
        return new UserDto(user.getId(), user.getBranchIds(), user.getFirstName(),
                user.getLastName(), user.getRole(), user.getMaxOrderAmount());
    }

    public String getId() {
        return id;
    }

    public String getBranchId() {
        return Arrays.toString(branchIds.toArray());
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
