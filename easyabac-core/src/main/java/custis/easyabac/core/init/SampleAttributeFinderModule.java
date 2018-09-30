package custis.easyabac.core.init;

import custis.easyabac.core.cache.Cache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.BagAttribute;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.finder.AttributeFinderModule;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SampleAttributeFinderModule extends AttributeFinderModule {

    private final static Log log = LogFactory.getLog(SampleAttributeFinderModule.class);

    private final SampleDatasource datasource;
    private final Cache cache;

    public SampleAttributeFinderModule(SampleDatasource datasource, Cache cache) {

        this.datasource = datasource;
        this.cache = cache;
    }

    @Override
    public Set<String> getSupportedCategories() {
        Set<String> categories = new HashSet<String>();
        categories.add(datasource.getRequiredAttribute().getCategory().getXacmlName());
        return categories;
    }

    @Override
    public Set getSupportedIds() {
        Set<String> ids = new HashSet<String>();
        ids.add(datasource.getRequiredAttribute().getId());
        return ids;
    }

    @Override
    public EvaluationResult findAttribute(URI attributeType, URI attributeId, String issuer,
                                          URI category, EvaluationCtx context) {

        extractParamsName(context);

        List<String> value;
        if (cache == null) {
            value = datasource.find();
        } else {
            value = cache.get(datasource.getParams(), datasource.getRequiredAttribute());
            if (value == null) {
                value = datasource.find();
                cache.set(datasource.getParams(), datasource.getRequiredAttribute(), datasource.getExpire(), value);
            }
        }

        BagAttribute bagAttribute = packToBag(attributeType, value);
        return new EvaluationResult(bagAttribute);
    }

    private BagAttribute packToBag(URI attributeType, List<String> foundValues) {
        List<AttributeValue> attributeValues = new ArrayList<AttributeValue>();

        if (foundValues != null) {
            for (String allowedCategory : foundValues) {
                attributeValues.add(new StringAttribute(allowedCategory));
            }
        }


        return new BagAttribute(attributeType, attributeValues);
    }

    private void extractParamsName(EvaluationCtx context) {
        for (Param param : datasource.getParams()) {

            EvaluationResult result;
            try {
                result = context.getAttribute(new URI(param.getAttributeParam().getType().getXacmlName()), new URI(param.getAttributeParam().getId()),
                        null, new URI(param.getAttributeParam().getCategory().getXacmlName()));
            } catch (URISyntaxException e) {
                log.error(e);
                break;
            }

            if (result != null && result.getAttributeValue() != null && result.getAttributeValue().isBag()) {
                BagAttribute bagAttribute = (BagAttribute) result.getAttributeValue();
                if (bagAttribute.size() > 0) {
                    param.setValue(((AttributeValue) bagAttribute.iterator().next()).encode());
                }
            }
        }
    }

    @Override
    public boolean isDesignatorSupported() {
        return true;
    }


}
