package custis.easyabac.core.init;

import custis.easyabac.ModelType;
import custis.easyabac.core.cache.Cache;
import custis.easyabac.core.model.abac.AbacAuthModel;

import java.io.InputStream;
import java.util.List;

public class PdpHandlerFactory {
    public static PdpHandler getPdpHandler(PdpType pdpType, ModelType modelType, AbacAuthModel abacAuthModel, InputStream policyXacml, List<Datasource> datasources, Cache cache) throws EasyAbacInitException {

        switch (modelType) {
            case XACML: {
                return BalanaPdpHandler.getInstance(policyXacml, datasources, cache);
            }
            case EASY_YAML: {
                return BalanaPdpHandler.getInstance(abacAuthModel, datasources, cache);
            }
        }
        throw new EasyAbacInitException("Модель " + modelType.name() + " не поддерживается");

    }


}
