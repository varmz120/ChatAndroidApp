package com.example.loginpage;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import io.getstream.chat.android.client.ChatClient;
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
        // Init ViewModel
//        ViewModelProvider.Factory factory = new MessageListViewModelFactory.Builder()
//                .cid("messaging:123")
//                .build();
//        ViewModelProvider provider = new ViewModelProvider(this, factory);
//        MessageListViewModel viewModel = provider.get(MessageListViewModel.class);

// Bind View and ViewModel
//        MessageListView messageListView = new MessageListView(getApplicationContext());
//        LifecycleOwner lifecycleOwner = new LifecycleOwner() {
//            @NonNull
//            @Override
//            public Lifecycle getLifecycle() {
//                return null;
//            }
//        };
//        MessageListViewModelBinding.bind(viewModel, messageListView, lifecycleOwner,false);


    }

}