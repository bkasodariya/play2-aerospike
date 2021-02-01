package model.annotation;

import java.lang.annotation.*;

/**
 * Annotation to be applied on Key field of object to be saved in Aerospike.
 * 
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Key {

}
