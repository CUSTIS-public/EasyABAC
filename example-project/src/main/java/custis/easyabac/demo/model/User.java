package custis.easyabac.demo.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * User aka User
 */
@Entity
@Table(name = "t_user")
public class User {

    @Id
    private String id;

    @ElementCollection
    @CollectionTable(name = "T_USER_BRANCH", joinColumns = @JoinColumn(name = "USER_ID"))
    private Set<String> branchIds = new HashSet<>();

    private String firstName;

    private String lastName;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private int maxOrderAmount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<String> getBranchIds() {
        return branchIds;
    }

    public void setBranchIds(Set<String> branchIds) {
        this.branchIds = branchIds;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public int getMaxOrderAmount() {
        return maxOrderAmount;
    }

    public void setMaxOrderAmount(int maxOrderAmount) {
        this.maxOrderAmount = maxOrderAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
