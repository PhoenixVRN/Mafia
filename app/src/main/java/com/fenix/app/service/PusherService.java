package com.fenix.app.service;

import androidx.appcompat.app.AppCompatActivity;

import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;

import java.util.Date;

public class PusherService {

    private Pusher pusherClient;
    private com.pusher.rest.Pusher pusherServer;
    private AppCompatActivity activity;

    public PusherService(AppCompatActivity activity) {
        this.activity = activity;

        PusherOptions options = new PusherOptions();
        options.setCluster("eu");

        pusherClient = new Pusher("42507c1d16edfe393a0e", options);
        pusherClient.connect((ConnectionEventListener) this.activity, ConnectionState.ALL);
/*
curl -H 'Content-Type: application/json' -d '{"data":"{\"message\":\"hello world\"}","name":"my-event","channel":"my-channel"}' \
"https://api-eu.pusher.com/apps/1093272/events?body_md5=2c99321eeba901356c4c7998da9be9e0&auth_version=1.0&auth_key=42507c1d16edfe393a0e&auth_timestamp=1604519453&auth_signature=1d92a803abc6d6f230a929a24dadacd4f1431287b79e1cdc1ae4ed0eec462ef1&"
*/


        pusherServer = new com.pusher.rest.Pusher("https://42507c1d16edfe393a0e:98348bffb60515ab1adc@api-eu.pusher.com/apps/1093272");
        pusherServer.setCluster("eu");
    }

    public void Bind(String channelName, String eventName) {
        Channel channel = pusherClient.subscribe(channelName);
        channel.bind(eventName, (SubscriptionEventListener) this.activity);
    }

    public void Push(String channelName, String eventName, Object data) {
        // pusherServer.trigger(channelName, eventName, data);
    }
}
