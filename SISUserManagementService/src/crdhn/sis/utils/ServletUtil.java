/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.sis.utils;

import firo.Request;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 *
 * @author namdv
 */
public class ServletUtil {

    public static int getIntParameter(Request request, String paramName) {
        return getIntParameter(request, paramName, 0);
    }

    public static int getIntParameter(Request request, String paramName, int defaultValue) {
        String paramString = getStringParameter(request, paramName);
        if (paramString == null) {
            return defaultValue;
        }
        int paramValue;
        try {
            paramValue = Integer.parseInt(paramString);
        } catch (NumberFormatException nfe) { // Handles null and bad format
            paramValue = defaultValue;
        }
        return (paramValue);
    }

    public static Long getLongParameter(Request request, String paramName) {
        return getLongParameter(request, paramName, 0L);
    }

    public static Long getLongParameter(Request request, String paramName, Long defaultValue) {
        String paramString = getStringParameter(request, paramName);
        if (paramString == null) {
            return defaultValue;
        }
        Long paramValue = null;
        try {
            paramValue = Long.parseLong(paramString);
        } catch (NumberFormatException nfe) { // Handles null and bad format
            paramValue = defaultValue;
        }
        return (paramValue);
    }

    public static Double getDoblieParameter(Request request, String paramName) {
        return getDoubleParameter(request, paramName, 0D);
    }

    public static Double getDoubleParameter(Request request, String paramName, Double defaultValue) {
        String paramString = getStringParameter(request, paramName);
        if (paramString == null) {
            return defaultValue;
        }
        Double paramValue = null;
        try {
            paramValue = Double.parseDouble(paramString);
        } catch (NumberFormatException nfe) { // Handles null and bad format
            paramValue = defaultValue;
        }
        return (paramValue);
    }

    public static List<Integer> getListIntParameter(Request request, String paramName) {
        String separate = ",";
        return getListIntParameter(request, paramName, separate);
    }

    public static List<Integer> getListIntParameter(Request request, String paramName, String separate) {
        String paramString = getStringParameter(request, paramName);
        if (paramString == null) {
            return null;
        }
        if (paramString.isEmpty()) {
            return null;
        }
        String[] str_list = paramString.split(separate);
        if (str_list.length == 0) {
            return null;
        }
        List<Integer> list = new ArrayList<Integer>();
        for (String item : str_list) {
            try {
                int int_item = Integer.parseInt(item);
                list.add(int_item);
            } catch (Exception e) {
            }
        }
        if (list.isEmpty()) {
            return null;
        }
        return list;
    }

    public static String getStringParameter(Request request, String paramName) {
        return getStringParameter(request, paramName, "");
    }

    public static String getStringParameter(Request request, String paramName, String defaultValue) {
        String paramString = getParameter(request, paramName);
        if (paramString == null) {
            return defaultValue;
        }
        return paramString;

    }

    public static List<String> getListStringParameter(Request request, String paramName, String separate) {
        String paramString = getStringParameter(request, paramName);
        if (paramString == null) {
            return null;
        }
        if (paramString.isEmpty()) {
            return null;
        }
        String[] str_list = paramString.split(separate);
        if (str_list.length == 0) {
            return null;
        }
        List<String> list = new ArrayList<String>();
        for (String txt : str_list) {
            list.add(txt);
        }

        return list;
    }

    public static String getParameter(Request request, String paramName) {
        String value = request.raw().getParameter(paramName);
        if (value == null) {
            Enumeration<String> requestParam = request.raw().getParameterNames();
            while (requestParam.hasMoreElements()) {
                String rep = requestParam.nextElement();
                if (rep.equalsIgnoreCase(paramName)) {
                    return request.raw().getParameter(rep);
                }
            }
        }
        return value;
    }

    public static List<Long> getListLongParameter(Request request, String paramName, String separate) {
        String paramString = getStringParameter(request, paramName);
        if (paramString == null) {
            return null;
        }
        if (paramString.isEmpty()) {
            return null;
        }
        String[] str_list = paramString.split(separate);
        if (str_list.length == 0) {
            return null;
        }
        List<Long> list = new ArrayList<Long>();
        for (String item : str_list) {
            try {
                long int_item = Long.parseLong(item);
                list.add(int_item);
            } catch (Exception e) {
            }
        }
        if (list.isEmpty()) {
            return null;
        }
        return list;
    }

}
