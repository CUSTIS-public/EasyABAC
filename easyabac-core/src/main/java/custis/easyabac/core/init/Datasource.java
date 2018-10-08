package custis.easyabac.core.init;

import custis.easyabac.core.EasyAbacDatasourceException;
import custis.easyabac.core.model.abac.attribute.Attribute;

import java.util.List;
import java.util.Set;

public abstract class Datasource {
    private final Set<Param> params;
    private final String requiredAttributeId;
    private final Long expire;

    private Attribute requiredAttribute;


    public Datasource(Set<Param> params, String requiredAttributeId) {
        this(params, requiredAttributeId, null);
    }

    public Datasource(Set<Param> params, String requiredAttributeId, Long expire) {
        this.params = params;
        this.requiredAttributeId = requiredAttributeId;
        this.expire = expire;
    }


    public Set<Param> getParams() {
        return params;
    }

    public String getRequiredAttributeId() {
        return requiredAttributeId;
    }

    public Attribute getRequiredAttribute() {
        return requiredAttribute;
    }

    public void setRequiredAttribute(Attribute requiredAttribute) {
        this.requiredAttribute = requiredAttribute;
    }

    public Long getExpire() {
        return expire;
    }

    abstract public List<String> find() throws EasyAbacDatasourceException;
}
