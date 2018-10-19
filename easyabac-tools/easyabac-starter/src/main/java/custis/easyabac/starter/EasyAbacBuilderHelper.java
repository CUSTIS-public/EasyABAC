package custis.easyabac.starter;

import custis.easyabac.api.impl.EntitySubjectAttributesProvider;
import custis.easyabac.api.impl.SubjectEntityProvider;
import custis.easyabac.core.EasyAbacBuilder;
import custis.easyabac.core.Options;
import custis.easyabac.core.pdp.balana.BalanaPdpHandlerFactory;
import custis.easyabac.core.trace.logging.LoggingViewTrace;
import custis.easyabac.model.AbacAuthModel;
import custis.easyabac.model.EasyAbacInitException;
import custis.easyabac.model.ModelCreator;
import custis.easyabac.model.easy.EasyAbacModelCreator;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class EasyAbacBuilderHelper {

    public static <T> EasyAbacBuilder defaultDebugBuilder(String source, SubjectEntityProvider<T> subjectEntityProvider) throws EasyAbacInitException {
        return defaultDebugBuilder(new ByteArrayInputStream(source.getBytes()), subjectEntityProvider);
    }

    /**
     * Default builder helper for EasyAbac creation
     * @param modelStream InputStream with model data
     * @param subjectEntityProvider Subject information provider
     */
    public static <T> EasyAbacBuilder defaultDebugBuilder(InputStream modelStream, SubjectEntityProvider<T> subjectEntityProvider) throws EasyAbacInitException {
        ModelCreator modelCreator = new EasyAbacModelCreator();
        AbacAuthModel model = modelCreator.createModel(modelStream);
        EasyAbacBuilder builder = new EasyAbacBuilder(model, BalanaPdpHandlerFactory.PROXY_INSTANCE)
                .options(
                        new Options.OptionsBuilder()
                                .enableTrace(true)
                                .enableOptimization(true)
                                .build()
                )
                .subjectAttributesProvider(new EntitySubjectAttributesProvider<>(model, subjectEntityProvider))
                .trace(LoggingViewTrace.INSTANCE);
        return builder;
    }
}



