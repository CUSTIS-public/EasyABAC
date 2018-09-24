package ru.custis.easyabac;

import org.junit.Test;
import ru.custis.easyabac.core.EasyAbac;

import java.io.InputStream;

public class LoadAttributeTest {

    @Test
    public void loadAttribute() {
        EasyAbac easyAbac = new EasyAbac();
        InputStream policy = this.getClass()
                .getClassLoader()
                .getResourceAsStream("easy-policy1.yaml");
        InputStream attributes = this.getClass()
                .getClassLoader()
                .getResourceAsStream("attributes-1.yaml");

        easyAbac.initInstanceEasyPolicy(policy, attributes);
    }
}
