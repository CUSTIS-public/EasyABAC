package custis.easyabac.core.init;

import custis.easyabac.core.model.attribute.Attribute;
import custis.easyabac.pdp.AuthAttribute;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.BagAttribute;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.cond.EvaluationResult;
import org.wso2.balana.ctx.EvaluationCtx;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class SampleAttributeFinderModule extends org.wso2.balana.finder.AttributeFinderModule {

    private final SampleDatasource datasource;
    private URI defaultSubjectId;

    class SampleDatasource {
        private final List<AuthAttribute> paramList;
        private final Attribute requiredAttribute;

        public SampleDatasource(List<AuthAttribute> paramList, Attribute requiredAttribute) {
            this.paramList = paramList;
            this.requiredAttribute = requiredAttribute;
        }

        public List<AuthAttribute> getParamList() {
            return paramList;
        }

        public Attribute getRequiredAttribute() {
            return requiredAttribute;
        }

        public List<String> find() {
            String userName = null;
            for (AuthAttribute authAttribute : paramList) {
                if (authAttribute.getId().equals("userName")) {
                    userName = authAttribute.getValues().get(0);
                }
            }


            if (userName.equals("bob")) {
                return Arrays.asList("iod", "dsp");
            } else if (userName.equals("alice")) {
                return Arrays.asList("dsp");
            } else if (userName.equals("peter")) {
                return Arrays.asList("iod");
            }

            return null;
        }
    }

    public SampleAttributeFinderModule(SampleDatasource datasource) {

        // здесь передаем id параметров и сам источник
        // параметры искомого атрибута

        try {
            defaultSubjectId = new URI("urn:oasis:names:tc:xacml:1.0:subject:subject-id");
        } catch (URISyntaxException e) {
            //ignore
        }

        this.datasource = datasource;
    }

    @Override
    public Set<String> getSupportedCategories() {
        Set<String> categories = new HashSet<String>();
//        categories.add("urn:oasis:names:tc:xacml:3.0:attribute-category:resource");
        categories.add(datasource.getRequiredAttribute().getCategory().getXacmlName());
        return categories;
    }

    @Override
    public Set getSupportedIds() {
        Set<String> ids = new HashSet<String>();
        ids.add(datasource.getRequiredAttribute().getId());
//        ids.add("urn:s_tst2:attr:01:subject:allowed-categories");
        return ids;
    }

    @Override
    public EvaluationResult findAttribute(URI attributeType, URI attributeId, String issuer,
                                          URI category, EvaluationCtx context) {
        List<String> allowedCategories = null;
        List<AttributeValue> attributeValues = new ArrayList<AttributeValue>();

        EvaluationResult result = context.getAttribute(attributeType, defaultSubjectId, issuer, category);
        if (result != null && result.getAttributeValue() != null && result.getAttributeValue().isBag()) {
            BagAttribute bagAttribute = (BagAttribute) result.getAttributeValue();
            if (bagAttribute.size() > 0) {
                String userName = ((AttributeValue) bagAttribute.iterator().next()).encode();
                allowedCategories = findAllowedCategories(userName);
            }
        }

        if (allowedCategories != null) {
            for (String allowedCategory : allowedCategories) {
                attributeValues.add(new StringAttribute(allowedCategory));
            }
        }

        return new EvaluationResult(new BagAttribute(attributeType, attributeValues));
    }

    @Override
    public boolean isDesignatorSupported() {
        return true;
    }


}
