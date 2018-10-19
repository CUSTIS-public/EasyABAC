package custis.easyabac.api.test.helper;

import custis.easyabac.api.test.TestDescription;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class TestDescriptionHelper {

    private static final Yaml yaml = new Yaml();

    public static TestDescription loadTestDescriptionFromResource(String resource) {
        return loadTestDescription(TestDescription.class.getResourceAsStream(resource));
    }

    public static TestDescription loadTestDescription(InputStream is) {
        TestDescription testDescription = yaml.loadAs(is, TestDescription.class);
        return testDescription;
    }

    public static TestDescription loadTestDescription(File file) throws FileNotFoundException {
        InputStream is = new FileInputStream(file);
        return loadTestDescription(is);
    }
}
