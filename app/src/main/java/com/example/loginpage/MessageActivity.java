package com.example.loginpage;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.channel.ChannelClient;
import io.getstream.chat.android.client.models.Channel;
import io.getstream.chat.android.client.models.Message;
import io.getstream.chat.android.client.models.User;
import io.getstream.chat.android.ui.message.MessageListActivity;

import android.os.Bundle;

public final class MessageActivity extends AppCompatActivity
{
    public MessageActivity(){
        super(R.layout.activity_second);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());

        Bundle data = this.getIntent().getExtras();
        String username = data.getString("username");
        System.out.println(username);
        ChatClient client = ChatClient.instance();

        User u = new User();
        u.setId("01");
        u.setName(username);
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiMDEifQ.v2OJoe1Q1t5WWRsZ-ycN1FGDbcrAY8Vnr6e4ArWGkvk";

        client.connectUser(u,token).enqueue(connectionDataResult->{
            if (connectionDataResult.isSuccess()) {
                // Logged in
                User userRes = connectionDataResult.data().getUser();
                String connectionId = connectionDataResult.data().getConnectionId();
                System.out.println("Connection ID:" + connectionId);
                System.out.println("User connected!");
                ChannelClient channelClient = client.channel("messaging","message_room");
                channelClient.watch().enqueue(clientResult->{
                    if(clientResult.isSuccess()){
                        Channel channel = clientResult.data();
                        System.out.println("Channel messages: "+channel.getRead());
                        Message message = new Message();
                        message.setText("Josh, I told them I was pesca-pescatarian. Which is one who eats solely fish who eat other fish.");
                        message.setId(userRes.getId());
                        message.setCid("messaging:message_room");

                        channelClient.sendMessage(message).enqueue(messageResult -> {
                            if (messageResult.isSuccess()) {
                                Message sentMessage = messageResult.data();
                            } else {
                                System.out.println("Unable to send message!" + messageResult.error());
                            }
                        });
                    } else {
                        System.out.println("Unable to watch channel!" + clientResult.error());
                    }
                });

            } else {
                System.out.println("User was not able to login!");
            }
        });
        //client.setUserWithoutConnectingIfNeeded();




        this.startActivity(MessageListActivity.createIntent(this, "messaging:message_room"));




    }

}