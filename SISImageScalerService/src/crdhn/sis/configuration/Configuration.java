package crdhn.sis.configuration;

import java.util.List;

public class Configuration {

    public static final String SERVICE_NAME;
    public static final String SERVICE_HOST;
    public static final int SERVICE_PORT;
    public static final String UPLOADIMAGE_CONTROLLER_PATH;
    public static final String ORIGINAL_IMAGE_DIRECTORY;
    public static final String SCALED_IMAGE_DIRECTORY;
    public static final int number_worker;
    public static final String url_static;
    public static final List<String> images_size;
    public static final int count_size_image;
    public static final String HOME_PATH = getHomePath();

    static {
        SERVICE_NAME = ConfigHelper.getParamString("service", "name", "");
        SERVICE_PORT = ConfigHelper.getParamInt("service", "port");
        SERVICE_HOST = ConfigHelper.getParamString("service", "host", "");
        UPLOADIMAGE_CONTROLLER_PATH = ConfigHelper.getParamString("service", "sis_uploadimage_controller_path", "");
        ORIGINAL_IMAGE_DIRECTORY = ConfigHelper.getParamString("service", "sis_original_directory", "");
        SCALED_IMAGE_DIRECTORY = ConfigHelper.getParamString("service", "sis_scaled_directory", "");
        number_worker = ConfigHelper.getParamInt("service", "number_worker");
        url_static = ConfigHelper.getParamString("service", "url_static", "");
        images_size = ConfigHelper.getListParamString("service", "size_images", ",");
        count_size_image = images_size.size();
    }

    private static String getHomePath() {
        String path = System.getProperty("apppath");
        if (path == null || path.isEmpty()) {
            path = ".";
        }
        return path;
    }
}
