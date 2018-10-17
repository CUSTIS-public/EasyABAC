package custis.easyabac;

import custis.easyabac.core.EasyAbacDatasourceException;
import custis.easyabac.core.init.Datasource;
import custis.easyabac.core.init.Param;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ReportCategoryDatasource extends Datasource {

    public ReportCategoryDatasource(Set<Param> params, String requiredAttributeId) {
        super(params, requiredAttributeId);
    }

    public ReportCategoryDatasource(Set<Param> params, String requiredAttributeId, Long expire) {
        super(params, requiredAttributeId, expire);
    }

    @Override
    public List<String> find() throws EasyAbacDatasourceException {
        {
            String reportId = null;
            for (Param param : getParams()) {
                if (param.getName().equals("reportId")) {
                    reportId = param.getValue();
                }
            }

            if (reportId == null) {
                throw new EasyAbacDatasourceException("reportId not found");
            }

            if (reportId != null) {
                switch (reportId) {
                    case "1":
                        return Arrays.asList("iod");
                    case "2":
                        return Arrays.asList("dsp");
                    case "3":
                        return Arrays.asList("iod");
                }
            }
            return Collections.emptyList();
        }
    }
}