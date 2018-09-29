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

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class TwoArgumentsValueGetter extends AbstractRequestGenerator {

    private final boolean firstIsList;
    private final boolean secondIsList;
    private final boolean resourceIsFirst;
    private List<Object> customActions = emptyList();

    public TwoArgumentsValueGetter(PermissionCheckerMetadata permissionCheckerInformation, boolean firstIsList, boolean secondIsList, boolean resourceIsFirst) {
        super(permissionCheckerInformation);
        this.firstIsList = firstIsList;
        this.secondIsList = secondIsList;
        this.resourceIsFirst = resourceIsFirst;
    }

    public TwoArgumentsValueGetter(PermissionCheckerInformation permissionCheckerInformation, List<Object> customActions) {
        this(permissionCheckerInformation, false, true, true);
        this.customActions = customActions;
    }

    @Override
    public Map<RequestId, List<AuthAttribute>> generate(List<Object> objects) {
        Map<RequestId, List<AuthAttribute>> result = new HashMap<>();
        if (customActions.isEmpty()) {
            // generic
            if (firstIsList && secondIsList) {
                List<Object> first = (List<Object>) objects.get(0);
                List<Object> second = (List<Object>) objects.get(1);

                doResult(first, second, resourceIsFirst, result);

            } else if (firstIsList) {
                List<Object> first = (List<Object>) objects.get(0);
                doResult(first, singletonList(objects.get(1)), resourceIsFirst, result);
            } else if (secondIsList) {
                List<Object> second = (List<Object>) objects.get(1);
                doResult(singletonList(objects.get(0)), second, resourceIsFirst, result);
            } else {
                doResult(singletonList(objects.get(0)), singletonList(objects.get(1)), resourceIsFirst, result);
            }
        } else {
            doResult(Collections.singletonList(objects.get(0)), customActions, true, result);
        }


        return result;
    }

    private void doResult(List<Object> first, List<Object> second, boolean resourceIsFirst, Map<RequestId, List<AuthAttribute>> result){
        for (Object o : first) {
            for (Object o1 : second) {
                RequestId requestId = RequestId.newRandom();
                result.put(requestId, AttributeValueExtractor.extract(resourceIsFirst ? o : o1, resourceIsFirst ? o1 : o));
            }
        }
    }

}
