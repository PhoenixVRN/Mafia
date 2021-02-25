package com.fenix.app.service.entity;

import com.fenix.app.service.MongoService;
import com.fenix.app.util.JsonUtil;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndReplaceOptions;

import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

import lombok.var;

public abstract class EntityServiceBase<E> {

    protected Class<E> entityClass;
    protected String entityKeyField;

    protected abstract void initEntityClass();

    protected MongoService service;
    protected MongoCollection<Document> collection;

    public EntityServiceBase(MongoService service, String collectionName) {
        initEntityClass();
        this.service = service;
        this.collection = service.getDocuments(collectionName);
    }

    protected Bson getOneFilter(E entity) {
        var json = JsonUtil.Serialize(entity);
        var doc = Document.parse(json);

        var filter = Filters.eq(entityKeyField, doc.get(entityKeyField));
        return filter;
    }

    //#region CRUDL

    public void create(E entity) {
        var json = JsonUtil.Serialize(entity);
        var doc = Document.parse(json);

        collection.insertOne(doc);
    }

    public E read(Bson filter) {
        var doc = collection.find(filter).limit(1).first();
        if (doc == null)
            return null;

        var json = doc.toJson();
        var entity = JsonUtil.Parse(entityClass, json);

        return entity;
    }

    public void update(E entity, Bson filter) {
        var json = JsonUtil.Serialize(entity);
        var doc = Document.parse(json);

        collection.findOneAndReplace(filter, doc, new FindOneAndReplaceOptions().upsert(true));
    }

    public void delete(Bson filter) {
        collection.deleteOne(filter);
    }

    public List<E> list(Bson filter) {
        var cursor = collection.find(filter).limit(1000).iterator();
        List<E> entities = new ArrayList<E>();
        while (cursor.hasNext()) {
            var doc = cursor.next();
            var json = doc.toJson();
            var entity = JsonUtil.Parse(entityClass, json);
            entities.add(entity);
        }
        return entities;
    }

    //#endregion

    //#region LSD

    public E load(String keyValue) {
        return read(Filters.eq(entityKeyField, keyValue));
    }

    public E save(E entity) {
        var json = JsonUtil.Serialize(entity);
        var doc = Document.parse(json);

        var result = collection.findOneAndReplace(
                Filters.eq(entityKeyField, doc.get(entityKeyField)),
                doc,
                new FindOneAndReplaceOptions().upsert(true));

        if (result == null)
            return null;

        var resultJson = result.toJson();
        var resultEtity = JsonUtil.Parse(entityClass, resultJson);

        return resultEtity;
    }

    public void delete(E entity) {
        var json = JsonUtil.Serialize(entity);
        var doc = Document.parse(json);

        collection.deleteOne(Filters.eq(entityKeyField, doc.get(entityKeyField)));
    }

    //#endregion

}

