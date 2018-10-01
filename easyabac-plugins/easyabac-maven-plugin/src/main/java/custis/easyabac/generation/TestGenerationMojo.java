package custis.easyabac.generation;

import com.github.javaparser.utils.SourceRoot;
import custis.easyabac.ModelType;
import custis.easyabac.core.init.AbacAuthModelFactory;
import custis.easyabac.core.model.abac.AbacAuthModel;
import custis.easyabac.core.model.easy.EasyResource;
import custis.easyabac.generation.util.ActionGenerator;
import custis.easyabac.generation.util.EntityGenerator;
import custis.easyabac.generation.util.TestGenerator;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.stream.Collectors;

@Mojo( name = "generatetest", requiresDependencyResolution = ResolutionScope.COMPILE)
public class TestGenerationMojo extends EasyAbacBaseMojo {

    // input paramters

    @Parameter( property = "testBasePackage", defaultValue = "easyabac.autogen" )
    private String testBasePackage;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            findAndCreateTests();
        } catch (FileNotFoundException e) {
            getLog().error(e.getMessage(), e);
        }
    }

    private void findAndCreateTests() throws FileNotFoundException {
        FileInputStream is = new FileInputStream(project.getBasedir() + "/" + policyFile);
        AbacAuthModel model = new AbacAuthModelFactory().getInstance(ModelType.EASY_YAML, is);

        Path rootPath = project.getBasedir().toPath().resolve(testPath);
        SourceRoot sourceRoot = new SourceRoot(rootPath);

        for (EasyResource entry : model.getResources().values()) {
            EntityGenerator.createEntity(entry, testBasePackage + ".model", sourceRoot);
            ActionGenerator.createAction(entry, testBasePackage + ".model", sourceRoot);
            TestGenerator.createTest(entry, testBasePackage, sourceRoot,
                    model.getPolicies()
                            .values()
                            .stream()
                            .collect(Collectors.toList()));
        }



        sourceRoot.saveAll();
    }
}
