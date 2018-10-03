package custis.easyabac.core.extend.subject;

import custis.easyabac.core.model.abac.attribute.AttributeValue;

import java.util.Collections;
import java.util.List;

public class DummySubjectAttributesProvider implements SubjectAttributesProvider {

    public static final SubjectAttributesProvider INSTANCE = new DummySubjectAttributesProvider();

    @Override
    public List<AttributeValue> provide() {
        return Collections.emptyList();
    }
}
