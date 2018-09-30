package custis.easyabac.core.init;

import custis.easyabac.core.model.attribute.Attribute;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class SampleDatasource {
    private final Set<Param> params;
    private final Attribute requiredAttribute;
    private final Long expire;


    public SampleDatasource(Set<Param> params, Attribute requiredAttribute) {
        this(params, requiredAttribute, null);
    }

    public SampleDatasource(Set<Param> params, Attribute requiredAttribute, Long expire) {
        this.params = params;
        this.requiredAttribute = requiredAttribute;
        this.expire = expire;
    }


    public Set<Param> getParams() {
        return params;
    }

    public Attribute getRequiredAttribute() {
        return requiredAttribute;
    }

    public Long getExpire() {
        return expire;
    }

    public List<String> find() {
        String userName = null;
        for (Param param : params) {
            if (param.getName().equals("userName")) {
                userName = param.getValue();
            }
        }

        if (userName.equals("bob")) {
            return Arrays.asList("iod", "dsp");
        } else if (userName.equals("alice")) {
            return Arrays.asList("dsp");
        } else if (userName.equals("peter")) {
            return Arrays.asList("iod");
        }

        return null;
    }
}
