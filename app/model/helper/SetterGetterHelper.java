package model.helper;

import java.lang.reflect.Field;

import org.apache.commons.lang3.text.WordUtils;

/**
 * Get setter/getter method names for a given {@link java.lang.reflect.Field}
 * 
 */
public final class SetterGetterHelper {

    private SetterGetterHelper() {
        // do not instantiate
    }

    public static String getSetterName(Field f) {
        return "set" + WordUtils.capitalize(f.getName());
    }

    public static String getGetterName(Field f) {
        String prefix = "get";
        if (f.getType().equals(Boolean.class) || f.getType().equals(boolean.class)) {
            prefix = "is";
        }
        return prefix + WordUtils.capitalize(f.getName());
    }
}
