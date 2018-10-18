package custis.easyabac.core.pdp.balana;

import custis.easyabac.model.AbacAuthModel;
import custis.easyabac.model.EasyAbacInitException;
import custis.easyabac.model.easy.EasyAbacModelCreator;
import org.wso2.balana.PolicySet;

import java.io.FileWriter;
import java.io.IOException;

public class BalanaPolicyBuilderMain {

    public static void main(String[] args) throws EasyAbacInitException, IOException {
        BalanaPolicyBuilder builder = new BalanaPolicyBuilder();
        EasyAbacModelCreator creator = new EasyAbacModelCreator();
        AbacAuthModel model = creator.createModel(BalanaPolicyBuilder.class.getResourceAsStream("/test.yaml"));
        PolicySet policySet = builder.buildFrom(model);
        final FileWriter fileWriter = new FileWriter("test.xacml");
        fileWriter.write(policySet.encode());
        fileWriter.flush();
        fileWriter.close();
    }
}
