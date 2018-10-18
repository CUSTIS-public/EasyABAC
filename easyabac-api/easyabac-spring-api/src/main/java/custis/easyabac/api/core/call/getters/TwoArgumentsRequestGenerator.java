package custis.easyabac.api.core.call.getters;

import custis.easyabac.api.core.PermissionCheckerInformation;
import custis.easyabac.api.core.PermissionCheckerMetadata;
import custis.easyabac.api.utils.ResourceActionPair;
import custis.easyabac.core.pdp.AuthAttribute;
import custis.easyabac.core.pdp.RequestId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

public class TwoArgumentsRequestGenerator extends AbstractRequestGenerator {

    private final boolean firstIsList;
    private final boolean secondIsList;
    private final boolean resourceIsFirst;
    private List<Object> customActions = emptyList();

    public TwoArgumentsRequestGenerator(PermissionCheckerMetadata permissionCheckerInformation, boolean firstIsList, boolean secondIsList, boolean resourceIsFirst) {
        super(permissionCheckerInformation);
        this.firstIsList = firstIsList;
        this.secondIsList = secondIsList;
        this.resourceIsFirst = resourceIsFirst;
    }

    public TwoArgumentsRequestGenerator(PermissionCheckerInformation permissionCheckerInformation, List<Object> customActions) {
        this(permissionCheckerInformation, false, true, true);
        this.customActions = customActions;
    }

    @Override
    public RequestWrapper generate(List<Object> objects) {
        Map<RequestId, List<AuthAttribute>> requests = new HashMap<>();
        Map<RequestId, ResourceActionPair> mapping = new HashMap<>();

        List<Object> first = wrapIfNeeded(objects.get(0), firstIsList);
        List<Object> second = customActions.isEmpty() ? wrapIfNeeded(objects.get(1), secondIsList) : customActions;
        doResult(first, second, resourceIsFirst, requests, mapping);

        return new RequestWrapper(requests, mapping);
    }

}
