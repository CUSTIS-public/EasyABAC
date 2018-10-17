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

import custis.easyabac.api.utils.Streamable;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.Assert;

import java.util.Collections;

/**
 * Base class to implement {@link PermissionCheckerConfigurationSource}s.
 *
 * @author Anton Lapitskiy
 */
public abstract class BasePermissionCheckerConfigurationSource implements PermissionCheckerConfigurationSource {

	protected static final String DEFAULT_PERMISSION_CHECKER_IMPL_POSTFIX = "Impl";

	private final Environment environment;
	private final PermissionCheckerBeanNameGenerator beanNameGenerator;
	private final BeanDefinitionRegistry registry;

	/**
	 * Creates a new {@link BasePermissionCheckerConfigurationSource} with the given environment.
	 *
	 * @param environment must not be {@literal null}.
	 * @param classLoader must not be {@literal null}.
	 * @param registry must not be {@literal null}.
	 */
	public BasePermissionCheckerConfigurationSource(Environment environment, ClassLoader classLoader,
													BeanDefinitionRegistry registry) {

		Assert.notNull(environment, "Environment must not be null!");
		Assert.notNull(classLoader, "ClassLoader must not be null!");
		Assert.notNull(registry, "BeanDefinitionRegistry must not be null!");

		this.environment = environment;
		this.beanNameGenerator = new PermissionCheckerBeanNameGenerator(classLoader);
		this.registry = registry;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config.RepositoryConfigurationSource#getCandidates(org.springframework.core.io.ResourceLoader)
	 */
	@Override
	public Streamable<BeanDefinition> getCandidates(ResourceLoader loader) {

		PermissionCheckerComponentProvider scanner = new PermissionCheckerComponentProvider(getIncludeFilters(), registry);
		scanner.setEnvironment(environment);
		scanner.setResourceLoader(loader);

        getExcludeFilters().forEach(it -> scanner.addExcludeFilter(it));

        return Streamable.of(() -> getBasePackages().stream()//
				.flatMap(it -> scanner.findCandidateComponents(it).stream()));
	}

	/**
	 * Return the {@link TypeFilter}s to define which types to exclude when scanning for repositories. Default
	 * implementation returns an empty collection.
	 *
	 * @return must not be {@literal null}.
	 */
	@Override
	public Streamable<TypeFilter> getExcludeFilters() {
		return Streamable.empty();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config.RepositoryConfigurationSource#getBeanNameGenerator()
	 */
	@Override
	public String generateBeanName(BeanDefinition beanDefinition) {
		return beanNameGenerator.generateBeanName(beanDefinition);
	}

	/**
	 * Return the {@link TypeFilter}s to define which types to include when scanning for repositories. Default
	 * implementation returns an empty collection.
	 *
	 * @return must not be {@literal null}.
	 */
	protected Iterable<TypeFilter> getIncludeFilters() {
		return Collections.emptySet();
	}

}
