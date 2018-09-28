package custis.easyabac;

import custis.easyabac.core.EasyAbac;
import custis.easyabac.core.auth.EasyAbacAuth;
import org.junit.Test;

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

        EasyAbacAuth abacAuth = new EasyAbac.Builder(policy, attributes).build();

//        abacAuth.auth();


//        EasyAbacTraceHandler handler;
//
//        easyAbac.neverTrace();
//        easyAbac.useTraceHandler(handler);
//        easyAbac.auth();
//        handler.getLastTrace();

//        for (EasyAttribute attribute : easyAbac.getEasyAttributeModel().getModel().get("lesson").getAttributes()) {
//            System.out.println(attribute.getCode());
//            System.out.println(attribute.getType());
//        }

    }
}
