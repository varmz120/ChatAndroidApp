package com.example.loginpage;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.models.User;
import io.getstream.chat.android.ui.channel.ChannelListActivity;
import io.getstream.chat.android.ui.channel.ChannelListFragment;
import io.getstream.chat.android.ui.message.MessageListActivity;

import android.os.Bundle;

public final class SecondActivity extends AppCompatActivity
{
    public SecondActivity(){
        super(R.layout.activity_second);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());

        Bundle data = this.getIntent().getExtras();
        String secret_key = data.getString("user_chat_token");
        String username = data.getString("username");
        System.out.println(username);
        ChatClient client = ChatClient.instance();

        User u = new User();
        u.setId("01");
        u.setName(username);
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiMDEifQ.v2OJoe1Q1t5WWRsZ-ycN1FGDbcrAY8Vnr6e4ArWGkvk";

        client.connectUser(u,token).enqueue(result->{
            if (result.isSuccess()) {
                // Logged in
                User userRes = result.data().getUser();
                String connectionId = result.data().getConnectionId();
                System.out.println("User connected! " + userRes);
            } else {
                System.out.println("User was not able to login!");
            }
        });
        this.startActivity(MessageListActivity.createIntent(this, "messaging:123"));

//        FilterObject filter = Filters.and(
//                Filters.eq("type","messaging"),
//                Filters.in("members", Collections.singletonList(u.getId()))
//        );
//
//        ViewModelProvider.Factory factory = new ChannelListViewModelFactory.Builder()
//                .filter(filter)
//                .sort(ChannelListViewModel.DEFAULT_SORT)
//                .build();
//        ChannelListViewModel channelsViewModel = new ViewModelProvider(this, factory).get(ChannelListViewModel.class);
//        ChannelListView channelListView = binding.getRoot().findViewById(R.id.channelListView);
//        ChannelListViewModelBinding.bind(channelsViewModel, channelListView, this);
//        channelListView.setChannelItemClickListener(channel -> {
//            // TODO - start channel activity
//        });

    }

}