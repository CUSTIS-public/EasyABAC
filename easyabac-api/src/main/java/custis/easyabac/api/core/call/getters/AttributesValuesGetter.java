package custis.easyabac.api.core.call.getters;

import custis.easyabac.api.core.PermissionCheckerMetadata;
import custis.easyabac.pdp.AuthAttribute;
import custis.easyabac.pdp.RequestId;

import java.util.List;
import java.util.Map;

public interface AttributesValuesGetter {

    Map<RequestId, List<AuthAttribute>> getAttributes(Object[] objects);
}
