package com.fenix.app;

import org.junit.Test;

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
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
}