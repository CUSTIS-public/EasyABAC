package custis.easyabac.generation;

import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.SourceRoot;
import custis.easyabac.ModelType;
import custis.easyabac.core.init.AbacAuthModelFactory;
import custis.easyabac.core.model.abac.AbacAuthModel;
import custis.easyabac.core.model.abac.attribute.Resource;
import custis.easyabac.generation.util.ActionGenerator;
import custis.easyabac.generation.util.EntityGenerator;
import custis.easyabac.generation.util.ModelGenerator;
import custis.easyabac.generation.util.TestGenerator;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.nio.file.Path;

public class Main {

    private static InputStream getResourceAsStream(String s) {
        return Main.class
                .getClassLoader()
                .getResourceAsStream(s);
    }

    public static void main(String[] args) throws Exception {
        Yaml yaml = new Yaml();

        InputStream is = getResourceAsStream("test.yaml");
        AbacAuthModel model = new AbacAuthModelFactory().getInstance(ModelType.EASY_YAML, is);


        Path rootPath = CodeGenerationUtils.mavenModuleRoot(ModelGenerator.class).resolve("src/test/java");
        SourceRoot sourceRoot = new SourceRoot(rootPath);

        String bPackage = "custis.easyabac.generation.test";
        for (Resource entry : model.getResources().values()) {
            EntityGenerator.createEntity(entry, bPackage + ".model", sourceRoot);
            ActionGenerator.createAction(entry, bPackage + ".model", sourceRoot);
            TestGenerator.createTest(entry, bPackage, sourceRoot, model.getPolicies());
        }



        sourceRoot.saveAll();


    }
}
