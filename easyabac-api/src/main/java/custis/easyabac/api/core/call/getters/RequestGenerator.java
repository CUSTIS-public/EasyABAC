package custis.easyabac.api.core.call.getters;

import java.util.List;

public interface RequestGenerator {

    RequestWrapper generate(List<Object> objects);

}
