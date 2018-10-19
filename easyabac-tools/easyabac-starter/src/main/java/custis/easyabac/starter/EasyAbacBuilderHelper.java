package custis.easyabac.starter;

import custis.easyabac.api.impl.EntityGetter;
import custis.easyabac.api.impl.EntitySubjectAttributesProvider;
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

    public static <T> EasyAbacBuilder defaultDebugBuilder(String source, EntityGetter<T> entityGetter) throws EasyAbacInitException {
        return defaultDebugBuilder(new ByteArrayInputStream(source.getBytes()), entityGetter);
    }

    public static <T> EasyAbacBuilder defaultDebugBuilder(InputStream modelStream, EntityGetter<T> entityGetter) throws EasyAbacInitException {
        ModelCreator modelCreator = new EasyAbacModelCreator();
        AbacAuthModel model = modelCreator.createModel(modelStream);
        EasyAbacBuilder builder = new EasyAbacBuilder(model, BalanaPdpHandlerFactory.PROXY_INSTANCE)
                .options(
                        new Options.OptionsBuilder()
                                .enableTrace(true)
                                .enableOptimization(true)
                                .build()
                )
                .subjectAttributesProvider(new EntitySubjectAttributesProvider<>(model, entityGetter))
                .trace(LoggingViewTrace.INSTANCE);
        return builder;
    }
}



