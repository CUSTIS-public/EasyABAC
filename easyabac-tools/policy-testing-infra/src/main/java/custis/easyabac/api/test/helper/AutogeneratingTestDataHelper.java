package custis.easyabac.api.test.helper;

import custis.easyabac.api.test.TestDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Predicate;

import static custis.easyabac.api.test.helper.TestDescriptionHelper.loadTestDescription;

public class AutogeneratingTestDataHelper {

    private final static Logger logger = LoggerFactory.getLogger(AutogeneratingTestDataHelper.class);

    public static List<Object[]> loadGeneratedTestsFromPackage(Class testClass, String resourceName) throws Exception {
        return loadGeneratedTestsFromPackage(testClass.getPackage().getName(), name -> name.startsWith(resourceName.toLowerCase()));
    }

    public static List<Object[]> loadGeneratedTestsFromPackage(String packageName, Predicate<String> resourceNamePredicate) throws Exception {
        List<Object[]> data = new ArrayList<>();

        Enumeration<URL> e = AutogeneratingTestDataHelper.class.getClassLoader().getResources("");
        while (e.hasMoreElements()) {
            URL url = e.nextElement();


            File folder = new File(url.getFile(), packageName.replace(".", "/"));
            if (!folder.exists()) {
                continue;
            }
            for (String fileName : folder.list((dir, name) -> resourceNamePredicate.test(name))) {
                logger.debug(fileName+"\n");
                TestDescription testDescription = loadTestDescription(new File(folder, fileName));
                data.add(TestDataHelper.getTestData(testDescription));
            }
        }

        return data;
    }

}
