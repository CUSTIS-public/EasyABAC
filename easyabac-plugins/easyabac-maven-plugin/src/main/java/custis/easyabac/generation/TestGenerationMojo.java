package custis.easyabac.generation;

import com.github.javaparser.utils.SourceRoot;
import custis.easyabac.ModelType;
import custis.easyabac.core.init.AbacAuthModelFactory;
import custis.easyabac.core.model.abac.AbacAuthModel;
import custis.easyabac.core.model.easy.EasyResource;
import custis.easyabac.generation.util.ActionGenerator;
import custis.easyabac.generation.util.EntityGenerator;
import custis.easyabac.generation.util.TestGenerator;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.eclipse.aether.RepositorySystemSession;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Mojo( name = "generatetest", requiresDependencyResolution = ResolutionScope.COMPILE)
public class TestGenerationMojo extends AbstractMojo {

    // input paramters

    @Parameter( property = "generatetest.policyFile", defaultValue = "src/main/resources/policy.yaml" )
    private String policyFile;

    @Parameter( property = "generatetest.testPath", defaultValue = "src/test/java" )
    private String testPath;

    @Parameter( property = "generatetest.basePackage", defaultValue = "easyabac.autogen" )
    private String basePackage;


    // injectable

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Parameter(defaultValue = "${plugin.artifacts}", required = true, readonly = true)
    private List<Artifact> artifacts;

    @Component
    private ProjectBuilder projectBuilder;

    @Parameter(defaultValue="${repositorySystemSession}", required = true, readonly = true)
    private RepositorySystemSession repoSession;


    private String testFolder;
    private String testResourceFolder;



    /**
     * Defines which of the included files in the source directories to exclude (non by default).
     */
    private String[] excludes;

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
            EntityGenerator.createEntity(entry, basePackage + ".model", sourceRoot);
            ActionGenerator.createAction(entry, basePackage + ".model", sourceRoot);
            TestGenerator.createTest(entry, basePackage, sourceRoot,
                    model.getPolicies()
                            .values()
                            .stream()
                            .collect(Collectors.toList()));
        }



        sourceRoot.saveAll();
    }
}
