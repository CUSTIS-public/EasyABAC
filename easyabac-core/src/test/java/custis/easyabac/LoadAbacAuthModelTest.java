package custis.easyabac;

import custis.easyabac.core.init.AbacAuthModelFactory;
import custis.easyabac.core.model.abac.AbacAuthModel;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;

public class LoadAbacAuthModelTest {

    @Test
    public void loadEasyAuthModel() {
        InputStream policy = this.getClass()
                .getClassLoader()
                .getResourceAsStream("test_pip_policy.yaml");

        AbacAuthModel abacAuthModel = new AbacAuthModelFactory().getInstance(ModelType.EASY_YAML, policy);
        Assert.assertNotNull(abacAuthModel);

        String reportFirstAction = abacAuthModel.getResources().get("report").getActions().get(0);
        Assert.assertEquals("edit", reportFirstAction);

    }
}
