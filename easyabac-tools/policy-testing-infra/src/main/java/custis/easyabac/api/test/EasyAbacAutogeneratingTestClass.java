package custis.easyabac.api.test;

import custis.easyabac.api.test.helper.TestDataHelper;
import custis.easyabac.model.AbacAuthModel;
import custis.easyabac.model.EasyAbacInitException;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static custis.easyabac.api.test.helper.TestDataHelper.getEntityCode;
import static custis.easyabac.api.test.helper.TestDescriptionHelper.loadTestDescription;

public abstract class EasyAbacAutogeneratingTestClass<T, A> extends EasyAbacBaseTestClass<T, A> {

    public EasyAbacAutogeneratingTestClass(InputStream modelSource) throws EasyAbacInitException {
        super(modelSource);
    }

    public EasyAbacAutogeneratingTestClass(AbacAuthModel model) {
        super(model);
    }

    protected static List<Object[]> generateTestData(Class testClass, Class entityClass, Class actionClass) throws Exception {
        String entityCode = getEntityCode(entityClass);

        List<Object[]> data = new ArrayList<>();

        String packageName = testClass.getPackage().getName().replace(".", "/");

        Enumeration<URL> e = testClass.getClassLoader().getResources("");
        while (e.hasMoreElements()) {
            URL url = e.nextElement();


            File folder = new File(url.getFile(), packageName);
            if (!folder.exists()) {
                continue;
            }
            String finalEntityCode = entityCode;
            for (String fileName : folder.list((dir, name) -> name.startsWith(finalEntityCode.toLowerCase()))) {
                TestDescription testDescription = loadTestDescription(new File(folder, fileName));
                data.add(TestDataHelper.getTestData(testDescription, entityClass, actionClass, entityCode));
            }
        }

        return data;
    }
}
