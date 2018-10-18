package custis.easyabac.demo.model;

import javax.persistence.*;
import java.util.Objects;

/**
 * Entity "Client" in system
 */
@Entity
@Table(name = "t_customer")
public class Customer {

    @Id
    private String id;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @AttributeOverride(name = "value", column = @Column(name = "branch_id"))
    private String branchId;

    Customer() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
