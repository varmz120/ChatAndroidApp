package com.example.loginpage;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import androidx.appcompat.app.AlertDialog;
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
   private AlertDialog.Builder createRoomPage;
   private AlertDialog createRoomDialog;

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
            Intent int1 = new Intent(HomePage.this,ChannelPage.class);
            int1.putExtra("username",username);
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
