package com.fenix.app.service;

import androidx.appcompat.app.AppCompatActivity;

import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.http.PusherServer;

public class PusherService {

    private Pusher pusherClient;
    private PusherServer pusherServer;
    private AppCompatActivity activity;

    public PusherService(AppCompatActivity activity) {
        this.activity = activity;

        PusherOptions options = new PusherOptions();
        options.setCluster("eu");

        pusherClient = new Pusher("42507c1d16edfe393a0e", options);
        pusherClient.connect((ConnectionEventListener) this.activity, ConnectionState.ALL);
    }

    public void Bind(String channelName, String eventName) {
        Channel channel = pusherClient.subscribe(channelName);
        channel.bind(eventName, (SubscriptionEventListener) this.activity);
    }

    public void Push(String channelName, String eventName, Object data) {
        if (pusherServer == null) {
            // "https://42507c1d16edfe393a0e:98348bffb60515ab1adc@api-eu.pusher.com/apps/1093272"
            pusherServer = new PusherServer("1093272", "42507c1d16edfe393a0e", "98348bffb60515ab1adc");
            pusherServer.setCluster("eu");
        }

        pusherServer.trigger(channelName, eventName, data);
    }
}
