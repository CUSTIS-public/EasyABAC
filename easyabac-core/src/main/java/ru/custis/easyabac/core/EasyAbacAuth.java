package ru.custis.easyabac.core;

public interface EasyAbacAuth {
    EasyAbacResponse auth(EasyAbacRequest request);
}
