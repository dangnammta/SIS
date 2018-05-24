package crdhn.sis.controller;

import crdhn.sis.configuration.Configuration;
import crdhn.sis.model.CommentInfo;
import crdhn.sis.model.ReportInfo;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.json.JSONException;
import org.json.JSONObject;

public class ReportController extends Controller {

    public ReportController() {
        rootPath = "/report/";
    }

    @RouteInfo(method = "get,post", path = "/getOrganizations")
    public Route getOrganizations() {
        return (Request request, Response response) -> {
            String sessionKey = ServletUtil.getStringParameter(request, "sessionKey");
            SessionInfo sessionInfo = SISMongoDBConnectorClient.getInstance().checkSession(sessionKey);
            if (sessionInfo == null) {
                return DataResponse.SESSION_EXPIRED;
            } else {
                return SISMongoDBConnectorClient.getInstance().getOrganizations();
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/getStatus")
    public Route getStatusReport() {
        return (Request request, Response response) -> {
            Utils.printLogSystem("ReportController", "getStatusReport");
            String sessionKey = ServletUtil.getStringParameter(request, "sessionKey");
            SessionInfo sessionInfo = SISMongoDBConnectorClient.getInstance().checkSession(sessionKey);
            if (sessionInfo != null) {
                List<Object> arrStatus = new ArrayList();
                for (ReportInfo.ReportStatus rStatus : ReportInfo.ReportStatus.values()) {
                    HashMap obj = new HashMap();
                    obj.put("value", rStatus.getValue());
                    obj.put("description", rStatus.getDescription());
                    arrStatus.add(obj);
                }
                return new DataResponse(arrStatus);
            } else {
                return DataResponse.SESSION_EXPIRED;
            }
        };
    }

    @RouteInfo(method = "get", path = "/getAddress")
    public Route getAddress() {
        return (Request request, Response response) -> {
            String sessionKey = ServletUtil.getStringParameter(request, "sessionKey");
            SessionInfo sessionInfo = SISMongoDBConnectorClient.getInstance().checkSession(sessionKey);
            if (sessionInfo != null) {
                String HOME_PATH = System.getProperty("apppath");
                if (HOME_PATH == null || HOME_PATH.isEmpty()) {
                    HOME_PATH = ".";
                }
                List<Object> arrAddress = new ArrayList();
                String path = HOME_PATH + File.separator + "data" + File.separator + Configuration.path_address_report;
                try (Stream<String> stream = Files.lines(Paths.get(path), StandardCharsets.UTF_8)) {

                    stream.forEach(line -> {
                        if (!line.isEmpty() && line.trim().length() > 0) {
                            try {
                                JSONObject dataAddress = new JSONObject(line);
                                HashMap obj = new HashMap();
                                obj.put("districtName", dataAddress.getString("districtName"));
                                obj.put("provinceName", dataAddress.getString("provinceName"));
                                arrAddress.add(obj);
                            } catch (JSONException ex) {
                                Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return new DataResponse(arrAddress);
            } else {
                return DataResponse.SESSION_EXPIRED;
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/addReport")
    public Route addReport() {
        return (Request request, Response response) -> {
            String sessionKey = ServletUtil.getStringParameter(request, "sessionKey");
            SessionInfo sessionInfo = SISMongoDBConnectorClient.getInstance().checkSession(sessionKey);
            if (sessionInfo != null) {
                String content = ServletUtil.getStringParameter(request, "content");
                List<String> images = ServletUtil.getListStringParameter(request, "images", ",");
                List<String> organizationIds = ServletUtil.getListStringParameter(request, "organizationIds", ",");
//                int type = ServletUtil.getIntParameter(request, "type");
//                ReportInfo.ReportType reportType = ReportInfo.ReportType.getReportTypeByValue(type);
//                if (reportType == null) {
//                    return DataResponse.PARAM_ERROR;
//                }
                String districtAddress = ServletUtil.getStringParameter(request, "districtName");
                String provinceName = ServletUtil.getStringParameter(request, "provinceName");

                ReportInfo rInfo = new ReportInfo();
                rInfo.setReporterName(sessionInfo.getAccountName());
                rInfo.setContent(content);
                rInfo.setImages(images);
                rInfo.setComments(new ArrayList<>());
                rInfo.setOrganizationIds(organizationIds);
                rInfo.setStatus(ReportInfo.ReportStatus.Created.getValue());
//                rInfo.setType(reportType.getValue());
                rInfo.setCityAddress(provinceName);
                rInfo.setDistrictAddress(districtAddress);
                rInfo.setCreateTime(System.currentTimeMillis());
                DataResponse resp = SISMongoDBConnectorClient.getInstance().addReport(rInfo);
                System.out.println("ReportController.addReport resp =" + resp);
                return resp;
            } else {
                return DataResponse.SESSION_EXPIRED;
            }
        };
    }
    
    @RouteInfo(method = "get,post", path = "/follow")
    public Route followReport() {
        return (Request request, Response response) -> {
            String sessionKey = ServletUtil.getStringParameter(request, "sessionKey");
            SessionInfo sessionInfo = SISMongoDBConnectorClient.getInstance().checkSession(sessionKey);
            if (sessionInfo != null) {
                long reportId = ServletUtil.getLongParameter(request, "reportId");
                if(reportId<=0){
                    return DataResponse.PARAM_ERROR;
                }
                DataResponse resp = SISMongoDBConnectorClient.getInstance().followReport(reportId, sessionInfo.accountName);
                System.out.println("ReportController.followReport resp =" + resp);
                return resp;
            } else {
                return DataResponse.SESSION_EXPIRED;
            }
        };
    }
    
    @RouteInfo(method = "get,post", path = "/unfollow")
    public Route unfollowReport() {
        return (Request request, Response response) -> {
            String sessionKey = ServletUtil.getStringParameter(request, "sessionKey");
            SessionInfo sessionInfo = SISMongoDBConnectorClient.getInstance().checkSession(sessionKey);
            if (sessionInfo != null) {
                long reportId = ServletUtil.getLongParameter(request, "reportId");
                if(reportId<=0){
                    return DataResponse.PARAM_ERROR;
                }
                DataResponse resp = SISMongoDBConnectorClient.getInstance().unfollowReport(reportId, sessionInfo.getAccountName());
                System.out.println("ReportController.unfollowReport resp =" + resp);
                return resp;
            } else {
                return DataResponse.SESSION_EXPIRED;
            }
        };
    }
    
    @RouteInfo(method = "get,post", path = "/getReportFollowing")
    public Route getReportFollowing() {
        return (Request request, Response response) -> {
            String sessionKey = ServletUtil.getStringParameter(request, "sessionKey");
            SessionInfo sessionInfo = SISMongoDBConnectorClient.getInstance().checkSession(sessionKey);
            if (sessionInfo != null) {
                int page = ServletUtil.getIntParameter(request, "page");
                DataResponse resp = SISMongoDBConnectorClient.getInstance().getListReportFollowing(sessionInfo.getAccountName(), page);
                System.out.println("ReportController.getReportFollowing resp =" + resp);
                return resp;
            } else {
                return DataResponse.SESSION_EXPIRED;
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/getDetail")
    public Route getDetailReport() {
        return (Request request, Response response) -> {
            String sessionKey = ServletUtil.getStringParameter(request, "sessionKey");
            SessionInfo sessionInfo = SISMongoDBConnectorClient.getInstance().checkSession(sessionKey);
            if (sessionInfo != null) {
                long reportId = ServletUtil.getLongParameter(request, "reportId");
                DataResponse resp = SISMongoDBConnectorClient.getInstance().getReportDetail(reportId, sessionInfo.getAccountName());
                System.out.println("ReportController.getDetailReport resp =" + resp);
                return resp;
            } else {
                return DataResponse.SESSION_EXPIRED;
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/getreport")
    public Route getReportsByReporterName() {
        return (Request request, Response response) -> {
            String sessionKey = ServletUtil.getStringParameter(request, "sessionKey");
            SessionInfo sessionInfo = SISMongoDBConnectorClient.getInstance().checkSession(sessionKey);
            if (sessionInfo != null) {
                int page = ServletUtil.getIntParameter(request, "page");
                DataResponse resp = SISMongoDBConnectorClient.getInstance().getReportsByReporterName(sessionInfo.getAccountName(), page);
                System.out.println("ReportController.getReportsByReporterName resp =" + resp);
                return resp;
            } else {
                return DataResponse.SESSION_EXPIRED;
            }
        };
    }
    
    @RouteInfo(method = "get,post", path = "/getreport/new")
    public Route getReportsLastTime() {
        return (Request request, Response response) -> {
            String sessionKey = ServletUtil.getStringParameter(request, "sessionKey");
            SessionInfo sessionInfo = SISMongoDBConnectorClient.getInstance().checkSession(sessionKey);
            if (sessionInfo != null) {
                int page = ServletUtil.getIntParameter(request, "page");
                DataResponse resp = SISMongoDBConnectorClient.getInstance().getReportsLastTime(sessionInfo.getAccountName(), page);
                System.out.println("ReportController.getReportsLastTime resp =" + resp);
                return resp;
            } else {
                return DataResponse.SESSION_EXPIRED;
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/getreport/address")
    public Route getReportsByAddress() {
        return (Request request, Response response) -> {
            String sessionKey = ServletUtil.getStringParameter(request, "sessionKey");
            SessionInfo sessionInfo = SISMongoDBConnectorClient.getInstance().checkSession(sessionKey);
            if (sessionInfo != null) {
                int page = ServletUtil.getIntParameter(request, "page");
                String districtName = ServletUtil.getStringParameter(request, "districtName");
                String provinceName = ServletUtil.getStringParameter(request, "provinceName");
                DataResponse resp = SISMongoDBConnectorClient.getInstance().getReportsByAddress(sessionInfo.getAccountName(), districtName, provinceName, page);
                System.out.println("ReportController.getReportsByAddress resp =" + resp);
                return resp;
            } else {
                return DataResponse.SESSION_EXPIRED;
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/organization/getreport")
    public Route getReportOrganizationsByStatus() {
        return (Request request, Response response) -> {
            String sessionKey = ServletUtil.getStringParameter(request, "sessionKey");
            SessionInfo sessionInfo = SISMongoDBConnectorClient.getInstance().checkSession(sessionKey);
            if (sessionInfo != null) {
                int paramStatus = ServletUtil.getIntParameter(request, "status");
                int page = ServletUtil.getIntParameter(request, "page");
                ReportInfo.ReportStatus reportStatus = ReportInfo.ReportStatus.getReportStatusByValue(paramStatus);
                if (paramStatus > 0 && reportStatus == null) {
                    return DataResponse.PARAM_ERROR;
                }
                DataResponse resp = SISMongoDBConnectorClient.getInstance().getReportOrganizationsByStatus(sessionInfo.getAccountName(), paramStatus, page);
                System.out.println("ReportController.getReportOrganizationsByStatus resp =" + resp);
                return resp;
            } else {
                return DataResponse.SESSION_EXPIRED;
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/forward")
    public Route forwardReport() {
        return (Request request, Response response) -> {
            String sessionKey = ServletUtil.getStringParameter(request, "sessionKey");
            SessionInfo sessionInfo = SISMongoDBConnectorClient.getInstance().checkSession(sessionKey);
            if (sessionInfo != null) {
                if (sessionInfo.accountType != UserInfo.TYPE_ORGANAZATION) {
                    return DataResponse.ACCESS_DENY;
                }
                int reportId = ServletUtil.getIntParameter(request, "reportId");
                List<String> forwardIds = ServletUtil.getListStringParameter(request, "destination", ",");
                if (reportId <= 0 || forwardIds.isEmpty()) {
                    return DataResponse.PARAM_ERROR;
                }
                DataResponse resp = SISMongoDBConnectorClient.getInstance().forwardReport(sessionInfo.getAccountName(), forwardIds, reportId);
                System.out.println("ReportController.forwardReport resp =" + resp);
                return resp;
            } else {
                return DataResponse.SESSION_EXPIRED;
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/changeStatus")
    public Route changeStatusReport() {
        return (Request request, Response response) -> {
            String sessionKey = ServletUtil.getStringParameter(request, "sessionKey");
            SessionInfo sessionInfo = SISMongoDBConnectorClient.getInstance().checkSession(sessionKey);
            if (sessionInfo != null) {
                if (sessionInfo.accountType != UserInfo.TYPE_ORGANAZATION) {
                    return DataResponse.ACCESS_DENY;
                }
                int reportId = ServletUtil.getIntParameter(request, "reportId");
                int status = ServletUtil.getIntParameter(request, "status");
                long deadlineTime = ServletUtil.getLongParameter(request, "deadlineTime");
                ReportInfo.ReportStatus reportStatus = ReportInfo.ReportStatus.getReportStatusByValue(status);
                if (reportId <= 0 || reportStatus == null || (reportStatus.getValue() == ReportInfo.ReportStatus.Received.getValue() && deadlineTime <= 0)) {
                    return DataResponse.PARAM_ERROR;
                }

                DataResponse resp = SISMongoDBConnectorClient.getInstance().changeStatusReport(sessionInfo.getAccountName(), reportId, reportStatus.getValue(), deadlineTime);
                System.out.println("ReportController.changeStatusReport resp =" + resp);
                return resp;
            } else {
                return DataResponse.SESSION_EXPIRED;
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/comment/add")
    public Route addComment() {
        return (Request request, Response response) -> {
            String sessionKey = ServletUtil.getStringParameter(request, "sessionKey");
            SessionInfo sessionInfo = SISMongoDBConnectorClient.getInstance().checkSession(sessionKey);
            if (sessionInfo != null) {
                int reportId = ServletUtil.getIntParameter(request, "reportId");
                String content = ServletUtil.getStringParameter(request, "content");
                List<String> images = ServletUtil.getListStringParameter(request, "images", ",");

                if (reportId <= 0 || content.isEmpty()) {
                    return DataResponse.PARAM_ERROR;
                }
                CommentInfo cInfo = new CommentInfo();
                cInfo.setContent(content);
                cInfo.setReportId(reportId);
                cInfo.setOwnerId(sessionInfo.accountName);
                cInfo.setTypeUser(sessionInfo.accountType);
                cInfo.setImages(images);
                cInfo.setCreateTime(System.currentTimeMillis());
                DataResponse resp = SISMongoDBConnectorClient.getInstance().addComment(cInfo);
                System.out.println("ReportController.addComment resp =" + resp);
                return resp;
            } else {
                return DataResponse.SESSION_EXPIRED;
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/comment/get")
    public Route getComment() {
        return (Request request, Response response) -> {
            String sessionKey = ServletUtil.getStringParameter(request, "sessionKey");
            SessionInfo sessionInfo = SISMongoDBConnectorClient.getInstance().checkSession(sessionKey);
            if (sessionInfo != null) {
                int reportId = ServletUtil.getIntParameter(request, "reportId");
                if (reportId <= 0) {
                    return DataResponse.PARAM_ERROR;
                }
                DataResponse resp = SISMongoDBConnectorClient.getInstance().getComment(reportId, 0);
                System.out.println("ReportController.getComment resp =" + resp);
                return resp;
            } else {
                return DataResponse.SESSION_EXPIRED;
            }
        };
    }

    @RouteInfo(method = "get,post", path = "/monitoring")
    public Route getDataMonitoring() {
        return (Request request, Response response) -> {
            String sessionKey = ServletUtil.getStringParameter(request, "sessionKey");
            SessionInfo sessionInfo = SISMongoDBConnectorClient.getInstance().checkSession(sessionKey);
            if (sessionInfo != null) {
                String organizationId = ServletUtil.getParameter(request, "organizationId");
                if (organizationId == null || organizationId.isEmpty()) {
                    return DataResponse.PARAM_ERROR;
                }
                if (sessionInfo.accountType != UserInfo.TYPE_ORGANAZATION) {
                    return DataResponse.ACCESS_DENY;
                }

                DataResponse resp = SISMongoDBConnectorClient.getInstance().getDataMonitoringByOrganization(organizationId);
                System.out.println("ReportController.getDataMonitoring resp =" + resp);
                return resp;
            } else {
                return DataResponse.SESSION_EXPIRED;
            }
        };
    }

}
