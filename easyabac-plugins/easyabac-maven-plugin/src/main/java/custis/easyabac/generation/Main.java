package custis.easyabac.generation;

import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.SourceRoot;
import custis.easyabac.core.init.AbacAuthModelFactory;
import custis.easyabac.core.model.easy.EasyAuthModel;
import custis.easyabac.core.model.easy.EasyObject;
import custis.easyabac.generation.util.ActionGenerator;
import custis.easyabac.generation.util.EntityGenerator;
import custis.easyabac.generation.util.ModelGenerator;
import custis.easyabac.generation.util.TestGenerator;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;

public class Main {

    private static InputStream getResourceAsStream(String s) {
        return Main.class
                .getClassLoader()
                .getResourceAsStream(s);
    }

    public static void main(String[] args) {
        Yaml yaml = new Yaml();

        InputStream is = getResourceAsStream("test.yaml");
        EasyAuthModel model = new AbacAuthModelFactory().load(is);


        Path rootPath = CodeGenerationUtils.mavenModuleRoot(ModelGenerator.class).resolve("src/test/java");
        SourceRoot sourceRoot = new SourceRoot(rootPath);

        String bPackage = "custis.easyabac.generation.test";
        for (Map.Entry<String, EasyObject> entry : model.getResources().entrySet()) {
            String key = StringUtils.capitalize(entry.getKey());
            EntityGenerator.createEntity(key, entry.getValue(), bPackage + ".model", sourceRoot);
            ActionGenerator.createAction(key, entry.getValue(), bPackage + ".model", sourceRoot);
            TestGenerator.createTest(key, entry.getValue(), bPackage, sourceRoot, model.getPermissions());
        }



        sourceRoot.saveAll();


    }
}
