package ru.custis.easyabac;

import org.junit.Assert;
import org.junit.Test;
import ru.custis.easyabac.core.Decision;
import ru.custis.easyabac.core.EasyAbac;
import ru.custis.easyabac.core.EasyAbacRequest;
import ru.custis.easyabac.core.EasyAbacResponse;

public class InitializeTest {
    public final static String TEST_1_POLICY = "<PolicySet PolicySetId=\"91218d7a-62b9-22f8-7bda-964fc7aac0ab\" PolicyCombiningAlgId=\"urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:deny-unless-permit\" Version=\"1\" xmlns=\"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\" xsi:schemaLocation=\"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17 http://docs.oasis-open.org/xacml/3.0/xacml-core-v3-schema-wd-17.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "  <Description>Новая группа политик</Description>\n" +
            "  <Target>\n" +
            "  </Target>\n" +
            "  <Policy PolicyId=\"686dac42-bce3-6769-539e-cceab05aea0c\" Version=\"0.1\" RuleCombiningAlgId=\"urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:deny-unless-permit\">\n" +
            "    <Description>p1</Description>\n" +
            "    <Target>\n" +
            "      <AnyOf>\n" +
            "        <AllOf>\n" +
            "          <Match MatchId=\"urn:oasis:names:tc:xacml:1.0:function:string-equal\">\n" +
            "            <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">form</AttributeValue>\n" +
            "            <AttributeDesignator AttributeId=\"urn:s_tst1:attr:01:resource:object\" DataType=\"http://www.w3.org/2001/XMLSchema#string\" Category=\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\" MustBePresent=\"false\"/>\n" +
            "          </Match>\n" +
            "        </AllOf>\n" +
            "      </AnyOf>\n" +
            "    </Target>\n" +
            "    <Rule RuleId=\"2d164316-2e4b-a807-292a-2f669587159b\" Effect=\"Permit\"  >\n" +
            "      <Description>Правило 1</Description>\n" +
            "      <Condition>\n" +
            "        <Apply FunctionId=\"urn:oasis:names:tc:xacml:1.0:function:and\">\n" +
            "          <Apply FunctionId=\"urn:oasis:names:tc:xacml:1.0:function:string-equal\">\n" +
            "            <Apply FunctionId=\"urn:oasis:names:tc:xacml:1.0:function:string-one-and-only\">\n" +
            "              <AttributeDesignator AttributeId=\"urn:s_tst1:attr:01:resource:role\" DataType=\"http://www.w3.org/2001/XMLSchema#string\" Category=\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\" MustBePresent=\"false\"/>\n" +
            "            </Apply>\n" +
            "            <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">ADMIN</AttributeValue>\n" +
            "          </Apply>\n" +
            "        </Apply>\n" +
            "      </Condition>\n" +
            "    </Rule>\n" +
            "  </Policy>\n" +
            "</PolicySet>\n" +
            "\n";


    public final static String TEST_1_REQUEST_PERMIT = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<Request ReturnPolicyIdList=\"false\" CombinedDecision=\"false\" xmlns=\"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\">\n" +
            "   <Attributes Category=\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\">\n" +
            "       <Attribute AttributeId=\"urn:s_tst1:attr:01:resource:role\" IncludeInResult=\"false\">\n" +
            "           <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">ADMIN</AttributeValue>\n" +
            "       </Attribute>\n" +
            " <Attribute AttributeId=\"urn:s_tst1:attr:01:resource:object\" IncludeInResult=\"false\">\n" +
            "           <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">form</AttributeValue>\n" +
            "       </Attribute>\n" +
            "   </Attributes>\n" +
            "</Request>";

    public final static String TEST_1_REQUEST_DENY = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<Request ReturnPolicyIdList=\"false\" CombinedDecision=\"false\" xmlns=\"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\">\n" +
            "   <Attributes Category=\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\">\n" +
            "       <Attribute AttributeId=\"urn:s_tst1:attr:01:resource:role\" IncludeInResult=\"false\">\n" +
            "           <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">USER</AttributeValue>\n" +
            "       </Attribute>\n" +
            " <Attribute AttributeId=\"urn:s_tst1:attr:01:resource:object\" IncludeInResult=\"false\">\n" +
            "           <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">form</AttributeValue>\n" +
            "       </Attribute>\n" +
            "   </Attributes>\n" +
            "</Request>";

    public final static String TEST_1_REQUEST_NOT_APPLICABLE = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<Request ReturnPolicyIdList=\"false\" CombinedDecision=\"false\" xmlns=\"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\">\n" +
            "   <Attributes Category=\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\">\n" +
            "       <Attribute AttributeId=\"urn:s_tst1:attr:01:resource:role\" IncludeInResult=\"false\">\n" +
            "           <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">USER</AttributeValue>\n" +
            "       </Attribute>\n" +
            " <Attribute AttributeId=\"urn:s_tst1:attr:01:resource:object\" IncludeInResult=\"false\">\n" +
            "           <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">report</AttributeValue>\n" +
            "       </Attribute>\n" +
            "   </Attributes>\n" +
            "</Request>";

    public final static String TEST_2_POLICY = "<PolicySet PolicySetId=\"91218d7a-62b9-22f8-7bda-964fc7aac0ab\" PolicyCombiningAlgId=\"urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:deny-unless-permit\" Version=\"1\" xmlns=\"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\" xsi:schemaLocation=\"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17 http://docs.oasis-open.org/xacml/3.0/xacml-core-v3-schema-wd-17.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "  <Description>Новая группа политик</Description>\n" +
            "  <Target>\n" +
            "  </Target>\n" +
            "  <Policy PolicyId=\"686dac42-bce3-6769-539e-cceab05aea0c\" Version=\"0.1\" RuleCombiningAlgId=\"urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:deny-unless-permit\">\n" +
            "    <Description>p1</Description>\n" +
            "    <Target>\n" +
            "      <AnyOf>\n" +
            "        <AllOf>\n" +
            "          <Match MatchId=\"urn:oasis:names:tc:xacml:1.0:function:string-equal\">\n" +
            "            <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">form</AttributeValue>\n" +
            "            <AttributeDesignator AttributeId=\"urn:s_tst1:attr:01:resource:object\" DataType=\"http://www.w3.org/2001/XMLSchema#string\" Category=\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\" MustBePresent=\"false\"/>\n" +
            "          </Match>\n" +
            "        </AllOf>\n" +
            "      </AnyOf>\n" +
            "    </Target>\n" +
            "    <Rule RuleId=\"2d164316-2e4b-a807-292a-2f669587159b\" Effect=\"Permit\"  >\n" +
            "      <Description>Правило 1</Description>\n" +
            "      <Condition>\n" +
            "        <Apply FunctionId=\"urn:oasis:names:tc:xacml:1.0:function:and\">\n" +
            "          <Apply FunctionId=\"urn:oasis:names:tc:xacml:1.0:function:string-equal\">\n" +
            "            <Apply FunctionId=\"urn:oasis:names:tc:xacml:1.0:function:string-one-and-only\">\n" +
            "              <AttributeDesignator AttributeId=\"urn:s_tst1:attr:01:resource:role\" DataType=\"http://www.w3.org/2001/XMLSchema#string\" Category=\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\" MustBePresent=\"false\"/>\n" +
            "            </Apply>\n" +
            "            <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">ADMIN</AttributeValue>\n" +
            "          </Apply>\n" +
            "        </Apply>\n" +
            "      </Condition>\n" +
            "    </Rule>\n" +
            "  </Policy>\n" +
            "  <Policy PolicyId=\"ff67e784-0d8e-7e8c-5042-29698c3655e7\" Version=\"0.1\" RuleCombiningAlgId=\"urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:deny-unless-permit\">\n" +
            "    <Description>p2</Description>\n" +
            "    <Target>\n" +
            "      <AnyOf>\n" +
            "        <AllOf>\n" +
            "          <Match MatchId=\"urn:oasis:names:tc:xacml:1.0:function:string-equal\">\n" +
            "            <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">report</AttributeValue>\n" +
            "            <AttributeDesignator AttributeId=\"urn:s_tst1:attr:01:resource:object\" DataType=\"http://www.w3.org/2001/XMLSchema#string\" Category=\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\" MustBePresent=\"false\"/>\n" +
            "          </Match>\n" +
            "        </AllOf>\n" +
            "      </AnyOf>\n" +
            "    </Target>\n" +
            "    <Rule RuleId=\"203e6d15-2569-16a0-a2bb-471222d03536\" Effect=\"Permit\"  >\n" +
            "      <Description>Правило 1</Description>\n" +
            "      <Condition>\n" +
            "        <Apply FunctionId=\"urn:oasis:names:tc:xacml:1.0:function:and\">\n" +
            "          <Apply FunctionId=\"urn:oasis:names:tc:xacml:1.0:function:string-equal\">\n" +
            "            <Apply FunctionId=\"urn:oasis:names:tc:xacml:1.0:function:string-one-and-only\">\n" +
            "              <AttributeDesignator AttributeId=\"urn:s_tst1:attr:01:resource:role\" DataType=\"http://www.w3.org/2001/XMLSchema#string\" Category=\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\" MustBePresent=\"false\"/>\n" +
            "            </Apply>\n" +
            "            <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">USER</AttributeValue>\n" +
            "          </Apply>\n" +
            "        </Apply>\n" +
            "      </Condition>\n" +
            "    </Rule>\n" +
            "  </Policy>\n" +
            "</PolicySet>\n";


    public final static String TEST_2_REQUEST_PERMIT = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<Request ReturnPolicyIdList=\"false\" CombinedDecision=\"false\" xmlns=\"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\">\n" +
            "   <Attributes Category=\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\">\n" +
            "       <Attribute AttributeId=\"urn:s_tst1:attr:01:resource:role\" IncludeInResult=\"false\">\n" +
            "           <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">USER</AttributeValue>\n" +
            "       </Attribute>\n" +
            " <Attribute AttributeId=\"urn:s_tst1:attr:01:resource:object\" IncludeInResult=\"false\">\n" +
            "           <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">report</AttributeValue>\n" +
            "       </Attribute>\n" +
            "   </Attributes>\n" +
            "</Request>";

    public final static String TEST_2_REQUEST_DENY = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<Request ReturnPolicyIdList=\"false\" CombinedDecision=\"false\" xmlns=\"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\">\n" +
            "   <Attributes Category=\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\">\n" +
            "       <Attribute AttributeId=\"urn:s_tst1:attr:01:resource:role\" IncludeInResult=\"false\">\n" +
            "           <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">ADMIN</AttributeValue>\n" +
            "       </Attribute>\n" +
            " <Attribute AttributeId=\"urn:s_tst1:attr:01:resource:object\" IncludeInResult=\"false\">\n" +
            "           <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">report</AttributeValue>\n" +
            "       </Attribute>\n" +
            "   </Attributes>\n" +
            "</Request>";

    public final static String TEST_2_REQUEST_NOT_APPLICABLE = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<Request ReturnPolicyIdList=\"false\" CombinedDecision=\"false\" xmlns=\"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17\">\n" +
            "   <Attributes Category=\"urn:oasis:names:tc:xacml:3.0:attribute-category:resource\">\n" +
            "       <Attribute AttributeId=\"urn:s_tst1:attr:01:resource:role\" IncludeInResult=\"false\">\n" +
            "           <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">USER</AttributeValue>\n" +
            "       </Attribute>\n" +
            " <Attribute AttributeId=\"urn:s_tst1:attr:01:resource:object\" IncludeInResult=\"false\">\n" +
            "           <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">report2</AttributeValue>\n" +
            "       </Attribute>\n" +
            "   </Attributes>\n" +
            "</Request>";


    @Test
    public void authTest1() {
        EasyAbac easyAbac = new EasyAbac();

        easyAbac.initInstanceXacmlPolicy(TEST_1_POLICY, null);

        EasyAbacResponse abacResponse = easyAbac.auth(new EasyAbacRequest(TEST_1_REQUEST_PERMIT));
        Assert.assertEquals("Должен быть PERMIT", Decision.PERMIT, abacResponse.getDesicion());

        abacResponse = easyAbac.auth(new EasyAbacRequest(TEST_1_REQUEST_DENY));
        Assert.assertEquals("Должен быть DENY", Decision.DENY, abacResponse.getDesicion());

        abacResponse = easyAbac.auth(new EasyAbacRequest(TEST_1_REQUEST_NOT_APPLICABLE));
        Assert.assertEquals("Должен быть DENY", Decision.DENY, abacResponse.getDesicion());

    }

    @Test
    public void authTest2() {
        EasyAbac easyAbac = new EasyAbac();

        easyAbac.initInstanceXacmlPolicy(TEST_2_POLICY, null);

        EasyAbacResponse abacResponse = easyAbac.auth(new EasyAbacRequest(TEST_2_REQUEST_PERMIT));
        Assert.assertEquals("Должен быть PERMIT", Decision.PERMIT, abacResponse.getDesicion());

        abacResponse = easyAbac.auth(new EasyAbacRequest(TEST_2_REQUEST_DENY));
        Assert.assertEquals("Должен быть DENY", Decision.DENY, abacResponse.getDesicion());

        abacResponse = easyAbac.auth(new EasyAbacRequest(TEST_2_REQUEST_NOT_APPLICABLE));
        Assert.assertEquals("Должен быть DENY", Decision.DENY, abacResponse.getDesicion());

    }
}
