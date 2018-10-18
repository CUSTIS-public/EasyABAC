package custis.easyabac.benchmark.model.attrprovider;

import custis.easyabac.api.attr.imp.AttributeAuthorizationEntity;
import custis.easyabac.benchmark.model.Order;
import custis.easyabac.core.pdp.AuthAttribute;

import java.util.List;

public class OrderExt extends Order implements AttributeAuthorizationEntity {

    @Override
    public List<AuthAttribute> getAuthAttributes() {
        return null;
    }
}