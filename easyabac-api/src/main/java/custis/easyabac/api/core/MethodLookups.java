/*
 * Copyright 2017 the original author or authors.
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

import custis.easyabac.api.PermissionChecker;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;

import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.springframework.core.GenericTypeResolver.resolveParameterType;

/**
 * Implementations of method lookup functions.
 *
 * @author Mark Paluch
 * @author Oliver Gierke
 * @since 2.0
 */
public interface MethodLookups {

	/**
	 * Direct method lookup filtering on exact method name, parameter count and parameter types.
	 *
	 * @return direct method lookup.
	 */
	public static MethodLookup direct() {

		MethodLookup.MethodPredicate direct = (invoked, candidate) -> candidate.getName().equals(invoked.getName())
				&& candidate.getParameterCount() == invoked.getParameterCount()
				&& Arrays.equals(candidate.getParameterTypes(), invoked.getParameterTypes());

		return () -> Collections.singletonList(direct);
	}

	/**
	 * Repository type-aware method lookup composed of {@link #direct()} and {@link PermissionCheckerAwareMethodLookup}.
	 * <p/>
	 * Repository-aware lookups resolve generic types from the repository declaration to verify assignability to Id/domain
	 * types. This lookup also permits assignable method signatures but prefers {@link #direct()} matches.
	 *
	 * @param permissionCheckerMetadata must not be {@literal null}.
	 * @return the composed, repository-aware method lookup.
	 * @see #direct()
	 */
	public static MethodLookup forPermissionCheckerTypes(PermissionCheckerMetadata permissionCheckerMetadata) {
		return direct().and(new PermissionCheckerAwareMethodLookup(permissionCheckerMetadata));
	}

	/**
	 * Default {@link MethodLookup} considering repository Id and entity types permitting calls to methods with assignable
	 * arguments.
	 *
	 * @author Mark Paluch
	 */
	static class PermissionCheckerAwareMethodLookup implements MethodLookup {

		@SuppressWarnings("rawtypes") private static final TypeVariable<Class<PermissionChecker>>[] PARAMETERS = PermissionChecker.class
				.getTypeParameters();
		private static final String DOMAIN_TYPE_NAME = PARAMETERS[0].getName();
		private static final String ID_TYPE_NAME = PARAMETERS[1].getName();

		private final ResolvableType entityType, idType;
		private final Class<?> permissionCheckerInterface;

		/**
		 * Creates a new {@link PermissionCheckerAwareMethodLookup} for the given {@link PermissionCheckerMetadata}.
		 * 
		 * @param permissionCheckerMetadata must not be {@literal null}.
		 */
		public PermissionCheckerAwareMethodLookup(PermissionCheckerMetadata permissionCheckerMetadata) {

			Assert.notNull(permissionCheckerMetadata, "Repository metadata must not be null!");

			this.entityType = ResolvableType.forClass(permissionCheckerMetadata.getResourceType());
			this.idType = ResolvableType.forClass(permissionCheckerMetadata.getActionType());
			this.permissionCheckerInterface = permissionCheckerMetadata.getPermissionCheckerInterface();
		}

		/*
		 * (non-Javadoc)
		 * @see org.springframework.data.repository.core.support.MethodLookup#getLookups()
		 */
		@Override
		public List<MethodPredicate> getLookups() {

			MethodPredicate detailedComparison = (invoked, candidate) -> Optional.of(candidate)
					.filter(baseClassMethod -> baseClassMethod.getName().equals(invoked.getName()))// Right name
					.filter(baseClassMethod -> baseClassMethod.getParameterCount() == invoked.getParameterCount())
					.filter(baseClassMethod -> parametersMatch(invoked.getMethod(), baseClassMethod))// All parameters match
					.isPresent();

			return Collections.singletonList(detailedComparison);
		}

		/**
		 * Checks whether the given parameter type matches the generic type of the given parameter. Thus when {@literal PK}
		 * is declared, the method ensures that given method parameter is the primary key type declared in the given
		 * repository interface e.g.
		 *
		 * @param variable must not be {@literal null}.
		 * @param parameterType must not be {@literal null}.
		 * @return
		 */
		protected boolean matchesGenericType(TypeVariable<?> variable, ResolvableType parameterType) {

			GenericDeclaration declaration = variable.getGenericDeclaration();

			if (declaration instanceof Class) {

				if (ID_TYPE_NAME.equals(variable.getName()) && parameterType.isAssignableFrom(idType)) {
					return true;
				}

				Type boundType = variable.getBounds()[0];
				String referenceName = boundType instanceof TypeVariable ? boundType.toString() : variable.toString();

				return DOMAIN_TYPE_NAME.equals(referenceName) && parameterType.isAssignableFrom(entityType);
			}

			for (Type type : variable.getBounds()) {
				if (ResolvableType.forType(type).isAssignableFrom(parameterType)) {
					return true;
				}
			}

			return false;
		}

		/**
		 * Checks the given method's parameters to match the ones of the given base class method. Matches generic arguments
		 * against the ones bound in the given repository interface.
		 *
		 * @param invokedMethod
		 * @param candidate
		 * @return
		 */
		private boolean parametersMatch(Method invokedMethod, Method candidate) {

			Class<?>[] methodParameterTypes = invokedMethod.getParameterTypes();
			Type[] genericTypes = candidate.getGenericParameterTypes();
			Class<?>[] types = candidate.getParameterTypes();

			for (int i = 0; i < genericTypes.length; i++) {

				Type genericType = genericTypes[i];
				Class<?> type = types[i];
				MethodParameter parameter = new MethodParameter(invokedMethod, i);
				Class<?> parameterType = resolveParameterType(parameter, permissionCheckerInterface);

				if (genericType instanceof TypeVariable<?>) {

					if (!matchesGenericType((TypeVariable<?>) genericType, ResolvableType.forMethodParameter(parameter))) {
						return false;
					}

					continue;
				}

				if (types[i].equals(parameterType)) {
					continue;
				}

				if (!type.isAssignableFrom(parameterType) || !type.equals(methodParameterTypes[i])) {
					return false;
				}
			}

			return true;
		}
	}

}
