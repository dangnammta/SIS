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
import org.json.JSONObject;
import crdhn.sis.utils.DataResponse;
import java.util.List;
import crdhn.sis.model.ReportInfo;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import static com.mongodb.client.model.Projections.include;
import com.mongodb.client.model.Sorts;
import static com.mongodb.client.model.Updates.combine;
import crdhn.sis.model.CommentInfo;
import crdhn.sis.model.SessionInfo;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author longmd
 */
public class UserConnector extends SISMongoDBConnectorClient {

    private Random random = new SecureRandom();

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

//    public UserInfo getUserInfo(String username) {
//        System.out.println("MongoDBConnectorClient::getUser username=" + username);
//        try {
//            UserInfo userInfo = new UserInfo();
//            MongoDatabase database = SISMongoDBConnectorClient.getInstance().getDatabase();
//            MongoCollection<Document> userCollection = database.getCollection(Configuration.MONGODB_USER_COLLECTION_NAME);
//            Document userDocument = userCollection.find(eq("_id", username)).first();
//            if (userDocument == null) {
//                return null;
//            }
//            userInfo.setUsername(username);
//            userInfo.setPassword(userDocument.getString("password"));
//            userInfo.setEmail(userDocument.getString("email"));
//            userInfo.setFullName(userDocument.getString("fullName"));
//            userInfo.setType(userDocument.getInteger("type"));
//            userInfo.setAddress(userDocument.getString("address"));
//            userInfo.setBirthday(userDocument.getLong("birthday"));
//            userInfo.setGender(userDocument.getInteger("gender"));
//            userInfo.setPhoneNumber(userDocument.getString("phoneNumber"));
//            return userInfo;
//        } catch (Exception ex) {
//            StackTraceElement traceElement = ex.getStackTrace()[0];
//            Utils.printLogSystem(this.getClass().getSimpleName(), traceElement.getMethodName() + "(): " + ex.getMessage());
//            return null;
//        }
//    }

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
            MongoCollection<Document> userCollection = database.getCollection(Configuration.MONGODB_USER_COLLECTION_NAME);
            Document userDocument = userCollection.find(query).projection(include("_id", "email")).first();
            String newAccountName = "";
            if (userDocument != null) {
                if (isEmail) {
                    newAccountName = userDocument.getString("email");
                } else {
                    newAccountName = userDocument.getString("_id");
                }
            }
            Bson deleteSession;
            if (newAccountName.isEmpty()) {
                deleteSession = eq("accountName", accountName);
            } else {
                deleteSession = in("accountName", asList(accountName, newAccountName));
            }
            MongoCollection<Document> sessionCollection = database.getCollection(Configuration.MONGODB_SESSION_COLLECTION_NAME);
            sessionCollection.deleteMany(deleteSession);
            sessionCollection.insertOne(session);
            return session;
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(UserConnector.class.getName()).log(Level.SEVERE, null, ex);
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

}
