package com.example.loginpage;

/**
 * @author saran
 * @date 20/2/2023
 */


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;


import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.loginpage.constants.Environment;
import com.example.loginpage.databinding.ActivityMessageBinding;

import com.example.loginpage.utility.BundleDeliveryMan;
import com.example.loginpage.customviews.CustomMessageSend;
import com.example.loginpage.customviews.CustomMessageViewHolderFactory;

import com.example.loginpage.utility.Database;
import com.example.loginpage.utility.LoadingDialogFragment;
import com.getstream.sdk.chat.viewmodel.MessageInputViewModel;
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel;
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel.Mode.Normal;
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel.Mode.Thread;
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel.State.NavigateUp;



import java.net.MalformedURLException;


import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.channel.ChannelClient;

import io.getstream.chat.android.client.models.Channel;
import io.getstream.chat.android.client.models.Message;

import io.getstream.chat.android.ui.message.list.header.MessageListHeaderView;
import io.getstream.chat.android.ui.message.list.header.viewmodel.MessageListHeaderViewModel;
import io.getstream.chat.android.ui.message.list.header.viewmodel.MessageListHeaderViewModelBinding;
import io.getstream.chat.android.ui.message.list.viewmodel.MessageListViewModelBinding;
import io.getstream.chat.android.ui.message.list.viewmodel.factory.MessageListViewModelFactory;


public class QuestionActivity extends AppCompatActivity {

    private static ChannelClient classChannel;
    private static final Database mDatabase = Database.getInstance();
    private final BundleDeliveryMan mBundleDeliveryMan = BundleDeliveryMan.getInstance();

    public LoadingDialogFragment loadingDialogFragment = new LoadingDialogFragment();

    public QuestionActivity() throws MalformedURLException {
        super(R.layout.activity_message);
    }
    public static Intent newIntent(Context context, ChannelClient channelClient) {
        classChannel = channelClient;
        final Intent intent = new Intent(context, QuestionActivity.class);
        intent.putExtra(Environment.CID_KEY, channelClient.getCid());
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Step 0 - inflate binding
        ActivityMessageBinding binding = ActivityMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Toolbar toolbar = findViewById(R.id.toolbar);


        TextView channelTitle = findViewById(R.id.toolbar_title);
        String channelid = classChannel.getChannelId();
        String channelCode = channelid.substring(11);

        channelTitle.setText("Room : "+channelCode);
        ImageButton backButton = toolbar.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!loadingDialogFragment.isAdded()) {
                    loadingDialogFragment.show(getSupportFragmentManager(), "loader");
                }
                Intent intent = new Intent(QuestionActivity.this,HomePage.class);
                Bundle b = mBundleDeliveryMan.HomePageBundle(ChatClient.instance().getCurrentUser().getId());
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        ImageButton deleteChannel = toolbar.findViewById(R.id.deleteChannel);
        deleteChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ChatClient client = ChatClient.instance();

                classChannel.watch().enqueue(result -> {
                    if (result.isSuccess()){
                        Channel channel1 = result.data();

                        if (client.getCurrentUser().getId().equals(channel1.getCreatedBy().getId())){
                            classChannel.delete().enqueue(result1 -> {
                                if (result1.isSuccess()){
                                    if (!loadingDialogFragment.isAdded()) {
                                        loadingDialogFragment.show(getSupportFragmentManager(), "loader");
                                    }
                                    Log.i("ChannelActivity","Channel has been deleted");
                                    Toast.makeText(getApplicationContext(), "The channel has been deleted.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(QuestionActivity.this,HomePage.class);
                                    Bundle b = mBundleDeliveryMan.HomePageBundle(ChatClient.instance().getCurrentUser().getId());
                                    intent.putExtras(b);
                                    startActivity(intent);
                                }
                                else{
                                    Log.i("ChannelActivity","Result of channel.delete()"+result1);
                                }
                            });
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "The user does not have permission to delete.", Toast.LENGTH_SHORT).show();
                            Log.e("ChannelActivity","User does not have permission to delete");
                        }
                    }
                    else{
                        Log.i("ChannelActivity","Result of channel.watch()"+result);
                    }
                });

            }
        });

        String cid = getIntent().getStringExtra(Environment.CID_KEY);
        if (cid == null) {
            throw new IllegalStateException("Specifying a channel id is required when starting ChannelActivity");
        }


        ViewModelProvider.Factory factory = new MessageListViewModelFactory.Builder()
                .cid(cid)
                .build();
        ViewModelProvider provider = new ViewModelProvider(this, factory);

        MessageListHeaderViewModel messageListHeaderViewModel = provider.get(MessageListHeaderViewModel.class);
        MessageListViewModel messageListViewModel = provider.get(MessageListViewModel.class);

        // Bind the view and ViewModels, they are loosely coupled so it's easy to customize
        MessageListHeaderViewModelBinding.bind(messageListHeaderViewModel, binding.messagesHeaderView, this);
        MessageListViewModelBinding.bind(messageListViewModel, binding.messageListView, this, true);

        // Customised View Model for Messages
        CustomMessageSend.classChannel = classChannel;
        new CustomMessageSend(this);

        binding.messageListView.setMessageViewHolderFactory(new CustomMessageViewHolderFactory());

    }

}