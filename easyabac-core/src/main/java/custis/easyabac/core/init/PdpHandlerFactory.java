package custis.easyabac.core.init;

import custis.easyabac.core.cache.Cache;
import custis.easyabac.core.model.abac.AbacAuthModel;

import java.io.InputStream;
import java.util.List;

public class PdpHandlerFactory {
    public PdpHandler getPdpHandler(PdpType pdpType, AbacAuthModel abacAuthModel, List<SampleDatasource> datasources, Cache cache) {
        return BalanaPdpHandler.getInstance(abacAuthModel, datasources, cache);
    }

    public PdpHandler getPdpHandler(PdpType pdpType, InputStream policyXacml, List<SampleDatasource> datasources, Cache cache) {
        return BalanaPdpHandler.getInstance(policyXacml, datasources, cache);
    }


}
