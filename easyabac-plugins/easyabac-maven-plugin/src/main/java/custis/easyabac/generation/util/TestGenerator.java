package custis.easyabac.generation.util;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.utils.SourceRoot;
import custis.easyabac.api.NotPermittedException;
import custis.easyabac.api.test.EasyAbacBaseTestClass;
import custis.easyabac.api.test.TestDescription;
import custis.easyabac.core.model.abac.AbacAuthModel;
import custis.easyabac.core.model.abac.Policy;
import custis.easyabac.core.model.abac.Rule;
import custis.easyabac.core.model.abac.attribute.Resource;
import custis.easyabac.generation.util.algorithm.CombinationAlgorithmFactory;
import custis.easyabac.generation.util.algorithm.TestGenerationAlgorithm;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.yaml.snakeyaml.Yaml;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static custis.easyabac.api.test.EasyAbacBaseTestClass.DATA_FILE_SUFFIX;
import static custis.easyabac.generation.util.CompleteGenerator.MODEL_SUFFIX;
import static custis.easyabac.generation.util.ModelGenerator.ACTION_SUFFIX;
import static custis.easyabac.generation.util.ModelGenerator.resolvePathForSourceFile;
import static org.apache.commons.lang3.StringUtils.capitalize;

public class TestGenerator {

    public static void createTests(Resource resource, String packageName, SourceRoot sourceRoot, AbacAuthModel abacAuthModel) throws IOException {
        if (resource.getActions() == null || resource.getActions().isEmpty()) {
            return;
        }

        // generating Deny tests
        createDenyTestClass(resource, packageName, sourceRoot, abacAuthModel);

        // generating Permit tests
        createPermitTestClass(resource, packageName, sourceRoot, abacAuthModel);
    }

    private static void createDenyTestClass(Resource resource, String packageName, SourceRoot sourceRoot, AbacAuthModel abacAuthModel) throws IOException {
        CompilationUnit testUnit = new CompilationUnit(packageName);
        String resourceName = capitalize(resource.getId());
        String testName = "EasyABAC_" + resourceName + "_Deny_Test";
        ClassOrInterfaceDeclaration type = createType(testUnit, testName, resource, packageName);

        createDenyTest(type);
        createData(type, abacAuthModel, resourceName.toLowerCase() + "_deny_", testName, resource, resourceName);

        testUnit.setStorage(resolvePathForSourceFile(sourceRoot, packageName, testName));
        sourceRoot.add(testUnit);
    }

    private static void createPermitTestClass(Resource resource, String packageName, SourceRoot sourceRoot, AbacAuthModel abacAuthModel) throws IOException {
        CompilationUnit testUnit = new CompilationUnit(packageName);
        String resourceName = capitalize(resource.getId());
        String testName = "EasyABAC_" + resourceName + "_Permit_Test";
        ClassOrInterfaceDeclaration type = createType(testUnit, testName, resource, packageName);

        createPermitTest(type);
        createData(type, abacAuthModel, resourceName.toLowerCase() + "_permit_", testName, resource, resourceName);

        testUnit.setStorage(resolvePathForSourceFile(sourceRoot, packageName, testName));
        sourceRoot.add(testUnit);
    }

    private static void createData(ClassOrInterfaceDeclaration type, AbacAuthModel abacAuthModel, String prefix, String testName, Resource resource, String resourceName) throws IOException {
        createDataMethod(type, prefix, testName, resource, resourceName);

        TestGenerationAlgorithm algorithm = CombinationAlgorithmFactory.createByCode(abacAuthModel.getCombiningAlgorithm());
        TestDataHolder testDataHolder = new TestDataHolder();
        algorithm.generatePolicies(abacAuthModel.getPolicies(), testDataHolder);

        Yaml yaml = new Yaml();
        for (int i = 0; i < testDataHolder.getPermitTests().size(); i++) {
            TestDescription permitTest = testDataHolder.getPermitTests().get(i);
            FileWriter writer = new FileWriter(resource.getId().toLowerCase() + "_permit_" + i + DATA_FILE_SUFFIX);
            yaml.dump(permitTest, writer);
        }

        for (int i = 0; i < testDataHolder.getDenyTests().size(); i++) {
            TestDescription permitTest = testDataHolder.getDenyTests().get(i);
            FileWriter writer = new FileWriter(resource.getId().toLowerCase() + "_deny_" + i + DATA_FILE_SUFFIX);
            yaml.dump(permitTest, writer);
        }
    }

    private static void createDataMethod(ClassOrInterfaceDeclaration type, String prefix, String testName, Resource resource, String resourceName) {
        type.addFieldWithInitializer(int.class, "numberOfTests", new IntegerLiteralExpr(0), Modifier.PRIVATE, Modifier.STATIC);
        type.addFieldWithInitializer(String.class, "dataFilePrefix", new StringLiteralExpr(prefix), Modifier.PRIVATE, Modifier.STATIC);


        MethodDeclaration method = type.addMethod("data", Modifier.PUBLIC, Modifier.STATIC);
        method.addThrownException(IllegalAccessException.class);
        method.addThrownException(InstantiationException.class);
        method.setType("List<Object[]>");
        NormalAnnotationExpr annotation = method.addAndGetAnnotation(Parameterized.Parameters.class);
        annotation.addPair("name", new StringLiteralExpr("{index}: resource({0}) and action({1})"));

        BlockStmt body = new BlockStmt();
        body.addStatement("return generateTestData(" + testName + ".class,\n " + resourceName + ".class,\n "
                + resourceName + ACTION_SUFFIX + ".class,\n numberOfTests,\n dataFilePrefix,\n \"" + resource.getId().toLowerCase() + "\");");
        method.setBody(body);
    }

    private static void createDenyTest(ClassOrInterfaceDeclaration type) {
        MethodDeclaration method = type.addMethod("test_Deny", Modifier.PUBLIC);
        method.addMarkerAnnotation(Ignore.class);
        NormalAnnotationExpr annotation = method.addAndGetAnnotation(Test.class);
        annotation.addPair("expected", new ClassExpr(new ClassOrInterfaceType(NotPermittedException.class.getSimpleName())));

        BlockStmt body = new BlockStmt();
        body.addStatement("permissionChecker.ensurePermitted(resource, action);");

        method.setBody(body);
    }

    private static void createPermitTest(ClassOrInterfaceDeclaration type) {
        MethodDeclaration method = type.addMethod("test_Permit", Modifier.PUBLIC);
        method.addMarkerAnnotation(Ignore.class);
        method.addMarkerAnnotation(Test.class);

        BlockStmt body = new BlockStmt();
        body.addStatement("permissionChecker.ensurePermitted(resource, action);");

        method.setBody(body);
    }

    private static ClassOrInterfaceDeclaration createType(CompilationUnit testUnit, String name, Resource resource, String packageName) {
        for (ImportDeclaration annotationImport : IMPORTS) {
            testUnit.addImport(annotationImport);
        }
        testUnit.addImport(new ImportDeclaration(packageName + MODEL_SUFFIX, false, true));

        String javaName = capitalize(name);

        Comment typeComment = new JavadocComment("Testing entity \"" + resource.getTitle() + "\"");
        testUnit.addOrphanComment(typeComment);

        ClassOrInterfaceDeclaration type = testUnit.addClass(javaName);
        ClassOrInterfaceDeclaration ext = type.addExtendedType(EasyAbacBaseTestClass.class.getSimpleName());

        return type;
    }

    private static void createTestsForCase(ClassOrInterfaceDeclaration type, Resource resource, String action, List<Policy> permissions) {
        String genAction = resource.getId() + "." + action;
        createPermitTestOld(type, action, resource);
        for (Policy permission : permissions) {
            if (permission.getTarget().getAccessToActions().contains(genAction)) {
                permission.getRules().forEach(rule -> createDenyTestOld(type, action, resource.getId(), rule));
            }
        }


    }

    private static void createDenyTestOld(ClassOrInterfaceDeclaration type, String value, String entityName, Rule rule) {
        String enumValue = value.toUpperCase();

        MethodDeclaration method = type.addMethod("test_" + enumValue + "_Deny_" + rule.getId(), Modifier.PUBLIC);
        method.addMarkerAnnotation(Ignore.class);
        NormalAnnotationExpr annotation = method.addAndGetAnnotation(Test.class);
        annotation.addPair("expected", new ClassExpr(new ClassOrInterfaceType(NotPermittedException.class.getSimpleName())));

        BlockStmt body = new BlockStmt();
        body.addStatement("permissionChecker.ensurePermitted(data_" + enumValue + "_Deny_" + rule.getId() + "(), " + enumValue + ");");

        method.setBody(body);


        MethodDeclaration dataMethod = type.addMethod("data_" + enumValue + "_Deny_" + rule.getId(), Modifier.PRIVATE);
        dataMethod.setType(capitalize(entityName));

        BlockStmt data = new BlockStmt();
        data.addStatement("return null;");
        dataMethod.setBody(data);
    }

    private static void createPermitTestOld(ClassOrInterfaceDeclaration type, String value, Resource resource) {
        String enumValue = value.toUpperCase();
        MethodDeclaration method = type.addMethod("test_" + enumValue + "_Permit", Modifier.PUBLIC);
        method.addMarkerAnnotation(Ignore.class);
        method.addMarkerAnnotation(Test.class);


        BlockStmt body = new BlockStmt();
        body.addStatement("permissionChecker.ensurePermitted(data_" + enumValue + "_Permit(), " + enumValue + ");");

        method.setBody(body);

        MethodDeclaration dataMethod = type.addMethod("data_" + enumValue + "_Permit", Modifier.PRIVATE);
        dataMethod.setType(capitalize(resource.getId()));

        BlockStmt data = new BlockStmt();
        data.addStatement("return null;");
        dataMethod.setBody(data);
    }

    private static List<ImportDeclaration> IMPORTS = new ArrayList<ImportDeclaration>() {
        {
            add(new ImportDeclaration(NotPermittedException.class.getName(), false, false));
            add(new ImportDeclaration(EasyAbacBaseTestClass.class.getName(), false, false));
            add(new ImportDeclaration(TestDescription.class.getName(), false, false));

            // test
            add(new ImportDeclaration(Ignore.class.getName(), false, false));
            add(new ImportDeclaration(Test.class.getName(), false, false));
            add(new ImportDeclaration(Map.class.getName(), false, false));
            add(new ImportDeclaration(List.class.getName(), false, false));
            add(new ImportDeclaration(ArrayList.class.getName(), false, false));

            // general
        }
    };

}
