package custis.easyabac.core.cache;

import custis.easyabac.core.init.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SampleCache implements Cache {

    private final Map<String, List<String>> cachedData = new HashMap<>();

    @Override
    public List<String> get(Set<Param> params, String returnAttributeId) {
        String key = makeKey(params, returnAttributeId);

        return cachedData.get(key);
    }

    @Override
    public void set(Set<Param> params, String returnAttributeId, List<String> value) {
        String key = makeKey(params, returnAttributeId);

        cachedData.put(key, value);
    }

    @Override
    public void set(Set<Param> params, String returnAttributeId, long expire, List<String> value) {
        set(params, returnAttributeId, value);
    }


    private String makeKey(Set<Param> params, String returnAttributeId) {
        StringBuilder keyBuilder = new StringBuilder();
        for (Param param : params) {
            keyBuilder.append(param.getAttributeParamId()).append(":").append(param.getValue()).append(":");
        }

        keyBuilder.append(returnAttributeId);
        return keyBuilder.toString();
    }
}
