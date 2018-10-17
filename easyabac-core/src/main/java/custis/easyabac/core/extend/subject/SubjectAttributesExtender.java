package custis.easyabac.core.extend.subject;

import custis.easyabac.core.extend.RequestExtender;
import custis.easyabac.core.model.abac.attribute.AttributeWithValue;

import java.util.List;

/**
 * Extension removes not used attributes
 */
public class SubjectAttributesExtender implements RequestExtender {

    private static final String SUBJECT_GROUP_ID = "subject_id";

    private final SubjectAttributesProvider subjectAttributesProvider;

    public SubjectAttributesExtender(SubjectAttributesProvider subjectAttributesProvider) {
        this.subjectAttributesProvider = subjectAttributesProvider;
    }

    @Override
    public void extend(List<AttributeWithValue> attributeWithValues) {
        attributeWithValues.addAll(subjectAttributesProvider.provide());
    }

}
