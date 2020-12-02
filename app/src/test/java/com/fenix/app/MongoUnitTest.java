package com.fenix.app;

import com.fenix.app.dto.ActorDto;
import com.fenix.app.service.MongoService;
import com.fenix.app.util.JsonUtil;
import com.mongodb.client.model.Filters;

import org.bson.BSON;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.Test;

import lombok.var;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class MongoUnitTest {

    @Test
    public void mongoCountTest(){
        System.out.println("**********   Count Test   **********");

        var service = new MongoService("fenix");
        var collection = service.getDocuments("actors");
        System.out.println(collection.countDocuments());

        System.out.println("\n\n\n");
    }

    /**
     * https://mongodb.github.io/mongo-java-driver/4.1/driver/tutorials/databases-collections/
     */
    @Test
    public void mongoAddUpdateTest(){
        System.out.println("**********   Add / Update Test   **********");

        var service = new MongoService("fenix");
        var collection = service.getDocuments("actors");

        var actor = new ActorDto();
        actor.setName("test 1");

        var json = JsonUtil.Serialize(actor);
        var document = Document.parse(json);

        System.out.println(collection.countDocuments());
        System.out.println(document.get("_id"));

        System.out.println(json);
        collection.insertOne(document);

        System.out.println(collection.countDocuments());
        System.out.println(document.get("_id"));

        actor.setName("test 2");
        var json2 = JsonUtil.Serialize(actor);
        var document2 = Document.parse(json);

        System.out.println(json2);
        var result = collection.replaceOne(Filters.eq("_id", document.get("_id")), document2);
        System.out.println(result.getMatchedCount());

        System.out.println("\n\n\n");
    }

    @Test
    public void mongoRemoveTest(){
        System.out.println("**********   Remove Test   **********");

        var service = new MongoService("fenix");
        var collection = service.getDocuments("actors");

        collection.deleteOne(Filters.eq("name", "test 2"));
        System.out.println(collection.countDocuments());

        System.out.println("\n\n\n");
    }
}