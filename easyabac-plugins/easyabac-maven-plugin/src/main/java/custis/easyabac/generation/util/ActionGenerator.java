package custis.easyabac.generation.util;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.utils.SourceRoot;
import custis.easyabac.api.AuthorizationAction;
import custis.easyabac.api.AuthorizationActionId;
import custis.easyabac.core.model.abac.attribute.DataType;
import custis.easyabac.core.model.abac.attribute.Resource;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static custis.easyabac.generation.util.ModelGenerator.*;

public class ActionGenerator {
    public  static void createAction(Resource resource, String packageName, SourceRoot sourceRoot) {
        if (resource.getActions() == null || resource.getActions().isEmpty()) {
            return;
        }

        CompilationUnit actionUnit = new CompilationUnit(packageName);
        String enumName = resource.getId() + "Action";

        EnumDeclaration type = createEnum(actionUnit, enumName, resource);

        for (String val : resource.getActions()) {
            EnumConstantDeclaration entry = new EnumConstantDeclaration();
            entry.setName(new SimpleName(StringUtils.upperCase(val)));
            entry.addArgument("\"" + val + "\"");


            type.addEntry(entry);
        }

        FieldDeclaration field = type.addField(getTypeForModelType(DataType.STRING), "id", Modifier.PRIVATE);
        field.addMarkerAnnotation(AuthorizationActionId.class.getSimpleName());

        // all arguments constructor
        ConstructorDeclaration constructor = type.addConstructor(Modifier.PRIVATE);

        Comment comment = new LineComment("Simple getters and setters");
        type.addOrphanComment(comment);

        generateFieldAccessors(type, constructor, Arrays.asList(field), false);

        actionUnit.setStorage(resolvePathForSourceFile(sourceRoot, packageName, enumName));
        sourceRoot.add(actionUnit);
    }

    private static EnumDeclaration createEnum(CompilationUnit actionUnit, String name, Resource resource) {
        for (ImportDeclaration annotationImport : ANNOTATION_ENUM_IMPORTS) {
            actionUnit.addImport(annotationImport);
        }

        String javaName = StringUtils.capitalize(name);

        Comment typeComment = new JavadocComment("Authorization enum \"" + resource.getTitle() + "\"");
        actionUnit.addOrphanComment(typeComment);

        EnumDeclaration type = actionUnit.addEnum(javaName);

        NormalAnnotationExpr annotation = type.addAndGetAnnotation(AuthorizationAction.class.getSimpleName());
        annotation.addPair("entity", "\"" + resource.getId() + "\"");
        return type;
    }

    private static List<ImportDeclaration> ANNOTATION_ENUM_IMPORTS = new ArrayList<ImportDeclaration>() {
        {
            add(new ImportDeclaration(AuthorizationAction.class.getName(), false, false));
            add(new ImportDeclaration(AuthorizationActionId.class.getName(), false, false));
        }
    };
}
