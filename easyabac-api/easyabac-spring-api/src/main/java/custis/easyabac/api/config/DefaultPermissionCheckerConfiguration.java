/*
 * Copyright 2012-2018 the original author or authors.
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

import custis.easyabac.api.utils.ConfigurationUtils;
import custis.easyabac.api.utils.Streamable;
import lombok.NonNull;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * Default implementation of {@link PermissionCheckerConfiguration}.
 *
 * @author Anton Lapitskiy
 */
public class DefaultPermissionCheckerConfiguration<T extends PermissionCheckerConfigurationSource>
		implements PermissionCheckerConfiguration<T> {

	public static final String DEFAULT_PERMISSION_CHECKER_IMPLEMENTATION_POSTFIX = "Impl";

	private final @NonNull T configurationSource;
	private final @NonNull BeanDefinition definition;
	private final @NonNull PermissionCheckerConfigurationExtension extension;

	public DefaultPermissionCheckerConfiguration(T configurationSource, BeanDefinition definition, PermissionCheckerConfigurationExtension extension) {
		this.configurationSource = configurationSource;
		this.definition = definition;
		this.extension = extension;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config.RepositoryConfiguration#getBeanId()
	 */
	public String getBeanId() {
		return StringUtils.uncapitalize(ClassUtils.getShortName(getPermissionCheckerBaseClassName().orElseThrow(
				() -> new IllegalStateException("Can't create bean identifier without a repository base class defined!"))));
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config.RepositoryConfiguration#getBasePackages()
	 */
	public Streamable<String> getBasePackages() {
		return configurationSource.getBasePackages();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config.RepositoryConfiguration#getImplementationBasePackages()
	 */
	@Override
	public Streamable<String> getImplementationBasePackages() {
		return Streamable.of(ClassUtils.getPackageName(getPermissionCheckerInterface()));
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config.RepositoryConfiguration#getPermissionCheckerInterface()
	 */
	public String getPermissionCheckerInterface() {
		return ConfigurationUtils.getRequiredBeanClassName(definition);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config.RepositoryConfiguration#getConfigSource()
	 */
	public PermissionCheckerConfigurationSource getConfigSource() {
		return configurationSource;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config.RepositoryConfiguration#getImplementationClassName()
	 */
	public String getImplementationClassName() {
		return ClassUtils.getShortName(getPermissionCheckerInterface()).concat(
				configurationSource.getPermissionCheckerImplementationPostfix().orElse(DEFAULT_PERMISSION_CHECKER_IMPLEMENTATION_POSTFIX));
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config.RepositoryConfiguration#getImplementationBeanName()
	 */
	public String getImplementationBeanName() {
		return configurationSource.generateBeanName(definition)
				+ configurationSource.getPermissionCheckerImplementationPostfix().orElse("Impl");
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config.RepositoryConfiguration#getSource()
	 */
	@Nullable
	@Override
	public Object getSource() {
		return configurationSource.getSource();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config.RepositoryConfiguration#getConfigurationSource()
	 */
	@Override
	public T getConfigurationSource() {
		return configurationSource;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config.RepositoryConfiguration#getPermissionCheckerBaseClassName()
	 */
	@Override
	public Optional<String> getPermissionCheckerBaseClassName() {
		return configurationSource.getPermissionCheckerBaseClassName();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config.RepositoryConfiguration#getPermissionCheckerFactoryBeanClassName()
	 */
	@Override
	public String getPermissionCheckerFactoryBeanClassName() {

		return configurationSource.getPermissionCheckerFactoryBeanClassName()
				.orElseGet(() -> extension.getPermissionCheckerFactoryBeanClassName());
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config.RepositoryConfiguration#isLazyInit()
	 */
	@Override
	public boolean isLazyInit() {
		return definition.isLazyInit();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.config.RepositoryConfiguration#getExcludeFilters()
	 */
	@Override
	public Streamable<TypeFilter> getExcludeFilters() {
		return configurationSource.getExcludeFilters();
	}

}
