package custis.easyabac.core.trace.model;

import custis.easyabac.core.model.abac.AbacAuthModel;

public interface Populatable {

    void populateByModel(AbacAuthModel abacAuthModel);
}
