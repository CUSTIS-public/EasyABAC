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

import custis.easyabac.api.impl.EasyABACPermissionChecker;
import lombok.Getter;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * Composite implementation to back repository method implementations.
 *
 * @since 2.0
 */
public class PermissionCheckerComposition {

	private static final BiFunction<Method, Object[], Object[]> PASSTHRU_ARG_CONVERTER = (methodParameter, o) -> o;
	private static final PermissionCheckerComposition EMPTY = new PermissionCheckerComposition(MethodLookups.direct(), PASSTHRU_ARG_CONVERTER);

	private final Map<Method, Optional<Method>> methodCache = new ConcurrentReferenceHashMap<>();
	private final @Getter
	MethodLookup methodLookup;
	private final @Getter
    BiFunction<Method, Object[], Object[]> argumentConverter;

	public PermissionCheckerComposition(MethodLookup methodLookup, BiFunction<Method, Object[], Object[]> argumentConverter) {
		this.methodLookup = methodLookup;
		this.argumentConverter = argumentConverter;
	}

	/**
	 * Create an empty {@link PermissionCheckerComposition}.
	 *
	 * @return an empty {@link PermissionCheckerComposition}.
	 */
	public static PermissionCheckerComposition empty() {
		return EMPTY;
	}

	/**
	 * Create a new {@link PermissionCheckerComposition} retaining current configuration and set {@code argumentConverter}.
	 *
	 * @param argumentConverter must not be {@literal null}.
	 * @return the new {@link PermissionCheckerComposition}.
	 */
	public PermissionCheckerComposition withArgumentConverter(BiFunction<Method, Object[], Object[]> argumentConverter) {
		return new PermissionCheckerComposition(methodLookup, argumentConverter);
	}

	/**
	 * Create a new {@link PermissionCheckerComposition} retaining current configuration and set {@code methodLookup}.
	 *
	 * @param methodLookup must not be {@literal null}.
	 * @return the new {@link PermissionCheckerComposition}.
	 */
	public PermissionCheckerComposition withMethodLookup(MethodLookup methodLookup) {
		return new PermissionCheckerComposition(methodLookup, argumentConverter);
	}

	/**
	 * Find the implementation method for the given {@link Method} invoked on the composite interface.
	 *
	 * @param method must not be {@literal null}.
	 * @return
	 */
	public Optional<Method> findMethod(Method method) {
		return methodCache.computeIfAbsent(method,
				key -> findMethod(MethodLookup.InvokedMethod.of(key), methodLookup,
                        Arrays.asList(EasyABACPermissionChecker.class.getMethods())
                ));
	}

    private static Optional<Method> findMethod(MethodLookup.InvokedMethod invokedMethod, MethodLookup lookup,
                                               List<Method> methods) {

        for (MethodLookup.MethodPredicate methodPredicate : lookup.getLookups()) {

            Optional<Method> resolvedMethod = methods.stream()
                    .filter(it -> methodPredicate.test(invokedMethod, it)) //
                    .findFirst();

            if (resolvedMethod.isPresent()) {
                return resolvedMethod;
            }
        }

        return Optional.empty();
    }

}
