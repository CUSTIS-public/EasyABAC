package custis.easyabac.core.cache;

import custis.easyabac.core.init.Param;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SampleCache implements Cache {

    private final static Log log = LogFactory.getLog(SampleCache.class);

    private final Map<String, List<String>> cachedData = new HashMap<>();

    @Override
    public List<String> get(Set<Param> params, String returnAttributeId) {
        String key = makeKey(params, returnAttributeId);

        List<String> value = cachedData.get(key);
//        log.debug("get key " + key + " value " + value.toString());
        return value;
    }

    @Override
    public void set(Set<Param> params, String returnAttributeId, List<String> value) {
        String key = makeKey(params, returnAttributeId);
//        log.debug("set key " + key + " value " + value.toString());
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
