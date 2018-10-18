package custis.easyabac.model.easy;

import custis.easyabac.model.AbacAuthModel;
import custis.easyabac.model.EasyAbacInitException;
import custis.easyabac.model.ModelCreator;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;

public class EasyAbacModelCreator implements ModelCreator {

    private static EasyAuthModel load(InputStream policy) {

        Yaml yaml = new Yaml();
        EasyAuthModel easyAuthModel = yaml.loadAs(policy, EasyAuthModel.class);

        return easyAuthModel;
    }

    @Override
    public AbacAuthModel createModel(InputStream stream) throws EasyAbacInitException {
        EasyAuthModel easyAuthModel = load(stream);
        AbacAuthModel abacAuthModel = new AuthModelTransformer(easyAuthModel).transform();
        return abacAuthModel;
    }
}
