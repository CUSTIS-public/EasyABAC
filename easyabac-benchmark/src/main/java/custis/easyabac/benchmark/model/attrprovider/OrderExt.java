package custis.easyabac.benchmark.model.attrprovider;

import custis.easyabac.api.attr.imp.AttributiveAuthEntity;
import custis.easyabac.benchmark.model.Order;
import custis.easyabac.core.pdp.AuthAttribute;

import java.util.List;

public class OrderExt extends Order implements AttributiveAuthEntity {

    public OrderExt(String id, Integer amount, String branchId, String customerId) {
        super(id, amount, branchId, customerId);
    }

    @Override
    public List<AuthAttribute> getAuthAttributes() {
        return null;
    }
}