package custis.easyabac.core.init;

import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.StringAttribute;
import org.wso2.balana.ctx.Attribute;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static custis.easyabac.core.init.AttributeValueFactory.stringValue;

public class AttributesFactory {

    public static final URI ATTRIBUTE_REQUEST_ID = URI.create("request-id");

    public static Attribute requestId(String requestId) {
        return new Attribute(ATTRIBUTE_REQUEST_ID, "", null, stringValue(requestId), 3);
    }

    public static Attribute stringAttribute(custis.easyabac.core.model.abac.attribute.Attribute attribute, List<String> values) {
        return stringAttributeWithReturn(attribute, values, true);

    }

    public static Attribute stringAttributeWithReturn(custis.easyabac.core.model.abac.attribute.Attribute attribute, List<String> values, boolean includeInResult) {
        List<org.wso2.balana.attr.AttributeValue> balanaAttributeValues = new ArrayList<>();

        for (String value : values) {
            balanaAttributeValues.add(stringValue(value));
        }

        return generalAttribute(attribute.getXacmlName(), StringAttribute.identifier, balanaAttributeValues, includeInResult);

    }

    public static Attribute generalAttribute(String id, String type, List<AttributeValue> balanaAttributeValues, boolean includeInResult) {
        return new org.wso2.balana.ctx.Attribute(
                URI.create(id),
                URI.create(type),
                "", null, balanaAttributeValues, false, 3);
    }
}
