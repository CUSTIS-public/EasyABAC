package custis.easyabac.core;

import custis.easyabac.model.AbacAuthModel;

import java.io.InputStream;

public interface ModelCreator {
    AbacAuthModel createModel(InputStream stream);
}
