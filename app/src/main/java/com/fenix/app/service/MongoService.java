package com.fenix.app.service;

import android.util.Log;

import com.fenix.app.util.ThreadUtil;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import org.bson.Document;

public class MongoService {

    private final MongoDatabase db;

    public MongoService(String databseName) {
        //MongoClientURI uri = new MongoClientURI("mongodb+srv://admin:8N3frAhiofOfMR01@cluster0.dlazp.mongodb.net/test");
        MongoClientURI uri = new MongoClientURI("mongodb://admin:8N3frAhiofOfMR01@cluster0-shard-00-00.dlazp.mongodb.net:27017,cluster0-shard-00-01.dlazp.mongodb.net:27017,cluster0-shard-00-02.dlazp.mongodb.net:27017/test?ssl=true&replicaSet=atlas-3zbbhk-shard-0&authSource=admin&w=majority");
        MongoClient mongoClient = new MongoClient(uri);
        db = mongoClient.getDatabase(databseName);
        ThreadUtil.Await(() -> db.listCollectionNames().first());
    }

    public MongoCollection<Document> getDocuments(String collectionName) {
        MongoCollection<Document> collection = db.getCollection(collectionName);
        return collection;
    }

}
