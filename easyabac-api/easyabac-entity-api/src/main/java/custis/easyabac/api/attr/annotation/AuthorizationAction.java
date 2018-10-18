package custis.easyabac.api.attr.annotation;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthorizationAction {

    /**
     * The name of the entity of action
     * <p/>
     */
    String entity();

}
