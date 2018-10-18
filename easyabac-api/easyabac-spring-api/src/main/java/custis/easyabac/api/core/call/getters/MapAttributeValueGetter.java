package custis.easyabac.api.core.call.getters;

import custis.easyabac.api.core.PermissionCheckerMetadata;
import custis.easyabac.api.utils.ResourceActionPair;
import custis.easyabac.core.pdp.AuthAttribute;
import custis.easyabac.core.pdp.RequestId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * @param objects
     * @return
     */
    @Override
    public RequestWrapper generate(List<Object> objects) {
        HashMap<RequestId, List<AuthAttribute>> requests = new HashMap<>();
        Map<RequestId, ResourceActionPair> mapping = new HashMap<>();

        Map<?, ?> object = (Map) objects.get(0);
        for (Map.Entry<?, ?> entry : object.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();

            List<Object> first = wrapIfNeeded(key, firstIsList);
            List<Object> second = wrapIfNeeded(value, secondIsList);

            doResult(first, second, resourceIsFirst, requests, mapping);
        }

        return new RequestWrapper(requests, mapping);
    }
}
