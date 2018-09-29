package custis.easyabac.test;

import custis.easyabac.api.tests.EasyAbacTestable;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.codehaus.plexus.util.StringUtils;
import org.eclipse.aether.RepositorySystemSession;
import org.reflections.Reflections;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Mojo( name = "generate")
public class TestGenerationMojo extends AbstractMojo {

    @Parameter( property = "generate.testPath", defaultValue = "src/test/java" )
    private String testPath;

    @Parameter( property = "generate.testResourcePath", defaultValue = "src/test/resources" )
    private String testResourcePath;

    @Parameter( property = "generate.basePackage", defaultValue = "easyabac.autogen" )
    private String basePackage;

    @Parameter( property = "generate.permissionCheckersPackage", defaultValue = "easyabac.permissioncheckers" )
    private String permissionCheckersPackage;

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

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        prepareParameters();

        clearOld();

        createTestStructure();

        findAndCreateTests();
    }

    private void findAndCreateTests() {

        try {
            System.out.println("compile");
            for (String compileClasspathElement : project.getCompileClasspathElements()) {
                System.out.println(compileClasspathElement);
            }

            System.out.println("test");
            for(String element : project.getTestClasspathElements()){
                System.out.println(element);
            }

            File basedir = project.getBasedir();
            System.out.println(basedir.toPath());
        } catch (DependencyResolutionRequiredException e) {
            e.printStackTrace();
        }



        Reflections reflections = new Reflections(permissionCheckersPackage);
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(EasyAbacTestable.class);
    }

    private void createTestStructure() {
        new File(testPath).mkdirs();
        new File(testResourcePath).mkdirs();
    }

    private void prepareParameters() {
        List<String> paths = Arrays.asList(basePackage.split("\\."));

        List<String> sourceFolderPath = new ArrayList<>();
        sourceFolderPath.add(testPath);
        sourceFolderPath.addAll(paths);

        List<String> resourceFolderPath = new ArrayList<>();
        resourceFolderPath.add(testResourcePath);
        resourceFolderPath.addAll(paths);

        this.testFolder = StringUtils.join(sourceFolderPath.iterator(), "/");
        this.testResourceFolder = StringUtils.join(resourceFolderPath.iterator(), "/");
    }

    private void clearOld() {
        deleteDirectory(new File(testFolder));
        deleteDirectory(new File(testResourceFolder));
    }

    private static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }
}
