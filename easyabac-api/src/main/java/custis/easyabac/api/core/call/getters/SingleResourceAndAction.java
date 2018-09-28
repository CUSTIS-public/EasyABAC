package custis.easyabac.api.core.call.getters;

import custis.easyabac.api.core.PermissionCheckerMetadata;
import custis.easyabac.api.impl.AttributeValueExtractor;
import custis.easyabac.pdp.AuthAttribute;
import custis.easyabac.pdp.RequestId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SingleResourceAndAction extends AbstractAttributesValuesGetter {

    private final boolean resourceIsFirst;

    public SingleResourceAndAction(PermissionCheckerMetadata permissionCheckerInformation, boolean resourceIsFirst) {
        super(permissionCheckerInformation);
        this.resourceIsFirst = resourceIsFirst;
    }

    @Override
    public Map<RequestId, List<AuthAttribute>> getAttributes(Object[] objects) {
        Map<RequestId, List<AuthAttribute>> result = new HashMap<>();
        result.put(RequestId.newRandom(), AttributeValueExtractor.collectAttributes(objects[resourceIsFirst ? 0 : 1], objects[resourceIsFirst ? 1 : 0]));
        return result;
    }
}
