package com.example.loginpage;

/**
 * @author rynertan
 * @date 9/3/2023
 */


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toolbar;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.loginpage.databinding.ActivityMessageBinding;
import com.example.loginpage.utility.BundleDeliveryMan;
import com.example.loginpage.utility.CustomReplySend;
import com.example.loginpage.utility.CustomReplyViewHolderFactory;

import com.getstream.sdk.chat.viewmodel.MessageInputViewModel;
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel;
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel.Mode.Normal;
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel.Mode.Thread;
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel.State.NavigateUp;

import com.example.loginpage.utility.Database;

import java.net.MalformedURLException;

import io.getstream.chat.android.client.ChatClient;
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
    private static final Database mDatabase = Database.getInstance();
    private final BundleDeliveryMan mBundleDeliveryMan = BundleDeliveryMan.getInstance();
    public ThreadActivity() throws MalformedURLException {super(R.layout.activity_message);}
    public static Intent newIntent(Context context, ChannelClient channel) {
        classChannel = channel;
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
        Toolbar toolbar = findViewById(R.id.toolbar);
        ImageButton backButton = toolbar.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] channelId_messageId = classChannel.getChannelId().split("_");
                String parentChannelId = channelId_messageId[0];
                String channelType = getString(R.string.livestreamChannelType);
                ChannelClient channelClient = mBundleDeliveryMan.QuestionsPageBundle(channelType,parentChannelId);
                startActivity(ChannelActivity.newIntent(ThreadActivity.this,channelClient));
            }
        });
        String cid = getIntent().getStringExtra(CID_KEY);
        if (cid == null) {
            throw new IllegalStateException("Specifying a channel id is required when starting ThreadActivity");
        }

        // Create ViewModels for binding
        ViewModelProvider.Factory factory = new MessageListViewModelFactory.Builder()
                .cid(cid)
                .build();
        ViewModelProvider provider = new ViewModelProvider(this, factory);

        MessageListHeaderViewModel messageListHeaderViewModel = provider.get(MessageListHeaderViewModel.class);
        MessageListViewModel messageListViewModel = provider.get(MessageListViewModel.class);
        MessageInputViewModel messageInputViewModel = provider.get(MessageInputViewModel.class);

        // Step 2 - Bind the view and ViewModels, they are loosely coupled so it's easy to customize
        MessageListHeaderViewModelBinding.bind(messageListHeaderViewModel, binding.messageListHeaderView, this);
        MessageListViewModelBinding.bind(messageListViewModel, binding.messageListView, this, true);
        MessageInputViewModelBinding.bind(messageInputViewModel, binding.messageInputView, this);

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
        binding.messageInputView.setSendMessageHandler(new CustomReplySend(classChannel,mDatabase));
        //editing a message so stop replies
        binding.messageListView.setMessageEditHandler(messageInputViewModel::postMessageToEdit);      //go into edit mode

        messageListViewModel.getState().observe(this, state -> {
            if (state instanceof NavigateUp) {
                finish();
            }
        });

        // Step 6 - Handle back button behaviour correctly when you're in a thread
        MessageListHeaderView.OnClickListener backHandler = () -> {
            String[] channelId_messageId = classChannel.getChannelId().split("_");
            String parentChannelId = channelId_messageId[0];
            String channelType = getString(R.string.livestreamChannelType);
            ChannelClient channelClient = mBundleDeliveryMan.QuestionsPageBundle(channelType,parentChannelId);
            startActivity(ChannelActivity.newIntent(this,channelClient));
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


