package custis.easyabac.generation;

import com.github.javaparser.utils.SourceRoot;
import custis.easyabac.core.init.AbacAuthModelFactory;
import custis.easyabac.core.model.ModelType;
import custis.easyabac.core.model.abac.AbacAuthModel;
import custis.easyabac.core.model.abac.attribute.Resource;
import custis.easyabac.generation.util.ActionGenerator;
import custis.easyabac.generation.util.EntityGenerator;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.FileInputStream;
import java.nio.file.Path;

@Mojo( name = "generatemodel", requiresDependencyResolution = ResolutionScope.COMPILE)
public class ModelGenerationMojo extends EasyAbacBaseMojo {

    // input paramters

    @Parameter( property = "modelPackage", defaultValue = "easyabac.model" )
    private String modelPackage;

    @Parameter( property = "permissioncheckerPackage", defaultValue = "easyabac.permissionchecker" )
    private String checkersPackage;


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            findAndCreateTests();
        } catch (Exception e) {
            getLog().error(e.getMessage(), e);
        }
    }

    private void findAndCreateTests() throws Exception {
        FileInputStream is = new FileInputStream(project.getBasedir() + "/" + modelFile);
        AbacAuthModel model = AbacAuthModelFactory.getInstance(ModelType.EASY_YAML, is);

        Path rootPath = project.getBasedir().toPath().resolve(sourcePath);
        SourceRoot sourceRoot = new SourceRoot(rootPath);

        for (Resource entry : model.getResources().values()) {
            EntityGenerator.createEntity(entry, modelPackage, sourceRoot);
            ActionGenerator.createAction(entry, modelPackage, sourceRoot);

        }



        sourceRoot.saveAll();
    }
}
