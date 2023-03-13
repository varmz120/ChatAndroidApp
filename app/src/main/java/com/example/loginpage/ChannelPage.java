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
import io.getstream.chat.android.client.models.Message;
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
//      ChatClient client = ChatClient.instance();
      // Step 3 - Authenticate and connect the user





   }
}

