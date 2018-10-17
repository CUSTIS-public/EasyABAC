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

import custis.easyabac.api.PermissionChecker;
import custis.easyabac.api.utils.ClassTypeInformation;
import custis.easyabac.api.utils.TypeInformation;
import lombok.Getter;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Default implementation of {@link custis.easyabac.api.core.PermissionCheckerMetadata}. Will inspect generic types of {@link PermissionChecker} to find out
 * about domain and id class.
 * 
 * @author Oliver Gierke
 * @author Thomas Darimont
 */
@Getter
public class DefaultPermissionCheckerMetadata implements PermissionCheckerMetadata {

	private static final String MUST_BE_A_PERMISSION_CHECKER = String.format("Given type must be assignable to %s!",
			PermissionChecker.class);

	private final Class<?> actionType;
	private final Class<?> resourceType;
    private final TypeInformation<?> typeInformation;
    private final Class<?> permissionCheckerInterface;


    /**
	 * Creates a new {@link DefaultPermissionCheckerMetadata} for the given repository interface.
	 * 
	 * @param permissionCheckerInterface must not be {@literal null}.
	 */
	public DefaultPermissionCheckerMetadata(Class<?> permissionCheckerInterface) {

        Assert.notNull(permissionCheckerInterface, "Given type must not be null!");
        Assert.isTrue(permissionCheckerInterface.isInterface(), "Given type must be an interface!");

        this.permissionCheckerInterface = permissionCheckerInterface;
        this.typeInformation = ClassTypeInformation.from(permissionCheckerInterface);
		Assert.isTrue(PermissionChecker.class.isAssignableFrom(permissionCheckerInterface), MUST_BE_A_PERMISSION_CHECKER);

		List<TypeInformation<?>> arguments = ClassTypeInformation.from(permissionCheckerInterface) //
				.getRequiredSuperTypeInformation(PermissionChecker.class)//
				.getTypeArguments();

		this.resourceType = resolveTypeParameter(arguments, 0,
				() -> String.format("Could not resolve domain type of %s!", permissionCheckerInterface));
		this.actionType = resolveTypeParameter(arguments, 1,
				() -> String.format("Could not resolve id type of %s!", permissionCheckerInterface));
	}


	/**
	 * Creates a new {@link PermissionCheckerMetadata} for the given repository interface.
	 *
	 * @param repositoryInterface must not be {@literal null}.
	 * @since 1.9
	 * @return
	 */
	public static PermissionCheckerMetadata getMetadata(Class<?> repositoryInterface) {

		Assert.notNull(repositoryInterface, "Repository interface must not be null!");

		return new DefaultPermissionCheckerMetadata(repositoryInterface);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.core.RepositoryMetadata#getPermissionCheckerInterface()
	 */
	public Class<?> getPermissionCheckerInterface() {
		return this.permissionCheckerInterface;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.core.RepositoryMetadata#getAlternativeDomainTypes()
	 */
	@Override
	public Set<Class<?>> getAlternativeDomainTypes() {
		return Collections.emptySet();
	}

	private static Class<?> resolveTypeParameter(List<TypeInformation<?>> arguments, int index,
                                                 Supplier<String> exceptionMessage) {

		if (arguments.size() <= index || arguments.get(index) == null) {
			throw new IllegalArgumentException(exceptionMessage.get());
		}

		return arguments.get(index).getType();
	}
}
