package com.example.loginpage;

/**
 * @author saran
 * @date 20/2/2023
 */


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.loginpage.databinding.ActivityMessageBinding;
import com.example.loginpage.utility.BundleDeliveryMan;
import com.example.loginpage.utility.CustomMessageSend;
import com.example.loginpage.utility.CustomMessageViewHolderFactory;
import com.example.loginpage.utility.CustomSuggestionListViewHolderFactory;
import com.example.loginpage.utility.Database;
import com.getstream.sdk.chat.viewmodel.MessageInputViewModel;
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel;
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel.Mode.Normal;
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel.Mode.Thread;
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel.State.NavigateUp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


import java.net.MalformedURLException;
import java.util.List;

import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.channel.ChannelClient;
import io.getstream.chat.android.client.models.Channel;
import io.getstream.chat.android.client.models.Message;
import io.getstream.chat.android.client.models.User;
import io.getstream.chat.android.ui.message.input.MessageInputView;
import io.getstream.chat.android.ui.message.input.transliteration.DefaultStreamTransliterator;
import io.getstream.chat.android.ui.message.input.viewmodel.MessageInputViewModelBinding;
import io.getstream.chat.android.ui.message.list.header.MessageListHeaderView;
import io.getstream.chat.android.ui.message.list.header.viewmodel.MessageListHeaderViewModel;
import io.getstream.chat.android.ui.message.list.header.viewmodel.MessageListHeaderViewModelBinding;
import io.getstream.chat.android.ui.message.list.viewmodel.MessageListViewModelBinding;
import io.getstream.chat.android.ui.message.list.viewmodel.factory.MessageListViewModelFactory;
import kotlin.coroutines.Continuation;

public class ChannelActivity extends AppCompatActivity {

    private final static String CID_KEY = "shk4bq5vqttmrfush2e98d9d83n7bz5cwj8ws4dtxe9xby3nw8hgsr5vjmr4qcms";
    private static ChannelClient classChannel;
    private static final Database mDatabase = Database.getInstance();
    private final BundleDeliveryMan mBundleDeliveryMan = BundleDeliveryMan.getInstance();

    public ChannelActivity() throws MalformedURLException {
        super(R.layout.activity_message);
    }
    public static Intent newIntent(Context context, ChannelClient channelClient) {
        classChannel = channelClient;
        final Intent intent = new Intent(context, ChannelActivity.class);
        intent.putExtra(CID_KEY, channelClient.getCid());
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Step 0 - inflate binding
        ActivityMessageBinding binding = ActivityMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.messageListHeaderView.setTitle(classChannel.getChannelId());
        String cid = getIntent().getStringExtra(CID_KEY);
        if (cid == null) {
            throw new IllegalStateException("Specifying a channel id is required when starting ChannelActivity");
        }

//        mDatabase.getMessagesReference(classChannel.getChannelId()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//               if(snapshot.exists()){
//                   finish();
//                   startActivity(getIntent());
//               }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                System.out.println("Error listening to change under channel in Firebase: " + error);
//            }
//        });

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
        binding.messageListView.setMessageViewHolderFactory(new CustomMessageViewHolderFactory(mDatabase));
        binding.messageInputView.setSuggestionListViewHolderFactory(new CustomSuggestionListViewHolderFactory());
        binding.messageInputView.setSendMessageHandler(new CustomMessageSend(mDatabase,classChannel));


        // Step 5 - Handle navigate up state
        messageListViewModel.getState().observe(this, state -> {
            if (state instanceof NavigateUp) {
                finish();
            }
        });

        // Step 6 - Handle back button behaviour correctly when you're in a thread
        MessageListHeaderView.OnClickListener backHandler = () -> {
            Intent intent = new Intent(ChannelActivity.this,HomePage.class);
            Bundle b = mBundleDeliveryMan.HomePageBundle(ChatClient.instance().getCurrentUser().getId());
            intent.putExtras(b);
            startActivity(intent);
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


