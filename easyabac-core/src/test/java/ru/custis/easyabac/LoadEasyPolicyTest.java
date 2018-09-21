package ru.custis.easyabac;

import org.junit.Test;
import ru.custis.easyabac.core.EasyAbac;
import ru.custis.easyabac.core.models.policy.Condition;

import java.io.InputStream;

public class LoadEasyPolicyTest {

    @Test
    public void loadPolicy() {
        EasyAbac easyAbac = new EasyAbac();

        InputStream policy = this.getClass()
                .getClassLoader()
                .getResourceAsStream("easy-policy1.yaml");
        easyAbac.initInstanceEasyPolicy(policy, null);
        Condition condition =
                easyAbac.getEasyPolicy().getPolicies().get("policy2").getRules().get("rule1").getConditions().get(0);
        System.out.println(condition.getExpression());
    }
}
