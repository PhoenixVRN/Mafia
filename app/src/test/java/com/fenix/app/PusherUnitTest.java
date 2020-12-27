package com.fenix.app;

import com.fenix.app.dto.ActorDto;
import com.fenix.app.service.MongoService;
import com.fenix.app.util.JsonUtil;
import com.pusher.http.PusherServer;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import lombok.var;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PusherUnitTest {

    @Test
    public void t001_TestPush()
    {
        PusherServer pusherServer;
        pusherServer = new PusherServer("1093272","42507c1d16edfe393a0e", "98348bffb60515ab1adc");
        pusherServer.setCluster("eu");
        pusherServer.trigger("map", "location", "test");

    }

    @Test
    public void t001_TestParse()
    {
        String json = "{\"_id\":{\"$oid\":\"5fe053c859ed8fa1d23fc7be\"},\"email\":\"testmail@test.ru\",\"name\":\"Jon\",\"pass\":\"qwerty\",\"phone\":\"1111111\"}";
        ActorDto dto = JsonUtil.Parse(ActorDto.class, json);
        System.out.println(dto.toString());
    }

    @Test
    public void t001_addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

}