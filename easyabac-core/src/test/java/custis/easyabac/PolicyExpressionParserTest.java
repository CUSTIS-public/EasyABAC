package custis.easyabac;

import custis.easyabac.core.model.abac.attribute.Category;
import custis.easyabac.core.model.abac.Function;
import custis.easyabac.core.model.abac.ParseException;
import custis.easyabac.core.model.abac.TargetCondition;
import org.junit.Assert;
import org.junit.Test;

import custis.easyabac.core.model.abac.PolicyExpressionParser;

import java.io.StringReader;

import static org.junit.Assert.assertEquals;

/**
 * Test suite for various policy expressions parsing into EasyABAC domain model
 */
public class PolicyExpressionParserTest {

    @Test
    public void shouldReadActionIdEqualsExpression() throws ParseException {
        PolicyExpressionParser parser =
                new PolicyExpressionParser(new StringReader("action-id == CourseUnit.Edit"));
        TargetCondition condition = parser.parse();
        Assert.assertNotNull("Parsed condition", condition);
        assertEquals("Action ID","action-id", condition.getFirstOperand().getId());
        assertEquals("Action Category", Category.ACTION, condition.getFirstOperand().getCategory());

        assertEquals("Action value", "CourseUnit.Edit", condition.getSecondOperand());

        assertEquals("Operation", Function.EQUAL, condition.getFunction());
    }
}