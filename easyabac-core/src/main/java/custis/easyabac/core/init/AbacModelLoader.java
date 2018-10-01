package custis.easyabac.core.init;

import custis.easyabac.core.model.attribute.load.EasyAttributeModel;
import custis.easyabac.core.model.policy.AbacModel;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

public class AbacModelLoader {
    public AbacModel load(InputStream policy, InputStream attributes) {
        Yaml yaml = new Yaml();

        AbacModel abacModel = yaml.loadAs(policy, AbacModel.class);

        EasyAttributeModel easyAttributeModel = yaml.loadAs(attributes, EasyAttributeModel.class);

        return abacModel;
    }
}
