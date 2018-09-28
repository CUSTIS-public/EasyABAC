/*
 * Copyright 2012-2017 the original author or authors.
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

import custis.easyabac.api.NoPermissionCheckerBean;
import custis.easyabac.api.PermissionChecker;
import custis.easyabac.api.PermissionCheckerDefinition;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Custom {@link ClassPathScanningCandidateComponentProvider} scanning for interfaces extending the given base
 * interface. Skips interfaces annotated with {@link NoPermissionCheckerBean}.
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 */
class PermissionCheckerComponentProvider extends ClassPathScanningCandidateComponentProvider {

    private BeanDefinitionRegistry registry;

    /**
     * Creates a new {@link PermissionCheckerComponentProvider} using the given {@link TypeFilter} to include components to be
     * picked up.
     *
     */
    public PermissionCheckerComponentProvider(Iterable<? extends TypeFilter> includeFilters, BeanDefinitionRegistry registry) {

        super(false);

        Assert.notNull(registry, "BeanDefinitionRegistry must not be null!");

        this.registry = registry;

        if (includeFilters.iterator().hasNext()) {
            for (TypeFilter filter : includeFilters) {
                addIncludeFilter(filter);
            }
        } else {
            super.addIncludeFilter(new InterfaceTypeFilter(PermissionChecker.class));
            super.addIncludeFilter(new AnnotationTypeFilter(PermissionCheckerDefinition.class, true, true));
        }

        addExcludeFilter(new AnnotationTypeFilter(NoPermissionCheckerBean.class));
    }

    /**
     * Custom extension of {@link #addIncludeFilter(TypeFilter)} to extend the added {@link TypeFilter}. For the
     * {@link TypeFilter} handed we'll have two filters registered: one additionally enforcing the
     * {@link PermissionCheckerDefinition} annotation, the other one forcing the extension of {@link PermissionChecker}.
     *
     * @see ClassPathScanningCandidateComponentProvider#addIncludeFilter(TypeFilter)
     */
    @Override
    public void addIncludeFilter(TypeFilter includeFilter) {

        List<TypeFilter> filterPlusInterface = new ArrayList<>(2);
        filterPlusInterface.add(includeFilter);
        filterPlusInterface.add(new InterfaceTypeFilter(PermissionChecker.class));

        super.addIncludeFilter(new AllTypeFilter(filterPlusInterface));

        List<TypeFilter> filterPlusAnnotation = new ArrayList<>(2);
        filterPlusAnnotation.add(includeFilter);
        filterPlusAnnotation.add(new AnnotationTypeFilter(PermissionCheckerDefinition.class, true, true));

        super.addIncludeFilter(new AllTypeFilter(filterPlusAnnotation));
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#isCandidateComponent(org.springframework.beans.factory.annotation.AnnotatedBeanDefinition)
     */
    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {

        boolean isNonRepositoryInterface = !PermissionChecker.class.getName().equals(beanDefinition.getBeanClassName());
        boolean isTopLevelType = !beanDefinition.getMetadata().hasEnclosingClass();

        return isNonRepositoryInterface && isTopLevelType;
    }

    /**
     * Customizes the repository interface detection and triggers annotation detection on them.
     */
    @Override
    public Set<BeanDefinition> findCandidateComponents(String basePackage) {

        Set<BeanDefinition> candidates = super.findCandidateComponents(basePackage);

        for (BeanDefinition candidate : candidates) {
            if (candidate instanceof AnnotatedBeanDefinition) {
                AnnotationConfigUtils.processCommonDefinitionAnnotations((AnnotatedBeanDefinition) candidate);
            }
        }

        return candidates;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#getRegistry()
     */
    @Override
    protected BeanDefinitionRegistry getRegistry() {
        return registry;
    }

    /**
     * {@link TypeFilter} that only matches interfaces. Thus setting this up makes
     * only sense providing an interface type as {@code targetType}.
     *
     * @author Oliver Gierke
     */
    private static class InterfaceTypeFilter extends AssignableTypeFilter {

        /**
         * Creates a new {@link InterfaceTypeFilter}.
         *
         * @param targetType
         */
        public InterfaceTypeFilter(Class<?> targetType) {
            super(targetType);
        }

        /*
         * (non-Javadoc)
         * @see org.springframework.core.type.filter.AbstractTypeHierarchyTraversingFilter#match(org.springframework.core.type.classreading.MetadataReader, org.springframework.core.type.classreading.MetadataReaderFactory)
         */
        @Override
        public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
                throws IOException {

            return metadataReader.getClassMetadata().isInterface() && super.match(metadataReader, metadataReaderFactory);
        }
    }

    /**
     * Helper class to create a {@link TypeFilter} that matches if all the delegates match.
     *
     * @author Oliver Gierke
     */
    private static class AllTypeFilter implements TypeFilter {

        private final List<TypeFilter> delegates;

        /**
         * Creates a new {@link AllTypeFilter} to match if all the given delegates match.
         *
         * @param delegates must not be {@literal null}.
         */
        public AllTypeFilter(List<TypeFilter> delegates) {

            Assert.notNull(delegates, "TypeFilter deleages must not be null!");
            this.delegates = delegates;
        }

        /*
         * (non-Javadoc)
         * @see org.springframework.core.type.filter.TypeFilter#match(org.springframework.core.type.classreading.MetadataReader, org.springframework.core.type.classreading.MetadataReaderFactory)
         */
        public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
                throws IOException {

            for (TypeFilter filter : delegates) {
                if (!filter.match(metadataReader, metadataReaderFactory)) {
                    return false;
                }
            }

            return true;
        }
    }
}
