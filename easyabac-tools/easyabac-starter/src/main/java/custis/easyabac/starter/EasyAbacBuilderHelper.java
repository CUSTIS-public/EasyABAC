package custis.easyabac.starter;

import custis.easyabac.api.impl.EntitySubjectAttributesProvider;
import custis.easyabac.api.impl.SubjectEntityProvider;
import custis.easyabac.core.EasyAbacBuilder;
import custis.easyabac.core.Options;
import custis.easyabac.core.pdp.balana.BalanaPdpHandlerFactory;
import custis.easyabac.model.AbacAuthModel;
import custis.easyabac.model.EasyAbacInitException;
import custis.easyabac.model.ModelCreator;
import custis.easyabac.model.easy.EasyAbacModelCreator;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class EasyAbacBuilderHelper {

    public static <T> EasyAbacBuilder defaultBuilder(String source, SubjectEntityProvider<T> subjectEntityProvider) throws EasyAbacInitException {
        return defaultBuilder(new ByteArrayInputStream(source.getBytes()), subjectEntityProvider);
    }

    /**
     * Default builder helper for EasyAbac creation
     * @param modelStream InputStream with model data
     * @param subjectEntityProvider Subject information provider
     */
    public static <T> EasyAbacBuilder defaultBuilder(InputStream modelStream, SubjectEntityProvider<T> subjectEntityProvider) throws EasyAbacInitException {
        ModelCreator modelCreator = new EasyAbacModelCreator();
        AbacAuthModel model = modelCreator.createModel(modelStream);
        EasyAbacBuilder builder = new EasyAbacBuilder(model, BalanaPdpHandlerFactory.DIRECT_INSTANCE)
                .options(
                        new Options.OptionsBuilder()
                                .enableTrace(false)
                                .enableOptimization(true)
                                .build()
                )
                .subjectAttributesProvider(new EntitySubjectAttributesProvider<>(model, subjectEntityProvider));
        return builder;
    }
}



