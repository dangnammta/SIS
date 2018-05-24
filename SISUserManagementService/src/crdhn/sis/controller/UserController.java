package crdhn.sis.controller;

import crdhn.sis.configuration.Configuration;
import crdhn.sis.model.SessionInfo;
import crdhn.sis.model.UserInfo;
import crdhn.sis.transport.SISMongoDBConnectorClient;
import crdhn.sis.utils.DataResponse;
import crdhn.sis.utils.ServletUtil;
import crdhn.sis.utils.Utils;
import firo.Controller;
import firo.Request;
import firo.Response;
import firo.Route;
import firo.RouteInfo;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

public class UserController extends Controller {

    public UserController() {
        rootPath = "/";
    }

    @RouteInfo(method = "get,post", path = "/login")
    public Route Login() {
        return (Request request, Response response) -> {
            String username = ServletUtil.getStringParameter(request, "username");
            String password = ServletUtil.getStringParameter(request, "password");
            if (username.isEmpty() || password.isEmpty()) {
                return DataResponse.PARAM_ERROR;
            }
            Document loginDocument = SISMongoDBConnectorClient.getInstance().login(username, password);
            if (loginDocument != null) {
                HashMap mapData = new HashMap();
                mapData.put("sessionKey", loginDocument.getString("_id"));
                mapData.put("expireTime", loginDocument.getLong("expireTime"));
                mapData.put("accountType", loginDocument.getInteger("accountType"));
                return new DataResponse(mapData);
            }
            return DataResponse.AUTHENTICATION_FAIL;
        };
    }

    @RouteInfo(method = "get,post", path = "/logout")
    public Route Logout() {
        return (Request request, Response response) -> {
            String sessionKey = ServletUtil.getStringParameter(request, "sessionKey");
            SISMongoDBConnectorClient.getInstance().logout(sessionKey);
            return DataResponse.SUCCESS;
        };
    }

    @RouteInfo(method = "post,get", path = "/register")
    public Route register() {
        return (Request request, Response response) -> {
            String username = ServletUtil.getStringParameter(request, "username");
            String password = ServletUtil.getStringParameter(request, "password");
            String email = ServletUtil.getStringParameter(request, "email");
            String phoneNumber = ServletUtil.getStringParameter(request, "phonenumber");
            String address = ServletUtil.getStringParameter(request, "address");
            String fullName = ServletUtil.getStringParameter(request, "fullname");
            long birthday = ServletUtil.getLongParameter(request, "birthday");
            int gender = ServletUtil.getIntParameter(request, "gender");
            String avatar = ServletUtil.getStringParameter(request, "avatar");
            if (username.trim().length() <= 0 || password.trim().length() <= 0 || !Utils.validateEmail(email) || fullName.trim().length() <= 0) {
                return DataResponse.MISSING_PARAM;
            }
            UserInfo uInfo = new UserInfo();
            uInfo.setUsername(username);
            uInfo.setAddress(address);
            uInfo.setBirthday(birthday);
            uInfo.setEmail(email);
            uInfo.setFullName(fullName);
            uInfo.setGender(gender);
            uInfo.setLastUpdateTime(System.currentTimeMillis());
            uInfo.setPhoneNumber(phoneNumber);
            uInfo.setPassword(password);
            uInfo.setAvatar(avatar);
            uInfo.setType(UserInfo.TYPE_USER);
            return SISMongoDBConnectorClient.getInstance().addUser(uInfo);
        };
    }

    @RouteInfo(method = "get", path = "/profile")
    public Route getProfile() {
        return (Request request, Response response) -> {
            String sessionKey = ServletUtil.getStringParameter(request, "sessionKey");
            SessionInfo checkSession = SISMongoDBConnectorClient.getInstance().checkSession(sessionKey);
            if (checkSession == null) {
                return DataResponse.SESSION_EXPIRED;
            }
            return SISMongoDBConnectorClient.getInstance().getUserInfo(checkSession.getAccountName());
        };
    }

    @RouteInfo(method = "post", path = "/profile/update")
    public Route updateProfile() {
        return (Request request, Response response) -> {
            String sessionKey = ServletUtil.getStringParameter(request, "sessionKey");
            SessionInfo checkSession = SISMongoDBConnectorClient.getInstance().checkSession(sessionKey);
            if (checkSession == null) {
                return DataResponse.SESSION_EXPIRED;
            }
//            String username = request.params(":username");
//            String username = ServletUtil.getStringParameter(request, "username");
            String password = ServletUtil.getStringParameter(request, "password");
            String email = ServletUtil.getStringParameter(request, "email");
            String phoneNumber = ServletUtil.getStringParameter(request, "phonenumber");
            String address = ServletUtil.getStringParameter(request, "address");
            String fullName = ServletUtil.getStringParameter(request, "fullname");
            long birthday = ServletUtil.getLongParameter(request, "birthday");
            int gender = ServletUtil.getIntParameter(request, "gender");
            String avatar = ServletUtil.getStringParameter(request, "avatar");
            UserInfo uInfo = new UserInfo();
            uInfo.setUsername(checkSession.accountName);
            uInfo.setAddress(address);
            uInfo.setBirthday(birthday);
            uInfo.setEmail(email);
            uInfo.setFullName(fullName);
            uInfo.setGender(gender);
            uInfo.setLastUpdateTime(System.currentTimeMillis());
            uInfo.setPhoneNumber(phoneNumber);
            uInfo.setPassword(password);
            uInfo.setAvatar(avatar);
            uInfo.setType(checkSession.accountType);
            return SISMongoDBConnectorClient.getInstance().updateUser(uInfo);
        };
    }

    @RouteInfo(method = "get,post", path = "/checksession")
    public Route checkSession() {
        return (Request request, Response response) -> {
            String sessionKey = ServletUtil.getStringParameter(request, "sessionKey");
            SessionInfo checkSession = SISMongoDBConnectorClient.getInstance().checkSession(sessionKey);
            if (checkSession == null) {
                return DataResponse.SESSION_EXPIRED;
            } else {
                HashMap resp = new HashMap();
                resp.put("accountName", checkSession.accountName);
                resp.put("accountType", checkSession.accountType);
                return new DataResponse(resp);
            }
        };
    }

    public static void insertAcountOrganization() {
//        String pathFile = System.getProperty("user.dir")+File.separator+Configuration.path_user_organizations;
        String HOME_PATH = System.getProperty("apppath");
        if (HOME_PATH == null || HOME_PATH.isEmpty()) {
            HOME_PATH = ".";
        }

        String path = HOME_PATH + File.separator + "data" + File.separator + Configuration.path_user_organizations;
        System.out.println("pathJson = " + path);
        try (Stream<String> stream = Files.lines(Paths.get(path), StandardCharsets.UTF_8)) {

            stream.forEach(line -> {
                if (!line.isEmpty() && line.trim().length() > 0) {
                    try {
                        JSONObject objOrga = new JSONObject(line);
                        UserInfo userInfo = new UserInfo();
                        userInfo.setUsername(objOrga.getString("username"));
                        userInfo.setPassword(objOrga.getString("password"));
                        userInfo.setEmail(objOrga.getString("email"));
                        userInfo.setFullName(objOrga.getString("fullName"));
                        userInfo.setType(objOrga.getInt("type"));
                        userInfo.setAddress(objOrga.getString("address"));
                        userInfo.setBirthday(objOrga.getLong("birthday"));
                        userInfo.setGender(objOrga.getInt("gender"));
                        userInfo.setPhoneNumber(objOrga.getString("phoneNumber"));
                        DataResponse result = SISMongoDBConnectorClient.getInstance().addUser(userInfo);
//                        if(result!=null && result.getError() == DataResponse.MONGO_USER_EXISTED.getError()){
//                            return;
//                        }
                    } catch (JSONException ex) {
                        Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
