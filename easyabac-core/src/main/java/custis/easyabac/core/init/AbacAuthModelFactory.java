package custis.easyabac.core.init;

import custis.easyabac.ModelType;
import custis.easyabac.core.model.abac.AbacAuthModel;
import custis.easyabac.core.model.easy.EasyAuthModel;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

public class AbacAuthModelFactory {

    public AbacAuthModel getInstance(ModelType modelType, InputStream policy) throws EasyAbacInitException {
        if (modelType == ModelType.EASY_YAML) {
            EasyAuthModel easyAuthModel = load(policy);
            AbacAuthModel abacAuthModel = new AuthModelTransformer(easyAuthModel).transform();

            return abacAuthModel;
        } else {
            throw new IllegalArgumentException(modelType.name() + " not supported");
        }
    }

    private EasyAuthModel load(InputStream policy) {

        Yaml yaml = new Yaml();
        EasyAuthModel easyAuthModel = yaml.loadAs(policy, EasyAuthModel.class);

        return easyAuthModel;
    }
}
