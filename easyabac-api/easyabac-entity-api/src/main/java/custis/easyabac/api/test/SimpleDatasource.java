package custis.easyabac.api.test;

import custis.easyabac.core.EasyAbacDatasourceException;
import custis.easyabac.core.init.Datasource;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptySet;

public class SimpleDatasource extends Datasource {

    private final String value;

    public SimpleDatasource(String key, String value) {
        super(emptySet(), key);
        this.value = value;
    }

    @Override
    public List<String> find() throws EasyAbacDatasourceException {
        return Arrays.asList(value);
    }
}
