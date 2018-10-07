package custis.easyabac.core.init;

import custis.easyabac.core.model.ModelType;
import custis.easyabac.core.model.abac.AbacAuthModel;
import custis.easyabac.core.model.easy.EasyAuthModel;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

public class AbacAuthModelFactory {

    public static AbacAuthModel getInstance(ModelType modelType, InputStream policy) throws EasyAbacInitException {
        if (modelType == ModelType.EASY_YAML || modelType == ModelType.XACML) {
            EasyAuthModel easyAuthModel = load(policy);
            AbacAuthModel abacAuthModel = new AuthModelTransformer(easyAuthModel).transform();

            return abacAuthModel;
        } else {
            throw new EasyAbacInitException("Model " + modelType.name() + " is not supported");
        }
    }

    private static EasyAuthModel load(InputStream policy) {

        Yaml yaml = new Yaml();
        EasyAuthModel easyAuthModel = yaml.loadAs(policy, EasyAuthModel.class);

        return easyAuthModel;
    }
}
