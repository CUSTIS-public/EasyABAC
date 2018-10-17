package custis.easyabac.api.core.call.getters;

import custis.easyabac.api.core.PermissionCheckerMetadata;
import custis.easyabac.api.utils.ResourceActionPair;
import custis.easyabac.pdp.AuthAttribute;
import custis.easyabac.pdp.RequestId;

import java.util.List;
import java.util.Map;

import static custis.easyabac.api.impl.AttributeValueExtractor.extract;
import static custis.easyabac.api.utils.ResourceActionPair.of;
import static custis.easyabac.pdp.RequestId.newRandom;
import static java.util.Collections.singletonList;

public abstract class AbstractRequestGenerator implements RequestGenerator {

    protected final Class<?> resourceType;
    protected final Class<?> actionType;


    protected AbstractRequestGenerator(PermissionCheckerMetadata metadata) {
        this.resourceType = metadata.getResourceType();
        this.actionType = metadata.getActionType();
    }

    static List<Object> wrapIfNeeded(Object object, boolean isList) {
        if (isList) {
            return (List<Object>) object;
        }
        return singletonList(object);
    }

    static void doResult(List<Object> first, List<Object> second, boolean resourceIsFirst, Map<RequestId, List<AuthAttribute>> result, Map<RequestId, ResourceActionPair> mapping){
        for (Object o : first) {
            for (Object o1 : second) {
                Object resource = resourceIsFirst ? o : o1;
                Object action = resourceIsFirst ? o1 : o;

                RequestId requestId = newRandom();
                result.put(requestId, extract(resource, action));
                mapping.put(requestId, of(resource, action));
            }
        }
    }

}
