package custis.easyabac.api.test.helper;

import java.io.*;
import java.net.URL;
import java.util.Enumeration;

public class ModelHelper {

    public static InputStream loadModelFromResource(Class testClass, String resource) throws IOException {
        Enumeration<URL> e = testClass.getClassLoader().getResources("");
        while (e.hasMoreElements()) {
            URL url = e.nextElement();


            File modelSource = new File(url.getFile(), resource);
            if (modelSource.exists() && modelSource.isFile()) {
                return new FileInputStream(modelSource);
            }
        }
        throw new FileNotFoundException(resource);
    }
}
