package custis.easyabac.demo.model;

import lombok.Getter;

import javax.persistence.*;
import java.util.Objects;

/**
 * Сущность "Клиент" в системе
 */
@Entity
@Table(name = "customer")
@Getter
public class Customer {

    @Id
    @AttributeOverride(name = "value", column = @Column(name = "id"))
    private CustomerId id;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @ManyToOne
    @JoinColumn(name = "branch_id", insertable = false, updatable = false)
    private Branch branch;

    @AttributeOverride(name = "value", column = @Column(name = "branch_id"))
    private BranchId branchId;

    Customer() {
    }

    public Customer(String firstName, String lastName, Branch branch) {
        this.id = CustomerId.newId();
        this.firstName = firstName;
        this.lastName = lastName;
        this.branchId = branch.getId();
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
