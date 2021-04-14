package cn.enaium.accessor.annotaiton;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Enaium
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Accessor {
    String value();
}
