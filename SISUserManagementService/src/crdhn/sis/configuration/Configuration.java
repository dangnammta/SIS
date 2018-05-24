package crdhn.sis.configuration;

public class Configuration {

    public static final String SERVICE_NAME;
    public static final String SERVICE_HOST;
    public static final int SERVICE_PORT;
    public static final String MONGODB_USER_COLLECTION_NAME;
    public static final String MONGODB_SESSION_COLLECTION_NAME;
    public static final String MONGODB_ORGANAZATION_COLLECTION_NAME;
    public static final String MONGODB_REPORT_COLLECTION_NAME;
    public static final String MONGODB_COMMENT_COLLECTION_NAME;
    public static final String MONGODB_HISTORY_COLLECTION_NAME;
    public static final String MONGODB_COUNTER_COLLECTION_NAME;
    public static final String ORGANAZATION_ID_COUNTER_KEY;
    public static final String REPORT_ID_COUNTER_KEY;
//    public static final String COMMENT_ID_COUNTER_KEY;
    public static String url_user_getfiles;
    public static long LOGIN_TIMEOUT;
    public static String path_user_organizations;
    public static String path_address_report;
    public static int number_report_on_page;
    public static int number_comment_on_page;

    static {
        SERVICE_NAME = Config.getParamString("service", "name", "");
        SERVICE_PORT = Config.getParamInt("service", "port");
        SERVICE_HOST = Config.getParamString("service", "host", "");
//		FILE_CONTROLLER_PATH = Config.getParamString("service", "fsp_file_controller_path", "");
//		CHUNK_CONTROLLER_PATH = Config.getParamString("service", "fsp_chunk_controller_path", "");
        MONGODB_USER_COLLECTION_NAME = Config.getParamString("mongodb", "user_collection_name", "");
        MONGODB_SESSION_COLLECTION_NAME = Config.getParamString("mongodb", "session_collection_name", "");
        MONGODB_ORGANAZATION_COLLECTION_NAME  = Config.getParamString("mongodb", "organization_collection_name", "");
        MONGODB_REPORT_COLLECTION_NAME = Config.getParamString("mongodb", "report_collection_name", "");
        MONGODB_COMMENT_COLLECTION_NAME = Config.getParamString("mongodb", "comment_collection_name", "");
        MONGODB_HISTORY_COLLECTION_NAME = Config.getParamString("mongodb", "history_collection_name", "");
        MONGODB_COUNTER_COLLECTION_NAME = Config.getParamString("mongodb", "counter_collection_name", "");
        ORGANAZATION_ID_COUNTER_KEY = Config.getParamString("mongodb", "organization_counter_key", "");
        REPORT_ID_COUNTER_KEY = Config.getParamString("mongodb", "report_counter_key", "");
//        COMMENT_ID_COUNTER_KEY = Config.getParamString("mongodb", "comment_counter_key", "");
        url_user_getfiles = Config.getParamString("url", "url_user_getfile", "");
        LOGIN_TIMEOUT = Config.getParamInt("setting", "login_timeout", 3600);
        path_user_organizations = Config.getParamString("setting", "path_organization", "");
        path_address_report = Config.getParamString("setting", "path_Address_report", "");
        number_report_on_page = Config.getParamInt("setting", "number_report_on_page", 10);
        number_comment_on_page = Config.getParamInt("setting", "number_comment_on_page", 10);
    }
}
