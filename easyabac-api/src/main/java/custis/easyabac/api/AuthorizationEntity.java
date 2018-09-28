package custis.easyabac.api;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthorizationEntity {

    /**
     * (Optional) The name of the entity.
     * <p/>
     * Defaults to the entity name.
     */
    String name() default "";
}
