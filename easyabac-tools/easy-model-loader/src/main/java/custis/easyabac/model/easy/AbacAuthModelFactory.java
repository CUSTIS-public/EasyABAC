package custis.easyabac.model.easy;

import custis.easyabac.model.AbacAuthModel;
import custis.easyabac.model.EasyAbacInitException;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

public class AbacAuthModelFactory {

    public static AbacAuthModel getInstance(InputStream policy) throws EasyAbacInitException {
            EasyAuthModel easyAuthModel = load(policy);
            AbacAuthModel abacAuthModel = new AuthModelTransformer(easyAuthModel).transform();
            return abacAuthModel;
    }

    private static EasyAuthModel load(InputStream policy) {

        Yaml yaml = new Yaml();
        EasyAuthModel easyAuthModel = yaml.loadAs(policy, EasyAuthModel.class);

        return easyAuthModel;
    }

}
