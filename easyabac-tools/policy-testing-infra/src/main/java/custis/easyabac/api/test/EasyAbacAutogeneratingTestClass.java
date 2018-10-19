package custis.easyabac.api.test;

import custis.easyabac.api.EntityPermissionChecker;
import custis.easyabac.api.impl.EasyABACPermissionChecker;
import custis.easyabac.core.EasyAbacBuilder;
import custis.easyabac.core.datasource.Datasource;
import custis.easyabac.core.datasource.SimpleDatasource;
import custis.easyabac.core.pdp.AuthService;
import custis.easyabac.model.AbacAuthModel;
import custis.easyabac.model.EasyAbacInitException;
import custis.easyabac.model.attribute.Attribute;
import custis.easyabac.model.attribute.AttributeWithValue;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static custis.easyabac.api.impl.AttributeValueExtractor.SUBJECT_NAME;
import static custis.easyabac.api.test.helper.AutogeneratingTestDataHelper.getEntityCode;
import static custis.easyabac.api.test.helper.TestDescriptionHelper.loadTestDescription;

public abstract class EasyAbacAutogeneratingTestClass extends EasyAbacBaseTestClass {

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
                //data.add(TestDataHelper.getTestData(testDescription, entityClass, actionClass, entityCode));
            }
        }

        return data;
    }

    protected EntityPermissionChecker getPermissionChecker() throws EasyAbacInitException {
        EasyAbacBuilder builder = new EasyAbacBuilder(model, pdpHandlerFactory);

        // subject extender
        if (testDescription.containsAttributesByCode(SUBJECT_NAME)) {
            builder.subjectAttributesProvider(() -> testDescription.getAttributesByCode(SUBJECT_NAME).entrySet()
                    .stream()
                    .map(stringObjectEntry -> {
                        Attribute attribute = model.getAttributes().get(SUBJECT_NAME + "." + stringObjectEntry.getKey());
                        return new AttributeWithValue(attribute, Collections.singletonList(stringObjectEntry.getValue().toString()));
                    }).collect(Collectors.toList()));
        }

        // other datasources
        List<Datasource> datasources = new ArrayList<>();
        for (Map.Entry<String, Map<String, Object>> entry : testDescription.getAttributes().entrySet()) {
            String entryKey = entry.getKey();
            if (!entryKey.equals(SUBJECT_NAME) && !entryKey.equals("order")) {
                for (Map.Entry<String, Object> valEntry : entry.getValue().entrySet()) {
                    datasources.add(new SimpleDatasource(entry.getKey() + "." + valEntry.getKey(), valEntry.getValue().toString()));
                }
            }
        }
        builder.datasources(datasources);

        AuthService authService = builder.build();
        EasyABACPermissionChecker<Object, Object> permissionChecker = new EasyABACPermissionChecker<>(authService);
        return permissionChecker;
    }

}
