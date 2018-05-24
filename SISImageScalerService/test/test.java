//
//import com.mongodb.MongoClient;
//import com.mongodb.client.MongoCollection;
//import com.mongodb.client.MongoCursor;
//import com.mongodb.client.MongoDatabase;
//import com.mongodb.client.model.Filters;
//import static com.mongodb.client.model.Filters.and;
//import static com.mongodb.client.model.Filters.eq;
//import com.mongodb.client.model.Updates;
//import java.util.ArrayList;
//import org.bson.Document;
//import org.bson.conversions.Bson;
//
///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//
///**
// *
// * @author longmd
// */
//public class test {
//	public static void main(String[] args) throws Exception {
//		MongoClient client = new MongoClient("127.0.0.1");
//		MongoDatabase db = client.getDatabase("school");
//		MongoCollection<Document> coll = db.getCollection("students");
//		
//		try (MongoCursor<Document> cursor = coll.find().iterator()){
//			ArrayList<Document> listSubDoc = new ArrayList<Document>();
//			Document doc;
//			Double min, score;
//
//			while (cursor.hasNext()){
//				doc = cursor.next();
//				listSubDoc = (ArrayList)doc.get("scores");
//				min = 100000.0;
//				for (Document subDoc : listSubDoc){
//					if (subDoc.getString("type").equals("homework")
//							&& min > subDoc.getDouble("score")){
//						min = subDoc.getDouble("score");
//					}
//				}
//				Bson update = Updates.pull("scores", and(eq("type","homework"), eq("score", min)));
//				coll.updateOne(eq("_id", doc.getInteger("_id")), update);
//			}
//		}
//		catch (Exception e){
//			e.printStackTrace();
//		}
//	}
//}
