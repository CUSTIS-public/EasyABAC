package custis.easyabac.api.config;

import custis.easyabac.api.impl.EasyABACPermissionCheckerFactoryBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

import static org.springframework.context.annotation.ComponentScan.Filter;

/**
 * Annotation to enable PermissionChecker services. Will scan the package of the annotated configuration class for
 * permission services by default
 *
 * @author Anton Lapitskiy
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(PermissionCheckerBeanDefinitionRegistrar.class)
public @interface EnablePermissionCheckers {

    /**
     * Alias for the {@link #basePackages()} stringAttribute. Allows for more concise annotation declarations e.g.:
     * {@code @EnableJpaRepositories("org.my.pkg")} instead of {@code @EnableJpaRepositories(basePackages="org.my.pkg")}.
     */
    String[] value() default {};

    /**
     * Base packages to scan for annotated components. {@link #value()} is an alias for (and mutually exclusive with) this
     * stringAttribute. Use {@link #basePackageClasses()} for a type-safe alternative to String-based package names.
     */
    String[] basePackages() default {};

    /**
     * Type-safe alternative to {@link #basePackages()} for specifying the packages to scan for annotated components. The
     * package of each class specified will be scanned. Consider creating a special no-op marker class or interface in
     * each package that serves no purpose other than being referenced by this stringAttribute.
     */
    Class<?>[] basePackageClasses() default {};

    /**
     * Specifies which types are eligible for component scanning. Further narrows the set of candidate components from
     * everything in {@link #basePackages()} to everything in the base packages that matches the given filter or filters.
     */
    Filter[] includeFilters() default {};

    /**
     * Specifies which types are not eligible for component scanning.
     */
    Filter[] excludeFilters() default {};

    /**
     * Returns the postfix to be used when looking up custom repository implementations. Defaults to {@literal Impl}. So
     * for a repository named {@code PersonRepository} the corresponding implementation class will be looked up scanning
     * for {@code PersonRepositoryImpl}.
     *
     * @return
     */
    String permissionCheckerImplementationPostfix() default "Impl";

    /**
     * Returns the {@link FactoryBean} class to be used for each repository instance. Defaults to
     * {@link EasyABACPermissionCheckerFactoryBean}.
     *
     * @return
     */
    Class<?> permissionCheckerFactoryBeanClass() default EasyABACPermissionCheckerFactoryBean.class;

    /**
     * Configure the repository base class to be used to create repository proxies for this particular configuration.
     *
     * @return
     */
    Class<?> permissionCheckerBaseClass() default DefaultPermissionCheckerBaseClass.class;
}
