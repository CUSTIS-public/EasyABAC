package custis.easyabac.api.impl.model;

import custis.easyabac.api.attr.imp.AttributeAuthorizationEntity;
import custis.easyabac.core.pdp.AuthAttribute;

import java.util.ArrayList;
import java.util.List;

public class ImplementingEntity implements AttributeAuthorizationEntity {

    private String id;
    private int amount;

    public ImplementingEntity(String id, int amount) {
        this.id = id;
        this.amount = amount;
    }

    @Override
    public List<AuthAttribute> getAuthAttributes() {
        List<AuthAttribute> attributes = new ArrayList<>();
        attributes.add(new AuthAttribute("id", id));
        attributes.add(new AuthAttribute("amount", amount + ""));
        return attributes;
    }
}
