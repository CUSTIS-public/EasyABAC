package custis.easyabac.core.cache;

import custis.easyabac.core.datasource.Param;

import java.util.List;
import java.util.Set;

public interface Cache {

    List<String> get(Set<Param> params, String returnAttributeId);

    void set(Set<Param> params, String returnAttributeId, List<String> value);

    void set(Set<Param> params, String returnAttributeId, long expire, List<String> value);

}
