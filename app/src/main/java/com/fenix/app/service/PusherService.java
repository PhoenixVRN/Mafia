package com.fenix.app.service;

import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.http.PusherServer;

import java.util.ArrayList;
import java.util.List;

import lombok.var;

import static com.fenix.app.MapsActivity.PUSH_MAP_CHANNEL;
import static com.fenix.app.MapsActivity.PUSH_MAP_CHANNEL_SEPARATOR;
import static com.fenix.app.util.LocationUtil.MAP_NUMBER_SEPARATOR;

public class PusherService {

    public static final String PUSH_EVENT = "event";

    private final Pusher pusherClient;
    private PusherServer pusherServer;
    private final EventListener activity;

    protected final List<String> channelList = new ArrayList<>();

    protected List<String> getChannelList() {
        return channelList;
    }

    public PusherService(EventListener activity) {
        this.activity = activity;

        PusherOptions options = new PusherOptions();
        options.setCluster("eu");

        pusherClient = new Pusher("42507c1d16edfe393a0e", options);
        pusherClient.connect(this.activity, ConnectionState.ALL);
    }

    public void Bind(String channelName) {
        var channelList = this.getChannelList();
        if (channelList.contains(channelName))
            return;

        Channel channel = pusherClient.subscribe(channelName);
        channel.bind(PUSH_EVENT, this.activity);
        channelList.add(channelName);
    }

    public void Unbind(String channelName) {
        if (this.getChannelList().contains(channelName)) {
            pusherClient.unsubscribe(channelName);
            this.getChannelList().remove(channelName);
        }
    }

    public void BindToMapChannels(String channelName) {
        // channelName is like this "map=2012;1012"
        var mapNumber = channelName.split(PUSH_MAP_CHANNEL_SEPARATOR)[1];
        var mapNumberArray = mapNumber.split(";");
        var mapX = Integer.parseInt(mapNumberArray[0]);
        var mapY = Integer.parseInt(mapNumberArray[1]);
        List<String> newChannelNames = new ArrayList<>();
        for (int x = mapX - 1; x <= mapX + 1; x++) {
            for (int y = mapY - 1; y <= mapY + 1; y++) {
                var currentChannelName = PUSH_MAP_CHANNEL + PUSH_MAP_CHANNEL_SEPARATOR + x + MAP_NUMBER_SEPARATOR + y;
                newChannelNames.add(currentChannelName);
            }
        }
        List<String> oldChannelNames = new ArrayList<>();
        this.getChannelList().forEach(oldChannelName -> {
            if (!newChannelNames.contains(oldChannelName))
                oldChannelNames.add(oldChannelName);
        });

        newChannelNames.forEach(name -> {
            this.Bind(name);
        });

        oldChannelNames.forEach(name -> {
            this.Unbind(name);
        });
    }

    public void Push(String channelName, Object data) {
        if (pusherServer == null) {
            // "https://42507c1d16edfe393a0e:98348bffb60515ab1adc@api-eu.pusher.com/apps/1093272"
            pusherServer = new PusherServer("1093272", "42507c1d16edfe393a0e", "98348bffb60515ab1adc");
            pusherServer.setCluster("eu");
        }

        pusherServer.trigger(channelName, PUSH_EVENT, data);
    }

    public interface EventListener extends ConnectionEventListener, SubscriptionEventListener {
    }
}
