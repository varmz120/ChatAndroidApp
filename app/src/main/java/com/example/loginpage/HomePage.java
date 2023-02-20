package com.example.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import androidx.appcompat.app.AppCompatActivity;
import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.models.Channel;
import io.getstream.chat.android.offline.plugin.configuration.Config;
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory;

/**
 * @author saran
 * @date 10/2/2023
 */

public class HomePage extends AppCompatActivity {
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.homepage);
      //TODO: Add functionality to handle join room
      Button btn = (Button) findViewById(R.id.createRoom);
      Bundle b = this.getIntent().getExtras();
      String username = b.getString("username");
      TextView txtView = (TextView) findViewById(R.id.usernameField);
      String welcomeMsg = "Welcome!" + username;
      txtView.setText(welcomeMsg);
      btn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            Bundle bundle = new Bundle();
            //bundle.putString("username",username);
            //start_client();
            Intent int1 = new Intent(HomePage.this,ChannelPage.class);
            //int1.putExtras(bundle);
            startActivity(int1);
         }
      });

   }
   private void start_client(){
      String api_key = "52pc3gw25eq5";
      boolean backGroundSyncEnable = true;
      boolean userPresence = true;
      Config config = new Config(backGroundSyncEnable,userPresence);
      StreamOfflinePluginFactory offlinePlugin = new StreamOfflinePluginFactory(config,getApplicationContext());
      new ChatClient.Builder(api_key,getApplicationContext()).withPlugin(offlinePlugin).build();
   }

}
