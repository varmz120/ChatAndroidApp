package com.example.loginpage;

import android.os.Bundle;
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
   private final Database mDatabase = new Database();
   private ChatClient client;
   private EditText RoomCode;
   private Bundle b;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      b = getIntent().getExtras();
      super.onCreate(savedInstanceState);
      setContentView(R.layout.homepage);
      //TODO: Add functionality to handle join room
      //TODO: Add functionality to let the room creator create a 4 digit code when creating room

      Button createRoomButton = findViewById(R.id.createRoom);
      Button submit = findViewById(R.id.roomSubmit);
      Button viewMembers = findViewById(R.id.viewMembers);
      RoomCode = (EditText) findViewById(R.id.roomCode);
      String roomCode = RoomCode.getText().toString();
      
      String userToken = b.getString("userToken");
      String uid = b.getString("uid");
      String role = b.getString("role");
      
      TextView txtView = findViewById(R.id.usernameField);
      String welcomeMsg = "Welcome!" + role;
      txtView.setText(welcomeMsg);
      createRoomButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            start_client();
            registerUser(uid,role,userToken);
         }
      });

      viewMembers.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            view_members(roomCode);
         }
      });

      submit.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            start_client();
            registerUser(uid,role,userToken);
            join_channel();

         }
      });

   }
   private void start_client(){
      try {
         String api_key = "akxdpvv55dsv";
         boolean backGroundSyncEnable = true;
         boolean userPresence = true;
         Config config = new Config(backGroundSyncEnable, userPresence);
         StreamOfflinePluginFactory offlinePlugin = new StreamOfflinePluginFactory(config, getApplicationContext());
         client = new ChatClient.Builder(api_key, getApplicationContext()).withPlugin(offlinePlugin).build();
         System.out.println(" Connected to client side ");
      }
      catch (Exception e){
         System.out.println("Error connecting to client object: " + e);
      }
   }

   private void registerUser(String uid, String role, String userToken){
      User streamUser = new User();
      streamUser.setId(uid);
      //streamUser.setRole(role);
      client.connectUser(
              streamUser,userToken
      ).enqueue(connectionResult->{
         if(connectionResult.isError()) {
            System.out.println("Error connecting to client!" + connectionResult.error());
         } else {
            startChannel();
         }
              }
      );
   }

   private void startChannel(){
      try{
         String channelId = "messageRoom";
         ChannelClient channelClient = client.channel("livestream", channelId);
         startActivity(ChannelActivity.newIntent(HomePage.this,channelClient,mDatabase));
         System.out.println(" Channel started successfully ");

      } catch (Exception e){
         System.out.println("Unable to start channel on HomePage: " + e);
      }

   }

   private void join_channel(){
      try{

         User user = new User();
         user.setId("02");
         user.setName("Varma");
         user.setId("admin");
         String tkn = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiMDIifQ.NAgqyl_yFdiymLKfHNchsgtH0a_lrz1mTSqtFLVU7UA";
         client.connectUser(user,tkn).enqueue(connectionResult->{
                    if(connectionResult.isError()) System.out.println("Error connecting to client!" + connectionResult.error());
                    else{
                       System.out.println("success");
                       ChannelClient channelClient = client.channel("livestream", "message_room");

                       client.channel("livestream","message_room").watch().enqueue(result -> {
                          if (result.isSuccess()){
                             Channel channel2 = result.data();
                             System.out.println(channel2.getMembers());
                             startActivity(ChannelActivity.newIntent(this,channelClient,mDatabase));
                          }
                          else{
                             System.out.println(result);
                          }
                       });
                    }
                 }
         );

      }
      catch(Exception e){
         System.out.println("cannot add users" + e);
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