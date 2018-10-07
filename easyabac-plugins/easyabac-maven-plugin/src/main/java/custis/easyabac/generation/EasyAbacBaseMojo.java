package custis.easyabac.generation;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.eclipse.aether.RepositorySystemSession;

import java.util.List;

public abstract class EasyAbacBaseMojo extends AbstractMojo {

    @Parameter( property = "policyFile", defaultValue = "src/main/resources/policy.yaml" )
    protected String policyFile;

    @Parameter( property = "sourcePath", defaultValue = "src/main/java" )
    protected String sourcePath;

    @Parameter( property = "testPath", defaultValue = "src/test/java" )
    protected String testPath;

    @Parameter( property = "testResourcePath", defaultValue = "src/test/resources" )
    protected String testResourcePath;



    // injectable

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    protected MavenProject project;

    @Parameter(defaultValue = "${plugin.artifacts}", required = true, readonly = true)
    protected List<Artifact> artifacts;

    @Component
    protected ProjectBuilder projectBuilder;

    @Parameter(defaultValue="${repositorySystemSession}", required = true, readonly = true)
    protected RepositorySystemSession repoSession;
}
