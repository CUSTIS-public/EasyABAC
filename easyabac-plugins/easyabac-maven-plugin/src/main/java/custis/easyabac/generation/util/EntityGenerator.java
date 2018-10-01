package custis.easyabac.generation.util;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.utils.SourceRoot;
import custis.easyabac.api.AuthorizationAttribute;
import custis.easyabac.api.AuthorizationEntity;
import custis.easyabac.core.model.attribute.load.EasyAttribute;
import custis.easyabac.core.model.attribute.load.EasyObject;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static custis.easyabac.generation.util.ModelGenerator.*;

public class EntityGenerator {
    public static void createEntity(String name, EasyObject easyObject, String packageName, SourceRoot sourceRoot) {
        CompilationUnit entityUnit = new CompilationUnit(packageName);


        ClassOrInterfaceDeclaration type = createType(entityUnit, name, easyObject);
        List<FieldDeclaration> fields = new ArrayList<>();

        for (EasyAttribute easyAttribute : easyObject.getAttributes()) {
            Comment comment = new JavadocComment("Authorization attribute \"" + easyAttribute.getTitle() + "\"");
            type.addOrphanComment(comment);

            FieldDeclaration field = type.addField(getTypeForModelType(easyAttribute.getType()), easyAttribute.getCode(), Modifier.PRIVATE);
            fields.add(field);

            NormalAnnotationExpr annotation = field.addAndGetAnnotation(AuthorizationAttribute.class.getSimpleName());
            annotation.addPair("id", "\"" + easyAttribute.getCode() + "\"");
        }


        // empty constructor
        ConstructorDeclaration emptyConstructor = type.addConstructor(Modifier.PUBLIC);

        // all arguments constructor
        ConstructorDeclaration constructor = type.addConstructor(Modifier.PUBLIC);

        Comment comment = new LineComment("Simple getters and setters");
        type.addOrphanComment(comment);

        generateFieldAccessors(type, constructor, fields, true);


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




    private static List<ImportDeclaration> ANNOTATION_IMPORTS = new ArrayList<ImportDeclaration>() {
        {
            add(new ImportDeclaration(AuthorizationAttribute.class.getName(), false, false));
            add(new ImportDeclaration(AuthorizationEntity.class.getName(), false, false));
        }
    };

}