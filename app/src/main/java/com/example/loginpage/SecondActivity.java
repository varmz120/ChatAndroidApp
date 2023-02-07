package com.example.loginpage;

import androidx.annotation.ContentView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.api.models.FilterObject;
import io.getstream.chat.android.client.models.Filters;
import io.getstream.chat.android.client.models.User;
import io.getstream.chat.android.client.token.TokenProvider;
import io.getstream.chat.android.ui.channel.list.ChannelListView;
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModel;
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModelBinding;
import io.getstream.chat.android.ui.channel.list.viewmodel.factory.ChannelListViewModelFactory;

import android.os.Bundle;
import android.view.View;

import com.example.loginpage.databinding.ActivityMainBinding;

import java.util.Collections;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("Starting Activity 2");
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Bundle data = this.getIntent().getExtras();
        String api_key = data.getString("api_key");
        String secret_key = data.getString("user_chat_token");
        String username = data.getString("username");
        System.out.println(username);
        System.out.println(api_key);
        ChatClient client = new ChatClient.Builder(api_key,getApplicationContext()).build();
        User u = new User();
        u.setId("01");
        u.setName(username);
        TokenProvider tokenProvider = new TokenProvider() {
            @NonNull
            @Override
            public String loadToken() {
                return provideToken(u.getName());
            }
        };
        client.connectUser(u,"user_token_123").enqueue(result->{
            if (result.isSuccess()) {
                // Logged in
                User userRes = result.data().getUser();
                String connectionId = result.data().getConnectionId();
                System.out.println("User connected! " + connectionId);
            } else {
                System.out.println("User was not able to login!");
            }
        });

        FilterObject filter = Filters.and(
                Filters.eq("type","messaging"),
                Filters.in("members", Collections.singletonList(u.getId()))
        );

        ViewModelProvider.Factory factory = new ChannelListViewModelFactory.Builder()
                .filter(filter)
                .sort(ChannelListViewModel.DEFAULT_SORT)
                .build();
        ChannelListViewModel channelsViewModel = new ViewModelProvider(this, factory).get(ChannelListViewModel.class);
//        ChannelListView channelListView = binding.getRoot().findViewById(R.id.channelListView);
//        ChannelListViewModelBinding.bind(channelsViewModel, channelListView, this);
//        channelListView.setChannelItemClickListener(channel -> {
//            // TODO - start channel activity
//        });

    }
    private String provideToken(String str){
        return str+str;
    }
}