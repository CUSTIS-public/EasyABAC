package custis.easyabac.api.impl;

import custis.easyabac.api.PermissionChecker;
import custis.easyabac.pdp.AttributiveAuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.util.Lazy;
import org.springframework.util.Assert;

import java.util.Optional;

@Slf4j
public class EasyABACPermissionCheckerFactoryBean<T extends PermissionChecker<S, A>, S, A>
    implements InitializingBean, BeanClassLoaderAware, BeanFactoryAware, ApplicationEventPublisherAware,
        FactoryBean<T>, ApplicationContextAware {

    private final Class<? extends T> permissionCheckerInterface;

    private EasyABACPermissionCheckerFactory factory;
    private Optional<Class<?>> permissionCheckerBaseClass = Optional.empty();
    private ClassLoader classLoader;
    private BeanFactory beanFactory;
    private boolean lazyInit = false;
    private ApplicationEventPublisher publisher;

    private Lazy<T> permissionChecker;
    private AttributiveAuthorizationService attributiveAuthorizationService;

    /**
     * Creates a new {@link EasyABACPermissionCheckerFactoryBean} for the given repository interface.
     *
     * @param permissionCheckerInterface must not be {@literal null}.
     */
    protected EasyABACPermissionCheckerFactoryBean(Class<? extends T> permissionCheckerInterface) {

        Assert.notNull(permissionCheckerInterface, "Repository interface must not be null!");
        this.permissionCheckerInterface = permissionCheckerInterface;
    }

    /**
     * Configures the repository base class to be used.
     *
     * @param permissionCheckerBaseClass the repositoryBaseClass to set, can be {@literal null}.
     * @since 1.11
     */
    public void setPermissionCheckerBaseClass(Class<?> permissionCheckerBaseClass) {
        this.permissionCheckerBaseClass = Optional.ofNullable(permissionCheckerBaseClass);
    }

    /**
     * Configures whether to initialize the repository proxy lazily. This defaults to {@literal false}.
     *
     * @param lazy whether to initialize the repository proxy lazily. This defaults to {@literal false}.
     */
    public void setLazyInit(boolean lazy) {
        this.lazyInit = lazy;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.FactoryBean#getObject()
     */
    public T getObject() {
        return this.permissionChecker.get();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.FactoryBean#getObjectType()
     */
    public Class<? extends T> getObjectType() {
        return permissionCheckerInterface;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.FactoryBean#isSingleton()
     */
    public boolean isSingleton() {
        return true;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() {
        Assert.notNull(attributiveAuthorizationService, "AttributiveAuthorizationService should not be null");

        this.factory = createPermissionCheckerFactory();
        this.factory.setBeanClassLoader(classLoader);
        this.factory.setBeanFactory(beanFactory);

        this.permissionChecker = Lazy.of(() -> this.factory.getPermissionChecker(permissionCheckerInterface));

        if (!lazyInit) {
            this.permissionChecker.get();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.BeanClassLoaderAware#setBeanClassLoader(java.lang.ClassLoader)
     */
    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
     */
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.context.ApplicationEventPublisherAware#setApplicationEventPublisher(org.springframework.context.ApplicationEventPublisher)
     */
    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }


    protected EasyABACPermissionCheckerFactory createPermissionCheckerFactory() {
        return new EasyABACPermissionCheckerFactory(attributiveAuthorizationService);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.attributiveAuthorizationService = applicationContext.getBean(AttributiveAuthorizationService.class);
    }
}
