package ru.custis.easyabac.core;

import java.io.InputStream;

public interface EasyAbacInit {
    void initInstanceEasyPolicy(String policy, String attributes);

    void initInstanceEasyPolicy(InputStream policy, InputStream attributes);

    void initInstanceXacmlPolicy(String policy, String attributes);
}
