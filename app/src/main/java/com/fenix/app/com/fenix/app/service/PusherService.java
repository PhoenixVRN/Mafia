package com.fenix.app.com.fenix.app.service;

import androidx.appcompat.app.AppCompatActivity;

import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;

public class PusherService {

    public Pusher pusher;

    private AppCompatActivity activity;

    public PusherService(AppCompatActivity activity) {
        this.activity = activity;

        PusherOptions options = new PusherOptions();
        options.setCluster("eu");

        pusher = new Pusher("42507c1d16edfe393a0e", options);
        pusher.connect((ConnectionEventListener) this.activity, ConnectionState.ALL);
    }

    public void Bind(String channelName, String eventName) {
        Channel channel = pusher.subscribe(channelName);
        channel.bind(eventName, (SubscriptionEventListener) this.activity);
    }
}
