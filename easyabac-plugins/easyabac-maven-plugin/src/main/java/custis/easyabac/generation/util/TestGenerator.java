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
import custis.easyabac.api.test.BaseTestClass;
import custis.easyabac.core.model.abac.Policy;
import custis.easyabac.core.model.abac.Rule;
import custis.easyabac.core.model.abac.attribute.Resource;
import org.apache.commons.lang3.StringUtils;
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
        ClassOrInterfaceDeclaration ext = type.addExtendedType(BaseTestClass.class.getSimpleName());

        type.addSingleMemberAnnotation(RunWith.class.getSimpleName(), new ClassExpr(new ClassOrInterfaceType(JUnit4.class.getSimpleName())));


        // creating tests
        for (String value : resource.getActions()) {
            createTestsForCase(type, resource, StringUtils.upperCase(value), permissions);
        }


        return type;
    }

    private static void createTestsForCase(ClassOrInterfaceDeclaration type, Resource resource, String value, List<Policy> permissions) {
        createPermitTest(type, value, resource);
        for (Policy permission : permissions) {
            if (permission.getTarget().getAccessToActions().contains(value)) {
                permission.getRules().forEach((s, easyRule) -> createDenyTest(type, value, resource.getId(), s, easyRule));
            }
        }


    }

    private static void createDenyTest(ClassOrInterfaceDeclaration type, String value, String entityName, String ruleId, Rule rule) {
        MethodDeclaration method = type.addMethod("test" + value + "_Deny_" + ruleId, Modifier.PUBLIC);
        method.addMarkerAnnotation(Ignore.class);
        NormalAnnotationExpr annotation = method.addAndGetAnnotation(Test.class);
        annotation.addPair("expected", new ClassExpr(new ClassOrInterfaceType(NotPermittedException.class.getSimpleName())));

        BlockStmt body = new BlockStmt();
        body.addStatement("permissionChecker.ensurePermitted(getDataForTest" + value + "_Deny" + ruleId + "(), " + value + ");");

        method.setBody(body);


        MethodDeclaration dataMethod = type.addMethod("getDataForTest" + value + "_Deny" + ruleId, Modifier.PRIVATE);
        dataMethod.setType(capitalize(entityName));

        BlockStmt data = new BlockStmt();
        data.addStatement("return null;");
        dataMethod.setBody(data);
    }

    private static void createPermitTest(ClassOrInterfaceDeclaration type, String value, Resource resource) {
        MethodDeclaration method = type.addMethod("test" + value + "_Permit", Modifier.PUBLIC);
        method.addMarkerAnnotation(Ignore.class);
        method.addMarkerAnnotation(Test.class);

        BlockStmt body = new BlockStmt();
        body.addStatement("permissionChecker.ensurePermitted(getDataForTest" + value + "_Permit(), " + value + ");");

        method.setBody(body);

        MethodDeclaration dataMethod = type.addMethod("getDataForTest" + value + "_Permit", Modifier.PRIVATE);
        dataMethod.setType(capitalize(resource.getId()));

        BlockStmt data = new BlockStmt();
        data.addStatement("return null;");
        dataMethod.setBody(data);
    }

    private static List<ImportDeclaration> IMPORTS = new ArrayList<ImportDeclaration>() {
        {
            add(new ImportDeclaration(NotPermittedException.class.getName(), false, false));
            add(new ImportDeclaration(BaseTestClass.class.getName(), false, false));

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
