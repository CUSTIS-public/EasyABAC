/*
 * Copyright 2012-2014 the original author or authors.
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

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.io.ResourceLoader;

import java.util.Collection;

/**
 * SPI to implement store specific extension to the repository bean definition registration process.
 * 
 * @see PermissionCheckerConfigurationExtensionImpl
 * @author Oliver Gierke
 */
public interface PermissionCheckerConfigurationExtension {

	/**
	 * Returns all {@link PermissionCheckerConfiguration}s obtained through the given {@link PermissionCheckerConfigurationSource}.
	 *
	 * @param configSource
	 * @param loader
	 * @param strictMatchesOnly whether to return strict repository matches only. Handing in {@literal true} will cause
	 *          the repository interfaces and domain types handled to be checked whether they are managed by the current
	 *          store.
	 * @return
	 * @since 1.9
	 */
	<T extends PermissionCheckerConfigurationSource> Collection<PermissionCheckerConfiguration<T>> getPermissionCheckerConfigurations(
            T configSource, ResourceLoader loader, boolean strictMatchesOnly);

	/**
	 * Returns the name of the repository factory class to be used.
	 * 
	 * @return
	 */
	String getPermissionCheckerFactoryBeanClassName();

	/**
	 * Callback to register additional bean definitions for a {@literal repositories} root node. This usually includes
	 * beans you have to set up once independently of the number of repositories to be created. Will be called before any
	 * repositories bean definitions have been registered.
	 * 
	 * @param registry
	 * @param configurationSource
	 */
	void registerBeansForRoot(BeanDefinitionRegistry registry, PermissionCheckerConfigurationSource configurationSource);

	/**
	 * Callback to post process the {@link BeanDefinition} and tweak the configuration if necessary.
	 * 
	 * @param builder will never be {@literal null}.
	 * @param config will never be {@literal null}.
	 */
	void postProcess(BeanDefinitionBuilder builder, PermissionCheckerConfigurationSource config);

	/**
	 * Callback to post process the {@link BeanDefinition} built from annotations and tweak the configuration if
	 * necessary.
	 * 
	 * @param builder will never be {@literal null}.
	 * @param config will never be {@literal null}.
	 */
	void postProcess(BeanDefinitionBuilder builder, AnnotationPermissionCheckerConfigurationSource config);

}
