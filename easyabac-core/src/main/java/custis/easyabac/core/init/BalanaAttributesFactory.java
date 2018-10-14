package custis.easyabac.core.init;

import custis.easyabac.core.model.abac.attribute.DataType;
import org.wso2.balana.ParsingException;
import org.wso2.balana.attr.*;
import org.wso2.balana.ctx.Attribute;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


public class BalanaAttributesFactory {

    public static final URI ATTRIBUTE_REQUEST_ID = URI.create("request-id");

    public static Attribute requestId(String requestId) {
        AttributeValue requestIdAttributeValue = null;
        try {
            requestIdAttributeValue = getAttributeValue(DataType.STRING, requestId);
        } catch (EasyAbacInitException e) {
            e.printStackTrace();
        }
        return new Attribute(ATTRIBUTE_REQUEST_ID, "", null, requestIdAttributeValue, 3);

    }


    static Attribute balanaAttribute(String xacmlId, DataType type, List<String> values, boolean includeInResult) throws EasyAbacInitException {
        List<AttributeValue> balanaAttributeValues = new ArrayList<>();

        for (String value : values) {
            AttributeValue balanaAttributeValue = getAttributeValue(type, value);
            balanaAttributeValues.add(balanaAttributeValue);
        }

        return generalAttribute(xacmlId, type.getXacmlName(), balanaAttributeValues, includeInResult);
    }

    static BagAttribute balanaBagAttributeValues(DataType type, List<String> values) throws EasyAbacInitException {
        List<AttributeValue> balanaAttributeValues = new ArrayList<>();

        for (String value : values) {
            AttributeValue balanaAttributeValue = getAttributeValue(type, value);
            balanaAttributeValues.add(balanaAttributeValue);
        }

        URI xacmlName = null;
        try {
            xacmlName = new URI(type.getXacmlName());
        } catch (URISyntaxException e) {
            throw new EasyAbacInitException(e.getMessage());
        }
        return new BagAttribute(xacmlName, balanaAttributeValues);
    }

    private static AttributeValue getAttributeValue(DataType type, String value) throws EasyAbacInitException {
        AttributeValue balanaAttributeValue;
        switch (type) {
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
                throw new EasyAbacInitException("Type " + type + " is not supported");
            }

        }
        return balanaAttributeValue;
    }

    private static Attribute generalAttribute(String id, String type, List<AttributeValue> balanaAttributeValues, boolean includeInResult) {
        return new org.wso2.balana.ctx.Attribute(
                URI.create(id),
                URI.create(type),
                "", null, balanaAttributeValues, includeInResult, 3);
    }
}
