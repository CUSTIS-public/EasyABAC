/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package custis.easyabac.api.config;

import custis.easyabac.api.PermissionChecker;
import custis.easyabac.api.attr.annotation.AuthorizationEntity;
import custis.easyabac.api.core.DefaultPermissionCheckerMetadata;
import custis.easyabac.api.core.PermissionCheckerMetadata;
import custis.easyabac.api.impl.EasyABACPermissionCheckerFactoryBean;
import custis.easyabac.api.utils.InspectionClassLoader;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.util.*;

import static org.springframework.beans.factory.support.BeanDefinitionReaderUtils.GENERATED_BEAN_NAME_SEPARATOR;
import static org.springframework.beans.factory.support.BeanDefinitionReaderUtils.generateBeanName;

/**
 * Base implementation of {@link PermissionCheckerConfigurationExtension} to ease the implementation of the interface. Will
 * default the default named query location based on a module prefix provided by implementors (see
 * {@link #getModulePrefix()}). Stubs out the post-processing methods as they might not be needed by default.
 * 
 * @author Oliver Gierke
 * @author Mark Paluch
 * @author Christoph Strobl
 */
public class PermissionCheckerConfigurationExtensionImpl implements PermissionCheckerConfigurationExtension {

    private static final Logger log = LoggerFactory.getLogger(PermissionCheckerConfigurationExtensionImpl.class);
	private static final String CLASS_LOADING_ERROR = "EasyABAC - Could not load type %s using class loader %s.";
	private static final String MULTI_STORE_DROPPED = "Spring Data EasyABAC - Could not safely identify store assignment for repository candidate {}.";

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config.PermissionCheckerConfigurationExtension#getPermissionCheckerConfigurations(org.springframework.data.repository.config.PermissionCheckerConfigurationSource, org.springframework.core.io.ResourceLoader, boolean)
	 */
	@Override
	public <T extends PermissionCheckerConfigurationSource> Collection<PermissionCheckerConfiguration<T>> getPermissionCheckerConfigurations(
			T configSource, ResourceLoader loader, boolean strictMatchesOnly) {

		Assert.notNull(configSource, "ConfigSource must not be null!");
		Assert.notNull(loader, "Loader must not be null!");

		Set<PermissionCheckerConfiguration<T>> result = new HashSet<>();

		for (BeanDefinition candidate : configSource.getCandidates(loader)) {

			PermissionCheckerConfiguration<T> configuration = getPermissionCheckerConfiguration(candidate, configSource);
			Class<?> repositoryInterface = loadRepositoryInterface(configuration,
					getConfigurationInspectionClassLoader(loader));

			if (repositoryInterface == null) {
				result.add(configuration);
				continue;
			}

			PermissionCheckerMetadata metadata = DefaultPermissionCheckerMetadata.getMetadata(repositoryInterface);

			boolean qualifiedForImplementation = !strictMatchesOnly || configSource.usesExplicitFilters()
					|| isStrictCandidate(metadata);

			if (qualifiedForImplementation) {
				result.add(configuration);
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config.PermissionCheckerConfigurationExtension#registerBeansForRoot(org.springframework.beans.factory.support.BeanDefinitionRegistry, org.springframework.data.repository.config.PermissionCheckerConfigurationSource)
	 */
	public void registerBeansForRoot(BeanDefinitionRegistry registry,
			PermissionCheckerConfigurationSource config) {
        Object source = config.getSource();

		// FIXME implement
		/*
        registerIfNotAlreadyRegistered(new RootBeanDefinition(EntityManagerBeanDefinitionRegistrarPostProcessor.class),
                registry, "AZAZAZ", source); // FIXME

        registerIfNotAlreadyRegistered(new RootBeanDefinition(JpaMetamodelMappingContextFactoryBean.class), registry,
				JPA_MAPPING_CONTEXT_BEAN_NAME, source);

        registerIfNotAlreadyRegistered(new RootBeanDefinition(PAB_POST_PROCESSOR), registry,
                AnnotationConfigUtils.PERSISTENCE_ANNOTATION_PROCESSOR_BEAN_NAME, source);

        // Register bean definition for DefaultJpaContext

        RootBeanDefinition contextDefinition = new RootBeanDefinition(DefaultJpaContext.class);
        contextDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);

        registerIfNotAlreadyRegistered(contextDefinition, registry, "AZAZAZAZA", source); // FIXME

        */
    }

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config.PermissionCheckerConfigurationExtension#postProcess(org.springframework.beans.factory.support.BeanDefinitionBuilder, org.springframework.data.repository.config.PermissionCheckerConfigurationSource)
	 */
	public void postProcess(BeanDefinitionBuilder builder, PermissionCheckerConfigurationSource source) {}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config.PermissionCheckerConfigurationExtension#postProcess(org.springframework.beans.factory.support.BeanDefinitionBuilder, org.springframework.data.repository.config.AnnotationPermissionCheckerConfigurationSource)
	 */
	public void postProcess(BeanDefinitionBuilder builder, AnnotationPermissionCheckerConfigurationSource config) {}

    @Override
    public String getPermissionCheckerFactoryBeanClassName() {
        return EasyABACPermissionCheckerFactoryBean.class.getName();
    }

	/**
	 * Sets the given source on the given {@link AbstractBeanDefinition} and registers it inside the given
	 * {@link BeanDefinitionRegistry}. For {@link BeanDefinition}s to be registered once-and-only-once for all
	 * configuration elements (annotation or XML), prefer calling
	 * {@link #registerIfNotAlreadyRegistered(AbstractBeanDefinition, BeanDefinitionRegistry, String, Object)} with a
	 * dedicated bean name to avoid the bead definition being registered multiple times. *
	 * 
	 * @param registry must not be {@literal null}.
	 * @param bean must not be {@literal null}.
	 * @param source must not be {@literal null}.
	 * @return the bean name generated for the given {@link BeanDefinition}
	 */
	public static String registerWithSourceAndGeneratedBeanName(BeanDefinitionRegistry registry,
			AbstractBeanDefinition bean, Object source) {

		bean.setSource(source);

		String beanName = generateBeanName(bean, registry);
		registry.registerBeanDefinition(beanName, bean);

		return beanName;
	}

	/**
	 * Registers the given {@link AbstractBeanDefinition} with the given registry with the given bean name unless the
	 * registry already contains a bean with that name.
	 * 
	 * @param bean must not be {@literal null}.
	 * @param registry must not be {@literal null}.
	 * @param beanName must not be {@literal null} or empty.
	 * @param source must not be {@literal null}.
	 */
	public static void registerIfNotAlreadyRegistered(AbstractBeanDefinition bean, BeanDefinitionRegistry registry,
			String beanName, Object source) {

		if (registry.containsBeanDefinition(beanName)) {
			return;
		}

		bean.setSource(source);
		registry.registerBeanDefinition(beanName, bean);
	}

	/**
	 * Returns whether the given {@link BeanDefinitionRegistry} already contains a bean of the given type assuming the
	 * bean name has been auto-generated.
	 * 
	 * @param type
	 * @param registry
	 * @return
	 */
	public static boolean hasBean(Class<?> type, BeanDefinitionRegistry registry) {

		String name = String.format("%s%s0", type.getName(), GENERATED_BEAN_NAME_SEPARATOR);
		return registry.containsBeanDefinition(name);
	}

	/**
	 * Creates a actual {@link PermissionCheckerConfiguration} instance for the given {@link PermissionCheckerConfigurationSource} and
	 * interface name. Defaults to the {@link DefaultPermissionCheckerConfiguration} but allows sub-classes to override this to
	 * customize the behavior.
	 * 
	 * @param definition will never be {@literal null} or empty.
	 * @param configSource will never be {@literal null}.
	 * @return
	 */
	protected <T extends PermissionCheckerConfigurationSource> PermissionCheckerConfiguration<T> getPermissionCheckerConfiguration(
			BeanDefinition definition, T configSource) {
		return new DefaultPermissionCheckerConfiguration<T>(configSource, definition, this);
	}

	/**
	 * Returns whether the given repository metadata is a candidate for bean definition creation in the strict repository
	 * detection mode. The default implementation inspects the domain type managed for a set of well-known annotations
	 * (see {@link #getIdentifyingAnnotations()}). If none of them is found, the candidate is discarded. Implementations
	 * should make sure, the only return {@literal true} if they're really sure the interface handed to the method is
	 * really a store interface.
	 *
	 * @param metadata
	 * @return
	 * @since 1.9
	 */
	protected boolean isStrictCandidate(PermissionCheckerMetadata metadata) {

		Collection<Class<?>> types = getIdentifyingTypes();
		Class<?> repositoryInterface = metadata.getPermissionCheckerInterface();

		for (Class<?> type : types) {
			if (type.isAssignableFrom(repositoryInterface)) {
				return true;
			}
		}

		Class<?> domainType = metadata.getResourceType();
		Collection<Class<? extends Annotation>> annotations = getIdentifyingAnnotations();

		if (annotations.isEmpty()) {
			return true;
		}

		for (Class<? extends Annotation> annotationType : annotations) {
			if (AnnotationUtils.findAnnotation(domainType, annotationType) != null) {
				return true;
			}
		}

        log.info(MULTI_STORE_DROPPED, repositoryInterface);

		return false;
	}

    /*
     * Possible extension point
     */
    protected Collection<Class<? extends Annotation>> getIdentifyingAnnotations() {
        return Arrays.asList(AuthorizationEntity.class);
    }

    /*
     * Possible extension point
     */
    protected Collection<Class<?>> getIdentifyingTypes() {
        return Collections.<Class<?>> singleton(PermissionChecker.class);
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport#getConfigurationInspectionClassLoader(org.springframework.core.io.ResourceLoader)
     */
    private ClassLoader getConfigurationInspectionClassLoader(ResourceLoader loader) {

        ClassLoader classLoader = loader.getClassLoader();

        return classLoader != null && LazyJvmAgent.isActive(loader.getClassLoader())
                ? new InspectionClassLoader(loader.getClassLoader())
                : loader.getClassLoader();
    }


    /**
	 * Loads the repository interface contained in the given {@link PermissionCheckerConfiguration} using the given
	 * {@link ClassLoader}.
	 *
	 * @param configuration must not be {@literal null}.
	 * @param classLoader can be {@literal null}.
	 * @return the repository interface or {@literal null} if it can't be loaded.
	 */
	@Nullable
	private Class<?> loadRepositoryInterface(PermissionCheckerConfiguration<?> configuration,
                                             @Nullable ClassLoader classLoader) {

		String repositoryInterface = configuration.getPermissionCheckerInterface();

		try {
			return org.springframework.util.ClassUtils.forName(repositoryInterface, classLoader);
		} catch (ClassNotFoundException | LinkageError e) {
            log.warn(String.format(CLASS_LOADING_ERROR, repositoryInterface, classLoader), e);
		}

		return null;
	}

    /**
     * Utility to determine if a lazy Java agent is being used that might transform classes at a later time.
     *
     * @author Mark Paluch
     * @since 2.1
     */
    @UtilityClass
    static class LazyJvmAgent {

        private static final Set<String> AGENT_CLASSES;

        static {

            Set<String> agentClasses = new LinkedHashSet<>();

            agentClasses.add("org.springframework.instrument.InstrumentationSavingAgent");
            agentClasses.add("org.eclipse.persistence.internal.jpa.deployment.JavaSECMPInitializerAgent");

            AGENT_CLASSES = Collections.unmodifiableSet(agentClasses);
        }

        /**
         * Determine if any agent is active.
         *
         * @return {@literal true} if an agent is active.
         */
        static boolean isActive(@Nullable ClassLoader classLoader) {

            return AGENT_CLASSES.stream() //
                    .anyMatch(agentClass -> ClassUtils.isPresent(agentClass, classLoader));
        }
    }
}
