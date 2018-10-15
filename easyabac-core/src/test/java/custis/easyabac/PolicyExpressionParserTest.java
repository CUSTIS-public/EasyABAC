package custis.easyabac;

import custis.easyabac.core.model.abac.*;
import custis.easyabac.core.model.abac.attribute.Attribute;
import custis.easyabac.core.model.abac.attribute.Category;
import custis.easyabac.core.model.abac.attribute.DataType;
import org.junit.Test;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test suite for various policy expressions parsing into EasyABAC domain model
 */
public class PolicyExpressionParserTest {

    @Test
    public void shouldReadActionIdEqualsExpression() throws ParseException {
        PolicyExpressionParser parser =
                new PolicyExpressionParser(new StringReader("action-id == 'Edit'"));
        TargetCondition condition = parser.parseTargetCondition();
        assertNotNull("Parsed condition", condition);
        assertEquals("Action ID","action-id", condition.getFirstOperand().getId());
        assertEquals("Action Category", Category.ACTION, condition.getFirstOperand().getCategory());
        assertEquals("Action ID Type", DataType.STRING, condition.getFirstOperand().getType());

        assertEquals("Action value", "Edit", condition.getSecondOperand());

        assertEquals("Operation", Function.EQUAL, condition.getFunction());
    }

    @Test
    public void shouldBuildCondition_whenAttributesAreInBothSidesOfExpression() throws ParseException {
        Map<String, Attribute> attributeMap = new HashMap<>();
        attributeMap.put("subject.person_id", new Attribute("subject.person_id", DataType.STRING, Category.SUBJECT, false));
        attributeMap.put("CourseUnit.creatorId", new Attribute("CourseUnit.creatorId", DataType.STRING, Category.RESOURCE, false));

        PolicyExpressionParser parser =
                new PolicyExpressionParser(new StringReader("subject.person_id == CourseUnit.creatorId"));

        parser.setAttributes(attributeMap);
        Condition condition = parser.parseRuleCondition(false);

        assertNotNull("Rule condition", condition);
        assertEquals("Left hand ID", "subject.person_id", condition.getFirstOperand().getId());
        assertEquals("Right hand ID", "CourseUnit.creatorId", condition.getSecondOperandAttribute().getId());
        assertEquals("Function", Function.EQUAL, condition.getFunction());
    }

    @Test
    public void shouldBuildCondition_whenListOfValuesGiven() throws ParseException {
        Map<String, Attribute> attributeMap = new HashMap<>();
        attributeMap.put("CourseUnit.status", new Attribute("CourseUnit.status", DataType.STRING, Category.RESOURCE, false));

        PolicyExpressionParser parser =
                new PolicyExpressionParser(new StringReader("CourseUnit.status in ['DRAFT','PENDING']"));
        parser.setAttributes(attributeMap);

        Condition condition = parser.parseRuleCondition(false);

        assertNotNull("Rule condition", condition);
        assertNotNull("Right hand values list", condition.getSecondOperandValue());
        assertEquals("Right hand values qty", 2, condition.getSecondOperandValue().size());
        assertEquals("Function", Function.IN, condition.getFunction());
    }

    @Test
    public void shouldBuildConditions_whenUsingDashesAndUnderscores() throws ParseException {
        Map<String, Attribute> attributeMap = new HashMap<>();
        attributeMap.put("CourseUnit.status-id", new Attribute("CourseUnit.status-id", DataType.STRING, Category.RESOURCE, false));
        attributeMap.put("CourseUnit.person_id", new Attribute("CourseUnit.person_id", DataType.STRING, Category.RESOURCE, false));

        PolicyExpressionParser parser =
                new PolicyExpressionParser(new StringReader("CourseUnit.person_id == CourseUnit.status-id"));
        parser.setAttributes(attributeMap);

        Condition condition = parser.parseRuleCondition(false);
        assertNotNull("Rule condition", condition);
    }

    @Test
    public void shouldBuildTimeBasedConditions_whenTimeIsUsed() throws ParseException {
        Map<String, Attribute> attributeMap = new HashMap<>();
        attributeMap.put("env.current_time", new Attribute("env.current_time", DataType.TIME, Category.ENV, false));

        PolicyExpressionParser parser = new PolicyExpressionParser(new StringReader("env.current_time >= 08:30"));
        parser.setAttributes(attributeMap);

        Condition condition = parser.parseRuleCondition(false);
        assertNotNull("Rule condition", condition);
        assertEquals("Time attribute", "env.current_time", condition.getFirstOperand().getId());
        assertEquals("Time value", "08:30", condition.getSecondOperandValue().get(0));
    }
}