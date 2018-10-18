package custis.easyabac.core.pdp.balana;

import custis.easyabac.core.EasyAbacDatasourceException;
import custis.easyabac.core.datasource.Datasource;
import custis.easyabac.core.datasource.Param;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class UserCategoryDatasource extends Datasource {

    public UserCategoryDatasource(Set<Param> params, String requiredAttributeId) {
        super(params, requiredAttributeId);
    }

    public UserCategoryDatasource(Set<Param> params, String requiredAttributeId, Long expire) {
        super(params, requiredAttributeId, expire);
    }

    @Override
    public List<String> find() throws EasyAbacDatasourceException {
        {
            String userName = null;
            for (Param param : getParams()) {
                if (param.getName().equals("userName")) {
                    userName = param.getValue();
                }
            }

            if (userName == null) {
                throw new EasyAbacDatasourceException("userName not found");
            }

            if (userName != null) {
                switch (userName) {
                    case "bob":
                        return Arrays.asList("iod", "dsp");
                    case "alice":
                        return Arrays.asList("dsp");
                    case "peter":
                        return Arrays.asList("iod");
                }
            }
            return Collections.emptyList();
        }
    }
}