package com.fenix.app;

import com.fenix.app.dto.ActorDto;
import com.fenix.app.service.PusherService;
import com.fenix.app.util.JsonUtil;
import com.fenix.app.util.LocationUtil;
import com.google.android.gms.maps.model.LatLng;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionStateChange;
import com.pusher.http.PusherServer;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.List;

import lombok.var;

import static com.fenix.app.MapsActivity.PUSH_MAP_CHANNEL;
import static com.fenix.app.MapsActivity.PUSH_MAP_CHANNEL_SEPARATOR;
import static com.fenix.app.MapsActivity.PUSH_MAP_GRAIN;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PusherUnitTest {

    @Test
    public void t001_TestPush() {
        PusherServer pusherServer;
        pusherServer = new PusherServer("1093272", "42507c1d16edfe393a0e", "98348bffb60515ab1adc");
        pusherServer.setCluster("eu");
        pusherServer.trigger("map", "location", "test");

    }

    @Test
    public void t002_TestParse() {
        String json = "{\"_id\":{\"$oid\":\"5fe053c859ed8fa1d23fc7be\"},\"email\":\"testmail@test.ru\",\"name\":\"Jon\",\"pass\":\"qwerty\",\"phone\":\"1111111\"}";
        ActorDto dto = JsonUtil.Parse(ActorDto.class, json);
        System.out.println(dto.toString());
    }

    @Test
    public void t003_SubscribeMapChannels() {

        var pusherService = new PusherServiceMock(new PusherService.EventListener() {
            @Override
            public void onEvent(PusherEvent event) {

            }

            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {

            }

            @Override
            public void onError(String message, String code, Exception e) {

            }
        });

        // Test 1
        {
            LatLng latLng1 = new LatLng(55.52700430316578, 37.80795498761681);

            String mapNumber1 = LocationUtil.calcMapNumber(latLng1, PUSH_MAP_GRAIN);
            System.out.println("mapNumber1 = " + mapNumber1);

            String myChanelName1 = PUSH_MAP_CHANNEL + PUSH_MAP_CHANNEL_SEPARATOR + mapNumber1;
            pusherService.BindToMapChannels(myChanelName1);

            System.out.println("********* Channels *********");
            final int[] i = {1};
            pusherService.getChannelList().forEach(cn -> {
                System.out.println("channel №" + i[0] + " = " + cn);
                i[0]++;
            });
            System.out.println("****************************");
        }

        // Test 2
        {
            LatLng latLng1 = new LatLng(55.52700430316578, 37.80795498761681);
            LatLng latLng2 = new LatLng(55.52700430316578, 37.81795498761681);
            System.out.println("Distance 1 -> 2 = " + LocationUtil.distance(latLng1, latLng2));

            String mapNumber2 = LocationUtil.calcMapNumber(latLng2, PUSH_MAP_GRAIN);
            System.out.println("mapNumber2 = " + mapNumber2);

            String myChanelName2 = PUSH_MAP_CHANNEL + PUSH_MAP_CHANNEL_SEPARATOR + mapNumber2;
            pusherService.BindToMapChannels(myChanelName2);

            System.out.println("********* Channels *********");
            final int[] i = {1};
            pusherService.getChannelList().forEach(cn -> {
                System.out.println("channel №" + i[0] + " = " + cn);
                i[0]++;
            });
            System.out.println("****************************");
        }

    }

    class PusherServiceMock extends PusherService {

        public PusherServiceMock(PusherService.EventListener activity) {
            super(activity);
        }

        @Override
        protected List<String> getChannelList() {
            return super.channelList;
        }
    }

}