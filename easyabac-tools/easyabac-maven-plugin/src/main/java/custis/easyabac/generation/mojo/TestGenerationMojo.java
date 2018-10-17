package custis.easyabac.generation.mojo;

import custis.easyabac.generation.util.CompleteGenerator;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.FileInputStream;
import java.nio.file.Path;

@Mojo( name = "generatetest", requiresDependencyResolution = ResolutionScope.COMPILE)
public class TestGenerationMojo extends EasyAbacBaseMojo {

    // input paramters

    @Parameter( property = "testBasePackage", defaultValue = "easyabac.autogen" )
    private String testBasePackage;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            findAndCreateTests();
        } catch (Exception e) {
            getLog().error(e.getMessage(), e);
        }
    }

    private void findAndCreateTests() throws Exception {
        FileInputStream is = new FileInputStream(project.getBasedir() + "/" + resourcePath + "/" + modelFile);
        Path sourcePath = project.getBasedir().toPath().resolve(testPath);
        Path resourcePath = project.getBasedir().toPath().resolve(testResourcePath);

        CompleteGenerator.generate(is, sourcePath, resourcePath, testBasePackage, modelFile);
    }
}
