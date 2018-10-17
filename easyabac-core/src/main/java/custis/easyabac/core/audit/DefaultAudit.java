package custis.easyabac.core.audit;

import custis.easyabac.core.model.abac.attribute.AttributeWithValue;
import custis.easyabac.core.model.abac.attribute.Category;
import custis.easyabac.pdp.AuthResponse;
import custis.easyabac.pdp.MultiAuthRequest;
import custis.easyabac.pdp.MultiAuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DefaultAudit implements Audit {

    public static final Logger LOGGER = LoggerFactory.getLogger(DefaultAudit.class);

    public static DefaultAudit INSTANCE = new DefaultAudit();

    @Override
    public void onRequest(List<AttributeWithValue> attributeWithValues, AuthResponse response) {
        List<AttributeWithValue> subject = attributeWithValues.stream()
                .filter(attributeWithValue -> attributeWithValue.getAttribute().getCategory() == Category.SUBJECT)
                .collect(Collectors.toList());

        Optional<AttributeWithValue> action = attributeWithValues.stream()
                .filter(attributeWithValue -> attributeWithValue.getAttribute().getCategory() == Category.ACTION)
                .findFirst();

        // LOGGER.info(attributeWithValues);
        // LOGGER.info(response);
    }

    @Override
    public void onMultipleRequest(MultiAuthRequest requestContext, MultiAuthResponse response) {
        List<AttributeWithValue> subject = requestContext.getRequests().values()
                .stream()
                .flatMap(attributeWithValueList -> attributeWithValueList.stream())
                .filter(attribute -> attribute.getAttribute().getCategory() == Category.SUBJECT)
                .collect(Collectors.toList());

        List<AttributeWithValue> actions = requestContext.getRequests().values()
                .stream()
                .flatMap(attributeWithValueList -> attributeWithValueList.stream())
                .filter(attribute -> attribute.getAttribute().getCategory() == Category.ACTION)
                .collect(Collectors.toList());

        response.getResults().entrySet().forEach(entry -> {
            // LOGGER.info(attributeWithValues);
            // LOGGER.info(response);

        });
    }

}
