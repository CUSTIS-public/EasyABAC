package custis.easyabac.model.xacmlconverter;

import custis.easyabac.core.pdp.balana.BalanaPolicyBuilder;
import custis.easyabac.model.AbacAuthModel;
import custis.easyabac.model.EasyAbacInitException;
import custis.easyabac.model.easy.EasyAbacModelCreator;

import java.io.InputStream;
import java.io.Reader;

public class EasyModel2XACMLConverter {
    private BalanaPolicyBuilder balanaPolicyBuilder;

    public EasyModel2XACMLConverter() {
        this.balanaPolicyBuilder = new BalanaPolicyBuilder();
    }

    public String convert(InputStream inputStream) throws ConversionException {
        try {
            AbacAuthModel model = new EasyAbacModelCreator().createModel(inputStream);
            return this.balanaPolicyBuilder.buildFrom(model).encode();
        } catch (EasyAbacInitException e) {
            throw new ConversionException("Failed to create EasyModel from InputStream", e);
        }
    }

    public String convert(Reader reader) throws ConversionException {
        try {
            AbacAuthModel model = new EasyAbacModelCreator().createModel(reader);
            return this.balanaPolicyBuilder.buildFrom(model).encode();
        } catch (EasyAbacInitException e) {
            throw new ConversionException("Failed to create EasyModel from Reader", e);
        }
    }
}