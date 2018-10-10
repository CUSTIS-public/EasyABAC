package custis.easyabac.core.init;

import custis.easyabac.core.cache.Cache;
import custis.easyabac.core.model.abac.AbacAuthModel;
import custis.easyabac.core.trace.Trace;

import java.io.InputStream;
import java.util.List;

public interface PdpHandlerFactory {

    boolean supportsXacmlPolicies();

    PdpHandler newInstance(AbacAuthModel abacAuthModel, List<Datasource> datasources, Cache cache, Trace trace);

    PdpHandler newXacmlInstance(InputStream policyXacml, List<Datasource> datasources, Cache cache, Trace trace);
}
