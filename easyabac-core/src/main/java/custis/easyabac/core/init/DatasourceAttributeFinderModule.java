package custis.easyabac.core.init;

import custis.easyabac.core.EasyAbacDatasourceException;
import custis.easyabac.core.cache.Cache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.BagAttribute;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.Status;
import org.wso2.balana.finder.AttributeFinderModule;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DatasourceAttributeFinderModule extends AttributeFinderModule {

    private final static Log log = LogFactory.getLog(DatasourceAttributeFinderModule.class);

    private final Datasource datasource;
    private final Cache cache;

    public DatasourceAttributeFinderModule(Datasource datasource, Cache cache) {

        this.datasource = datasource;
        this.cache = cache;
    }

    @Override
    public Set<String> getSupportedCategories() {
        Set<String> categories = new HashSet<>();
        categories.add(datasource.getRequiredAttribute().getCategory().getXacmlName());
        return categories;
    }

    @Override
    public Set getSupportedIds() {
        Set<String> ids = new HashSet<>();
        ids.add(datasource.getRequiredAttribute().getXacmlName());
        return ids;
    }

    @Override
    public EvaluationResult findAttribute(URI attributeType, URI attributeId, String issuer,
                                          URI category, EvaluationCtx context) {
        for (Param param : datasource.getParams()) {
            try {
                String paramValues = extractParamValue(context, param);

                param.setValue(paramValues);
            } catch (EasyAbacDatasourceException e) {
                return getMissingEvaluationResult(e.getMessage());
            }
        }

        List<String> requiredValue;
        List<String> cachedValue = null;
        if (cache != null) {
            try {
                cachedValue = cache.get(datasource.getParams(), datasource.getRequiredAttribute());
            } catch (RuntimeException e) {
                log.error("cache error", e);
            }

        }
        if (cachedValue != null) {
            BagAttribute bagAttribute = packToBag(attributeType, cachedValue);
            return new EvaluationResult(bagAttribute);
        }

        try {
            requiredValue = datasource.find();
            if (requiredValue == null) {
                throw new EasyAbacDatasourceException("The result cannot be null");
            }

        } catch (EasyAbacDatasourceException e) {
            return getMissingEvaluationResult(e.getMessage());
        } catch (RuntimeException e) {
            return getMissingEvaluationResult(e.getMessage());
        }

        if (cache != null && cachedValue == null && requiredValue != null) {
            cache.set(datasource.getParams(), datasource.getRequiredAttribute(), datasource.getExpire(), requiredValue);
        }


        if (requiredValue.isEmpty()) {
            return new EvaluationResult(BagAttribute.createEmptyBag(attributeType));
        }
        BagAttribute bagAttribute = packToBag(attributeType, requiredValue);
        return new EvaluationResult(bagAttribute);
    }

    private BagAttribute packToBag(URI attributeType, List<String> foundValues) {
        List<AttributeValue> attributeValues = new ArrayList<>();
        //TODO здесь запаковываем в нужный тип
        if (foundValues != null) {
            for (String foundValue : foundValues) {

                attributeValues.add(new StringAttribute(foundValue));
            }
        }


        return new BagAttribute(attributeType, attributeValues);
    }

    private String extractParamValue(EvaluationCtx context, Param param) throws EasyAbacDatasourceException {
        URI type, id, category;
        String errorMessage = "Parameter value " + param.getName() + " is not defined";
        try {
            type = new URI(param.getAttributeParam().getType().getXacmlName());
            id = new URI(param.getAttributeParam().getXacmlName());
            category = new URI(param.getAttributeParam().getCategory().getXacmlName());
        } catch (URISyntaxException e) {
            log.error(e);
            throw new EasyAbacDatasourceException(errorMessage, e);
        }


        EvaluationResult result = context.getAttribute(type, id, null, category);

        if (result.indeterminate()) {
            throw new EasyAbacDatasourceException(errorMessage);
        }

        if (result != null && result.getAttributeValue() != null && result.getAttributeValue().isBag()) {
            BagAttribute returnBag = (BagAttribute) (result.getAttributeValue());
            if (returnBag.isEmpty()) {
                throw new EasyAbacDatasourceException(errorMessage);
            }
            return ((AttributeValue) returnBag.iterator().next()).encode();
        } else {
            throw new EasyAbacDatasourceException(errorMessage);
        }
    }

    private EvaluationResult getMissingEvaluationResult(String message) {
        log.error(message);
        ArrayList code = new ArrayList();
        code.add(Status.STATUS_MISSING_ATTRIBUTE);
        Status status = new Status(code, message);
        return new EvaluationResult(status);
    }

    @Override
    public boolean isDesignatorSupported() {
        return true;
    }


}
