package cn.enaium.accessor.annotaiton;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Enaium
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface Field {
    String value();

    boolean dynamic() default true;
}
