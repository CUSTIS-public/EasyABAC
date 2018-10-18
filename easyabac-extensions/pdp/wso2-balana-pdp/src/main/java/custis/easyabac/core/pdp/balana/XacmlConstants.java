package custis.easyabac.core.pdp.balana;

import custis.easyabac.model.EasyAbacInitException;
import custis.easyabac.model.attribute.Category;
import custis.easyabac.model.attribute.DataType;

import java.util.HashMap;
import java.util.Map;

import static custis.easyabac.model.attribute.Category.*;
import static custis.easyabac.model.attribute.DataType.*;

public class XacmlConstants {

    private static final Map<String, Category> XACML_CATEGORY_MAPPING = new HashMap() {
        {
            put("urn:oasis:names:tc:xacml:3.0:attribute-category:resource", RESOURCE);
            put("urn:oasis:names:tc:xacml:1.0:subject-category:access-subject", SUBJECT);
            put("urn:oasis:names:tc:xacml:3.0:attribute-category:action", ACTION);
            put("urn:oasis:names:tc:xacml:3.0:attribute-category:environment", ENV);
        }

    };

    private static final Map<String, DataType> XACML_TYPE_MAPPING = new HashMap<String, DataType>() {
        {
            put("http://www.w3.org/2001/XMLSchema#string", STRING);
            put("http://www.w3.org/2001/XMLSchema#integer", INT);
            put("http://www.w3.org/2001/XMLSchema#boolean", BOOLEAN);
            put("http://www.w3.org/2001/XMLSchema#dateTime", DATE_TIME);
            put("http://www.w3.org/2001/XMLSchema#time", TIME);
            put("http://www.w3.org/2001/XMLSchema#date", DATE);

        }
    };

    public static DataType findDataTypeByXacmlName(String xacmlName) throws EasyAbacInitException {
        DataType dataType = XACML_TYPE_MAPPING.get(xacmlName);
        if (dataType == null) {
            throw new EasyAbacInitException("Type " + xacmlName + " is not supported");
        } else {
            return dataType;
        }

    }

    public static Category findCategoryByXacmlName(String xacmlName) throws EasyAbacInitException {
        Category category = XACML_CATEGORY_MAPPING.get(xacmlName);
        if (category == null) {
            throw new EasyAbacInitException("Category " + xacmlName + " is not supported");
        } else {
            return category;
        }

    }

    public static String findXacmlName(Category category) {
        for (Map.Entry<String, Category> entry : XACML_CATEGORY_MAPPING.entrySet()) {
            if (entry.getValue() == category) {
                return entry.getKey();
            }
        }

        throw new IllegalArgumentException("Unknown category " + category);
    }

    public static String findXacmlName(DataType dataType) {
        for (Map.Entry<String, DataType> entry : XACML_TYPE_MAPPING.entrySet()) {
            if (entry.getValue() == dataType) {
                return entry.getKey();
            }
        }

        throw new IllegalArgumentException("Unknown dataType " + dataType);
    }
}
