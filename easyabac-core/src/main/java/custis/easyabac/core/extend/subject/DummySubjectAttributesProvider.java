package custis.easyabac.core.extend.subject;

import custis.easyabac.model.attribute.AttributeWithValue;

import java.util.Collections;
import java.util.List;

public class DummySubjectAttributesProvider implements SubjectAttributesProvider {

    public static final SubjectAttributesProvider INSTANCE = new DummySubjectAttributesProvider();

    @Override
    public List<AttributeWithValue> provide() {
        return Collections.emptyList();
    }
}
