package custis.easyabac.model;

import java.io.InputStream;
import java.io.Reader;

public interface ModelCreator {
    AbacAuthModel createModel(InputStream stream) throws EasyAbacInitException;
    AbacAuthModel createModel(Reader reader) throws EasyAbacInitException;
}
