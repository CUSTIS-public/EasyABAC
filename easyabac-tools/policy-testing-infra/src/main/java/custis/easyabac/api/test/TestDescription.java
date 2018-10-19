package custis.easyabac.api.test;

import java.util.Map;

public class TestDescription {

    private String expectedResult;
    private String action;

    private Map<String, Map<String, Object>> attributes;

    public String getExpectedResult() {
        return expectedResult;
    }

    public void setExpectedResult(String expectedResult) {
        this.expectedResult = expectedResult;
    }

    public String getAction() {
        return action;
    }

    public String getShortAction() {
        return action.substring(action.lastIndexOf(".") + 1);
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Map<String, Map<String, Object>> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Map<String, Object>> attributes) {
        this.attributes = attributes;
    }

    public Map<String, Object> getAttributesByCode(String code) {
        return attributes.get(code);
    }

    public boolean containsAttributesByCode(String code) {
        return attributes.containsKey(code);
    }
}
