/*
 * Copyright 2014-2017 the original author or authors.
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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Delegate for configuration integration to reuse the general way of detecting repositories. Customization is done by
 * providing a configuration format specific {@link PermissionCheckerConfigurationSource} (currently either XML or annotations
 * are supported).
 * s.
 *
 * @author Oliver Gierke
 * @author Jens Schauder
 */
@Slf4j
public class PermissionCheckerConfigurationDelegate {

	private static final String PERMISSION_CHECKER_REGISTRATION = "Permission Checker EasyABAC - Registering permission checker {} - Interface: {} - Factory: {}";
	private static final String MULTIPLE_MODULES = "Multiple Spring Data modules found, entering strict repository configuration mode!";

	static final String FACTORY_BEAN_OBJECT_TYPE = "factoryBeanObjectType";

	private final PermissionCheckerConfigurationSource configurationSource;
	private final ResourceLoader resourceLoader;
	private final Environment environment;

	/**
	 * Creates a new {@link PermissionCheckerConfigurationDelegate} for the given {@link PermissionCheckerConfigurationSource} and
	 * {@link ResourceLoader} and {@link Environment}.
	 * 
	 * @param configurationSource must not be {@literal null}.
	 * @param resourceLoader must not be {@literal null}.
	 * @param environment must not be {@literal null}.
	 */
	public PermissionCheckerConfigurationDelegate(PermissionCheckerConfigurationSource configurationSource,
												  ResourceLoader resourceLoader, Environment environment) {

		boolean isAnnotation = configurationSource instanceof AnnotationPermissionCheckerConfigurationSource;

		Assert.isTrue(isAnnotation,
				"Configuration source must either be an AnnotationBasedConfigurationSource!");
		Assert.notNull(resourceLoader, "ResourceLoader must not be null!");

		this.configurationSource = configurationSource;
		this.resourceLoader = resourceLoader;
		this.environment = defaultEnvironment(environment, resourceLoader);
	}

	/**
	 * Defaults the environment in case the given one is null. Used as fallback, in case the legacy constructor was
	 * invoked.
	 * 
	 * @param environment can be {@literal null}.
	 * @param resourceLoader can be {@literal null}.
	 * @return
	 */
	private static Environment defaultEnvironment(@Nullable Environment environment,
			@Nullable ResourceLoader resourceLoader) {

		if (environment != null) {
			return environment;
		}

		return resourceLoader instanceof EnvironmentCapable ? ((EnvironmentCapable) resourceLoader).getEnvironment()
				: new StandardEnvironment();
	}

	/**
	 * Registers the found repositories in the given {@link BeanDefinitionRegistry}.
	 * 
	 * @param registry
	 * @return {@link BeanComponentDefinition}s for all repository bean definitions found.
	 */
	public List<BeanComponentDefinition> registerPermissionCheckersIn(BeanDefinitionRegistry registry,
                                                                      PermissionCheckerConfigurationExtension extension) {

		extension.registerBeansForRoot(registry, configurationSource);

		PermissionCheckerBeanDefinitionBuilder builder = new PermissionCheckerBeanDefinitionBuilder(registry, extension, resourceLoader,
				environment);
		List<BeanComponentDefinition> definitions = new ArrayList<>();

		if (log.isDebugEnabled()) {
			log.debug("Scanning for repositories in packages {}.",
					configurationSource.getBasePackages().stream().collect(Collectors.joining(", ")));
		}

		// processing all configurations found
		for (PermissionCheckerConfiguration<? extends PermissionCheckerConfigurationSource> configuration : extension
				.getPermissionCheckerConfigurations(configurationSource, resourceLoader, false)) {

			BeanDefinitionBuilder definitionBuilder = builder.build(configuration);

            extension.postProcess(definitionBuilder, configurationSource); // TODO implement XML
            extension.postProcess(definitionBuilder, (AnnotationPermissionCheckerConfigurationSource) configurationSource);


			AbstractBeanDefinition beanDefinition = definitionBuilder.getBeanDefinition();
			String beanName = configurationSource.generateBeanName(beanDefinition);

			if (log.isDebugEnabled()) {
				log.debug(PERMISSION_CHECKER_REGISTRATION, beanName, configuration.getPermissionCheckerInterface(),
						configuration.getPermissionCheckerFactoryBeanClassName());
			}

			beanDefinition.setAttribute(FACTORY_BEAN_OBJECT_TYPE, configuration.getPermissionCheckerInterface());

			registry.registerBeanDefinition(beanName, beanDefinition);
			definitions.add(new BeanComponentDefinition(beanDefinition, beanName));
		}

		if (log.isDebugEnabled()) {
			log.debug("Finished repository scanning.");
		}

		return definitions;
	}

}
