package crdhn.sis.configuration;

import firo.utils.config.InstrumentedCache;
import java.io.File;
import java.util.Iterator;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;

public class Config {

    private static final InstrumentedCache<String, String> localconfig;
    public static final String CONFIG_HOME = "conf";
    public static final String CONFIG_FILE = "config.ini";
    static CompositeConfiguration config;

    static {
//		String CONFIG_ITEMS = System.getProperty("cfg_items");
//		String HOME_PATH = System.getProperty("apppath");
//		String APP_ENV = System.getProperty("appenv");
//		if ((CONFIG_ITEMS == null) || (CONFIG_ITEMS.equals(""))) {
//			CONFIG_ITEMS = "500";
//		}
//		if (APP_ENV == null) {
//			APP_ENV = "";
//		}
//		if (!"".equals(APP_ENV)) {
//			APP_ENV = APP_ENV + ".";
//		}
//		localconfig = new InstrumentedCache(Integer.valueOf(CONFIG_ITEMS).intValue());
//
//		config = new CompositeConfiguration();
//
//		File configFile = new File(HOME_PATH + File.separator + "conf" + File.separator + APP_ENV + "config.ini");
//		try {
//			config.addConfiguration(new HierarchicalINIConfiguration(configFile));
//
//			Iterator<String> ii = config.getKeys();
//			while (ii.hasNext()) {
//				String key = (String) ii.next();
//				localconfig.put(key, config.getString(key));
//			}
//		}
//		catch (ConfigurationException e) {
//			System.exit(1);
//		}
        String CONFIG_ITEMS = System.getProperty("cfg_items");
        String HOME_PATH = System.getProperty("apppath");
        String APP_ENV = System.getProperty("appenv");
        if ((CONFIG_ITEMS == null) || (CONFIG_ITEMS.equals(""))) {
            CONFIG_ITEMS = "500";
        }
        if (HOME_PATH == null) {
            HOME_PATH = ".";
        }
        if (APP_ENV == null) {
            APP_ENV = "production";
        }
        if (!APP_ENV.isEmpty()) {
            APP_ENV = APP_ENV + ".";
        }
        localconfig = new InstrumentedCache(Integer.valueOf(CONFIG_ITEMS).intValue());

        config = new CompositeConfiguration();

        File configFile = new File(HOME_PATH + File.separator + "conf" + File.separator + APP_ENV + "config.ini");
        try {
            config.addConfiguration(new HierarchicalINIConfiguration(configFile));

            Iterator<String> ii = config.getKeys();
            while (ii.hasNext()) {
                String key = (String) ii.next();
                localconfig.put(key, config.getString(key));
            }
        } catch (ConfigurationException e) {
            System.exit(1);
        }
    }

    public static String getHomePath() {
        return System.getProperty("apppath");
    }

    public static String getParam(String section, String name) {
        String key = section + "." + name;

        String value = (String) localconfig.get(key);
        if (value != null) {
            return value;
        }
        value = config.getString(key);
        if (value != null) {
            localconfig.put(key, value);
        }
        return value;
    }

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
}
