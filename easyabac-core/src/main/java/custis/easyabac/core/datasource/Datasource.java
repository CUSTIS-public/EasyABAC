package custis.easyabac.core.datasource;

import custis.easyabac.core.EasyAbacDatasourceException;
import custis.easyabac.model.attribute.Attribute;

import java.util.List;
import java.util.Set;

public abstract class Datasource {
    private final Set<Param> params;
    private final String returnAttributeId;
    private final long expire;

    private Attribute returnAttribute;


    public Datasource(Set<Param> params, String returnAttributeId) {
        this(params, returnAttributeId, 0);
    }

    public Datasource(Set<Param> params, String returnAttributeId, long expire) {
        this.params = params;
        this.returnAttributeId = returnAttributeId;
        this.expire = expire;
    }


    public Set<Param> getParams() {
        return params;
    }

    public String getReturnAttributeId() {
        return returnAttributeId;
    }

    public Attribute getReturnAttribute() {
        return returnAttribute;
    }

    public void setReturnAttribute(Attribute returnAttribute) {
        this.returnAttribute = returnAttribute;
    }

    public long getExpire() {
        return expire;
    }

    abstract public List<String> find() throws EasyAbacDatasourceException;
}
