/*
 * Copyright 2011-2017 the original author or authors.
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
package custis.easyabac.api.impl;

import custis.easyabac.api.core.PermissionCheckerComposition;
import custis.easyabac.api.core.PermissionCheckerInformation;
import custis.easyabac.api.core.PermissionCheckerMetadata;
import custis.easyabac.api.utils.Streamable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.util.ReflectionUtils.makeAccessible;

/**
 * Default implementation of {@link PermissionCheckerInformation}.
 *
 * @author Anton Lapitskiy
 */
class DefaultPermissionCheckerInformation implements PermissionCheckerInformation {

	private final Map<Method, Method> methodCache = new ConcurrentHashMap<>();

	private final PermissionCheckerMetadata metadata;
	private final Class<?> permissionCheckerBaseClass;
	private final PermissionCheckerComposition composition;
	private final PermissionCheckerComposition baseComposition;

	/**
	 * Creates a new {@link DefaultPermissionCheckerMetadata} for the given repository interface and repository base class.
	 *
	 * @param metadata must not be {@literal null}.
	 * @param permissionCheckerBaseClass must not be {@literal null}.
	 * @param composition must not be {@literal null}.
	 */
	public DefaultPermissionCheckerInformation(PermissionCheckerMetadata metadata, Class<?> permissionCheckerBaseClass,
											   PermissionCheckerComposition composition) {

		Assert.notNull(metadata, "Repository metadata must not be null!");
		Assert.notNull(permissionCheckerBaseClass, "Repository base class must not be null!");
		Assert.notNull(composition, "Repository composition must not be null!");

		this.metadata = metadata;
		this.permissionCheckerBaseClass = permissionCheckerBaseClass;
		this.composition = composition;
		this.baseComposition = PermissionCheckerComposition.empty() //
				.withArgumentConverter(composition.getArgumentConverter()) //
				.withMethodLookup(composition.getMethodLookup());
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.support.RepositoryMetadata#getDomainClass()
	 */
	@Override
	public Class<?> getResourceType() {
		return metadata.getResourceType();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.support.RepositoryMetadata#getIdClass()
	 */
	@Override
	public Class<?> getActionType() {
		return metadata.getActionType();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.support.RepositoryInformation#getPermissionCheckerBaseClass()
	 */
	@Override
	public Class<?> getPermissionCheckerBaseClass() {
		return this.permissionCheckerBaseClass;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.support.RepositoryInformation#getTargetClassMethod(java.lang.reflect.Method)
	 */
	@Override
	public Method getTargetClassMethod(Method method) {

		if (methodCache.containsKey(method)) {
			return methodCache.get(method);
		}

		Method result = composition.findMethod(method).orElse(method);

		if (!result.equals(method)) {
			return cacheAndReturn(method, result);
		}

		return cacheAndReturn(method, baseComposition.findMethod(method).orElse(method));
	}

	private Method cacheAndReturn(Method key, Method value) {

		if (value != null) {
			makeAccessible(value);
		}

		methodCache.put(key, value);
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.support.RepositoryInformation#getAuthorizationCalls()
	 */
	@Override
	public Streamable<Method> getAuthorizationCalls() {

		Set<Method> result = new HashSet<>();

		for (Method method : getPermissionCheckerInterface().getMethods()) {
			method = ClassUtils.getMostSpecificMethod(method, getPermissionCheckerInterface());
			if (isAuthorizationCallCandidate(method)) {
				result.add(method);
			}
		}

		return Streamable.of(Collections.unmodifiableSet(result));
	}

	/**
	 * Checks whether the given method is a query method candidate.
	 *
	 * @param method
	 * @return
	 */
	private boolean isAuthorizationCallCandidate(Method method) {
	    return !method.isBridge() && !method.isDefault() //
				&& !Modifier.isStatic(method.getModifiers()) //
				&& !isBaseClassMethod(method);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.core.RepositoryInformation#isAuthorizationCall(java.lang.reflect.Method)
	 */
	@Override
	public boolean isAuthorizationCall(Method method) {
		return getAuthorizationCalls().stream().anyMatch(it -> it.equals(method));
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.core.RepositoryInformation#isBaseClassMethod(java.lang.reflect.Method)
	 */
	@Override
	public boolean isBaseClassMethod(Method method) {

		Assert.notNull(method, "Method must not be null!");
		return baseComposition.findMethod(method).isPresent();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.core.RepositoryMetadata#getPermissionCheckerInterface()
	 */
	@Override
	public Class<?> getPermissionCheckerInterface() {
		return metadata.getPermissionCheckerInterface();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.core.RepositoryMetadata#getAlternativeDomainTypes()
	 */
	@Override
	public Set<Class<?>> getAlternativeDomainTypes() {
		return metadata.getAlternativeDomainTypes();
	}

}
