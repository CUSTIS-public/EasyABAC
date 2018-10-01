package custis.easyabac.generation;

import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.SourceRoot;
import custis.easyabac.core.model.attribute.load.EasyAttribute;
import custis.easyabac.core.model.attribute.load.EasyAttributeModel;
import custis.easyabac.core.model.attribute.load.EasyObject;
import custis.easyabac.generation.util.ActionGenerator;
import custis.easyabac.generation.util.EntityGenerator;
import custis.easyabac.generation.util.ModelGenerator;
import custis.easyabac.generation.util.TestGenerator;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;

public class Main {

    private static InputStream getResourceAsStream(String s) {
        return Main.class
                .getClassLoader()
                .getResourceAsStream(s);
    }

    public static void main(String[] args) {
        Yaml yaml = new Yaml();

        InputStream attributes = getResourceAsStream("attributes-1.yaml");
        //EasyAttributeModel easyAttributeModel = yaml.loadAs(attributes, EasyAttributeModel.class);
        EasyAttributeModel easyAttributeModel = new EasyAttributeModel();
        easyAttributeModel.setModel(new HashMap<>());
        EasyObject obj = new EasyObject();
        EasyAttribute attr = new EasyAttribute();
        attr.setCode("id");
        attr.setTitle("Идентификатор");

        EasyAttribute attr2 = new EasyAttribute();
        attr2.setCode("name");
        attr2.setTitle("Название");
        obj.setAttributes(Arrays.asList(
                attr, attr2
        ));


        EasyAttribute action = new EasyAttribute();
        action.setCode("action");
        action.setTitle("Действие");
        action.setAllowableValues(new HashSet<String>() {
            {
                add("READ");
                add("WRITE");
            }
        });


        obj.setActions(new ArrayList<>());
        obj.getActions().add(action);

        easyAttributeModel.getModel().put("Order", obj);

        Path rootPath = CodeGenerationUtils.mavenModuleRoot(ModelGenerator.class).resolve("src/test/java");
        SourceRoot sourceRoot = new SourceRoot(rootPath);

        String bPackage = "custis.easyabac.generation.test";
        for (Map.Entry<String, EasyObject> entry : easyAttributeModel.getModel().entrySet()) {
            EntityGenerator.createEntity(entry.getKey(), entry.getValue(), bPackage + ".model", sourceRoot);
            ActionGenerator.createAction(entry.getKey(), entry.getValue(), bPackage + ".model", sourceRoot);
            TestGenerator.createTest(entry.getKey(), entry.getValue(), bPackage, sourceRoot);
        }



        sourceRoot.saveAll();


    }
}
