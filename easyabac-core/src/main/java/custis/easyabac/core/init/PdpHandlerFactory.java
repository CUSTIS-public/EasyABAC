package custis.easyabac.core.init;

import custis.easyabac.core.cache.Cache;
import custis.easyabac.core.model.abac.AbacAuthModel;

import java.io.InputStream;
import java.util.List;

public interface PdpHandlerFactory {

    boolean supportsXacmlPolicies();

    PdpHandler newInstance(AbacAuthModel abacAuthModel, List<Datasource> datasources, Cache cache);

    PdpHandler newXacmlInstance(AbacAuthModel abacAuthModel, InputStream policyXacml, List<Datasource> datasources, Cache cache);
}
