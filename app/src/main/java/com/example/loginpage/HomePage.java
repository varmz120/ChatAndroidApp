package com.example.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import androidx.appcompat.app.AppCompatActivity;

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
            bundle.putString("username",username);
            Intent int1 = new Intent(HomePage.this,MessageActivity.class);
            int1.putExtras(bundle);
            startActivity(int1);
         }
      });

   }

}
