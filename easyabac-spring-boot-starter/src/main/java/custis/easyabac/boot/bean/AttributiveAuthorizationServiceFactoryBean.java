package main.java.custis.easyabac.boot.bean;

import custis.easyabac.pdp.AttributiveAuthorizationService;
import custis.easyabac.pdp.AttributiveAuthorizationServiceImpl;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Service;

@Service
public class AttributiveAuthorizationServiceFactoryBean implements FactoryBean<AttributiveAuthorizationService> {
    @Override
    public AttributiveAuthorizationService getObject() throws Exception {
        return new AttributiveAuthorizationServiceImpl();
    }

    @Override
    public Class<?> getObjectType() {
        return AttributiveAuthorizationService.class;
    }
}
