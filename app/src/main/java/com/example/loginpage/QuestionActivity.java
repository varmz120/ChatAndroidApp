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
import com.example.loginpage.constants.Roles;
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
    ChatClient client = ChatClient.instance();

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
        //Inflate binding
        ActivityMessageBinding binding = ActivityMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView channelTitle = findViewById(R.id.toolbar_title);
        ImageButton backButton = toolbar.findViewById(R.id.back_button);
        ImageButton deleteChannel = toolbar.findViewById(R.id.deleteChannel);
        ImageButton filterButton = toolbar.findViewById(R.id.filteredQuestionsButton);

        // determines whether the user should be allowed to delete the channel
        deleteButtonFunctionality(deleteChannel);
        // determines whether the user should be allowed to filter the channel to view questions
        filterButtonFunctionality(filterButton);

        String channelid = classChannel.getChannelId();
        String channelCode = "Room : " + channelid.substring(11);

        channelTitle.setText(channelCode);

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

        // Customised View model and logic handling for sending messages
        CustomMessageSend.classChannel = classChannel;
        new CustomMessageSend(this);
        // Customised View Model for Messages
        binding.messageListView.setMessageViewHolderFactory(new CustomMessageViewHolderFactory());

    }
    private void deleteButtonFunctionality(ImageButton deleteChannel){
        classChannel.watch().enqueue(result -> {
            if (result.isSuccess()){
                Channel channel1 = result.data();
                if (client.getCurrentUser().getId().equals(channel1.getCreatedBy().getId())){
                    deleteChannel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
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
                    });

                } else {
                    deleteChannel.setVisibility(View.GONE);
                }


            }
            else{
                Log.i("ChannelActivity","Result of channel.watch() fdr delete channel button "+result);
            }
        });
    }
    private void filterButtonFunctionality(ImageButton filterButton){
        String currentUserId = client.getCurrentUser().getId();
        mDatabase.getRole(currentUserId).onSuccessTask(result->{
            if(result.exists()){
                String userRole = (String) result.getValue();
                if(userRole.equals(Roles.PROFESSOR)){
                    filterButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(QuestionActivity.this,FilteredActivity.class);
                            Bundle bundle = mBundleDeliveryMan.FilteredPageBundle(classChannel.getChannelId());
                            intent.putExtras(bundle);
                            view.getContext().startActivity(intent);
                        }
                    });
                } else {
                    filterButton.setVisibility(View.GONE);
                }
            }
            return null;
        });
    }

}