package custis.easyabac.generation;

import com.github.javaparser.utils.SourceRoot;
import custis.easyabac.core.init.AbacAuthModelFactory;
import custis.easyabac.core.init.EasyAbacInitException;
import custis.easyabac.core.model.ModelType;
import custis.easyabac.core.model.abac.AbacAuthModel;
import custis.easyabac.core.model.abac.attribute.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class CompleteGenerator {
    static final String MODEL_SUFFIX = ".model";

    public static void generate(InputStream is, Path testSourcePath, Path testResourcePath, String testBasePackage, String modelFileName) throws EasyAbacInitException, IOException {
        AbacAuthModel model = new AbacAuthModelFactory().getInstance(ModelType.EASY_YAML, is);
        SourceRoot sourceRoot = new SourceRoot(testSourcePath);
        SourceRoot resourceRoot = new SourceRoot(testResourcePath);

        for (Resource resource : model.getResources().values()) {
            EntityGenerator.createEntity(resource, testBasePackage + MODEL_SUFFIX, sourceRoot);
            if (!resource.getActions().isEmpty()) {
                ActionGenerator.createAction(resource, testBasePackage + MODEL_SUFFIX, sourceRoot);
                TestGenerator.createTests(resource, testBasePackage, sourceRoot, resourceRoot, model, modelFileName);
            }
        }



        sourceRoot.saveAll();
    }
}
