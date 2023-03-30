package com.example.loginpage;

/**
 * @author rynertan
 * @date 9/3/2023
 */


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.loginpage.databinding.ActivityMessageBinding;
import com.example.loginpage.utility.CustomReplyViewHolderFactory;

import com.getstream.sdk.chat.viewmodel.MessageInputViewModel;
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel;
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel.Mode.Normal;
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel.Mode.Thread;
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel.State.NavigateUp;

import com.example.loginpage.utility.Database;

import io.getstream.chat.android.client.channel.ChannelClient;
import io.getstream.chat.android.client.models.Message;
import io.getstream.chat.android.ui.message.input.viewmodel.MessageInputViewModelBinding;
import io.getstream.chat.android.ui.message.list.header.MessageListHeaderView;
import io.getstream.chat.android.ui.message.list.header.viewmodel.MessageListHeaderViewModel;
import io.getstream.chat.android.ui.message.list.header.viewmodel.MessageListHeaderViewModelBinding;
import io.getstream.chat.android.ui.message.list.viewmodel.MessageListViewModelBinding;
import io.getstream.chat.android.ui.message.list.viewmodel.factory.MessageListViewModelFactory;

public class ThreadActivity extends AppCompatActivity {

    private final static String CID_KEY = "wg8ebbdfv74pkrfaqstha627gs3s96s7smr7ehwseaep5v5sn2z56gn5e9auuwhn";
    private static ChannelClient classChannel;
    private static Database mDatabase;
    public ThreadActivity(){super(R.layout.activity_message);}
    public static Intent newIntent(Context context, ChannelClient channel, Database database) {
        classChannel = channel;
        mDatabase = database;
        final Intent intent = new Intent(context, ThreadActivity.class);
        intent.putExtra(CID_KEY, channel.getCid());
        return intent;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Step 0 - inflate binding
        ActivityMessageBinding binding = ActivityMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String cid = getIntent().getStringExtra(CID_KEY);
        System.out.println(cid);
        //String cid = getIntent().getStringExtra(CID_KEY);
        if (cid == null) {
            throw new IllegalStateException("Specifying a channel id is required when starting ChannelActivity");
        }

        // Step 1 - Create three separate ViewModels for the views so it's easy
        //          to customize them individually
        ViewModelProvider.Factory factory = new MessageListViewModelFactory.Builder()
                .cid(cid)
                .build();
        ViewModelProvider provider = new ViewModelProvider(this, factory);

        MessageListHeaderViewModel messageListHeaderViewModel = provider.get(MessageListHeaderViewModel.class);
        MessageListViewModel messageListViewModel = provider.get(MessageListViewModel.class);
        MessageInputViewModel messageInputViewModel = provider.get(MessageInputViewModel.class);

        // TODO set custom Imgur attachment factory

        // Step 2 - Bind the view and ViewModels, they are loosely coupled so it's easy to customize
        MessageListHeaderViewModelBinding.bind(messageListHeaderViewModel, binding.messageListHeaderView, this);
        MessageListViewModelBinding.bind(messageListViewModel, binding.messageListView, this, true);
        MessageInputViewModelBinding.bind(messageInputViewModel, binding.messageInputView, this);

        // Step 3 - Let both MessageListHeaderView and MessageInputView know when we open a thread
        messageListViewModel.getMode().observe(this, mode -> {
            if (mode instanceof Thread) {
                Message parentMessage = ((Thread) mode).getParentMessage();
                messageListHeaderViewModel.setActiveThread(parentMessage);
                messageInputViewModel.setActiveThread(parentMessage);
            } else if (mode instanceof Normal) {
                messageListHeaderViewModel.resetThread();
                messageInputViewModel.resetThread();
            }
        });
        // Customised View Model for Messages
        binding.messageListView.setMessageViewHolderFactory(new CustomReplyViewHolderFactory(mDatabase));


        // Step 4 - Let the message input know when we are editing a message
        // TODO: Add message filtering

        binding.messageListView.setRepliesEnabled(false);                                             //editing a message so stop replies
        binding.messageListView.setMessageEditHandler(messageInputViewModel::postMessageToEdit);      //go into edit mode
        binding.messageListView.setMessageReplyHandler((parent,message)-> {                           //specify parent and child in replyhandler
            System.out.println(message);
            System.out.println(parent);
            System.out.println("Breakd point");
            // TODO Logic handling for replies
        });
        binding.messageListView.setThreadStartHandler((result)->{
            System.out.println("THREAD STARTED!");
            System.out.println(result);
        });
        binding.messageInputView.setOnSendButtonClickListener(()->{

        });
        // Step 5 - Handle navigate up state
        messageListViewModel.getState().observe(this, state -> {
            if (state instanceof NavigateUp) {
                finish();
            }
        });

        // Step 6 - Handle back button behaviour correctly when you're in a thread
        MessageListHeaderView.OnClickListener backHandler = () -> {
            Intent int1 = new Intent(ThreadActivity.this,ChannelActivity.class);        //moves user back to ChannelActivity
            Bundle b = new Bundle();                                                                 //prepare to transfer information as well
            b.putString("username","Admin");                                                         //add username:Admin as something I want to pass back
            int1.putExtras(b);                                                                       //pack information into the Intent
            startActivity(int1);
            messageListViewModel.onEvent(MessageListViewModel.Event.BackButtonPressed.INSTANCE);
        };
        binding.messageListHeaderView.setBackButtonClickListener(backHandler);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backHandler.onClick();

            }
        });
    }
}


