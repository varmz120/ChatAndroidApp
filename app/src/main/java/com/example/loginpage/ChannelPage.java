package com.example.loginpage;

/**
 * @author saran
 * @date 20/2/2023
 */


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.loginpage.databinding.ActivityMainBinding;
import com.example.loginpage.databinding.ActivityChannelsBinding;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.api.models.FilterObject;
import io.getstream.chat.android.client.call.Call;
import io.getstream.chat.android.client.channel.ChannelClient;
import io.getstream.chat.android.client.logger.ChatLogLevel;
import io.getstream.chat.android.client.models.Channel;
import io.getstream.chat.android.client.models.Filters;
import io.getstream.chat.android.client.models.User;
import io.getstream.chat.android.offline.model.message.attachments.UploadAttachmentsNetworkType;
import io.getstream.chat.android.offline.plugin.configuration.Config;
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory;
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModel;
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModelBinding;
import io.getstream.chat.android.ui.channel.list.viewmodel.factory.ChannelListViewModelFactory;

import static java.util.Collections.singletonList;

public final class ChannelPage extends AppCompatActivity {

   protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      // Step 0 - inflate binding
      ActivityChannelsBinding binding = ActivityChannelsBinding.inflate(getLayoutInflater());
      setContentView(binding.getRoot());

      // Step 1 - Set up the OfflinePlugin for offline storage
//      StreamOfflinePluginFactory streamOfflinePluginFactory = new StreamOfflinePluginFactory(
//              new Config(
//                      true,
//                      true,
//                      true,
//                      UploadAttachmentsNetworkType.NOT_ROAMING
//              ),
//              getApplicationContext()
//      );
//
//      // Step 2 - Set up the client for API calls with the plugin for offline storage
//      ChatClient client = new ChatClient.Builder("52pc3gw25eq5", getApplicationContext())
//              .withPlugin(streamOfflinePluginFactory)
//              .logLevel(ChatLogLevel.ALL) // Set to NOTHING in prod
//              .build();
      ChatClient client = ChatClient.instance();
      String username = getIntent().getExtras().getString("username");
//      ChatClient client = ChatClient.instance();
      // Step 3 - Authenticate and connect the user
      User user = new User();
      user.setId("01");
      user.setName(username);
      // TODO make algorithm to generate JWT Token
      String tkn = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiMDEifQ.v2OJoe1Q1t5WWRsZ-ycN1FGDbcrAY8Vnr6e4ArWGkvk";
      client.connectUser(
              user,tkn
      ).enqueue(connectionResult->{
         if(connectionResult.isError()) System.out.println("Error connecting to client!" + connectionResult.error());
         ChannelClient channelClient = client.channel("messaging", "message_room");
         Map<String, Object> extraData = new HashMap<>();
         extraData.put("name","");
         List<String> memberIds = new LinkedList<>();
         channelClient.watch().enqueue(result -> {
                    if (result.isSuccess()) {
                       System.out.println("----------CHANNEL CONNECTION RESULT: " + result.data());
                       Channel newChannel = result.data();
                       startActivity(ChannelActivity.newIntent(this,newChannel));
                       // Step 4 - Set the channel list filter and order
                       // This can be read as requiring only channels whose "type" is "messaging" AND
                       // whose "members" include our "user.id"
//                       FilterObject filter = Filters.and(
//                               Filters.eq("type", "messaging"),
//                               Filters.in("members", singletonList(user.getId()))
//                       );
//
//                       ViewModelProvider.Factory factory = new ChannelListViewModelFactory.Builder()
//                               .filter(filter)
//                               .sort(ChannelListViewModel.DEFAULT_SORT)
//                               .build();
//
//                       ChannelListViewModel channelsViewModel =
//                               new ViewModelProvider(this, factory).get(ChannelListViewModel.class);
//
//                       // Step 5 - Connect the ChannelListViewModel to the ChannelListView, loose
//                       //          coupling makes it easy to customize
//                       ChannelListViewModelBinding.bind(channelsViewModel, binding.channelListView, this);
//                       binding.channelListView.setChannelItemClickListener(channel -> {
//                          startActivity(ChannelActivity.newIntent(this,channel));
//                       });
                    } else {
                       System.out.println("Unable to connect to channel!" + result.error());
                    }
                 });

      }
      );



   }
}

