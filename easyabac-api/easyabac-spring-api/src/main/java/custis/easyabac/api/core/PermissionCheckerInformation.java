/*
 * Copyright 2011-2014 the original author or authors.
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
package custis.easyabac.api.core;

import custis.easyabac.api.utils.Streamable;

import java.lang.reflect.Method;

/**
 * Additional repository specific information
 * 
 * @author Oliver Gierke
 */
public interface PermissionCheckerInformation extends PermissionCheckerMetadata {

	/**
	 * Returns the base class to be used to create the proxy backing instance.
	 * 
	 * @return
	 */
	Class<?> getPermissionCheckerBaseClass();

	/**
	 * Returns whether the given method is a query method.
	 * 
	 * @param method
	 * @return
	 */
	boolean isAuthorizationCall(Method method);

	/**
	 * Returns whether the given method is logically a base class method. This also includes methods (re)declared in the
	 * repository interface that match the signatures of the base implementation.
	 * 
	 * @param method must not be {@literal null}.
	 * @return
	 */
	boolean isBaseClassMethod(Method method);

	/**
	 * Returns all methods considered to be query methods.
	 *
	 * @return
	 */
	Streamable<Method> getAuthorizationCalls();

	/**
	 * Returns the target class method that is backing the given method. This can be necessary if a repository interface
	 * redeclares a method of the core repository interface (e.g. for transaction behavior customization). Returns the
	 * method itself if the target class does not implement the given method. Implementations need to make sure the
	 * {@link Method} returned can be invoked via reflection, i.e. needs to be accessible.
	 * 
	 * @param method must not be {@literal null}.
	 * @return
	 */
	Method getTargetClassMethod(Method method);

	default boolean hasAuthorizationCalls() {
		return getAuthorizationCalls().iterator().hasNext();
	}
}
