package custis.easyabac.core.extend.subject;

import custis.easyabac.core.extend.RequestExtender;
import custis.easyabac.core.model.abac.attribute.AttributeGroup;
import custis.easyabac.core.model.abac.attribute.AttributeValue;
import custis.easyabac.core.model.abac.attribute.Category;
import custis.easyabac.pdp.MdpAuthRequest;

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
    public void extend(List<AttributeValue> attributeValues) {
        attributeValues.addAll(subjectAttributesProvider.provide());
    }

    @Override
    public void extend(MdpAuthRequest mdpAuthRequest) {
        AttributeGroup group = new AttributeGroup(SUBJECT_GROUP_ID, Category.SUBJECT, subjectAttributesProvider.provide());

        mdpAuthRequest.addGroup(group);
        mdpAuthRequest.addReferenceToAllRequests(SUBJECT_GROUP_ID);
    }

}
