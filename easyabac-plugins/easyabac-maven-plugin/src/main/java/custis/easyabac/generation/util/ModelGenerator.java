package custis.easyabac.generation.util;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.VoidType;
import com.github.javaparser.utils.SourceRoot;
import custis.easyabac.api.AuthorizationAction;
import custis.easyabac.api.AuthorizationActionId;
import custis.easyabac.api.AuthorizationAttribute;
import custis.easyabac.api.AuthorizationEntity;
import custis.easyabac.core.model.attribute.load.EasyAttribute;
import custis.easyabac.core.model.attribute.load.EasyObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelGenerator {

    public static final Logger LOGGER = LoggerFactory.getLogger(ModelGenerator.class);

    public static void generate(String name, EasyObject easyObject, String packageName, SourceRoot sourceRoot) {
        LOGGER.info("Generating model for [{}]", easyObject.getTitle());
        // In this case the root directory is found by taking the root from the current Maven module,
        // with src/main/resources appended.
        prepareEntityCompilationUnit(name, easyObject, packageName, sourceRoot);
        prepareActionsCompilationUnit(name, easyObject, packageName, sourceRoot);

    }

    private static void prepareActionsCompilationUnit(String name, EasyObject easyObject, String packageName, SourceRoot sourceRoot) {
        CompilationUnit actionUnit = new CompilationUnit(packageName);
        String enumName = name + "Action";

        EnumDeclaration type = createEnum(actionUnit, enumName, easyObject);

        for (EasyAttribute easyAttribute : easyObject.getActions()) {
            EnumConstantDeclaration entry = new EnumConstantDeclaration();
            for (String val : easyAttribute.getAllowableValues()) {
                entry.setName(new SimpleName(val));
            }

            type.addEntry(entry);

            Comment comment = new JavadocComment("Authorization attribute \"" + easyAttribute.getTitle() + "\"");
            type.addOrphanComment(comment);

            FieldDeclaration field = type.addField(getTypeForModelType(easyAttribute.getType()), easyAttribute.getCode(), Modifier.PRIVATE);

            NormalAnnotationExpr annotation = field.addAndGetAnnotation(AuthorizationAttribute.class.getSimpleName());
            annotation.addPair("id", "\"" + easyAttribute.getCode() + "\"");
        }


        //generateFieldAccessors(type, easyObject.getAttributes());

        actionUnit.setStorage(resolvePathForSourceFile(sourceRoot, packageName, enumName));
        sourceRoot.add(actionUnit);
    }

    private static Path resolvePathForSourceFile(SourceRoot sourceRoot, String packageName, String name) {
        String[] packages = packageName.split("\\.");
        Path targetPath = sourceRoot.getRoot();
        for (String aPackage : packages) {
            targetPath = targetPath.resolve(aPackage);
        }
        return targetPath.resolve(StringUtils.capitalize(name) + ".java");
    }

    private static void prepareEntityCompilationUnit(String name, EasyObject easyObject, String packageName, SourceRoot sourceRoot) {
        CompilationUnit entityUnit = new CompilationUnit(packageName);


        ClassOrInterfaceDeclaration type = createType(entityUnit, name, easyObject);

        for (EasyAttribute easyAttribute : easyObject.getAttributes()) {
            Comment comment = new JavadocComment("Authorization attribute \"" + easyAttribute.getTitle() + "\"");
            type.addOrphanComment(comment);

            FieldDeclaration field = type.addField(getTypeForModelType(easyAttribute.getType()), easyAttribute.getCode(), Modifier.PRIVATE);

            NormalAnnotationExpr annotation = field.addAndGetAnnotation(AuthorizationAttribute.class.getSimpleName());
            annotation.addPair("id", "\"" + easyAttribute.getCode() + "\"");
        }


        generateFieldAccessors(type, easyObject.getAttributes());


        String[] packages = packageName.split("\\.");
        Path targetPath = sourceRoot.getRoot();
        for (String aPackage : packages) {
            targetPath = targetPath.resolve(aPackage);
        }

        entityUnit.setStorage(resolvePathForSourceFile(sourceRoot, packageName, name));
        sourceRoot.add(entityUnit);
    }

    private static ClassOrInterfaceDeclaration createType(CompilationUnit entityUnit, String name, EasyObject easyObject) {
        for (ImportDeclaration annotationImport : ANNOTATION_IMPORTS) {
            entityUnit.addImport(annotationImport);
        }

        String javaName = StringUtils.capitalize(name);

        Comment typeComment = new JavadocComment("Authorization entity \"" + easyObject.getTitle() + "\"");
        entityUnit.addOrphanComment(typeComment);

        ClassOrInterfaceDeclaration type = entityUnit.addClass(javaName);

        NormalAnnotationExpr annotation = type.addAndGetAnnotation(AuthorizationEntity.class.getSimpleName());
        annotation.addPair("name", "\"" + name + "\"");
        return type;
    }

    private static EnumDeclaration createEnum(CompilationUnit actionUnit, String name, EasyObject easyObject) {
        for (ImportDeclaration annotationImport : ANNOTATION_ENUM_IMPORTS) {
            actionUnit.addImport(annotationImport);
        }

        String javaName = StringUtils.capitalize(name);

        Comment typeComment = new JavadocComment("Authorization enum \"" + easyObject.getTitle() + "\"");
        actionUnit.addOrphanComment(typeComment);

        EnumDeclaration type = actionUnit.addEnum(javaName);

        NormalAnnotationExpr annotation = type.addAndGetAnnotation(AuthorizationAction.class.getSimpleName());
        return type;
    }

    private static void generateFieldAccessors(ClassOrInterfaceDeclaration type, List<EasyAttribute> attributes) {
        // empty constructor
        ConstructorDeclaration emptyConstructor = type.addConstructor(Modifier.PUBLIC);

        // all arguments constructor
        ConstructorDeclaration constructor = type.addConstructor(Modifier.PUBLIC);

        Comment comment = new LineComment("Simple getters and setters");
        type.addOrphanComment(comment);

        BlockStmt body = new BlockStmt();
        for (EasyAttribute easyAttribute : attributes) {
            constructor.addAndGetParameter(getTypeForModelType(easyAttribute.getType()), easyAttribute.getCode());

            body.addStatement("this." + easyAttribute.getCode() + " = " + easyAttribute.getCode() + ";");

            generateGetter(type, easyAttribute);
            generateSetter(type, easyAttribute);
        }

        constructor.setBody(body);
    }

    private static void generateGetter(ClassOrInterfaceDeclaration type, EasyAttribute easyAttribute) {
        MethodDeclaration method = type.addMethod("get" + StringUtils.capitalize(easyAttribute.getCode()), Modifier.PUBLIC);
        method.setType(getTypeForModelType(easyAttribute.getType()));

        BlockStmt body = new BlockStmt();
        body.addStatement("return this." + easyAttribute.getCode() + ";");

        method.setBody(body);
    }

    private static void generateSetter(ClassOrInterfaceDeclaration type, EasyAttribute easyAttribute) {
        MethodDeclaration method = type.addMethod("set" + StringUtils.capitalize(easyAttribute.getCode()), Modifier.PUBLIC);
        method.setType(new VoidType());
        method.addParameter(new Parameter(getTypeForModelType(easyAttribute.getType()), easyAttribute.getCode()));

        BlockStmt body = new BlockStmt();
        body.addStatement("this." + easyAttribute.getCode() + " = " + easyAttribute.getCode() + ";");

        method.setBody(body);
    }

    public static void populate(EasyObject easyObject, String packageName) {
        LOGGER.info("Populating model for [{}]", easyObject.getTitle());
    }

    private static Type getTypeForModelType(String type) {
        return TYPE_MAPPING.get(type);
    }

    private static List<ImportDeclaration> ANNOTATION_IMPORTS = new ArrayList<ImportDeclaration>() {
        {
            add(new ImportDeclaration(AuthorizationAttribute.class.getName(), false, false));
            add(new ImportDeclaration(AuthorizationEntity.class.getName(), false, false));
        }
    };

    private static List<ImportDeclaration> ANNOTATION_ENUM_IMPORTS = new ArrayList<ImportDeclaration>() {
        {
            add(new ImportDeclaration(AuthorizationAction.class.getName(), false, false));
            add(new ImportDeclaration(AuthorizationActionId.class.getName(), false, false));
        }
    };

    private static Map<String, Type> TYPE_MAPPING = new HashMap<String, Type>() {
        {
            put("string", new ClassOrInterfaceType("String"));
        }
    };
}
