package com.fenix.app.service;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

public class MongoService {

    private MongoDatabase db;

    public MongoService(String databseName) {
        MongoClientURI uri = new MongoClientURI("mongodb+srv://admin:8N3frAhiofOfMR01@cluster0.dlazp.mongodb.net/test");
        MongoClient mongoClient = new MongoClient(uri);
        db = mongoClient.getDatabase(databseName);
    }

    public MongoCollection<Document> getDocuments(String collectionName) {
        MongoCollection<Document> collection = db.getCollection(collectionName);
        return collection;
    }

}
