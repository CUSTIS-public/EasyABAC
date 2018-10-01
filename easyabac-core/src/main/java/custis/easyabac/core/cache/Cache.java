package custis.easyabac.core.cache;

import custis.easyabac.core.init.Param;
import custis.easyabac.core.model.abac.attribute.Attribute;

import java.util.List;
import java.util.Set;

public interface Cache {

    List<String> get(Set<Param> params, Attribute requiredAttribute);

    void set(Set<Param> params, Attribute requiredAttribute, List<String> value);

    void set(Set<Param> params, Attribute requiredAttribute, long expire, List<String> value);

}
