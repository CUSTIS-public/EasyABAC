package custis.easyabac.api;

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
