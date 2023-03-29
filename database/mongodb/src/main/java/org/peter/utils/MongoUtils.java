package org.peter.utils;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;

public class MongoUtils {

    private final static String IP = "101.201.81.221";

    // basics ---fuck
    public static void getClient() {
        String uri = "mongodb://admin:123456@" + IP + ":27017/?maxPoolSize=20&w=majority";

        MongoClient mongoClient = MongoClients.create(uri);
        List<Integer> books = Arrays.asList(27464, 747854);
        Document person = new Document("_id", "jo3")
                .append("name", "Jo Bloggs1")
                .append("address", new BasicDBObject("street", "123 Fake St1")
                        .append("city", "Faketon1")
                        .append("state", "MA1")
                        .append("zip", 123456))
                .append("books", books);

        MongoDatabase database = mongoClient.getDatabase("Examples");


        MongoCollection<Document> cl = database.getCollection("people");
        cl.insertOne(person);
    }
}
