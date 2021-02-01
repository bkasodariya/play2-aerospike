package model.annotation;

import java.lang.annotation.*;


/**
 * Annotation to be applied on the object to be saved in Aerospike. Such objects (aka VOs) should implement
 * {@link model.service.AerospikeEntity}. Analogous to table of RDBMS.
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Set {

    /**
     * Name of the schema to which this object (table) belongs.
     * 
     * @return
     */
    public String namespace();

    /**
     * Name of the VO. Or table name.
     * 
     * @return
     */
    public String name();
}
