package ru.custis.easyabac;

import org.junit.Test;
import ru.custis.easyabac.core.EasyAbac;
import ru.custis.easyabac.core.models.attribute.load.EasyAttribute;

import java.io.InputStream;

public class LoadAttributeTest {

    @Test
    public void loadAttribute() {
        InputStream policy = this.getClass()
                .getClassLoader()
                .getResourceAsStream("easy-policy1.yaml");
        InputStream attributes = this.getClass()
                .getClassLoader()
                .getResourceAsStream("attributes-1.yaml");

        EasyAbac.Builder(policy, attributes)

        easyAbac.initInstanceEasyPolicy(policy, attributes);

//        EasyAbacTraceHandler handler;
//
//        easyAbac.neverTrace();
//        easyAbac.useTraceHandler(handler);
//        easyAbac.auth();
//        handler.getLastTrace();

        for (EasyAttribute attribute : easyAbac.getEasyAttributeModel().getModel().get("lesson").getAttributes()) {
            System.out.println(attribute.getCode());
            System.out.println(attribute.getType());
        }

    }
}
