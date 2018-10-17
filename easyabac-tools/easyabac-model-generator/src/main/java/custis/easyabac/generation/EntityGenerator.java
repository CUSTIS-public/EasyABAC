package custis.easyabac.generation;

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
import custis.easyabac.core.model.abac.attribute.Attribute;
import custis.easyabac.core.model.abac.attribute.Resource;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class EntityGenerator {
    public static void createEntity(Resource resource, String packageName, SourceRoot sourceRoot) {
        CompilationUnit entityUnit = new CompilationUnit(packageName);


        ClassOrInterfaceDeclaration type = createType(entityUnit, resource);
        List<FieldDeclaration> fields = new ArrayList<>();

        for (Attribute attribute : resource.getAttributes()) {
            Comment comment = new JavadocComment("Authorization attribute \"" + attribute.getTitle() + "\"");
            type.addOrphanComment(comment);

            String attributeId = attribute.getId();
            attributeId = attributeId.substring(attributeId.lastIndexOf(".") + 1);
            FieldDeclaration field = null;
            if (attribute.isMultiple()) {
                field = type.addField("List<" + ModelGenerator.getTypeForModelType(attribute.getType()) + ">", ModelGenerator.escape(attributeId), Modifier.PRIVATE);
            } else {
                field = type.addField(ModelGenerator.getTypeForModelType(attribute.getType()), ModelGenerator.escape(attributeId), Modifier.PRIVATE);

            }
            fields.add(field);

            NormalAnnotationExpr annotation = field.addAndGetAnnotation(AuthorizationAttribute.class.getSimpleName());
            annotation.addPair("id", "\"" + attributeId + "\"");
        }


        // empty constructor
        ConstructorDeclaration emptyConstructor = type.addConstructor(Modifier.PUBLIC);

        // all arguments constructor
        ConstructorDeclaration constructor = type.addConstructor(Modifier.PUBLIC);

        Comment comment = new LineComment("Simple getters and setters");
        type.addOrphanComment(comment);

        ModelGenerator.generateFieldAccessors(type, constructor, fields, true);


        String[] packages = packageName.split("\\.");
        Path targetPath = sourceRoot.getRoot();
        for (String aPackage : packages) {
            targetPath = targetPath.resolve(aPackage);
        }

        entityUnit.setStorage(ModelGenerator.resolvePathForSourceFile(sourceRoot, packageName, resource.getId()));
        sourceRoot.add(entityUnit);
    }

    private static ClassOrInterfaceDeclaration createType(CompilationUnit entityUnit, Resource resource) {
        for (ImportDeclaration annotationImport : ANNOTATION_IMPORTS) {
            entityUnit.addImport(annotationImport);
        }

        String javaName = StringUtils.capitalize(resource.getId());

        Comment typeComment = new JavadocComment("Authorization entity \"" + resource.getTitle() + "\"");
        entityUnit.addOrphanComment(typeComment);

        ClassOrInterfaceDeclaration type = entityUnit.addClass(javaName);

        NormalAnnotationExpr annotation = type.addAndGetAnnotation(AuthorizationEntity.class.getSimpleName());
        annotation.addPair("name", "\"" + resource.getId() + "\"");
        return type;
    }




    private static List<ImportDeclaration> ANNOTATION_IMPORTS = new ArrayList<ImportDeclaration>() {
        {
            add(new ImportDeclaration(AuthorizationAttribute.class.getName(), false, false));
            add(new ImportDeclaration(AuthorizationEntity.class.getName(), false, false));
        }
    };

}