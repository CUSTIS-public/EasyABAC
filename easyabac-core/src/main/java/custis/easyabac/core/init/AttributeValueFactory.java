package custis.easyabac.core.init;

import org.wso2.balana.attr.StringAttribute;

public class AttributeValueFactory {

    public static StringAttribute stringValue(String value) {
        return new StringAttribute(value);
    }
}
