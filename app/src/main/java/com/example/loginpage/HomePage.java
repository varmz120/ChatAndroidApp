package com.example.loginpage;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.loginpage.utility.Database;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.api.models.FilterObject;
import io.getstream.chat.android.client.api.models.QueryChannelRequest;
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
   private String api_key;
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      b = getIntent().getExtras();
      super.onCreate(savedInstanceState);
      setContentView(R.layout.homepage);

      Button createRoomButton = findViewById(R.id.createRoom);
      Button submit = findViewById(R.id.roomSubmit);
      Button viewMembers = findViewById(R.id.viewMembers);
      RoomCode = (EditText) findViewById(R.id.roomCode);
      String roomCode = RoomCode.getText().toString();
      
      String userToken = b.getString("userToken");
      String uid = b.getString("uid");
      //String role = b.getString("role");
      api_key = b.getString("api_key");
      LIVESTREAM = getString(R.string.livestreamChannelType);
      TextView txtView = findViewById(R.id.usernameField);
      //String welcomeMsg = "Welcome!" + role;
      //txtView.setText(welcomeMsg);
      createRoomButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            registerUser(uid,userToken);
         }
      });

      submit.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            startChannel();
            registerUser(uid,userToken);
         }
      });
      viewMembers.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            view_members(roomCode);
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
            startChannel();
         }
              }
      );
   }

   private void startChannel(){
      try{
         String channelId = "messageRoom";
         ChannelClient channelClient = client.channel(LIVESTREAM, channelId);
         startActivity(ChannelActivity.newIntent(HomePage.this,channelClient,mDatabase));
         Log.i("HomePage","Channel started successfully");

      } catch (Exception e){
         Log.e("HomePage","Unable to start channel on HomePage: " + e);
      }

   }

   private void join_channel(){
      try{

         ChannelClient channelClient = client.channel("livestream", "messageRoom");

         channelClient.watch().enqueue(result -> {
            if (result.isSuccess()){
               Channel channel2 = result.data();
               Log.i("HomePage","Members: "+channel2.getMembers());
               startActivity(ChannelActivity.newIntent(this,channelClient,mDatabase));
            }
            else{
               Log.i("HomePage", String.valueOf(result));
            }
         });
      }
      catch(Exception e){
         Log.e("HomePage","Unable to add users"+e);
      }
   }

   public void view_members(String roomCode){
      ChannelClient channelClient2 = client.channel("livestream", roomCode);
      channelClient2.watch().enqueue(result -> {
         if (result.isSuccess()) {
            Channel channel = result.data();
         } else {
         }
      });


   }

}