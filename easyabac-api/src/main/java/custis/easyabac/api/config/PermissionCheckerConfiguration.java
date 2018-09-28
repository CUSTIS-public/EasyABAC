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

import custis.easyabac.api.utils.Streamable;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.lang.Nullable;

import java.util.Optional;

/**
 * Configuration information for a single repository instance.
 *
 * @author Oliver Gierke
 * @author Mark Paluch
 */
public interface PermissionCheckerConfiguration<T extends PermissionCheckerConfigurationSource> {

	/**
	 * Returns the base packages that the repository was scanned under.
	 *
	 * @return
	 */
	Streamable<String> getBasePackages();

	/**
	 * Returns the base packages to scan for repository implementations.
	 *
	 * @return
	 * @since 2.0
	 */
	Streamable<String> getImplementationBasePackages();

	/**
	 * Returns the class name of the custom implementation.
	 *
	 * @return
	 * @deprecated since 2.0. Use repository compositions by creating mixins.
	 */
	@Deprecated
	String getImplementationClassName();

	/**
	 * Returns the bean name of the custom implementation.
	 *
	 * @return
	 * @deprecated since 2.0. Use repository compositions by creating mixins.
	 */
	@Deprecated
	String getImplementationBeanName();

	/**
	 * Returns the interface name of the repository.
	 *
	 * @return
	 */
	String getPermissionCheckerInterface();

	/**
	 * Returns the name of the repository base class to be used or {@literal null} if the store specific defaults shall be
	 * applied.
	 *
	 * @return
	 * @since 1.11
	 */
	Optional<String> getPermissionCheckerBaseClassName();

	/**
	 * Returns the name of the repository factory bean class to be used.
	 *
	 * @return
	 */
	String getPermissionCheckerFactoryBeanClassName();

	/**
	 * Returns the source of the {@link PermissionCheckerConfiguration}.
	 *
	 * @return
	 */
	@Nullable
	Object getSource();

	/**
	 * Returns the {@link PermissionCheckerConfigurationSource} that backs the {@link PermissionCheckerConfiguration}.
	 *
	 * @return
	 */
	T getConfigurationSource();

	/**
	 * Returns whether to initialize the repository proxy lazily.
	 *
	 * @return
	 */
	boolean isLazyInit();

	/**
	 * Returns the {@link TypeFilter}s to be used to exclude packages from repository scanning.
	 *
	 * @return
	 */
	Streamable<TypeFilter> getExcludeFilters();

}
