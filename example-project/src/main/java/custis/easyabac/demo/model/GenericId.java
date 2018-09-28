package custis.easyabac.demo.model;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
public class GenericId implements Serializable {

    protected String value;

    public GenericId(String value) {
        this.value = value;
    }

    public GenericId() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GenericId genericId = (GenericId) o;

        return value != null ? value.equals(genericId.value) : genericId.value == null;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString() {
        return value;
    }
}
