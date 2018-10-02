package custis.easyabac;

import custis.easyabac.core.init.AbacAuthModelFactory;
import custis.easyabac.core.model.abac.AbacAuthModel;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;

public class LoadAbacAuthModelTest {

    @Test
    public void loadEasyAuthModel() throws Exception {
        InputStream policy = this.getClass()
                .getClassLoader()
                .getResourceAsStream("test_pip_policy.yaml");

        AbacAuthModel abacAuthModel = new AbacAuthModelFactory().getInstance(ModelType.EASY_YAML, policy);
        Assert.assertNotNull(abacAuthModel);

        String reportFirstAction = abacAuthModel.getResources().get("report").getActions().iterator().next();
        String reportId = abacAuthModel.getResources().get("report").getId();
        String reportFirstAttrId = abacAuthModel.getResources().get("report").getAttributes().get(0).getId();

        Assert.assertEquals("report", reportId);
        Assert.assertEquals("edit", reportFirstAction);
        Assert.assertEquals("report.id", reportFirstAttrId);


        Assert.assertEquals(1, abacAuthModel.getPolicies().size());
        Assert.assertEquals(2, abacAuthModel.getPolicies().get(0).getTarget().getConditions().size());
        Assert.assertEquals("report.action", abacAuthModel.getPolicies().get(0).getTarget().getConditions().get(0).getFirstOperand().getId());
        Assert.assertEquals("report.edit", abacAuthModel.getPolicies().get(0).getTarget().getConditions().get(0).getSecondOperand());
    }

}
