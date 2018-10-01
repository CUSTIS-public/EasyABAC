package custis.easyabac;

import custis.easyabac.core.init.AbacAuthModelFactory;
import custis.easyabac.core.model.abac.AbacAuthModel;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.InputStream;

public class LoadAbacAuthModelTest {

    @Test
    @Ignore
    public void loadEasyAuthModel() {
        InputStream policy = this.getClass()
                .getClassLoader()
                .getResourceAsStream("test_pip_policy.yaml");

        AbacAuthModel load = new AbacAuthModelFactory().getInstance(ModelType.EASY_YAML, policy);
        Assert.assertNotNull(load);
    }
}
