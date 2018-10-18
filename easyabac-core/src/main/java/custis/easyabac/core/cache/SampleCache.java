package custis.easyabac.core.cache;

import custis.easyabac.core.datasource.Param;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.stream.Collectors;

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
        List<Param> sortedParams = params.stream().sorted(Comparator.comparing(Param::getName)).collect(Collectors.toList());

        StringBuilder keyBuilder = new StringBuilder();
        for (Param param : sortedParams) {
            keyBuilder.append(param.getName()).append(":").append(param.getValue()).append(":");
        }

        keyBuilder.append(returnAttributeId);
        return keyBuilder.toString();
    }
}
