package custis.easyabac.model;

import java.io.InputStream;

public interface ModelCreator {
    AbacAuthModel createModel(InputStream stream) throws EasyAbacInitException;
}
