package custis.easyabac.api.config;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;

import static org.springframework.beans.factory.support.BeanDefinitionReaderUtils.GENERATED_BEAN_NAME_SEPARATOR;

/**
 * Class registers all PermissionChecker instances
 */
public class PermissionCheckerBeanDefinitionRegistrar
        implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    private ResourceLoader resourceLoader;
    private Environment environment;

    /*
     * (non-Javadoc)
     * @see org.springframework.context.ResourceLoaderAware#setResourceLoader(org.springframework.core.io.ResourceLoader)
     */
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.context.EnvironmentAware#setEnvironment(org.springframework.core.env.Environment)
     */
    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.context.annotation.ImportBeanDefinitionRegistrar#registerBeanDefinitions(org.springframework.core.type.AnnotationMetadata, org.springframework.beans.factory.support.BeanDefinitionRegistry)
     */
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {

        Assert.notNull(annotationMetadata, "AnnotationMetadata must not be null!");
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null!");
        Assert.notNull(resourceLoader, "ResourceLoader must not be null!");

        // Guard against calls for sub-classes
        if (annotationMetadata.getAnnotationAttributes(getAnnotation().getName()) == null) {
            return;
        }

        AnnotationPermissionCheckerConfigurationSource configurationSource = new AnnotationPermissionCheckerConfigurationSource(
                annotationMetadata, getAnnotation(), resourceLoader, environment, registry);

        PermissionCheckerConfigurationExtension extension = new PermissionCheckerConfigurationExtensionImpl();
        exposeRegistration(extension, registry, configurationSource);

        PermissionCheckerConfigurationDelegate delegate = new PermissionCheckerConfigurationDelegate(configurationSource, resourceLoader,
                environment);

        delegate.registerPermissionCheckersIn(registry, extension);
    }

    /**
     * Return the annotation to obtain configuration information from. Will be wrappen into an
     * {@link AnnotationPermissionCheckerConfigurationSource} so have a look at the constants in there for what annotation
     * attributes it expects.
     *
     * @return
     */
    private Class<? extends Annotation> getAnnotation() {
        return EnablePermissionCheckers.class;
    }

    static void exposeRegistration(PermissionCheckerConfigurationExtension extension, BeanDefinitionRegistry registry,
                                   PermissionCheckerConfigurationSource configurationSource) {

        Assert.notNull(extension, "PermissionCheckerConfigurationExtension must not be null!");
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null!");
        Assert.notNull(configurationSource, "PermissionCheckerConfigurationSource must not be null!");

        Class<? extends PermissionCheckerConfigurationExtension> extensionType = extension.getClass();
        String beanName = extensionType.getName().concat(GENERATED_BEAN_NAME_SEPARATOR).concat("0");

        if (registry.containsBeanDefinition(beanName)) {
            return;
        }

        // Register extension as bean to indicate repository parsing and registration has happened
        RootBeanDefinition definition = new RootBeanDefinition(extensionType);
        definition.setSource(configurationSource.getSource());
        definition.setRole(AbstractBeanDefinition.ROLE_INFRASTRUCTURE);
        definition.setLazyInit(true);

        registry.registerBeanDefinition(beanName, definition);
    }
}
