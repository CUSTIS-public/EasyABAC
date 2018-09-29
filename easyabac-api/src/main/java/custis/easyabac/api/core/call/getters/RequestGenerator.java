package custis.easyabac.api.core.call.getters;

import custis.easyabac.pdp.AuthAttribute;
import custis.easyabac.pdp.RequestId;

import java.util.List;
import java.util.Map;

public interface RequestGenerator {

    Map<RequestId, List<AuthAttribute>> generate(List<Object> objects);

}
