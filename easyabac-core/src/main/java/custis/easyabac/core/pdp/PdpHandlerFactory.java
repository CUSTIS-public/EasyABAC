package custis.easyabac.core.pdp;

import custis.easyabac.core.cache.Cache;
import custis.easyabac.core.datasource.Datasource;
import custis.easyabac.model.AbacAuthModel;

import java.io.InputStream;
import java.util.List;

public interface PdpHandlerFactory {

    boolean supportsXacmlPolicies();

    PdpHandler newInstance(AbacAuthModel abacAuthModel, List<Datasource> datasources, Cache cache);

    PdpHandler newXacmlInstance(InputStream policyXacml, List<Datasource> datasources, Cache cache);
}
