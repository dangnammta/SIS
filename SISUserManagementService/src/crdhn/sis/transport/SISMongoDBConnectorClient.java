/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crdhn.sis.transport;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoWriteException;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.model.Updates;
import static com.mongodb.client.model.Updates.inc;
import static com.mongodb.client.model.Updates.set;
import org.bson.Document;
import crdhn.sis.configuration.Configuration;
import crdhn.sis.model.UserInfo;
import crdhn.sis.utils.Utils;
import firo.utils.config.Config;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.Base64;
import java.util.Random;
import org.bson.conversions.Bson;
import crdhn.sis.utils.DataResponse;
import java.util.List;
import crdhn.sis.model.ReportInfo;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import static com.mongodb.client.model.Projections.include;
import com.mongodb.client.model.Sorts;
import static com.mongodb.client.model.Updates.combine;
import crdhn.sis.model.CommentInfo;
import crdhn.sis.model.SessionInfo;

/**
 *
 * @author longmd
 */
public class SISMongoDBConnectorClient {

    private static SISMongoDBConnectorClient instance = null;
    private MongoClient client = null;
    private boolean initialized = false;

    private static final String HOST;
    private static final int PORT;
    private static final String DATABASE_NAME;
    private static final int CONNECT_TIMEOUT;
    private static final int SOCKET_TIMEOUT;
    private static final int MAX_WAIT_TIME;
    private static final int SERVER_SELECTION_TIMEOUT;
    private Random random = new SecureRandom();

    static {
        HOST = Config.getParamString("mongodb", "host", "");
        PORT = Config.getParamInt("mongodb", "port");
        DATABASE_NAME = Config.getParamString("mongodb", "database_name", "");
        CONNECT_TIMEOUT = Config.getParamInt("mongodb", "connect_timeout");
        SOCKET_TIMEOUT = Config.getParamInt("mongodb", "socket_timeout");
        MAX_WAIT_TIME = Config.getParamInt("mongodb", "max_waittime");
        SERVER_SELECTION_TIMEOUT = Config.getParamInt("mongodb", "server_selection_timeout");
    }

    public static SISMongoDBConnectorClient getInstance() {
        if (instance == null) {
            instance = new SISMongoDBConnectorClient();
            instance.initMongoClient();
        }
        return instance;
    }

    private void initMongoClient() {
        if (client == null) {
            ServerAddress serverAddress = new ServerAddress(HOST, PORT);
            MongoClientOptions.Builder optionBuilder = new MongoClientOptions.Builder().connectTimeout(CONNECT_TIMEOUT)
                    .socketTimeout(SOCKET_TIMEOUT)
                    .maxWaitTime(MAX_WAIT_TIME)
                    .serverSelectionTimeout(SERVER_SELECTION_TIMEOUT);
            client = new MongoClient(serverAddress, optionBuilder.build());
        }
    }

    private boolean isAlive(int retryCount) {
        initMongoClient();
        if (client == null) {
            return false;
        }

        try {
            if (client.getAddress() != null) {
                return true;
            }
        } catch (Exception ex) {
            Utils.printLogSystem("[SISUSERMANAGEMENT] SISMongoDBConnector::isAlive() : ", ex.getMessage());
        }
        client.close();
        client = null;
        if (retryCount > 0) {
            return isAlive(retryCount - 1);
        }
        return false;
    }

    private void initCollection(final MongoDatabase database) {
        MongoCollection<Document> userCollection = database.getCollection(Configuration.MONGODB_USER_COLLECTION_NAME);
//        BasicDBObject index = new BasicDBObject().append("email", 1);
//        userCollection.createIndex(index);
        MongoCollection<Document> HistoryCollection = database.getCollection(Configuration.MONGODB_HISTORY_COLLECTION_NAME);
        HistoryCollection.createIndex(new BasicDBObject().append("reportId", 1));
        HistoryCollection.createIndex(new BasicDBObject().append("organizationId", 1));

        MongoCollection<Document> commentCollection = database.getCollection(Configuration.MONGODB_COMMENT_COLLECTION_NAME);
        commentCollection.createIndex(new BasicDBObject().append("reportId", 1));
        initCounter(Configuration.ORGANAZATION_ID_COUNTER_KEY, database);
        initCounter(Configuration.REPORT_ID_COUNTER_KEY, database);
        initialized = true;
    }

    public MongoDatabase getDatabase() {
        if (isAlive(1)) { //try again 1 times
            MongoDatabase database = client.getDatabase(DATABASE_NAME);
            if (!initialized) {
                initCollection(database);
            }
            return database;
        }
        return null;
    }

    public DataResponse addUser(UserInfo userInfo) {
        System.out.println("MongoDBConnectorClient::addUser" + userInfo.username);
        try {
            MongoDatabase database = SISMongoDBConnectorClient.getInstance().getDatabase();
            String passwordHash = makePasswordHash(userInfo.password, Integer.toString(random.nextInt()));
            MongoCollection<Document> userCollection = database.getCollection(Configuration.MONGODB_USER_COLLECTION_NAME);
            Document userDocument = new Document("_id", userInfo.username)
                    .append("password", passwordHash)
                    .append("fullName", userInfo.fullName)
                    .append("address", userInfo.address)
                    .append("email", userInfo.email)
                    .append("birthday", userInfo.birthday)
                    .append("gender", userInfo.gender)
                    .append("type", userInfo.type)
                    .append("lastUpdateTime", userInfo.lastUpdateTime)
                    .append("phoneNumber", userInfo.phoneNumber)
                    .append("avatar", userInfo.avatar)
                    .append("followingReportIds", userInfo.followingReportIds);
            userCollection.insertOne(userDocument);
            return DataResponse.SUCCESS;
        } catch (MongoWriteException e) {
            e.printStackTrace();
            return DataResponse.MONGO_USER_EXISTED;
        } catch (Exception ex) {
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return DataResponse.UNKNOWN_EXCEPTION;
        }
    }

    public DataResponse updateUser(UserInfo userInfo) {
        System.out.println("MongoDBConnectorClient::updateUser" + userInfo.username);
        try {
            MongoDatabase database = SISMongoDBConnectorClient.getInstance().getDatabase();
            String passwordHash = makePasswordHash(userInfo.password, Integer.toString(random.nextInt()));
            MongoCollection<Document> userCollection = database.getCollection(Configuration.MONGODB_USER_COLLECTION_NAME);
            Document userDocument = new Document();
            if (!userInfo.password.isEmpty()) {
                userDocument.append("password", passwordHash);
            }
            if (!userInfo.fullName.isEmpty()) {
                userDocument.append("fullName", userInfo.fullName);
            }
            if (!userInfo.address.isEmpty()) {
                userDocument.append("address", userInfo.address);
            }
            if (userInfo.birthday > 0) {
                userDocument.append("birthday", userInfo.birthday);
            }
            if (userInfo.gender > 0) {
                userDocument.append("gender", userInfo.gender);
            }
            if (userInfo.type >= 0) {
                userDocument.append("type", userInfo.type);
            }
            if (userInfo.lastUpdateTime >= 0) {
                userDocument.append("lastUpdateTime", userInfo.lastUpdateTime);
            }
            if (!userInfo.phoneNumber.isEmpty()) {
                userDocument.append("phoneNumber", userInfo.phoneNumber);
            }
            if (!userInfo.avatar.isEmpty()) {
                userDocument.append("avatar", userInfo.avatar);
            }
            if (!userDocument.isEmpty()) {
                userCollection.updateOne(eq("_id", userInfo.username), userDocument);
            }
            return DataResponse.SUCCESS;
        } catch (MongoWriteException e) {
            e.printStackTrace();
            return DataResponse.MONGO_WRITE_EXCEPTION;
        } catch (Exception ex) {
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return DataResponse.UNKNOWN_EXCEPTION;
        }
    }

    public DataResponse getUserInfo(String username) {
        System.out.println("MongoDBConnectorClient::getUser username=" + username);
        try {
            MongoDatabase database = SISMongoDBConnectorClient.getInstance().getDatabase();
            MongoCollection<Document> userCollection = database.getCollection(Configuration.MONGODB_USER_COLLECTION_NAME);
            Document userDocument = userCollection.find(eq("_id", username)).projection(include("email","fullName","type","address","birthday","gender","phoneNumber","avatar")).first();
            if (userDocument == null) {
                return DataResponse.MONGO_NOT_FOUND;
            }
            HashMap userInfo = new HashMap();
            userInfo.put("username",username);
            userInfo.put("email",userDocument.getString("email"));
            userInfo.put("fullName",userDocument.getString("fullName"));
            userInfo.put("type",userDocument.getInteger("type"));
            userInfo.put("address",userDocument.getString("address"));
            userInfo.put("birthday",userDocument.getLong("birthday"));
            userInfo.put("gender",userDocument.getInteger("gender"));
            userInfo.put("phoneNumber",userDocument.getString("phoneNumber"));
            userInfo.put("avatar",userDocument.getString("avatar"));
            return new DataResponse(userInfo);
        } catch (Exception ex) {
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return DataResponse.UNKNOWN_EXCEPTION;
        }
    }

    public Document login(String accountName, String password) {
        MongoDatabase database = SISMongoDBConnectorClient.getInstance().getDatabase();
        if (database == null) {
            return null;
        }
        Bson query;
        if (Utils.validateEmail(accountName)) {
            query = eq("email", accountName);
        } else {
            query = eq("_id", accountName);
        }
        MongoCollection<Document> userCollection = database.getCollection(Configuration.MONGODB_USER_COLLECTION_NAME);
        Document userDocument = userCollection.find(query).first();
        if (userDocument == null) {
            return null;
        }
        int typeUser = userDocument.getInteger("type");
        String hashedAndSalted = userDocument.get("password").toString();
        String salt = hashedAndSalted.split(",")[1];

        if (!hashedAndSalted.equals(makePasswordHash(password, salt))) {
            System.out.println("Submitted password is not a match");
            return null;
        }
        return createSession(accountName, typeUser);
    }

    public boolean logout(String sessionId) {
        MongoDatabase database = SISMongoDBConnectorClient.getInstance().getDatabase();
        MongoCollection<Document> sessionCollection = database.getCollection(Configuration.MONGODB_SESSION_COLLECTION_NAME);
        sessionCollection.deleteOne(eq("_id", sessionId));
        return true;
    }

    public SessionInfo checkSession(String sessionId) {
        MongoDatabase database = SISMongoDBConnectorClient.getInstance().getDatabase();
        MongoCollection<Document> sessionCollection = database.getCollection(Configuration.MONGODB_SESSION_COLLECTION_NAME);
        Document sessionDocument = sessionCollection.find(eq("_id", sessionId)).first();
        if (sessionDocument == null) {
            return null;
        }
        String accountName = sessionDocument.getString("accountName");
        long expireTime = sessionDocument.getLong("expireTime");
        long activeTime = sessionDocument.getLong("activeTime");
        long currentTime = System.currentTimeMillis();
        if (currentTime - (activeTime + expireTime) > 0) {
//            Bson filter = and(eq("username", username),
//                    lt("loginTime", currentTime - expireTime));
            sessionCollection.deleteOne(sessionDocument);
            return null;
        } else {
            Bson update = combine(set("activeTime", System.currentTimeMillis()));
            sessionCollection.updateOne(eq("_id", accountName), update);
        }
        int accountType = sessionDocument.getInteger("accountType");
        return new SessionInfo(accountName, activeTime, accountType, expireTime);
    }

    private Document createSession(String accountName, int userType) {

        try {
            MessageDigest digest256 = MessageDigest.getInstance("SHA-256");
            String sessionPart = accountName + System.nanoTime() + random.nextLong() + "namdv";
            String sessionID = Utils.toHex(sessionPart.getBytes(), digest256);
            //Base64.getEncoder().encodeToString(randomBytes);

            // build the BSON object
            Document session = new Document("_id", sessionID)
                    .append("accountName", accountName)
                    .append("accountType", userType)
                    .append("activeTime", System.currentTimeMillis())
                    .append("expireTime", Configuration.LOGIN_TIMEOUT * 1000l);
            Bson query;
            boolean isEmail = false;
            if (Utils.validateEmail(accountName)) {
                query = eq("email", accountName);
                isEmail = true;
            } else {
                query = eq("_id", accountName);
            }
            MongoDatabase database = SISMongoDBConnectorClient.getInstance().getDatabase();
            //TODO: Tạm thời cho phép 1 user có nhiều current session để phục vụ demo, sau này bàn  lại sau.
//            MongoCollection<Document> userCollection = database.getCollection(Configuration.MONGODB_USER_COLLECTION_NAME);
//            Document userDocument = userCollection.find(query).projection(include("_id", "email")).first();
//            String newAccountName = "";
//            if (userDocument != null) {
//                if (isEmail) {
//                    newAccountName = userDocument.getString("email");
//                } else {
//                    newAccountName = userDocument.getString("_id");
//                }
//            }
//            Bson deleteSession;
//            if (newAccountName.isEmpty()) {
//                deleteSession = eq("accountName", accountName);
//            } else {
//                deleteSession = in("accountName", asList(accountName, newAccountName));
//            }
            MongoCollection<Document> sessionCollection = database.getCollection(Configuration.MONGODB_SESSION_COLLECTION_NAME);
//            sessionCollection.deleteMany(deleteSession);
            sessionCollection.insertOne(session);
            return session;
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(SISMongoDBConnectorClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private String makePasswordHash(String password, String salt) {
        try {
            String saltedAndHashed = password + "," + salt;
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(saltedAndHashed.getBytes());
            byte hashedBytes[] = (new String(digest.digest(), "UTF-8")).getBytes();
            return Base64.getEncoder().encodeToString(hashedBytes) + "," + salt;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 is not available", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 unavailable?  Not a chance", e);
        }
    }

    public DataResponse getOrganizations() {
        System.out.println("MongoDBConnectorClient::getOrganizations");
        try {
            MongoDatabase database = SISMongoDBConnectorClient.getInstance().getDatabase();
            List<Object> arrOrg = new ArrayList<>();
            Block<Document> printBlock = new Block<Document>() {
                @Override
                public void apply(final Document doc) {
                    HashMap obj = new HashMap();
                    obj.put("username", doc.getString("_id"));
                    obj.put("email", doc.getString("email"));
                    obj.put("fullname", doc.getString("fullName"));
                    obj.put("type", doc.getInteger("type"));
                    obj.put("address", doc.getString("address"));
                    obj.put("birthday", doc.getString("address"));
                    obj.put("gender", doc.getInteger("gender"));
                    obj.put("phone", doc.getString("phoneNumber"));
                    arrOrg.add(obj);
                }
            };
            Bson projections = include("_id", "fullName", "address", "email", "gender", "type", "phoneNumber", "birthday");
            MongoCollection<Document> orgCollection = database.getCollection(Configuration.MONGODB_USER_COLLECTION_NAME);
            orgCollection.find(eq("type", UserInfo.TYPE_ORGANAZATION)).projection(projections).forEach(printBlock);
            return new DataResponse(arrOrg);
        } catch (Exception ex) {
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return DataResponse.UNKNOWN_EXCEPTION;
        }
    }

    public DataResponse addReport(ReportInfo reportInfo) {
        System.out.println("MongoDBConnectorClient::addReport reporterName=" + reportInfo.reporterName);
        try {
            MongoDatabase database = SISMongoDBConnectorClient.getInstance().getDatabase();
            long reportId = getNextValue(Configuration.REPORT_ID_COUNTER_KEY, database);
            MongoCollection<Document> orgCollection = database.getCollection(Configuration.MONGODB_REPORT_COLLECTION_NAME);
            Document orgDocument = new Document("_id", reportId)
                    .append("ownerName", reportInfo.reporterName)
                    .append("content", reportInfo.content)
                    .append("images", reportInfo.images)
                    .append("organizationIds", reportInfo.organizationIds)
                    //                    .append("type", reportInfo.type)
                    .append("status", reportInfo.status)
                    .append("comments", reportInfo.comments)
                    .append("districtName", reportInfo.districtAddress)
                    .append("provinceName", reportInfo.cityAddress)
                    .append("deadlineTime", reportInfo.deadlineTime)
                    .append("createTime", reportInfo.createTime);
            orgCollection.insertOne(orgDocument);
            reportInfo.organizationIds.stream().forEach((organizationId) -> {
                MongoCollection<Document> historyCollection = database.getCollection(Configuration.MONGODB_HISTORY_COLLECTION_NAME);
                Document historyData = new Document()
                        .append("reportId", reportId)
                        .append("organizationId", organizationId)
                        .append("status", reportInfo.status)
                        .append("modifierTime", System.currentTimeMillis());
                Bson filter = new Document("reportId", reportId).append("organizationId", organizationId);
                historyCollection.replaceOne(filter, historyData, new UpdateOptions().upsert(true));
            });
            return DataResponse.SUCCESS;
        } catch (MongoWriteException e) {
            e.printStackTrace();
            return DataResponse.MONGO_WRITE_EXCEPTION;
        } catch (Exception ex) {
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return DataResponse.UNKNOWN_EXCEPTION;
        }
    }

    public DataResponse getReportsByReporterName(String reporterName, int page) {
        System.out.println("MongoDBConnectorClient::getReportsByReporterName" + reporterName);
        try {
            MongoDatabase database = SISMongoDBConnectorClient.getInstance().getDatabase();
            List<Object> arrReport = new ArrayList<>();
            List<Long> followingReportIds = getListReportIdFollowing(reporterName);
            Block<Document> printBlock = new Block<Document>() {
                @Override
                public void apply(final Document doc) {
                    HashMap obj = parserReport(doc, followingReportIds, 1);
                    arrReport.add(obj);
                }
            };
            MongoCollection<Document> reportCollection = database.getCollection(Configuration.MONGODB_REPORT_COLLECTION_NAME);
            Bson queryReport = eq("ownerName", reporterName);
//            Bson projection = include("_id", "ownerName", "content", "images", "organizationIds", "type", "status", "districtName", "provinceName", "createTime");
            if (page > 0) {
                reportCollection.find(queryReport).sort(Sorts.descending("createTime")).skip((page - 1) * Configuration.number_report_on_page).limit(Configuration.number_report_on_page).forEach(printBlock);
            } else {
                reportCollection.find(queryReport).sort(Sorts.descending("createTime")).limit(Configuration.number_report_on_page).forEach(printBlock);
            }

            return new DataResponse(arrReport);
        } catch (MongoWriteException e) {
            e.printStackTrace();
            return DataResponse.MONGO_WRITE_EXCEPTION;
        } catch (Exception ex) {
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return DataResponse.UNKNOWN_EXCEPTION;
        }
    }

    public DataResponse getReportsByAddress(String username, String districtName, String provinceName, int page) {
        System.out.println("MongoDBConnectorClient::getReportsByAddress districtName=" + districtName + "\t provinceName=" + provinceName);
        try {
            MongoDatabase database = SISMongoDBConnectorClient.getInstance().getDatabase();
            List<Object> arrReport = new ArrayList<>();
            List<Long> followingReportIds = getListReportIdFollowing(username);
            Block<Document> printBlock = new Block<Document>() {
                @Override
                public void apply(final Document doc) {
                    HashMap obj = parserReport(doc, followingReportIds, 1);
                    arrReport.add(obj);
                }
            };
            MongoCollection<Document> reportCollection = database.getCollection(Configuration.MONGODB_REPORT_COLLECTION_NAME);
            Bson queryReport = and(eq("provinceName", provinceName), eq("provinceName", provinceName));
//            Bson projection = include("_id", "ownerName", "content", "images", "organizationIds", "type", "status", "districtName", "provinceName", "createTime");
            if (page > 0) {
                reportCollection.find(queryReport).sort(Sorts.descending("createTime")).skip((page - 1) * Configuration.number_report_on_page).limit(Configuration.number_report_on_page).forEach(printBlock);
            } else {
                reportCollection.find(queryReport).sort(Sorts.descending("createTime")).limit(Configuration.number_report_on_page).forEach(printBlock);
            }

            return new DataResponse(arrReport);
        } catch (MongoWriteException e) {
            e.printStackTrace();
            return DataResponse.MONGO_WRITE_EXCEPTION;
        } catch (Exception ex) {
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return DataResponse.UNKNOWN_EXCEPTION;
        }
    }
    
    public DataResponse getReportsLastTime(String username, int page) {
        System.out.println("MongoDBConnectorClient::getReportsLastTime username=" + username);
        try {
            MongoDatabase database = SISMongoDBConnectorClient.getInstance().getDatabase();
            List<Object> arrReport = new ArrayList<>();
            List<Long> followingReportIds = getListReportIdFollowing(username);
            Block<Document> printBlock = new Block<Document>() {
                @Override
                public void apply(final Document doc) {
                    HashMap obj = parserReport(doc, followingReportIds, 1);
                    arrReport.add(obj);
                }
            };
            MongoCollection<Document> reportCollection = database.getCollection(Configuration.MONGODB_REPORT_COLLECTION_NAME);
            if (page > 0) {
                reportCollection.find().sort(Sorts.descending("createTime")).skip((page - 1) * Configuration.number_report_on_page).limit(Configuration.number_report_on_page).forEach(printBlock);
            } else {
                reportCollection.find().sort(Sorts.descending("createTime")).limit(Configuration.number_report_on_page).forEach(printBlock);
            }

            return new DataResponse(arrReport);
        } catch (MongoWriteException e) {
            e.printStackTrace();
            return DataResponse.MONGO_WRITE_EXCEPTION;
        } catch (Exception ex) {
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return DataResponse.UNKNOWN_EXCEPTION;
        }
    }

    public DataResponse getReportDetail(long reportId, String username) {
        System.out.println("MongoDBConnectorClient::getReportDetail reportId=" + reportId);
        try {
            MongoDatabase database = SISMongoDBConnectorClient.getInstance().getDatabase();
            MongoCollection<Document> reportCollection = database.getCollection(Configuration.MONGODB_REPORT_COLLECTION_NAME);
            Bson queryReport = eq("_id", reportId);
            Document report = reportCollection.find(queryReport).first();
            if (report != null) {
                List<Long> followingReportIds = getListReportIdFollowing(username);
                HashMap obj = parserReport(report, followingReportIds, 0);
                return new DataResponse(obj);
            }
            return DataResponse.MONGO_NOT_FOUND;
        } catch (MongoWriteException e) {
            e.printStackTrace();
            return DataResponse.MONGO_WRITE_EXCEPTION;
        } catch (Exception ex) {
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return DataResponse.UNKNOWN_EXCEPTION;
        }
    }

    public DataResponse followReport(long reportId, String username) {
        System.out.println("MongoDBConnectorClient::followReport ");
        try {
            MongoDatabase database = SISMongoDBConnectorClient.getInstance().getDatabase();
            MongoCollection<Document> userCollection = database.getCollection(Configuration.MONGODB_USER_COLLECTION_NAME);
            Bson addFollow = combine(set("lastUpdateTime", System.currentTimeMillis()),
                    Updates.addToSet("followingReportIds", reportId));
            userCollection.updateOne(eq("_id", username), addFollow);
            return DataResponse.SUCCESS;
        } catch (MongoWriteException e) {
            e.printStackTrace();
            return DataResponse.MONGO_WRITE_EXCEPTION;
        } catch (Exception ex) {
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return DataResponse.UNKNOWN_EXCEPTION;
        }
    }

    public DataResponse unfollowReport(long reportId, String username) {
        System.out.println("MongoDBConnectorClient::followReport ");
        try {
            MongoDatabase database = SISMongoDBConnectorClient.getInstance().getDatabase();
            MongoCollection<Document> userCollection = database.getCollection(Configuration.MONGODB_USER_COLLECTION_NAME);
            Bson addFollow = combine(set("lastUpdateTime", System.currentTimeMillis()),
                    Updates.pull("followingReportIds", reportId));
            userCollection.updateOne(eq("_id", username), addFollow);
            return DataResponse.SUCCESS;
        } catch (MongoWriteException e) {
            e.printStackTrace();
            return DataResponse.MONGO_WRITE_EXCEPTION;
        } catch (Exception ex) {
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return DataResponse.UNKNOWN_EXCEPTION;
        }
    }

    public DataResponse getListReportFollowing(String username, int page) {
        System.out.println("MongoDBConnectorClient::getListReportFollowing username=" + username + "\t page=" + page);
        try {
            MongoDatabase database = SISMongoDBConnectorClient.getInstance().getDatabase();
            List<Object> arrReport = new ArrayList<>();
            Block<Document> reportsBlock = (final Document doc) -> {
                HashMap obj = new HashMap();
                obj.put("reportId", doc.get("_id"));
                obj.put("ownerName", doc.getString("ownerName"));
                obj.put("content", doc.getString("content"));
                obj.put("images", doc.get("images"));
                obj.put("organizationIds", doc.get("organizationIds"));
                obj.put("status", doc.getInteger("status"));
                obj.put("districtName", doc.getString("districtName"));
                obj.put("provinceName", doc.getString("provinceName"));
                obj.put("deadlineTime", doc.getLong("deadlineTime"));
                obj.put("createTime", doc.getLong("createTime"));
                arrReport.add(obj);
            };
            MongoCollection<Document> userCollection = database.getCollection(Configuration.MONGODB_USER_COLLECTION_NAME);
            Document docUser = userCollection.find(eq("_id", username)).projection(include("_id", "followingReportIds")).first();
            if (docUser != null) {
                List<Long> listReportId = (ArrayList<Long>) docUser.get("followingReportIds");
                if (listReportId != null && !listReportId.isEmpty()) {
                    MongoCollection<Document> reportCollection = database.getCollection(Configuration.MONGODB_REPORT_COLLECTION_NAME);
                    if (page > 0) {

                        reportCollection.find(in("_id", listReportId)).sort(Sorts.descending("createTime")).skip((page - 1) * Configuration.number_report_on_page).limit(Configuration.number_report_on_page).forEach(reportsBlock);
                    } else {
                        reportCollection.find(in("_id", listReportId)).sort(Sorts.descending("createTime")).forEach(reportsBlock);
                    }

                }

            }

            return new DataResponse(arrReport);
        } catch (MongoWriteException e) {
            e.printStackTrace();
            return DataResponse.MONGO_WRITE_EXCEPTION;
        } catch (Exception ex) {
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return DataResponse.UNKNOWN_EXCEPTION;
        }
    }

    private List<Long> getListReportIdFollowing(String username) {
        try {
            MongoDatabase database = SISMongoDBConnectorClient.getInstance().getDatabase();
            MongoCollection<Document> userCollection = database.getCollection(Configuration.MONGODB_USER_COLLECTION_NAME);
            Document docUser = userCollection.find(eq("_id", username)).projection(include("_id", "followingReportIds")).first();
            if (docUser != null) {
                return (ArrayList<Long>) docUser.get("followingReportIds");
            }
        } catch (MongoWriteException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
        }
        return null;
    }

    public DataResponse addComment(CommentInfo cInfo) {
        System.out.println("MongoDBConnectorClient::addComent ");
        try {
            MongoDatabase database = SISMongoDBConnectorClient.getInstance().getDatabase();
            MongoCollection<Document> commentCollection = database.getCollection(Configuration.MONGODB_COMMENT_COLLECTION_NAME);

            Document comment = new Document("reportId", cInfo.reportId)
                    .append("content", cInfo.content)
                    .append("images", cInfo.images)
                    .append("ownerId", cInfo.ownerId)
                    .append("typeUser", cInfo.typeUser)
                    .append("createTime", cInfo.createTime);
            commentCollection.insertOne(comment);
            return DataResponse.SUCCESS;
        } catch (MongoWriteException e) {
            e.printStackTrace();
            return DataResponse.MONGO_WRITE_EXCEPTION;
        } catch (Exception ex) {
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return DataResponse.UNKNOWN_EXCEPTION;
        }
    }

    public DataResponse getComment(long reportId, int limit) {
        System.out.println("MongoDBConnectorClient::getComment reportId=" + reportId);
        try {
            List<Object> arrComment = new ArrayList<>();
            Block<Document> printBlock = new Block<Document>() {
                @Override
                public void apply(final Document doc) {
                    HashMap obj = new HashMap();
                    obj.put("content", doc.getString("content"));
                    obj.put("images", doc.get("images"));
                    obj.put("reportId", doc.getLong("reportId"));
                    obj.put("ownerId", doc.get("ownerId"));
                    obj.put("typeUser", doc.getInteger("typeUser"));
                    obj.put("createTime", doc.get("createTime"));
                    arrComment.add(obj);
                }
            };
            MongoDatabase database = SISMongoDBConnectorClient.getInstance().getDatabase();
            MongoCollection<Document> commentCollection = database.getCollection(Configuration.MONGODB_COMMENT_COLLECTION_NAME);
            if (limit > 0) {
                commentCollection.find(eq("reportId", reportId)).sort(Sorts.descending("createTime")).limit(limit).forEach(printBlock);
            } else {
                commentCollection.find(eq("reportId", reportId)).sort(Sorts.descending("createTime")).forEach(printBlock);
            }

            return new DataResponse(arrComment);
        } catch (MongoWriteException e) {
            e.printStackTrace();
            return DataResponse.MONGO_WRITE_EXCEPTION;
        } catch (Exception ex) {
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return DataResponse.UNKNOWN_EXCEPTION;
        }
    }

    public DataResponse changeStatusReport(String viewerId, long reportId, int status, long deadlineTime) {
        System.out.println("MongoDBConnectorClient::changeStatusReport reportId=" + reportId + "\t viewerId=" + viewerId + "\t status=" + status);
        try {
            MongoDatabase database = SISMongoDBConnectorClient.getInstance().getDatabase();
            MongoCollection<Document> reportCollection = database.getCollection(Configuration.MONGODB_REPORT_COLLECTION_NAME);
            Bson updateParams;
            if (status == ReportInfo.ReportStatus.Received.getValue()) {
                updateParams = combine(set("status", status), set("deadlineTime", deadlineTime));
            } else {
                updateParams = set("status", status);
            }
            UpdateResult updateReport = reportCollection.updateOne(eq("_id", reportId), updateParams);
            if (updateReport != null && updateReport.getMatchedCount() > 0) {
                MongoCollection<Document> historyCollection = database.getCollection(Configuration.MONGODB_HISTORY_COLLECTION_NAME);
                Document historyData = new Document()
                        .append("reportId", reportId)
                        .append("organizationId", viewerId)
                        .append("status", status)
                        .append("modifierTime", System.currentTimeMillis());
                historyCollection.replaceOne(and(eq("reportId", reportId), eq("organizationId", viewerId)), historyData, new UpdateOptions().upsert(true));
                return DataResponse.SUCCESS;
            }
            return DataResponse.MONGO_NOT_FOUND;
        } catch (MongoWriteException e) {
            e.printStackTrace();
            return DataResponse.MONGO_WRITE_EXCEPTION;
        } catch (Exception ex) {
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return DataResponse.UNKNOWN_EXCEPTION;
        }
    }

    public DataResponse forwardReport(String viewerId, List<String> forwardIds, long reportId) {
        System.out.println("MongoDBConnectorClient::forwardReport viewerId=" + viewerId + "\t reportId=" + reportId);
        try {
            MongoDatabase database = SISMongoDBConnectorClient.getInstance().getDatabase();
            MongoCollection<Document> reportCollection = database.getCollection(Configuration.MONGODB_REPORT_COLLECTION_NAME);
            Document docReport = reportCollection.find(eq("_id", reportId)).projection(include("_id", "status", "organizationIds")).first();
            if (docReport != null) {
                List<String> organizationIds = (ArrayList<String>) docReport.get("organizationIds");
                if (!organizationIds.contains(viewerId)) {
                    return DataResponse.ACCESS_DENY;
                }
                MongoCollection<Document> historyCollection = database.getCollection(Configuration.MONGODB_HISTORY_COLLECTION_NAME);
                forwardIds.stream().forEach((organizationId) -> {
                    Document historyData = new Document()
                            .append("reportId", reportId)
                            .append("organizationId", organizationId)
                            .append("status", docReport.getInteger("status"))
                            .append("modifierTime", System.currentTimeMillis());
                    historyCollection.replaceOne(and(eq("reportId", reportId), eq("organizationId", organizationId)), historyData, new UpdateOptions().upsert(true));
//                    reportCollection.updateOne(eq("_id", reportId),  Updates.push("organizationIds", organizationId));
                });
                Document historyMoveData = new Document()
                        .append("reportId", reportId)
                        .append("organizationId", viewerId)
                        .append("status", ReportInfo.ReportStatus.Moved.getValue())
                        .append("modifierTime", System.currentTimeMillis());
                historyCollection.replaceOne(and(eq("reportId", reportId), eq("organizationId", viewerId)), historyMoveData, new UpdateOptions().upsert(true));
                reportCollection.updateOne(eq("_id", reportId), combine(set("status", ReportInfo.ReportStatus.Moved.getValue()), Updates.pushEach("organizationIds", forwardIds)));
                reportCollection.updateOne(eq("_id", reportId), Updates.pull("organizationIds", viewerId));

                return DataResponse.SUCCESS;
            }
            return DataResponse.MONGO_NOT_FOUND;
        } catch (MongoWriteException e) {
            e.printStackTrace();
            return DataResponse.MONGO_WRITE_EXCEPTION;
        } catch (Exception ex) {
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return DataResponse.UNKNOWN_EXCEPTION;
        }
    }

    public DataResponse getReportOrganizationsByStatus(String organizationId, int reportStatus, int page) {
        System.out.println("MongoDBConnectorClient::getReportOrganizationsByStatus organizationId=" + organizationId + "\t reportStatus=" + reportStatus);
        try {
            MongoDatabase database = SISMongoDBConnectorClient.getInstance().getDatabase();
            List<Long> listReportId = new ArrayList<>();
            Block<Document> processHistoryBlock = new Block<Document>() {
                @Override
                public void apply(final Document doc) {
                    listReportId.add(doc.getLong("reportId"));
                }
            };
            List<Object> arrReport = new ArrayList<>();
            Block<Document> reportsBlock = (final Document doc) -> {
                HashMap obj = new HashMap();
                obj.put("reportId", doc.get("_id"));
                obj.put("ownerName", doc.getString("ownerName"));
                obj.put("content", doc.getString("content"));
                obj.put("images", doc.get("images"));
                obj.put("organizationIds", doc.get("organizationIds"));
//                obj.put("type", doc.getInteger("type"));
                obj.put("status", doc.getInteger("status"));
                obj.put("districtName", doc.getString("districtName"));
                obj.put("provinceName", doc.getString("provinceName"));
                obj.put("deadlineTime", doc.getLong("deadlineTime"));
                obj.put("createTime", doc.getLong("createTime"));
                arrReport.add(obj);
            };
            MongoCollection<Document> historyCollection = database.getCollection(Configuration.MONGODB_HISTORY_COLLECTION_NAME);
            //loai bo cai report da move: ne("status", ReportInfo.ReportStatus.Moved.getValue())
            Bson queryHistory = and(eq("organizationId", organizationId), eq("status", reportStatus));
            if (reportStatus == 0) {// query all
                queryHistory = and(eq("organizationId", organizationId), ne("status", ReportInfo.ReportStatus.Moved.getValue()));
            }
            if (page > 0) {
                historyCollection.find(queryHistory).sort(Sorts.descending("modifierTime")).skip((page - 1) * Configuration.number_report_on_page).limit(Configuration.number_report_on_page).forEach(processHistoryBlock);
            } else {
                historyCollection.find(queryHistory).sort(Sorts.descending("modifierTime")).forEach(processHistoryBlock);
            }
            MongoCollection<Document> reportCollection = database.getCollection(Configuration.MONGODB_REPORT_COLLECTION_NAME);
//            Bson projection = include("_id", "ownerName", "content", "images", "organizationIds", "type", "status","districtName", "provinceName","createTime");
            reportCollection.find(in("_id", listReportId)).sort(Sorts.descending("createTime")).forEach(reportsBlock);
            return new DataResponse(arrReport);
        } catch (MongoWriteException e) {
            e.printStackTrace();
            return DataResponse.MONGO_WRITE_EXCEPTION;
        } catch (Exception ex) {
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return DataResponse.UNKNOWN_EXCEPTION;
        }
    }

    private HashMap parserReport(Document report, List<Long> followingReportIds, int limitComment) {
        HashMap obj = new HashMap();
        long reportId = report.getLong("_id");
        obj.put("reportId", reportId);
        obj.put("ownerName", report.getString("ownerName"));
        obj.put("content", report.getString("content"));
        obj.put("images", report.get("images"));
        obj.put("organizationIds", report.get("organizationIds"));
        obj.put("status", report.getInteger("status"));
        obj.put("districtName", report.getString("districtName"));
        obj.put("provinceName", report.getString("provinceName"));
        obj.put("deadlineTime", report.getLong("deadlineTime"));
        obj.put("createTime", report.getLong("createTime"));
        if (followingReportIds != null && followingReportIds.contains(reportId)) {
            obj.put("isFollowing", true);
        } else {
            obj.put("isFollowing", false);
        }
        DataResponse respComment = getComment(reportId, limitComment);
        if (respComment != null && respComment.getError() == 0) {
            obj.put("comments", respComment.getData());
        } else {
            obj.put("comments", "Error get comment");
        }
        return obj;
    }

    public DataResponse getDataMonitoringByOrganization(String organizationId) {
        System.out.println("MongoDBConnectorClient::monitoringReportByOrganization organizationId=" + organizationId);
        try {
            MongoDatabase database = SISMongoDBConnectorClient.getInstance().getDatabase();
            HashMap<String, HashMap<String, Integer>> objMonitoring = new HashMap();
            HashMap objDefault = new HashMap();
            objDefault.put("count", 0);
            objDefault.put("code", ReportInfo.ReportStatus.Created.getValue());
            objMonitoring.put("Created", objDefault);
            objDefault = new HashMap();
            objDefault.put("count", 0);
            objDefault.put("code", ReportInfo.ReportStatus.Moved.getValue());
            objMonitoring.put("Moved", objDefault);
            objDefault = new HashMap();
            objDefault.put("count", 0);
            objDefault.put("code", ReportInfo.ReportStatus.Cancelled.getValue());
            objMonitoring.put("Cancelled", objDefault);
            objDefault = new HashMap();
            objDefault.put("count", 0);
            objDefault.put("code", ReportInfo.ReportStatus.Closed.getValue());
            objMonitoring.put("Closed", objDefault);
            objDefault = new HashMap();
            objDefault.put("count", 0);
            objDefault.put("code", ReportInfo.ReportStatus.Received.getValue());
            objMonitoring.put("Received", objDefault);
            objDefault = new HashMap();
            objDefault.put("count", 0);
            objDefault.put("code", ReportInfo.ReportStatus.Responsed.getValue());
            objMonitoring.put("Responsed", objDefault);
            Block<Document> historyBlock = new Block<Document>() {
                @Override
                public void apply(final Document doc) {
                    int status = doc.getInteger("status");
                    if (status == ReportInfo.ReportStatus.Created.getValue()) {
                        HashMap<String, Integer> value = objMonitoring.get("Created");
                        int count = value.getOrDefault("count", 0);
                        count++;
                        HashMap obj = new HashMap();
                        obj.put("count", count);
                        obj.put("code", status);
                        objMonitoring.put("Created", obj);

                    } else if (status == ReportInfo.ReportStatus.Moved.getValue()) {
                        HashMap<String, Integer> value = objMonitoring.get("Moved");
                        int count = value.getOrDefault("count", 0);
                        count++;
                        HashMap obj = new HashMap();
                        obj.put("count", count);
                        obj.put("code", status);
                        objMonitoring.put("Moved", obj);
                    } else if (status == ReportInfo.ReportStatus.Cancelled.getValue()) {
                        HashMap<String, Integer> value = objMonitoring.get("Cancelled");
                        int count = value.getOrDefault("count", 0);
                        count++;
                        HashMap obj = new HashMap();
                        obj.put("count", count);
                        obj.put("code", status);
                        objMonitoring.put("Cancelled", obj);
                    } else if (status == ReportInfo.ReportStatus.Closed.getValue()) {
                        HashMap<String, Integer> value = objMonitoring.get("Closed");
                        int count = value.getOrDefault("count", 0);
                        count++;
                        HashMap obj = new HashMap();
                        obj.put("count", count);
                        obj.put("code", status);
                        objMonitoring.put("Closed", obj);
//                    } else if (status == ReportInfo.ReportStatus.InProcessing.getValue()) {
//                        HashMap<String, Integer> value = objMonitoring.get("InProcessing");
//                        int count = value.getOrDefault("count", 0);
//                        count++;
//                        HashMap obj = new HashMap();
//                        obj.put("count", count);
//                        obj.put("code", status);
//                        objMonitoring.put("InProcessing", obj);
                    } else if (status == ReportInfo.ReportStatus.Received.getValue()) {
                        HashMap<String, Integer> value = objMonitoring.get("Received");
                        int count = value.getOrDefault("count", 0);
                        count++;
                        HashMap obj = new HashMap();
                        obj.put("count", count);
                        obj.put("code", status);
                        objMonitoring.put("Received", obj);
                    } else if (status == ReportInfo.ReportStatus.Responsed.getValue()) {
                        HashMap<String, Integer> value = objMonitoring.get("Responsed");
                        int count = value.getOrDefault("count", 0);
                        count++;
                        HashMap obj = new HashMap();
                        obj.put("count", count);
                        obj.put("code", status);
                        objMonitoring.put("Responsed", obj);
                    }
                }

            };
            MongoCollection<Document> historyCollection = database.getCollection(Configuration.MONGODB_HISTORY_COLLECTION_NAME);
            historyCollection.find(eq("organizationId", organizationId)).projection(include("status")).forEach(historyBlock);
            List<Object> arrMonitoring = new ArrayList();
            arrMonitoring.add(objMonitoring.get("Created"));
            arrMonitoring.add(objMonitoring.get("Moved"));
            arrMonitoring.add(objMonitoring.get("Cancelled"));
            arrMonitoring.add(objMonitoring.get("Closed"));
            arrMonitoring.add(objMonitoring.get("Received"));
            arrMonitoring.add(objMonitoring.get("Responsed"));
            return new DataResponse(arrMonitoring);
        } catch (MongoWriteException e) {
            e.printStackTrace();
            return DataResponse.MONGO_WRITE_EXCEPTION;
        } catch (Exception ex) {
            StackTraceElement traceElement = ex.getStackTrace()[0];
            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
            return DataResponse.UNKNOWN_EXCEPTION;
        }
    }

    public boolean initCounter(String key, final MongoDatabase database) {
        MongoCollection<Document> counterCollection = database.getCollection(Configuration.MONGODB_COUNTER_COLLECTION_NAME);
        Document checkDocument = counterCollection.find(eq("_id", key)).first();
        if (checkDocument == null) {
            Document counter = new Document("_id", key).append("seq", 1l);
            counterCollection.insertOne(counter);
        }
        return true;
    }

    private synchronized long getNextValue(String key, final MongoDatabase database) {

        try {
            MongoCollection<Document> counterCollection = database.getCollection(Configuration.MONGODB_COUNTER_COLLECTION_NAME);
            Document objDocument = counterCollection.findOneAndUpdate(eq("_id", key), inc("seq", 1l));
            return objDocument.getLong("seq");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }
}
