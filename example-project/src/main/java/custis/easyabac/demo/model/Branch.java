package custis.easyabac.demo.model;

import lombok.Getter;

import javax.persistence.*;
import java.util.Objects;

/**
 * Сущность "Филиал" в системе
 */
@Entity
@Table(name = "branch")
@Getter
public class Branch {

    @Id
    @AttributeOverride(name = "value", column = @Column(name = "id"))
    private BranchId id;

    @Column
    private String name;

    Branch() {

    }

    public Branch(String name) {
        this.id = BranchId.newId();
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Branch branch = (Branch) o;
        return Objects.equals(id, branch.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
