package custis.easyabac.api.core.call.getters;

import custis.easyabac.api.core.PermissionCheckerMetadata;
import custis.easyabac.api.impl.AttributeValueExtractor;
import custis.easyabac.pdp.AuthAttribute;
import custis.easyabac.pdp.RequestId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;

/**
 * Map<Entity, Action>
 * Map<Entity, List<Action>>
 * Map<Action, List<Entity>>
 * Map<Action, Entity>
 */
public class MapAttributeValueGetter extends AbstractRequestGenerator {

    private final boolean firstIsList;
    private final boolean secondIsList;
    private final boolean resourceIsFirst;

    public MapAttributeValueGetter(PermissionCheckerMetadata permissionCheckerInformation, boolean firstIsList, boolean secondIsList, boolean resourceIsFirst) {
        super(permissionCheckerInformation);
        this.firstIsList = firstIsList;
        this.secondIsList = secondIsList;
        this.resourceIsFirst = resourceIsFirst;
    }

    /**
     * FIXME type-checking в момент вызова.... Можно попытаться что-то придумать
     * @param objects
     * @return
     */
    @Override
    public Map<RequestId, List<AuthAttribute>> generate(List<Object> objects) {
        HashMap<RequestId, List<AuthAttribute>> result = new HashMap<>();
        Map<?, ?> object = (Map) objects.get(0);
        for (Map.Entry<?, ?> entry : object.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();

            if (firstIsList && secondIsList) {
                List<Object> first = (List<Object>) key;
                List<Object> second = (List<Object>) value;

                doResult(first, second, resourceIsFirst, result);

            } else if (firstIsList) {
                List<Object> first = (List<Object>) key;
                doResult(first, singletonList(value), resourceIsFirst, result);
            } else if (secondIsList) {
                List<Object> second = (List<Object>) value;
                doResult(singletonList(key), second, resourceIsFirst, result);
            } else {
                doResult(singletonList(key), singletonList(value), resourceIsFirst, result);
            }
        }

        return result;
    }

    private void doResult(List<Object> first, List<Object> second, boolean resourceIsFirst, Map<RequestId, List<AuthAttribute>> result){
        for (Object o : first) {
            for (Object o1 : second) {
                result.put(RequestId.newRandom(), AttributeValueExtractor.extract(resourceIsFirst ? o : o1, resourceIsFirst ? o1 : o));
            }
        }
    }
}
