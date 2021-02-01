package model.annotation;

import java.lang.annotation.*;

/**
 * Annotation to be applied on fields of the object to be saved in Aerospike. Analogous to column of RDBMS.
 * 
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Bin {

    /**
     * Column name.
     * 
     * @return
     */
    public String name();

}