package custis.easyabac.core.init;

import custis.easyabac.ModelType;
import custis.easyabac.core.model.abac.AbacAuthModel;
import custis.easyabac.core.model.easy.EasyAuthModel;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

public class AbacAuthModelFactory {

    public AbacAuthModel getInstance(ModelType modelType, InputStream policy) {
        if (modelType == ModelType.EASY_YAML) {
            EasyAuthModel easyAuthModel = load(policy);
            return transform(easyAuthModel);
        }
        new RuntimeException("Ошипко");
        return null;
    }

    private EasyAuthModel load(InputStream policy) {

        Yaml yaml = new Yaml();
        EasyAuthModel easyAuthModel = yaml.loadAs(policy, EasyAuthModel.class);

        return new EasyAuthModel();
    }

    private AbacAuthModel transform(EasyAuthModel easyAuthModel) {

        return new AbacAuthModel();
    }
}
