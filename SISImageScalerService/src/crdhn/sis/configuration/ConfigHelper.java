package crdhn.sis.configuration;

import static crdhn.fcore.common.Config.getParam;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigHelper extends crdhn.fcore.common.Config {

    public static Integer getParamInt(String section, String name) {
        String value = getParam(section, name);

        if (value != null) {
            return Integer.valueOf(Integer.parseInt(value));
        }
        return -1;
    }

    public static Integer getParamInt(String section, String name, int defaultValue) {
        String value = getParam(section, name);
        if (value != null) {
            try {
                return Integer.valueOf(Integer.parseInt(value));
            } catch (NumberFormatException ex) {

            }
        }
        return defaultValue;
    }

    public static String getParamString(String section, String name, String defaultValue) {
        String value = getParam(section, name);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    public static List<String> getListParamString(String section, String name, String separator) {
        String value = getParam(section, name);
        List<String> result = new ArrayList<>();
        if (value != null) {
            String[] arrStr = value.split(separator);
            result.addAll(Arrays.asList(arrStr));
        }
        return result;
    }
}
