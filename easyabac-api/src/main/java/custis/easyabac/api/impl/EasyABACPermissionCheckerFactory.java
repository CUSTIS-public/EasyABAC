package custis.easyabac.api.impl;

import custis.easyabac.api.PermissionChecker;
import custis.easyabac.api.core.*;
import custis.easyabac.pdp.AttributiveAuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.interceptor.ExposeInvocationInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.util.Map;
import java.util.Optional;

@Slf4j
public class EasyABACPermissionCheckerFactory implements BeanClassLoaderAware, BeanFactoryAware {

    private Optional<Class<?>> permissionCheckerBaseClass;
    protected ClassLoader classLoader;
    protected BeanFactory beanFactory;

    private final Map<String, PermissionCheckerInformation> repositoryInformationCache;
    private final AttributiveAuthorizationService attributiveAuthorizationService;

    public EasyABACPermissionCheckerFactory(AttributiveAuthorizationService attributiveAuthorizationService) {
        this.attributiveAuthorizationService = attributiveAuthorizationService;
        this.repositoryInformationCache = new ConcurrentReferenceHashMap<>(16, ConcurrentReferenceHashMap.ReferenceType.WEAK);

        this.permissionCheckerBaseClass = Optional.empty();
        this.classLoader = org.springframework.util.ClassUtils.getDefaultClassLoader();
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public void setPermissionCheckerBaseClass(Optional<Class<?>> permissionCheckerBaseClass) {
        this.permissionCheckerBaseClass = permissionCheckerBaseClass;
    }

    /**
     * Callback to create a {@link EasyABACPermissionChecker} instance with the given {@link EntityManager}
     *
     * @return
     */
    private EasyABACPermissionChecker<?, ?> getTargetPermissionChecker(PermissionCheckerInformation information) {

        // FIXME JpaEntityInformation<?, ?> entityInformation = getEntityInformation(information.getResourceType());

        // FIXME return getTargetRepositoryViaReflection(information, entityInformation, attributiveAuthorizationService);
        return new EasyABACPermissionChecker<>(attributiveAuthorizationService);
    }

    /**
     * Returns the {@link PermissionCheckerMetadata} for the given repository interface.
     *
     * @param repositoryInterface will never be {@literal null}.
     * @return
     */
    protected PermissionCheckerMetadata getPermissionCheckerMetadata(Class<?> repositoryInterface) {
        return DefaultPermissionCheckerMetadata.getMetadata(repositoryInterface);
    }

    public <T> T getPermissionChecker(Class<T> permissionCheckerInterface) {
        if (log.isDebugEnabled()) {
            log.debug("Initializing permission checker instance for {}…", permissionCheckerInterface.getName());
        }

        Assert.notNull(permissionCheckerInterface, "Permission Checker interface must not be null!");

        PermissionCheckerMetadata metadata = getPermissionCheckerMetadata(permissionCheckerInterface);
        PermissionCheckerComposition composition = getPermissionCheckerComposition(metadata);
        PermissionCheckerInformation information = getPermissionCheckerInformation(metadata, composition);

        // validate(information, composition);

        Object target = getTargetPermissionChecker(information);

        // Create proxy
        ProxyFactory result = new ProxyFactory();
        result.setTarget(target);
        result.setInterfaces(permissionCheckerInterface, PermissionChecker.class);
        result.addAdvisor(ExposeInvocationInterceptor.ADVISOR);

        /** FIXME

        postProcessors.forEach(processor -> processor.postProcess(result, information));
*/


        result.addAdvice(new DefaultMethodInterceptor());
        result.addAdvice(new DynamicMethodInterceptor(information, attributiveAuthorizationService));
        T repository = (T) result.getProxy(classLoader);

        if (log.isDebugEnabled()) {
            log.debug("Finished creation of permission checker instance for {}.", permissionCheckerInterface.getName());
        }

        return repository;
    }

    private PermissionCheckerComposition getPermissionCheckerComposition(PermissionCheckerMetadata metadata) {
        PermissionCheckerComposition composition = PermissionCheckerComposition.empty();
        return composition.withMethodLookup(MethodLookups.forPermissionCheckerTypes(metadata));
    }

    /**
     * Returns the {@link PermissionCheckerInformation} for the given repository interface.
     *
     * @param metadata
     * @param composition
     * @return
     */
    private PermissionCheckerInformation getPermissionCheckerInformation(PermissionCheckerMetadata metadata, PermissionCheckerComposition composition) {
        return repositoryInformationCache.computeIfAbsent(metadata.getPermissionCheckerInterface().getName() + "#" + composition.hashCode(), key -> {

            Class<?> baseClass = permissionCheckerBaseClass.orElse(EasyABACPermissionChecker.class);

            return new DefaultPermissionCheckerInformation(metadata, baseClass, composition);
        });
    }

}
