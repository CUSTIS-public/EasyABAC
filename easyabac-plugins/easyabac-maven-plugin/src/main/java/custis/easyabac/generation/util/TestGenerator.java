package custis.easyabac.generation.util;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.utils.SourceRoot;
import custis.easyabac.api.NotPermittedException;
import custis.easyabac.api.test.EasyAbacBaseTestClass;
import custis.easyabac.core.model.abac.Policy;
import custis.easyabac.core.model.abac.Rule;
import custis.easyabac.core.model.abac.attribute.Resource;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import static custis.easyabac.generation.util.ModelGenerator.resolvePathForSourceFile;
import static org.apache.commons.lang3.StringUtils.capitalize;

public class TestGenerator {

    public static void createTest(Resource resource, String packageName, SourceRoot sourceRoot, List<Policy> permissions) {
        if (resource.getActions() == null || resource.getActions().isEmpty()) {
            return;
        }

        CompilationUnit testUnit = new CompilationUnit(packageName);

        String testName = "EasyABAC_" + capitalize(resource.getId()) + "_Test";
        createType(testUnit, testName, resource, packageName, permissions);

        testUnit.setStorage(resolvePathForSourceFile(sourceRoot, packageName, testName));
        sourceRoot.add(testUnit);

    }

    private static ClassOrInterfaceDeclaration createType(CompilationUnit testUnit, String name, Resource resource, String packageName, List<Policy> permissions) {
        for (ImportDeclaration annotationImport : IMPORTS) {
            testUnit.addImport(annotationImport);
        }
        testUnit.addImport(new ImportDeclaration(packageName + ".model", false, true));
        testUnit.addImport(new ImportDeclaration(packageName + ".model." + capitalize(resource.getId()) + "Action", true, true));

        String javaName = capitalize(name);

        Comment typeComment = new JavadocComment("Testing entity \"" + resource.getTitle() + "\"");
        testUnit.addOrphanComment(typeComment);

        ClassOrInterfaceDeclaration type = testUnit.addClass(javaName);
        ClassOrInterfaceDeclaration ext = type.addExtendedType(EasyAbacBaseTestClass.class.getSimpleName());

        // creating tests
        for (String value : resource.getActions()) {
            createTestsForCase(type, resource, value, permissions);
        }


        return type;
    }

    private static void createTestsForCase(ClassOrInterfaceDeclaration type, Resource resource, String action, List<Policy> permissions) {
        String genAction = resource.getId() + "." + action;
        createPermitTest(type, action, resource);
        for (Policy permission : permissions) {
            if (permission.getTarget().getAccessToActions().contains(genAction)) {
                permission.getRules().forEach(rule -> createDenyTest(type, action, resource.getId(), rule));
            }
        }


    }

    private static void createDenyTest(ClassOrInterfaceDeclaration type, String value, String entityName, Rule rule) {
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

    private static void createPermitTest(ClassOrInterfaceDeclaration type, String value, Resource resource) {
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

            // test
            add(new ImportDeclaration(BeforeClass.class.getName(), false, false));
            add(new ImportDeclaration(Ignore.class.getName(), false, false));
            add(new ImportDeclaration(Test.class.getName(), false, false));
            add(new ImportDeclaration(RunWith.class.getName(), false, false));
            add(new ImportDeclaration(JUnit4.class.getName(), false, false));

            // general
        }
    };

}
