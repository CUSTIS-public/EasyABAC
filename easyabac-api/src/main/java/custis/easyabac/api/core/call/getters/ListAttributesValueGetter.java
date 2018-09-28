package custis.easyabac.api.core.call.getters;

import custis.easyabac.api.core.PermissionCheckerInformation;
import custis.easyabac.api.core.PermissionCheckerMetadata;
import custis.easyabac.api.impl.AttributeValueExtractor;
import custis.easyabac.pdp.AuthAttribute;
import custis.easyabac.pdp.RequestId;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.*;

public class ListAttributesValueGetter extends AbstractAttributesValuesGetter {

    private final boolean firstIsList;
    private final boolean secondIsList;
    private final boolean resourceIsFirst;
    private List<Object> customActions = emptyList();

    public ListAttributesValueGetter(PermissionCheckerMetadata permissionCheckerInformation, boolean firstIsList, boolean secondIsList, boolean resourceIsFirst) {
        super(permissionCheckerInformation);
        this.firstIsList = firstIsList;
        this.secondIsList = secondIsList;
        this.resourceIsFirst = resourceIsFirst;
    }

    public ListAttributesValueGetter(PermissionCheckerInformation permissionCheckerInformation, List<Object> customActions) {
        this(permissionCheckerInformation, false, true, true);
        this.customActions = customActions;
    }

    public Map<RequestId, List<AuthAttribute>> getAttributes(Object[] objects) {
        Map<RequestId, List<AuthAttribute>> result = new HashMap<>();
        if (customActions.isEmpty()) {
            // generic
            if (firstIsList && secondIsList) {
                List<Object> first = (List<Object>) objects[0];
                List<Object> second = (List<Object>) objects[1];

                doResult(first, second, resourceIsFirst, result);

            } else if (firstIsList) {
                List<Object> first = (List<Object>) objects[0];
                doResult(first, singletonList(objects[1]), resourceIsFirst, result);
            } else if (secondIsList) {
                List<Object> second = (List<Object>) objects[1];
                doResult(singletonList(objects[0]), second, resourceIsFirst, result);
            }
        } else {
            doResult(Collections.singletonList(objects[0]), customActions, true, result);
        }


        return result;
    }

    private void doResult(List<Object> first, List<Object> second, boolean resourceIsFirst, Map<RequestId, List<AuthAttribute>> result){
        for (Object o : first) {
            for (Object o1 : second) {
                result.put(RequestId.newRandom(), AttributeValueExtractor.collectAttributes(resourceIsFirst ? o : o1, resourceIsFirst ? o1 : o));
            }
        }
    }

}
