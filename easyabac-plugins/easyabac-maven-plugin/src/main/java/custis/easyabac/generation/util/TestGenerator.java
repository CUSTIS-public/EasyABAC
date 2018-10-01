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
import custis.easyabac.core.model.easy.EasyPolicy;
import custis.easyabac.core.model.easy.EasyResource;
import custis.easyabac.core.model.easy.EasyRule;
import org.apache.commons.lang3.StringUtils;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;

import static custis.easyabac.generation.util.ModelGenerator.resolvePathForSourceFile;

public class TestGenerator {

    public static void createTest(String name, EasyResource easyObject, String packageName, SourceRoot sourceRoot, List<EasyPolicy> permissions) {
        if (easyObject.getActions() == null || easyObject.getActions().isEmpty()) {
            return;
        }

        CompilationUnit testUnit = new CompilationUnit(packageName);

        String testName = "EasyABAC_" + name + "_Test";
        createType(testUnit, testName, easyObject, packageName, name, permissions);

        testUnit.setStorage(resolvePathForSourceFile(sourceRoot, packageName, testName));
        sourceRoot.add(testUnit);

    }

    private static ClassOrInterfaceDeclaration createType(CompilationUnit testUnit, String name, EasyResource easyObject, String packageName, String entityName, List<EasyPolicy> permissions) {
        for (ImportDeclaration annotationImport : IMPORTS) {
            testUnit.addImport(annotationImport);
        }
        testUnit.addImport(new ImportDeclaration(packageName + ".model", false, true));
        testUnit.addImport(new ImportDeclaration(packageName + ".model." + entityName + "Action", true, true));

        String javaName = StringUtils.capitalize(name);

        Comment typeComment = new JavadocComment("Testing entity \"" + easyObject.getTitle() + "\"");
        testUnit.addOrphanComment(typeComment);

        ClassOrInterfaceDeclaration type = testUnit.addClass(javaName);
        ClassOrInterfaceDeclaration ext = type.addExtendedType(BaseTestClass.class.getSimpleName());

        type.addSingleMemberAnnotation(RunWith.class.getSimpleName(), new ClassExpr(new ClassOrInterfaceType(JUnit4.class.getSimpleName())));


        // creating tests
        for (String value : easyObject.getActions()) {
            createTestsForCase(type, easyObject, StringUtils.upperCase(value), entityName, permissions);
        }


        return type;
    }

    private static void createTestsForCase(ClassOrInterfaceDeclaration type, EasyResource easyObject, String value, String entityName, List<EasyPolicy> permissions) {
        createPermitTest(type, value, entityName);
        for (EasyPolicy permission : permissions) {
            if (permission.getAccessToActions().contains(value)) {
                permission.getRules().forEach((s, easyRule) -> createDenyTest(type, value, entityName, s, easyRule));
            }
        }


    }

    private static void createDenyTest(ClassOrInterfaceDeclaration type, String value, String entityName, String ruleId, EasyRule easyRule) {
        MethodDeclaration method = type.addMethod("test" + value + "_Deny_" + ruleId, Modifier.PUBLIC);
        NormalAnnotationExpr annotation = method.addAndGetAnnotation(Test.class);
        annotation.addPair("expected", new ClassExpr(new ClassOrInterfaceType(NotPermittedException.class.getSimpleName())));

        BlockStmt body = new BlockStmt();
        body.addStatement("permissionChecker.ensurePermitted(getDataForTest" + value + "_Deny" + ruleId + "(), " + value + ");");

        method.setBody(body);


        MethodDeclaration dataMethod = type.addMethod("getDataForTest" + value + "_Deny" + ruleId, Modifier.PRIVATE);
        dataMethod.setType(entityName);

        BlockStmt data = new BlockStmt();
        data.addStatement("return null;");
        dataMethod.setBody(data);
    }

    private static void createPermitTest(ClassOrInterfaceDeclaration type, String value, String entityName) {
        MethodDeclaration method = type.addMethod("test" + value + "_Permit", Modifier.PUBLIC);
        method.addAnnotation(Test.class);

        BlockStmt body = new BlockStmt();
        body.addStatement("permissionChecker.ensurePermitted(getDataForTest" + value + "_Permit(), " + value + ");");

        method.setBody(body);

        MethodDeclaration dataMethod = type.addMethod("getDataForTest" + value + "_Permit", Modifier.PRIVATE);
        dataMethod.setType(entityName);

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
