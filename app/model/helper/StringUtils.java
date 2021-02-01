package model.helper;

import java.util.*;

public class StringUtils {

    private StringUtils() {
        // do not instantiate
    }

    public static boolean isEmpty(String str) {
        if (str == null)
            return true;
        return "".equals(str.trim());
    }

    public static List<String> split(String input, String regex) {
        return Arrays.asList(input.split(regex));
    }

    public static List<String> split(String input) {
        return split(input, ",");
    }

}
