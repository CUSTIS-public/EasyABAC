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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;

/**
 * Builder to create {@link BeanDefinitionBuilder} instance to eventually create Spring Data repository instances.
 *
 * @author Anton Lapitskiy
 */
class PermissionCheckerBeanDefinitionBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(PermissionCheckerBeanDefinitionBuilder.class);

	private final BeanDefinitionRegistry registry;
	private final PermissionCheckerConfigurationExtension extension;
	private final ResourceLoader resourceLoader;

	/**
	 * Creates a new {@link PermissionCheckerBeanDefinitionBuilder} from the given {@link BeanDefinitionRegistry},
	 * {@link PermissionCheckerConfigurationExtension} and {@link ResourceLoader}.
	 *
	 * @param registry must not be {@literal null}.
	 * @param extension must not be {@literal null}.
	 * @param resourceLoader must not be {@literal null}.
	 * @param environment must not be {@literal null}.
	 */
	public PermissionCheckerBeanDefinitionBuilder(BeanDefinitionRegistry registry, PermissionCheckerConfigurationExtension extension,
                                                  ResourceLoader resourceLoader, Environment environment) {

		Assert.notNull(extension, "PermissionCheckerConfigurationExtension must not be null!");
		Assert.notNull(resourceLoader, "ResourceLoader must not be null!");
		Assert.notNull(environment, "Environment must not be null!");

		this.registry = registry;
		this.extension = extension;
		this.resourceLoader = resourceLoader;
	}

	/**
	 * Builds a new {@link BeanDefinitionBuilder} from the given {@link BeanDefinitionRegistry} and {@link ResourceLoader}
	 * .
	 *
	 * @param configuration must not be {@literal null}.
	 * @return
	 */
	public BeanDefinitionBuilder build(PermissionCheckerConfiguration<?> configuration) {

		Assert.notNull(registry, "BeanDefinitionRegistry must not be null!");
		Assert.notNull(resourceLoader, "ResourceLoader must not be null!");

		BeanDefinitionBuilder builder = BeanDefinitionBuilder
				.rootBeanDefinition(configuration.getPermissionCheckerFactoryBeanClassName());

		builder.getRawBeanDefinition().setSource(configuration.getSource());
		builder.addConstructorArgValue(configuration.getPermissionCheckerInterface());
		builder.addPropertyValue("lazyInit", configuration.isLazyInit());

		configuration.getPermissionCheckerBaseClassName()//
				.ifPresent(it -> builder.addPropertyValue("permissionCheckerBaseClass", it));

		return builder;
	}

}
