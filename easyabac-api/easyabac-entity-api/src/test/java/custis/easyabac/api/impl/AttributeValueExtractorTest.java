package custis.easyabac.api.impl;

import custis.easyabac.api.impl.model.AnnotatedEntity;
import custis.easyabac.api.impl.model.DefaultEntity;
import custis.easyabac.api.impl.model.ImplementingEntity;
import custis.easyabac.core.pdp.AuthAttribute;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static custis.easyabac.api.impl.AttributeValueExtractor.extractAttributesFromResource;

public class AttributeValueExtractorTest {

    @Test
    public void checkExtractorAnnotated() {
        AnnotatedEntity entity = new AnnotatedEntity("idVal", 1, "123");
        List<AuthAttribute> attributes = extractAttributesFromResource(entity);
        Assert.assertEquals(2, attributes.size());
        AuthAttribute attr1 = attributes.get(0);
        AuthAttribute attr2 = attributes.get(1);
        Assert.assertEquals(true, attr1.getId().startsWith("ent."));
        Assert.assertEquals(true, attr2.getId().startsWith("ent."));
        if (attr1.getId().equals("ent.i")) {
            Assert.assertEquals(1, attr1.getValues().size());
            Assert.assertEquals("idVal", attr1.getValues().get(0));

            Assert.assertEquals(1, attr2.getValues().size());
            Assert.assertEquals("1", attr2.getValues().get(0));
        } else {
            Assert.assertEquals(1, attr2.getValues().size());
            Assert.assertEquals("idVal", attr2.getValues().get(0));

            Assert.assertEquals(1, attr1.getValues().size());
            Assert.assertEquals("1", attr1.getValues().get(0));
        }
    }

    @Test
    public void checkExtractorDefault() {
        DefaultEntity entity = new DefaultEntity("idVal", 1);
        List<AuthAttribute> attributes = extractAttributesFromResource(entity);
        Assert.assertEquals(2, attributes.size());
        AuthAttribute attr1 = attributes.get(0);
        AuthAttribute attr2 = attributes.get(1);
        Assert.assertEquals(true, attr1.getId().startsWith("DefaultEntity."));
        Assert.assertEquals(true, attr2.getId().startsWith("DefaultEntity."));
        if (attr1.getId().equals("DefaultEntity.id")) {
            Assert.assertEquals(1, attr1.getValues().size());
            Assert.assertEquals("idVal", attr1.getValues().get(0));

            Assert.assertEquals(1, attr2.getValues().size());
            Assert.assertEquals("1", attr2.getValues().get(0));
        } else {
            Assert.assertEquals(1, attr2.getValues().size());
            Assert.assertEquals("idVal", attr2.getValues().get(0));

            Assert.assertEquals(1, attr1.getValues().size());
            Assert.assertEquals("1", attr1.getValues().get(0));
        }
    }

    @Test
    public void checkExtractorImpl() {
        ImplementingEntity entity = new ImplementingEntity("idVal", 1);
        List<AuthAttribute> attributes = extractAttributesFromResource(entity);
        Assert.assertEquals(2, attributes.size());
        AuthAttribute attr1 = attributes.get(0);
        AuthAttribute attr2 = attributes.get(1);
        if (attr1.getId().equals("id")) {
            Assert.assertEquals(1, attr1.getValues().size());
            Assert.assertEquals("idVal", attr1.getValues().get(0));

            Assert.assertEquals(1, attr2.getValues().size());
            Assert.assertEquals("1", attr2.getValues().get(0));
        } else {
            Assert.assertEquals(1, attr2.getValues().size());
            Assert.assertEquals("idVal", attr2.getValues().get(0));

            Assert.assertEquals(1, attr1.getValues().size());
            Assert.assertEquals("1", attr1.getValues().get(0));
        }
    }
}
