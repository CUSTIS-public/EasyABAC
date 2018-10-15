package custis.easyabac.generation.util.algorithm;

import custis.easyabac.core.init.EasyAbacInitException;
import custis.easyabac.core.model.abac.Condition;
import custis.easyabac.core.model.abac.Function;
import custis.easyabac.core.model.abac.Rule;
import custis.easyabac.core.model.abac.attribute.Attribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static custis.easyabac.generation.util.algorithm.FunctionUtils.UNKNOWN_PREFIX;

public class RuleGenerationUtils {

    public static final Logger LOGGER = LoggerFactory.getLogger(RuleGenerationUtils.class);

    public static List<Map<String, String>> generateRule(Rule rule, boolean expectedResult,
                                                                      Map<String, String> attributes) throws EasyAbacInitException {
        switch (rule.getOperation()) {
            case AND:
                return generateDataForAnd(rule.getConditions(), expectedResult, attributes);
            case OR:
                return generateDataForOr(rule.getConditions(), expectedResult, attributes);
            case NAND:
                return generateDataForAnd(rule.getConditions(), !expectedResult, attributes);
            case NOR:
                return generateDataForOr(rule.getConditions(), !expectedResult, attributes);
        }

        return Collections.emptyList();
    }

    public static List<Map<String, String>> generateDataForAnd(List<Condition> conditions,
                                                                            boolean expectedResult,
                                                                            Map<String, String> attributes) throws EasyAbacInitException {

        List<Map<String, String>> out = new ArrayList<>();
        if (expectedResult) {
            Map<String, String> copied = deepCopy(attributes);
            for (int i = 0; i < conditions.size(); i++) {
                Condition condition = conditions.get(i);
                generateForCondition(condition, expectedResult, copied);
                if (copied.isEmpty()) {
                    break;
                }
            }
            if (!copied.isEmpty()) {
                out.add(copied);
            }
        } else {
            for (int i = 0; i < conditions.size(); i++) {
                Map<String, String> copied = deepCopy(attributes);
                Condition condition = conditions.get(i);
                generateForCondition(condition, expectedResult, copied);
                if (copied.isEmpty()) {
                    continue;
                }
                for (int j = 0; j < i; j++) {
                    generateForCondition(conditions.get(j), !expectedResult, copied);
                    if (copied.isEmpty()) {
                        break;
                    }
                }
                if (copied.isEmpty()) {
                    continue;
                }

                out.add(copied);
            }
        }
        return out;
    }

    public static List<Map<String, String>> generateDataForOr(List<Condition> conditions,
                                                                            boolean expectedResult,
                                                                            Map<String, String> attributes) throws EasyAbacInitException {
        List<Map<String, String>> out = new ArrayList<>();

        if (expectedResult) {
            for (int i = 0; i < conditions.size(); i++) {
                Map<String, String> copied = deepCopy(attributes);
                Condition condition = conditions.get(i);
                generateForCondition(condition, expectedResult, copied);
                if (copied.isEmpty()) {
                    continue;
                }
                for (int j = 0; j < i; j++) {
                    generateForCondition(conditions.get(j), !expectedResult, copied);
                    if (copied.isEmpty()) {
                        break;
                    }
                }
                if (copied.isEmpty()) {
                    continue;
                }

                out.add(copied);
            }
        } else {
            Map<String, String> copied = deepCopy(attributes);
            for (int i = 0; i < conditions.size(); i++) {
                Condition condition = conditions.get(i);
                generateForCondition(condition, expectedResult, copied);
                if (copied.isEmpty()) {
                    break;
                }
            }
            if (!copied.isEmpty()) {
                out.add(copied);
            }
        }


        return out;
    }

    private static void generateForCondition(Condition condition, boolean expectedResult, Map<String, String> attributes) throws EasyAbacInitException {
        Attribute firstAttribute = condition.getFirstOperand();
        Function function = condition.getFunction();
        Attribute secondAttribute = condition.getSecondOperandAttribute();
        if (secondAttribute == null) {
            List<String> values = condition.getSecondOperandValue();
            // we've got values

            String firstValue = attributes.get(firstAttribute.getId());
            String value = FunctionUtils.generateValue(function, values, expectedResult);
            resolveAttributes(attributes, firstAttribute.getId(), value);
            if (attributes.isEmpty()) {
                return;
            }

        } else {
            // we've got placeholder
            // so we add two attributes

            String firstValue = attributes.get(firstAttribute.getId());
            String secondValue = attributes.get(secondAttribute.getId());
            if (firstValue != null) {
                // value for first one

                secondValue = FunctionUtils.generateValue(function, Collections.singletonList(firstValue), expectedResult);
                resolveAttributes(attributes, secondAttribute.getId(), secondValue);
                if (attributes.isEmpty()) {
                    return;
                }

            } else {
                if (secondValue != null) {
                    // value for second one
                    firstValue = FunctionUtils.generateValue(function, Collections.singletonList(secondValue), expectedResult);
                    resolveAttributes(attributes, firstAttribute.getId(), firstValue);
                    if (attributes.isEmpty()) {
                        return;
                    }
                } else {
                    firstValue = FunctionUtils.newUnknownResult();
                    resolveAttributes(attributes, firstAttribute.getId(), firstValue);
                    if (attributes.isEmpty()) {
                        return;
                    }

                    secondValue = FunctionUtils.generateValue(function, Collections.singletonList(firstValue), expectedResult);
                    resolveAttributes(attributes, secondAttribute.getId(), secondValue);
                    if (attributes.isEmpty()) {
                        return;
                    }
                }
            }



        }
    }

    private static void resolveAttributes(Map<String, String> attributes, String id, String value) throws EasyAbacInitException {
        if (attributes.containsKey(id)) {
            // already with value
            String existingValue = attributes.get(id);
            if (existingValue.startsWith(UNKNOWN_PREFIX)) {
                // existing is unknown
                if (value.startsWith(UNKNOWN_PREFIX)) {
                    attributes.put(id, value);
                } else {
                    // replacing values
                    List<String> keysToReplace = attributes.entrySet().stream()
                            .filter(stringStringEntry -> stringStringEntry.getValue().equals(existingValue))
                            .map(stringStringEntry -> stringStringEntry.getKey())
                            .collect(Collectors.toList());
                    keysToReplace.forEach(s -> attributes.put(id, value));
                    attributes.put(id, value);
                }
            } else {
                // existing known and real value
                if (value.startsWith(UNKNOWN_PREFIX)) {
                    List<String> keysToReplace = attributes.entrySet().stream()
                            .filter(stringStringEntry -> stringStringEntry.getValue().equals(value))
                            .map(stringStringEntry -> stringStringEntry.getKey())
                            .collect(Collectors.toList());
                    keysToReplace.forEach(s -> attributes.put(id, existingValue));
                    //attributes.put(id, value);
                } else {
                    if (!existingValue.equals(value)) {
                        LOGGER.warn("Test generation error, cannot calculate not conflicting attribute " + id);
                        attributes.clear();
                    }
                }
            }
        } else {
            attributes.put(id, value);
        }
    }

    public static Map<String, String> deepCopy(Map<String, String> map) {
        Map<String, String> copy = new HashMap<>();
        map.forEach((s, o) -> copy.put(s, o));
        return copy;
    }

}
