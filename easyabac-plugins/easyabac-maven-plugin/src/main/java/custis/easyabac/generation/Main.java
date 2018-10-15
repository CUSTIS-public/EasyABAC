package custis.easyabac.generation;

import com.github.javaparser.utils.CodeGenerationUtils;
import custis.easyabac.generation.util.CompleteGenerator;
import custis.easyabac.generation.util.ModelGenerator;

import java.io.InputStream;
import java.nio.file.Path;

public class Main {

    private static InputStream getResourceAsStream(String s) {
        return Main.class
                .getClassLoader()
                .getResourceAsStream(s);
    }

    public static void main(String[] args) throws Exception {
        InputStream is = getResourceAsStream("test.yaml");
        Path testSourcePath = CodeGenerationUtils.mavenModuleRoot(ModelGenerator.class).resolve("src/test/java");
        Path testResourcePath = CodeGenerationUtils.mavenModuleRoot(ModelGenerator.class).resolve("src/test/resources");
        String bPackage = "generation";

        CompleteGenerator.generate(is, testSourcePath, testResourcePath, bPackage, "test.yaml");

    }
}
