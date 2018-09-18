package ru.custis.easyabac.core;

import ru.custis.easyabac.core.models.attribute.Attribute;
import ru.custis.easyabac.core.models.policy.simple.SimplePolicy;

import java.util.List;

public interface EasyAbacInit {
    void initInstanceSimplePolicy(String policy, String attributes);

    void initInstanceSimplePolicy(SimplePolicy simplePolicy, List<Attribute> attributes);

    void initInstanceXacmlPolicy(String policy, String attributes);
}
