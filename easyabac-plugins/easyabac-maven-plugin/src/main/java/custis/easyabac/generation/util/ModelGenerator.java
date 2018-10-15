package custis.easyabac.generation.util;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.nodeTypes.NodeWithMembers;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.VoidType;
import com.github.javaparser.utils.SourceRoot;
import custis.easyabac.core.model.abac.attribute.DataType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelGenerator {

    public static final Logger LOGGER = LoggerFactory.getLogger(ModelGenerator.class);
    public static final String ACTION_SUFFIX = "Action";

    public static Path resolvePathForSourceFile(SourceRoot sourceRoot, String packageName, String name) {
        String[] packages = packageName.split("\\.");
        Path targetPath = sourceRoot.getRoot();
        for (String aPackage : packages) {
            targetPath = targetPath.resolve(aPackage);
        }
        return targetPath.resolve(StringUtils.capitalize(name) + ".java");
    }

    public static void generateFieldAccessors(NodeWithMembers type, ConstructorDeclaration constructor, List<FieldDeclaration> attributes, boolean withSetters) {
        BlockStmt body = new BlockStmt();
        for (FieldDeclaration fieldDeclaration : attributes) {
            String code = fieldDeclaration.getVariables().get(0).getNameAsString();
            constructor.addAndGetParameter(fieldDeclaration.getElementType(), code);

            body.addStatement("this." + code + " = " + code + ";");

            generateGetter(type, fieldDeclaration);
            if (withSetters) {
                generateSetter(type, fieldDeclaration);
            }
        }

        constructor.setBody(body);
    }

    public static void generateGetter(NodeWithMembers type, FieldDeclaration fieldDeclaration) {
        String code = fieldDeclaration.getVariables().get(0).getNameAsString();
        MethodDeclaration method = type.addMethod("get" + StringUtils.capitalize(code), Modifier.PUBLIC);
        method.setType(fieldDeclaration.getElementType());

        BlockStmt body = new BlockStmt();
        body.addStatement("return this." + code + ";");

        method.setBody(body);
    }

    public static void generateSetter(NodeWithMembers type, FieldDeclaration fieldDeclaration) {
        String code = fieldDeclaration.getVariables().get(0).getNameAsString();
        MethodDeclaration method = type.addMethod("set" + StringUtils.capitalize(code), Modifier.PUBLIC);
        method.setType(new VoidType());
        method.addParameter(new Parameter(fieldDeclaration.getElementType(), code));

        BlockStmt body = new BlockStmt();
        body.addStatement("this." + code + " = " + code + ";");

        method.setBody(body);
    }

    public static Type getTypeForModelType(DataType type) {
        return TYPE_MAPPING.get(type);
    }

    private static Map<DataType, Type> TYPE_MAPPING = new HashMap<DataType, Type>() {
        {
            put(DataType.STRING, new ClassOrInterfaceType("String"));
            put(DataType.INT, new ClassOrInterfaceType("Integer"));
            put(DataType.BOOLEAN, new ClassOrInterfaceType("Boolean"));
            put(DataType.DATE, new ClassOrInterfaceType("Date"));
            put(DataType.DATE_TIME, new ClassOrInterfaceType("Date"));
            put(DataType.TIME, new ClassOrInterfaceType("String"));
        }
    };

    public static String escape(String id) {
        return id.replaceAll("-", "_");
    }
}
