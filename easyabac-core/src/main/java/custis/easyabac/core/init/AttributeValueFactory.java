package custis.easyabac.core.init;

import org.wso2.balana.attr.DateAttribute;
import org.wso2.balana.attr.DateTimeAttribute;
import org.wso2.balana.attr.IntegerAttribute;
import org.wso2.balana.attr.StringAttribute;

import java.util.Date;

public class AttributeValueFactory {

    public static StringAttribute stringValue(String value) {
        return new StringAttribute(value);
    }

    public static IntegerAttribute stringValue(long value) {
        return new IntegerAttribute(value);
    }

    public static DateAttribute dateValue(Date value) {
        return new DateAttribute(value);
    }

    public static DateTimeAttribute dateTimeValue(Date value) {
        return new DateTimeAttribute(value);
    }
}
