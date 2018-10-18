package custis.easyabac.generation;

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
import com.github.javaparser.utils.SourceRoot;
import custis.easyabac.api.attr.annotation.AuthorizationAction;
import custis.easyabac.api.attr.annotation.AuthorizationActionId;
import custis.easyabac.model.attribute.DataType;
import custis.easyabac.model.attribute.Resource;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ActionGenerator {
    public  static void createAction(Resource resource, String packageName, SourceRoot sourceRoot) {
        if (resource.getActions() == null || resource.getActions().isEmpty()) {
            return;
        }

        CompilationUnit actionUnit = new CompilationUnit(packageName);
        String enumName = resource.getId() + ModelGenerator.ACTION_SUFFIX;

        EnumDeclaration type = createEnum(actionUnit, enumName, resource);

        for (String val : resource.getActions()) {
            EnumConstantDeclaration entry = new EnumConstantDeclaration();
            entry.setName(new SimpleName(StringUtils.upperCase(val)));
            entry.addArgument("\"" + val + "\"");


            type.addEntry(entry);
        }

        FieldDeclaration field = type.addField(ModelGenerator.getTypeForModelType(DataType.STRING), "id", Modifier.PRIVATE);
        field.addMarkerAnnotation(AuthorizationActionId.class.getSimpleName());

        // all arguments constructor
        ConstructorDeclaration constructor = type.addConstructor(Modifier.PRIVATE);

        Comment comment = new LineComment("Simple getters and setters");
        type.addOrphanComment(comment);

        ModelGenerator.generateFieldAccessors(type, constructor, Arrays.asList(field), false);

        createByIdMethod(type);

        actionUnit.setStorage(ModelGenerator.resolvePathForSourceFile(sourceRoot, packageName, enumName));
        sourceRoot.add(actionUnit);
    }

    private static void createByIdMethod(EnumDeclaration type) {
        BlockStmt body = new BlockStmt();
        body.addStatement("Optional<" + type.getName().asString() + "> optional = Arrays.asList(values())\n" +
                ".stream()\n" +
                ".filter(\n" +
                "   action -> action.id.equals(id)\n" +
                ").\n" +
                "findFirst();");
        body.addStatement("if (optional.isPresent()) {\n" +
                "            return optional.get();\n" +
                "        }");
        body.addStatement("throw new IllegalArgumentException(id);");


        MethodDeclaration methodById = type.addMethod("byId", Modifier.STATIC, Modifier.PUBLIC);
        methodById.setType(type.getName().asString());
        methodById.setBody(body);
        methodById.addParameter(String.class, "id");

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
            add(new ImportDeclaration(Optional.class.getName(), false, false));
            add(new ImportDeclaration(Arrays.class.getName(), false, false));
        }
    };
}
