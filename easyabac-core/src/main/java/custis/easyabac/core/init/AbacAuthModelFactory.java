package custis.easyabac.core.init;

import custis.easyabac.ModelType;
import custis.easyabac.core.model.abac.AbacAuthModel;
import custis.easyabac.core.model.easy.EasyAuthModel;
import custis.easyabac.core.model.easy.EasyResource;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class AbacAuthModelFactory {

    public AbacAuthModel getInstance(ModelType modelType, InputStream policy) {
        if (modelType == ModelType.EASY_YAML) {
            EasyAuthModel easyAuthModel = load(policy);

            for (Map.Entry<String, EasyResource> entry : easyAuthModel.getResources().entrySet()) {
                entry.getValue().setId(entry.getKey());
            }

            return transform(easyAuthModel);
        }
        new RuntimeException("Ошипко");
        return null;
    }

    public EasyAuthModel load(InputStream policy) {

        Yaml yaml = new Yaml();
        EasyAuthModel easyAuthModel = yaml.loadAs(policy, EasyAuthModel.class);

        return easyAuthModel;
    }

    private AbacAuthModel transform(EasyAuthModel easyAuthModel) {

        AbacAuthModel abacAuthModel = new AbacAuthModel();

        abacAuthModel.setResources(easyAuthModel.getResources());

        return abacAuthModel;
    }
}
