package com.example.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.loginpage.utility.Database;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.channel.ChannelClient;
import io.getstream.chat.android.client.models.Channel;
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
   private Button Create;
   private String userRole;
   private FirebaseAuth mAuth;



   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.homepage);
      //TODO: Add functionality to handle join room
      //TODO: Add functionality to let the room creator create a 4 digit code when creating room
      Button btn = findViewById(R.id.createRoom);
      Button submit = findViewById(R.id.roomSubmit);
      Button viewMembers = findViewById(R.id.viewMembers);
      Button Create = findViewById(R.id.create);
      RoomCode = (EditText) findViewById(R.id.roomCode);
      String roomCode = RoomCode.getText().toString();
//      String b = this.getIntent().getExtras();
//      String username = b.getString("username");
      TextView txtView = findViewById(R.id.usernameField);
//      String welcomeMsg = "Welcome!" + username;
//      txtView.setText(welcomeMsg);

      btn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            start_client();
            startChannel(roomCode);
         }
      });
      Create.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            Intent intent = getIntent();
            String uid = intent.getStringExtra("uid");
            String userRole = intent.getStringExtra("userRole");
            String userToken = intent.getStringExtra("userToken");
            Log.d("TAG",uid);
            Log.d("TAG",userRole);
            Log.d("TAG",userToken);
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
            registerUser("Varma");
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
   private void registerUser(String username){
      User user = new User();
      user.setId("01");
      user.setName(username);
      // TODO make algorithm to generate JWT Token
      String tkn = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiMDEifQ.T8dm9FWij7dW4i0baXWFa7mb9Aixm2erfZNkij-WpWk";
      client.connectUser(
              user,tkn
      ).enqueue(connectionResult->{
         if(connectionResult.isError()) System.out.println("Error connecting to client!" + connectionResult.error());
              }
      );
   }

   private void startChannel(String createRoomCode){
      try{
         ChannelClient channelClient = client.channel("livestream", "message_room");
         startActivity(ChannelActivity.newIntent(this,channelClient,mDatabase));
         System.out.println(" Channel started successfully ");
      } catch (Exception e){
         System.out.println("Unable to start channel on HomePage: " + e);
      }

   }

   private void join_channel(){
//      ChannelClient channelClient = client.channel("messaging", "message_room");
      try{
         Map<String, Object> extraData = new HashMap<>();
         List<String> memberIds = new LinkedList<>();
         memberIds.add("02");
//            memberIds.add("tomasso");


         User user = new User();
         user.setId("02");
         user.setName("Varma");
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

         //adds
         // user to channel
//            channelClient.create(memberIds, extraData).enqueue(result -> {
//               if (result.isSuccess()){
//                  Channel newchannel = result.data();
//                  System.out.println(newchannel.getMembers());
//               }
//               else{
//                  System.out.println(result);
//               }
//            });

//            String id = channelClient.getCid();
//            Channel channel1 = new Channel();
//            channel1.setCid(id);
//            System.out.println("channel1 id: "+channel1.getCid());
//            System.out.println("users added");
//            System.out.println("Members: "+channel1.getMembers());
//            System.out.println("Members count: "+channel1.getMemberCount());
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
            // Handle result.error()
         }
      });


   }

}