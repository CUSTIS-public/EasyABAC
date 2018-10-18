package custis.easyabac.model.easy;

import custis.easyabac.model.EasyAbacInitException;
import custis.easyabac.model.attribute.DataType;

import java.util.HashMap;
import java.util.Map;

import static custis.easyabac.model.attribute.DataType.*;

public class EasyConstants {

    private static final Map<String, DataType> EASY_TYPE_MAPPING = new HashMap<String, DataType>() {
        {
            put("string", STRING);
            put("int", INT);
            put("boolean", BOOLEAN);
            put("dateTime", DATE_TIME);
            put("time", TIME);
            put("date", DATE);

        }
    };

    public static DataType findByEasyName(String easyName) throws EasyAbacInitException {
        DataType dataType = EASY_TYPE_MAPPING.get(easyName);
        if (dataType == null) {
            throw new EasyAbacInitException("Type " + easyName + " is not supported");
        } else {
            return dataType;
        }

    }
}
