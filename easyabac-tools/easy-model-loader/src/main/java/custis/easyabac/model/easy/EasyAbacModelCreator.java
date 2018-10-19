package custis.easyabac.model.easy;

import custis.easyabac.model.AbacAuthModel;
import custis.easyabac.model.EasyAbacInitException;
import custis.easyabac.model.ModelCreator;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.io.Reader;

public class EasyAbacModelCreator implements ModelCreator {

    private static EasyAuthModel load(InputStream policy) {
        Yaml yaml = new Yaml();
        return yaml.loadAs(policy, EasyAuthModel.class);
    }

    private static EasyAuthModel load(Reader policy) {
        Yaml yaml = new Yaml();
        return yaml.loadAs(policy, EasyAuthModel.class);
    }

    @Override
    public AbacAuthModel createModel(InputStream stream) throws EasyAbacInitException {
        EasyAuthModel easyAuthModel = load(stream);
        return new AuthModelTransformer(easyAuthModel).transform();
    }

    @Override
    public AbacAuthModel createModel(Reader reader) throws EasyAbacInitException {
        EasyAuthModel easyAuthModel = load(reader);
        return new AuthModelTransformer(easyAuthModel).transform();
    }
}
