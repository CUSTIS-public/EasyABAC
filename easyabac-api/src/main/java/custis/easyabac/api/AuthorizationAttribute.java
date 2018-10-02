package custis.easyabac.api;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthorizationAttribute {

    /**
     * (Optional) The id of the stringAttribute.
     * <p/>
     * Defaults to the field name.
     */
    String id() default "";

}
