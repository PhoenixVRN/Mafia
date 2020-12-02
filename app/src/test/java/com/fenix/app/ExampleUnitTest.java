package com.fenix.app;

import com.fenix.app.dto.ActorDto;
import com.fenix.app.service.MongoService;
import com.fenix.app.util.JsonUtil;

import org.junit.Test;

import lombok.var;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void TestPush()
    {
        com.pusher.rest.Pusher pusherServer;
        pusherServer = new com.pusher.rest.Pusher("1093272","42507c1d16edfe393a0e", "98348bffb60515ab1adc");
        pusherServer.setCluster("eu");
        pusherServer.trigger("map", "location", "test");

    }

    @Test
    public void TestParse()
    {
        String json = "\"{\\\"location\\\":{\\\"latitude\\\":51.5859045,\\\"longitude\\\":38.9942308},\\\"name\\\":\\\"MY_NAME\\\"}\"";
                //"{\"location\":{\"latitude\":51.5859061,\"longitude\":38.9942357},\"name\":\"MY_NAME\"}";
        ActorDto dto = JsonUtil.Parse(ActorDto.class, json);
        System.out.println(dto.toString());
    }

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void mongoTest(){
        var service = new MongoService("fenix");
        var collection = service.getDocuments("actors");
        System.out.println(collection.countDocuments());
    }
}