package custis.easyabac.core.init;

import custis.easyabac.core.cache.Cache;
import custis.easyabac.core.model.policy.AbacModel;

import java.io.InputStream;
import java.util.List;

public class PdpHandlerFactory {
    public PdpHandler getPdpHandler(PdpType pdpType, AbacModel abacModel, List<SampleDatasource> datasources, Cache cache) {
        return BalanaPdpHandler.getInstance(abacModel, datasources, cache);
    }

    public PdpHandler getPdpHandler(PdpType pdpType, InputStream policyXacml, List<SampleDatasource> datasources, Cache cache) {
        return BalanaPdpHandler.getInstance(policyXacml, datasources, cache);
    }


}
