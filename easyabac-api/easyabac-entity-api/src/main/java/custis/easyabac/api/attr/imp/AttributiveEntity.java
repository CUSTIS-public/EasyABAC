package custis.easyabac.api.attr.imp;

import custis.easyabac.core.pdp.AuthAttribute;

import java.util.List;

/**
 * Entity attributes interface
 */
public interface AttributiveEntity {

    List<AuthAttribute> getAuthAttributes();
}
