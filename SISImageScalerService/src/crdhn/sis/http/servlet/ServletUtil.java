/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.sis.http.servlet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author longmd
 */
public class ServletUtil {

	public static int getIntParameter(HttpServletRequest request, String paramName) {
		return getIntParameter(request, paramName, 0);
	}

	public static int getIntParameter(HttpServletRequest request, String paramName, int defaultValue) {
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

	public static Long getLongParameter(HttpServletRequest request, String paramName) {
		return getLongParameter(request, paramName, 0L);
	}

	public static Long getLongParameter(HttpServletRequest request, String paramName, Long defaultValue) {
		String paramString = getStringParameter(request, paramName);
		if (paramString == null) {
			return defaultValue;
		}
		Long paramValue;
		try {
			paramValue = Long.parseLong(paramString);
		} catch (NumberFormatException nfe) { // Handles null and bad format
			paramValue = defaultValue;
		}
		return (paramValue);
	}

	public static Double getDoblieParameter(HttpServletRequest request, String paramName) {
		return getDoubleParameter(request, paramName, 0D);
	}

	public static Double getDoubleParameter(HttpServletRequest request, String paramName, Double defaultValue) {
		String paramString = getStringParameter(request, paramName);
		if (paramString == null) {
			return defaultValue;
		}
		Double paramValue;
		try {
			paramValue = Double.parseDouble(paramString);
		} catch (NumberFormatException nfe) { // Handles null and bad format
			paramValue = defaultValue;
		}
		return (paramValue);
	}

	public static List<Integer> getListIntParameter(HttpServletRequest request, String paramName) {
		String separate = ",";
		return getListIntParameter(request, paramName, separate);
	}

	public static List<Integer> getListIntParameter(HttpServletRequest request, String paramName, String separate) {
		String paramString = getStringParameter(request, paramName);
		if (paramString == null || paramString.isEmpty()) {
			return null;
		}
		String[] str_list = paramString.split(separate);
		if (str_list.length == 0) {
			return null;
		}
		List<Integer> list = new ArrayList<>();
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
	
	public static List<Long> getListLongParameter(HttpServletRequest request, String paramName) {
		String separate = ",";
		return getListLongParameter(request, paramName, separate);
	}
	
	public static List<Long> getListLongParameter(HttpServletRequest request, String paramName, String separate) {
		String paramString = getStringParameter(request, paramName);
		if (paramString == null || paramString.isEmpty()) {
			return null;
		}
		String[] str_list = paramString.split(separate);
		if (str_list.length == 0) {
			return null;
		}
		List<Long> list = new ArrayList<>();
		for (String item : str_list) {
			try {
				long long_item = Long.parseLong(item);
				list.add(long_item);
			} catch (Exception e) {
			}
		}
		if (list.isEmpty()) {
			return null;
		}
		return list;
	}

	public static String getStringParameter(HttpServletRequest request, String paramName) {
		return getStringParameter(request, paramName, "");
	}

	public static String getStringParameter(HttpServletRequest request, String paramName, String defaultValue) {
		String paramString = getParameter(request, paramName);
		if (paramString == null) {
			return defaultValue;
		}
		return paramString;

	}
	
	public static List<String> getListStringParameter(HttpServletRequest request, String paramName, String separate) {
		String paramString = getStringParameter(request, paramName);
		if (paramString == null || paramString.isEmpty()) {
			return null;
		}
		String[] str_list = paramString.split(separate);
		if (str_list.length == 0) {
			return null;
		}
		List<String> list = new ArrayList<>();
		list.addAll(Arrays.asList(str_list));
		
		return list;
	}

	public static String getParameter(HttpServletRequest request, String paramName) {
		String value = request.getParameter(paramName);
		if (value == null) {
			Enumeration<String> requestParam = request.getParameterNames();
			while (requestParam.hasMoreElements()) {
				String rep = requestParam.nextElement();
				if (rep.equalsIgnoreCase(paramName)) {
					return request.getParameter(rep);
				}
			}
		}
		return value;
	}
}
