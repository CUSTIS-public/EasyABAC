package ru.custis.easyabac.core;

import ru.custis.easyabac.core.models.attribute.Attribute;
import ru.custis.easyabac.core.models.policy.EasyPolicy;

import java.io.InputStream;
import java.util.List;

public interface EasyAbacInit {
    void initInstanceEasyPolicy(String policy, String attributes);

    void initInstanceEasyPolicy(InputStream policy, InputStream attributes);

    void initInstanceEasyPolicy(EasyPolicy easyPolicy, List<Attribute> attributes);

    void initInstanceXacmlPolicy(String policy, String attributes);
}
