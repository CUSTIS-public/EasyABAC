package custis.easyabac.api.core.call.getters;

import custis.easyabac.api.core.PermissionCheckerMetadata;
import custis.easyabac.api.impl.AttributeValueExtractor;
import custis.easyabac.pdp.AuthAttribute;
import custis.easyabac.pdp.RequestId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Map<Entity, Action>
 * Map<Entity, List<Action>>
 * Map<Action, List<Entity>>
 * Map<Action, Entity>
 */
public class MapAttributeValueGetter extends AbstractAttributesValuesGetter {

    public MapAttributeValueGetter(PermissionCheckerMetadata permissionCheckerInformation) {
        super(permissionCheckerInformation);
    }

    /**
     * FIXME type-checking в момент вызова.... Можно попытаться что-то придумать
     * @param objects
     * @return
     */
    @Override
    public Map<RequestId, List<AuthAttribute>> getAttributes(Object[] objects) {
        HashMap<RequestId, List<AuthAttribute>> result = new HashMap<>();
        Map<?, ?> object = (Map) objects[0];
        for (Map.Entry<?, ?> entry : object.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();

            if (resourceType.isAssignableFrom(key.getClass())) {
                // simple resource
                if (actionType.isAssignableFrom(value.getClass())) {
                    // single action
                    result.put(RequestId.newRandom(), AttributeValueExtractor.collectAttributes(key, value));
                } else {
                    // list of actions
                    for (Object o : ((List) value)) {
                        result.put(RequestId.newRandom(), AttributeValueExtractor.collectAttributes(key, o));
                    }
                }
            } else if (actionType.isAssignableFrom(key.getClass())) {
                // simple action
                if (resourceType.isAssignableFrom(value.getClass())) {
                    // single resource
                    result.put(RequestId.newRandom(), AttributeValueExtractor.collectAttributes(value, key));
                } else {
                    // list of resources
                    for (Object o : ((List) value)) {
                        result.put(RequestId.newRandom(), AttributeValueExtractor.collectAttributes(o, key));
                    }
                }
            }
        }

        return result;
    }
}
