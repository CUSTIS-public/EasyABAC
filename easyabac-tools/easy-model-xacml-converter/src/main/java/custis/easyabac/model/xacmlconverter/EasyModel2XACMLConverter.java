package custis.easyabac.model.xacmlconverter;

import custis.easyabac.core.pdp.balana.BalanaPolicyBuilder;
import custis.easyabac.model.AbacAuthModel;

public class EasyModel2XACMLConverter {
    private BalanaPolicyBuilder balanaPolicyBuilder;

    public EasyModel2XACMLConverter() {
        this.balanaPolicyBuilder = new BalanaPolicyBuilder();
    }

    public String convert(AbacAuthModel authModel) {
        return balanaPolicyBuilder.buildFrom(authModel).encode();
    }
}