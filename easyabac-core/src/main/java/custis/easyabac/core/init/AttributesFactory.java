package custis.easyabac.core.init;

import custis.easyabac.core.model.abac.attribute.AttributeWithValue;
import org.wso2.balana.ParsingException;
import org.wso2.balana.attr.*;
import org.wso2.balana.ctx.Attribute;

import java.net.URI;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static custis.easyabac.core.init.AttributeValueFactory.stringValue;

public class AttributesFactory {

    public static final URI ATTRIBUTE_REQUEST_ID = URI.create("request-id");

    public static Attribute requestId(String requestId) {
        return new Attribute(ATTRIBUTE_REQUEST_ID, "", null, stringValue(requestId), 3);
    }


    public static Attribute balanaAttribute(AttributeWithValue attributeWithValue, boolean includeInResult) throws EasyAbacInitException {
        List<AttributeValue> balanaAttributeValues = new ArrayList<>();
        AttributeValue balanaAttributeValue = null;


        for (String value : attributeWithValue.getValues()) {

            switch (attributeWithValue.getAttribute().getType()) {
                case STRING:
                    balanaAttributeValue = StringAttribute.getInstance(value);
                    break;
                case INT:
                    try {
                        balanaAttributeValue = IntegerAttribute.getInstance(value);
                    } catch (NumberFormatException e) {
                        throw new EasyAbacInitException(e.toString(), e);
                    }
                    break;
                case BOOLEAN:
                    try {
                        balanaAttributeValue = BooleanAttribute.getInstance(value);
                    } catch (ParsingException e) {
                        throw new EasyAbacInitException(e.toString(), e);
                    }
                    break;
                case DATE_TIME:
                    try {
                        balanaAttributeValue = DateTimeAttribute.getInstance(value);
                    } catch (ParsingException | ParseException e) {
                        throw new EasyAbacInitException(e.toString(), e);
                    }
                    break;
                case TIME:
                    try {
                        balanaAttributeValue = TimeAttribute.getInstance(value);
                    } catch (ParsingException | ParseException e) {
                        throw new EasyAbacInitException(e.toString(), e);
                    }
                    break;
                case DATE:
                    try {
                        balanaAttributeValue = DateAttribute.getInstance(value);
                    } catch (ParseException e) {
                        throw new EasyAbacInitException(e.toString(), e);
                    }
                    break;
                default: {
                    throw new EasyAbacInitException("Type " + attributeWithValue.getAttribute().getType() + " is not supported");
                }

            }
            balanaAttributeValues.add(balanaAttributeValue);
        }

        return generalAttribute(attributeWithValue.getAttribute().getXacmlName(), attributeWithValue.getAttribute().getType().getXacmlName(),
                balanaAttributeValues, includeInResult);
    }

    private static Attribute generalAttribute(String id, String type, List<AttributeValue> balanaAttributeValues, boolean includeInResult) {
        return new org.wso2.balana.ctx.Attribute(
                URI.create(id),
                URI.create(type),
                "", null, balanaAttributeValues, includeInResult, 3);
    }
}
