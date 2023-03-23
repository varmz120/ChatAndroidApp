package com.example.loginpage;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.loginpage.utility.Database;
import com.example.loginpage.utility.jwtgenerate;

import androidx.appcompat.app.AppCompatActivity;
import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.channel.ChannelClient;
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
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.homepage);
      //TODO: Add functionality to handle join room
      //TODO: Add functionality to let the room creator create a 4 digit code when creating room
      Button btn = findViewById(R.id.createRoom);
      Bundle b = this.getIntent().getExtras();
      String username = b.getString("username");
      TextView txtView = findViewById(R.id.usernameField);
      String welcomeMsg = "Welcome!" + username;
      txtView.setText(welcomeMsg);
      btn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            start_client();
            registerUser(username);
//            Intent int1 = new Intent(HomePage.this,ChannelPage.class);
//            int1.putExtra("username",username);
//            startActivity(int1);
            startChannel();
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
      jwtgenerate generator = new jwtgenerate();
      String tkn = generator.tokenGenerator(findViewById(R.id.ETUsername).toString(),findViewById(R.id.ETPassword).toString());
      System.out.println(tkn);
      Log.d("JWT TOKEN",tkn);
      // TODO make algorithm to generate JWT Token
      client.connectUser(
              user,tkn
      ).enqueue(connectionResult->{
                 if(connectionResult.isError()) System.out.println("Error connecting to client!" + connectionResult.error());
              }
      );
   }
   private void startChannel(){
      try{
         ChannelClient channelClient = client.channel("messaging", "message_room");
//                 Map<String, Object> extraData = new HashMap<>();
//                 extraData.put("name","");
//                 List<String> memberIds = new LinkedList<>();
         startActivity(ChannelActivity.newIntent(this,channelClient,mDatabase));
         System.out.println(" Channel started successfully ");
      } catch (Exception e){
         System.out.println("Unable to start channel on HomePage: " + e);
      }

   }

}
