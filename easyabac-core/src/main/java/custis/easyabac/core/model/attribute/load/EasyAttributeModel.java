package custis.easyabac.core.model.attribute.load;

import java.util.Map;

public class EasyAttributeModel {
    private Map<String, EasyObject> model;

    public Map<String, EasyObject> getModel() {
        return model;
    }

    public void setModel(Map<String, EasyObject> model) {
        this.model = model;
    }
}
