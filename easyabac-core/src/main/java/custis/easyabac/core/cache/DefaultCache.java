package custis.easyabac.core.cache;

import custis.easyabac.core.datasource.Param;

import java.util.List;
import java.util.Set;

public class DefaultCache implements Cache {

    public static final Cache INSTANCE = new DefaultCache();

    @Override
    public List<String> get(Set<Param> params, String returnAttributeId) {
        return null;
    }

    @Override
    public void set(Set<Param> params, String returnAttributeId, List<String> value) {

    }

    @Override
    public void set(Set<Param> params, String returnAttributeId, long expire, List<String> value) {

    }
}
