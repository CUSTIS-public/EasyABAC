package custis.easyabac.benchmark.model.attrprovider;

import custis.easyabac.api.attr.imp.AttributeAuthorizationEntity;
import custis.easyabac.benchmark.model.Customer;
import custis.easyabac.core.pdp.AuthAttribute;

import java.util.List;

public class CustomerExt  extends Customer implements AttributeAuthorizationEntity {

    @Override
    public List<AuthAttribute> getAuthAttributes() {
        return null;
    }
}