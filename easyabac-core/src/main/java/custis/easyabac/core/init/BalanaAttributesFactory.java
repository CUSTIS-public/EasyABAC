package custis.easyabac.core.init;

import custis.easyabac.core.model.abac.attribute.DataType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.balana.ParsingException;
import org.wso2.balana.UnknownIdentifierException;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.BagAttribute;
import org.wso2.balana.attr.StandardAttributeFactory;
import org.wso2.balana.ctx.Attribute;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class BalanaAttributesFactory {

    private final static Log log = LogFactory.getLog(BalanaAttributesFactory.class);

    public static final String ATTRIBUTE_REQUEST_ID = "request-id";


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

    public static AttributeValue getAttributeValue(DataType type, String value) throws EasyAbacInitException {
        URI xacmlType;
        try {
            xacmlType = new URI(type.getXacmlName());
        } catch (URISyntaxException e) {
            log.error("getAttributeValue xacmlType URI", e);
            throw new EasyAbacInitException(e.toString(), e);
        }

        AttributeValue balanaAttributeValue;

        try {
            balanaAttributeValue = StandardAttributeFactory.getFactory().createValue(xacmlType, value);
        } catch (UnknownIdentifierException | ParsingException e) {
            log.error("getAttributeValue createValue", e);
            throw new EasyAbacInitException("getAttributeValue createValue", e);
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
