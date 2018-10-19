package custis.easyabac.generation;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.utils.SourceRoot;
import custis.easyabac.api.test.EasyAbacBaseTestClass;
import custis.easyabac.api.test.TestAction;
import custis.easyabac.api.test.TestDescription;
import custis.easyabac.api.test.helper.AutogeneratingTestDataHelper;
import custis.easyabac.api.test.helper.ModelHelper;
import custis.easyabac.generation.algorithm.CombinationAlgorithmFactory;
import custis.easyabac.generation.algorithm.FunctionUtils;
import custis.easyabac.generation.algorithm.TestGenerationAlgorithm;
import custis.easyabac.model.AbacAuthModel;
import custis.easyabac.model.EasyAbacInitException;
import custis.easyabac.model.Effect;
import custis.easyabac.model.attribute.Attribute;
import custis.easyabac.model.attribute.DataType;
import custis.easyabac.model.attribute.Resource;
import org.junit.runners.Parameterized;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static custis.easyabac.generation.ModelGenerator.resolvePathForSourceFile;
import static custis.easyabac.generation.algorithm.FunctionUtils.ANY_FUNCTION;
import static org.apache.commons.lang3.StringUtils.capitalize;

public class TestGenerator {

    public static final String DATA_FILE_SUFFIX = ".yaml";

    public static void createTests(Resource resource, String packageName, SourceRoot sourceRoot, SourceRoot resourceRoot, AbacAuthModel abacAuthModel, String modelFileName) throws IOException, EasyAbacInitException {
        if (resource.getActions() == null || resource.getActions().isEmpty()) {
            return;
        }

        // generating tests
        createTestClass(resource, packageName, sourceRoot, resourceRoot, abacAuthModel, modelFileName);
    }

    private static void createTestClass(Resource resource, String packageName, SourceRoot sourceRoot, SourceRoot resourceRoot, AbacAuthModel abacAuthModel, String modelFileName) throws IOException, EasyAbacInitException {
        CompilationUnit testUnit = new CompilationUnit(packageName);
        String resourceName = capitalize(resource.getId());
        String testName = resourceName + "AuthTest";
        ClassOrInterfaceDeclaration type = createType(testUnit, testName, resource, packageName);

        createConstructor(type, modelFileName);
        boolean atLeastOne = createData(type, abacAuthModel, testName, resource, resourceRoot, packageName);

        if (atLeastOne) {
            testUnit.setStorage(resolvePathForSourceFile(sourceRoot, packageName, testName));
            sourceRoot.add(testUnit);
        }
    }

    private static void createConstructor(ClassOrInterfaceDeclaration type, String modelFileName) {
        ConstructorDeclaration constructor = type.addConstructor(Modifier.PUBLIC);
        constructor.addThrownException(Exception.class);
        BlockStmt body = constructor.getBody();
        body.addStatement("super(loadModelFromResource(\"" + modelFileName + "\"));");
    }

    private static boolean createData(ClassOrInterfaceDeclaration type, AbacAuthModel abacAuthModel, String testName, Resource resource, SourceRoot resourceRoot, String packageName) throws IOException, EasyAbacInitException {
        createDataMethod(type, testName, resource);

        TestGenerationAlgorithm algorithm = CombinationAlgorithmFactory.getByCode(abacAuthModel.getCombiningAlgorithm());
        List<Map<String, String>> testsPermit = algorithm.generatePolicies(abacAuthModel.getPolicies(), Effect.PERMIT);
        List<Map<String, String>> testsDeny = algorithm.generatePolicies(abacAuthModel.getPolicies(), Effect.DENY);

        serializeData(testsPermit, Effect.PERMIT, abacAuthModel, resourceRoot, packageName, resource);
        serializeData(testsDeny, Effect.DENY, abacAuthModel, resourceRoot, packageName, resource);
        return (testsPermit.size() + testsDeny.size()) > 0;
    }

    private static void serializeData(List<Map<String, String>> tests, Effect permit, AbacAuthModel abacAuthModel, SourceRoot resourceRoot, String packageName, Resource resource) throws IOException {
        Yaml yaml = new Yaml();
        for (int i = 0; i < tests.size(); i++) {
            TestDescription testDescription = new TestDescription();
            Map<String, String> data = tests.get(i);
            String action = data.remove(FunctionUtils.ACTION);
            testDescription.setExpectedResult(permit.name());
            testDescription.setAction(new TestAction("order.action", action));

            Map<String, Object> prettyData = beautifyValues(abacAuthModel, data);

            Map<String, Map<String, Object>> structMap = new HashMap<>();
            prettyData.entrySet().stream()
                    .forEach(stringStringEntry -> {
                        String key = stringStringEntry.getKey();
                        String entity = key.substring(0, key.indexOf("."));
                        Map<String, Object> attrMap = structMap.computeIfAbsent(entity, s -> new HashMap<>());
                        attrMap.put(key.substring(entity.length() + 1), stringStringEntry.getValue());
                    });


            testDescription.setAttributes(structMap);

            // creating folders
            String folderName = resourceRoot.getRoot().toString() + "/" + packageName.replace(".", "/");
            new File(folderName).mkdirs();
            String dataFileName = folderName + "/" + resource.getId().toLowerCase() + "_" + i + DATA_FILE_SUFFIX;
            File dataFile = new File(dataFileName);
            if (!dataFile.exists()) {
                dataFile.createNewFile();
            }
            FileWriter writer = new FileWriter(dataFile);
            yaml.dump(testDescription, writer);
        }

    }

    /**
     * Making values pretty and make concrete type
     */
    private static Map<String, Object> beautifyValues(AbacAuthModel abacAuthModel, Map<String, String> data) {
        Map<String, Object> beautifullMap = new HashMap<>();
        Map<String, Object> mapping = new HashMap<>();
        Map<String, Integer> attributeCounter = new HashMap<>();
        Map<String, Attribute> attributes = abacAuthModel.getAttributes();


        // process terminal elements
        data.forEach((attributeKey, value) -> {
            Attribute attribute = attributes.get(attributeKey);
            Object newValue = value;

            if (value.startsWith(FunctionUtils.UNKNOWN_PREFIX)) {
                // should be beautified
                if (mapping.containsKey(value)) {
                    newValue = mapping.get(value);
                } else {
                    Integer counter = attributeCounter.computeIfAbsent(attributeKey, s -> 0);
                    ++counter;

                    if (attribute.getType() == DataType.STRING) {
                        newValue = attributeKey + "_" + counter;
                    } else if (attribute.getType() == DataType.INT) {
                        newValue = 1000; // to be in positive range
                    }
                    mapping.put(value, newValue);
                }
            }
            beautifullMap.put(attributeKey, newValue);
        });

        // process function elements
        beautifullMap.forEach((attributeKey, value) -> {
            Attribute attribute = attributes.get(attributeKey);
            if (value instanceof String) {
                // possible function
                String strValue = value.toString();
                if (strValue.startsWith(ANY_FUNCTION)) {
                    Object newValue = processFunctional(strValue, beautifullMap, mapping, attributeKey);
                    mapping.put(strValue, newValue);
                    beautifullMap.put(attributeKey, newValue);
                }
            }


        });

        return beautifullMap;
    }

    private static Object processFunctional(String strValue, Map<String, Object> beautifullMap, Map<String, Object> mapping, String attributeKey) {
        if (strValue.startsWith(ANY_FUNCTION)) {
            String withoutPrefix = strValue.substring(ANY_FUNCTION.length() + FunctionUtils.FUNCTION_CODE_LENGTH);
            String function = strValue.substring(0, ANY_FUNCTION.length() + FunctionUtils.FUNCTION_CODE_LENGTH);
            Object nestedValue = processFunctional(withoutPrefix, beautifullMap, mapping, attributeKey);
            // apply function
            return FunctionUtils.calculateValue(nestedValue, function);

        } else {
            return mapping.getOrDefault(strValue, strValue);
        }
    }

    private static void createDataMethod(ClassOrInterfaceDeclaration type, String testName, Resource resource) {
        MethodDeclaration method = type.addMethod("data", Modifier.PUBLIC, Modifier.STATIC);
        method.addThrownException(Exception.class);
        method.setType("List<Object[]>");
        NormalAnnotationExpr annotation = method.addAndGetAnnotation(Parameterized.Parameters.class);
        annotation.addPair("name", new StringLiteralExpr("{index}: resource({0}) and action({1}). Expecting permit = ({2})"));

        BlockStmt body = new BlockStmt();
        body.addStatement("return loadGeneratedTestsFromPackage(" + testName + ".class, \"" + resource.getId() + "\");");
        method.setBody(body);
    }

    private static ClassOrInterfaceDeclaration createType(CompilationUnit testUnit, String name, Resource resource, String packageName) {
        for (ImportDeclaration annotationImport : IMPORTS) {
            testUnit.addImport(annotationImport);
        }
        String javaName = capitalize(name);

        Comment typeComment = new JavadocComment("Testing entity \"" + resource.getTitle() + "\"");
        testUnit.addOrphanComment(typeComment);

        ClassOrInterfaceDeclaration type = testUnit.addClass(javaName);
        ClassOrInterfaceDeclaration ext = type.addExtendedType(EasyAbacBaseTestClass.class.getSimpleName());

        return type;
    }

    private static List<ImportDeclaration> IMPORTS = new ArrayList<ImportDeclaration>() {
        {
            add(new ImportDeclaration(EasyAbacBaseTestClass.class.getName(), false, false));
            add(new ImportDeclaration(AutogeneratingTestDataHelper.class.getName() + ".loadGeneratedTestsFromPackage", true, false));
            add(new ImportDeclaration(ModelHelper.class.getName() + ".loadModelFromResource", true, false));
            // test
            add(new ImportDeclaration(List.class.getName(), false, false));

            // general
        }
    };

}
