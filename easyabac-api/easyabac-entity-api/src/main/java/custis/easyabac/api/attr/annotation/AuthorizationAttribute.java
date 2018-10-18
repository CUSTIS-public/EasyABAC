package custis.easyabac.api.attr.annotation;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthorizationAttribute {

    /**
     * (Optional) The id of the attribute.
     * <p/>
     * Defaults to the field name.
     */
    String id() default "";

}
