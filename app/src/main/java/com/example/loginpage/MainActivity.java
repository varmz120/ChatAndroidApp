package com.example.loginpage;

import androidx.appcompat.app.AppCompatActivity;
import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.offline.plugin.configuration.Config;
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    private EditText Name;
    private EditText Password;
    private TextView Info;
    private Button Login;
    private ImageView Profile;
    private int counter = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }



    private void init(){
        Name = (EditText) findViewById(R.id.ETUsername);
        Password = (EditText) findViewById(R.id.ETPassword);
        Info = (TextView) findViewById(R.id.TVAttemptsLeft);
        Login = (Button) findViewById(R.id.LoginButton);
        Profile = (ImageView) findViewById(R.id.imageView);
        Info.setText("Number of attempts remaining: 5");

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = Name.getText().toString();
                String password = Password.getText().toString();
                if(validated(username,password)){
                    Bundle b = new Bundle();
                    b.putString("username",username);
                    Intent intent = new Intent(MainActivity.this, HomePage.class);
                    intent.putExtras(b);
                    startActivity(intent);
                }
            }
        });
    }


    private boolean validated(String userUsername, String userPassword) {
        System.out.println(userUsername);
        System.out.println(userPassword);
        if ((Objects.equals(userUsername, "")) && (Objects.equals(userPassword, ""))) {
            System.out.println("Validated!");
            return true;
        }
        else {
            counter--;

            Info.setText("Number of attempts remaining: " + String.valueOf(counter));

            if (counter == 0) {
                Login.setEnabled(false);
            }
        }
        return false;

    }

}