package ru.custis.easyabac;

import org.junit.Test;
import ru.custis.easyabac.core.EasyAbac;

import java.io.InputStream;

public class LoadEasyPolicyTest {

    @Test
    public void loadPolicy() {
        EasyAbac easyAbac = new EasyAbac();

        InputStream policy = this.getClass()
                .getClassLoader()
                .getResourceAsStream("easy-policy1.yaml");
        easyAbac.initInstanceSimplePolicy(policy, null);

    }
}
