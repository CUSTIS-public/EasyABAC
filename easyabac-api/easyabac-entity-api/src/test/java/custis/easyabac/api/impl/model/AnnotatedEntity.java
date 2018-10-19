package custis.easyabac.api.impl.model;

import custis.easyabac.api.attr.annotation.AuthorizationAttribute;
import custis.easyabac.api.attr.annotation.AuthorizationEntity;

@AuthorizationEntity(name = "ent")
public class AnnotatedEntity {
    @AuthorizationAttribute(id = "i")
    private String id;
    @AuthorizationAttribute
    private int amount;
    private String notUsed;

    public AnnotatedEntity(String id, int amount, String notUsed) {
        this.id = id;
        this.amount = amount;
        this.notUsed = notUsed;
    }
}
