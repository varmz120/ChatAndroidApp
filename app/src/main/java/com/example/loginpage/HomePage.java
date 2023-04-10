package com.example.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginpage.utility.BundleDeliveryMan;
import com.example.loginpage.utility.Database;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Member;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.api.models.FilterObject;
import io.getstream.chat.android.client.api.models.QueryChannelRequest;
import io.getstream.chat.android.client.api.models.QueryChannelsRequest;
import io.getstream.chat.android.client.api.models.querysort.QuerySortByField;
import io.getstream.chat.android.client.api.models.querysort.QuerySorter;
import io.getstream.chat.android.client.channel.ChannelClient;
import io.getstream.chat.android.client.models.Channel;
import io.getstream.chat.android.client.models.CustomObject;
import io.getstream.chat.android.client.models.Filters;
import io.getstream.chat.android.client.models.Message;
import io.getstream.chat.android.client.models.User;
import io.getstream.chat.android.offline.plugin.configuration.Config;
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory;

/**
 * @author saran
 * @date 10/2/2023
 */

public class HomePage extends AppCompatActivity {
   private final Database mDatabase = Database.getInstance();
   private final ChatClient client = ChatClient.instance();
   private EditText RoomCode;
   private Bundle b;
   private String LIVESTREAM;
   private final BundleDeliveryMan mDeliveryMan = BundleDeliveryMan.getInstance();
   private String api_key;

   public HomePage() throws MalformedURLException {
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      b = getIntent().getExtras();
      super.onCreate(savedInstanceState);
      setContentView(R.layout.homepage);

      Button createRoomButton = findViewById(R.id.createRoom);
      Button submit = findViewById(R.id.roomSubmit);
      Button settingsButton = findViewById(R.id.settingsButton);

      String userToken = b.getString("userToken");
      String uid = b.getString("uid");
      api_key = b.getString("api_key");
      LIVESTREAM = getString(R.string.livestreamChannelType);

      createRoomButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            registerUser(uid,userToken);
         }
      });

      submit.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            registerUser_another(uid,userToken);
         }
      });
      settingsButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            Intent intentSettings = new Intent(HomePage.this,SettingActivity.class);
            Bundle settingsPageBundle = mDeliveryMan.SettingsPageBundle(uid);
            intentSettings.putExtras(settingsPageBundle);
            startActivity(intentSettings);
         }
      });
   }

   private void registerUser(String uid, String userToken){
      User streamUser = new User();
      streamUser.setId(uid);
      client.connectUser(
              streamUser,userToken
      ).enqueue(connectionResult->{
         if(connectionResult.isError()) {
            Log.e("HomePage","Error connecting to client."+connectionResult.error());
         } else {
            String createRoomCode=String.valueOf(randomInteger());
            startChannel(createRoomCode);
            System.out.println("successfully created a room with code:"+createRoomCode);
         }
              }
      );
   }
   private void registerUser_another(String uid, String userToken){
      User streamUser = new User();
      streamUser.setId(uid);
      client.connectUser(
              streamUser,userToken
      ).enqueue(connectionResult->{
                 if(connectionResult.isError()) {
                    System.out.println("Error connecting to client!" + connectionResult.error());
                 } else {

                    RoomCode = (EditText) findViewById(R.id.roomCode);
                    String roomCode = RoomCode.getText().toString();
                    String channelId = "messageRoom"+roomCode;
                    FilterObject filter = Filters.and(
                            Filters.eq("type", "livestream"),
                            Filters.in("id", Arrays.asList(channelId))
                    );

                    int offset = 0;
                    int limit = 10;
                    QuerySortByField<Channel> sort = QuerySortByField.descByName("last_message_at");
                    int messageLimit = 0;
                    int memberLimit = 0;

                    QueryChannelsRequest request = new QueryChannelsRequest(filter, offset, limit, sort, messageLimit, memberLimit)
                            .withWatch()
                            .withState();

                    client.queryChannels(request).enqueue(result -> {
                       if (result.isSuccess()) {
                          List<Channel> channels = result.data();
                          System.out.println(channels);
                          System.out.println("Channels printed");
                          if(channels.size()==0){
                             Log.w("TAG", "channel does not exist");
                             Toast.makeText(HomePage.this, "channel does not exist",
                                     Toast.LENGTH_SHORT).show();
                          }
                          if(channels.size()!=0){
                             startChannel(roomCode);
                          }
                       } else {
                          System.out.println(result);
                       }
                    });






                 }
              }
      );
   }

   private void startChannel(String createRoomCode){
      try{
         String channelId = "messageRoom"+createRoomCode;
         ChannelClient channelClient = client.channel(LIVESTREAM, channelId);
         startActivity(ChannelActivity.newIntent(HomePage.this,channelClient));
         Log.i("HomePage","Channel started successfully");

      } catch (Exception e){
         Log.e("HomePage","Unable to start channel on HomePage: " + e);
      }

   }

   //method to create a random 4 digit number for room creating purposes
   private int randomInteger(){
      Random rand = new Random();
      int randomNumber = rand.nextInt(9000) + 1000;
      return randomNumber;

   }


}