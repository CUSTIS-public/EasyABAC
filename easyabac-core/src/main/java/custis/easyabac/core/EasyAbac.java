package custis.easyabac.core;

import custis.easyabac.core.cache.Cache;
import custis.easyabac.core.init.PolicyInitializer;
import custis.easyabac.core.model.attribute.Datasource;
import custis.easyabac.core.model.attribute.load.EasyAttributeModel;
import custis.easyabac.core.model.policy.EasyPolicy;
import custis.easyabac.core.trace.Trace;
import custis.easyabac.pdp.AttributiveAuthorizationService;
import custis.easyabac.pdp.AuthAttribute;
import custis.easyabac.pdp.AuthResponse;
import custis.easyabac.pdp.RequestId;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.balana.PDP;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class EasyAbac implements AttributiveAuthorizationService {

    private final static Log log = LogFactory.getLog(EasyAbac.class);

    private final PDP pdpInstance;


    private EasyAbac(PDP pdpInstance) {

        this.pdpInstance = pdpInstance;
    }

    @Override
    public AuthResponse authorize(List<AuthAttribute> attributes) {
        return null;
    }

    @Override
    public Map<RequestId, AuthResponse> authorizeMultiple(Map<RequestId, List<AuthAttribute>> attributes) {
        return null;
    }


    public static class Builder {
        private final EasyPolicy easyPolicy;
        private final EasyAttributeModel easyAttributeModel;
        private List<Datasource> datasources;
        private Cache cache;
        private Trace trace;

        public Builder(String policy, String attributes) {

            Yaml yaml = new Yaml();

            easyPolicy = yaml.loadAs(policy, EasyPolicy.class);

            easyAttributeModel = yaml.loadAs(attributes, EasyAttributeModel.class);

        }

        public Builder(InputStream policy, InputStream attributes) {

            Yaml yaml = new Yaml();

            easyPolicy = yaml.loadAs(policy, EasyPolicy.class);

            easyAttributeModel = yaml.loadAs(attributes, EasyAttributeModel.class);
        }

        public Builder datasources(List<Datasource> datasources) {
            this.datasources = datasources;
            return this;
        }

        public Builder cache(Cache cache) {
            this.cache = cache;
            return this;
        }

        public Builder trace(Trace trace) {
            this.trace = trace;
            return this;
        }

        public AttributiveAuthorizationService build() {
            PolicyInitializer policyInitializer = new PolicyInitializer();
            PDP pdpInstance = policyInitializer.getPDPNewInstance(easyPolicy, easyAttributeModel, datasources);

            return new EasyAbac(pdpInstance);
        }
    }



}
