package com.example.loginpage;

import androidx.appcompat.app.AppCompatActivity;
import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.api.ChatClientConfig;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
        String api_key = "r9ckvmqw8u97";
        ChatClient client = new ChatClient.Builder(api_key,getApplicationContext()).build();

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
                validate(Name.getText().toString(),Password.getText().toString());
            }
        });
    }

    private void validate(String userUsername, String userPassword) {
        if ((userUsername == "Admin") && (userPassword == "1234")) {
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            startActivity(intent);
        } else {
            counter--;

            Info.setText("Number of attempts remaining: " + String.valueOf(counter));

            if (counter == 0) {
                Login.setEnabled(false);
            }
        }

    }
    public EditText getName() {
        return Name;
    }

    public void setName(EditText name) {
        Name = name;
    }

    public EditText getPassword() {
        return Password;
    }

    public void setPassword(EditText password) {
        Password = password;
    }

    public TextView getInfo() {
        return Info;
    }

    public void setInfo(TextView info) {
        Info = info;
    }

    public Button getLogin() {
        return Login;
    }

    public void setLogin(Button login) {
        Login = login;
    }

    public ImageView getProfile() {
        return Profile;
    }

    public void setProfile(ImageView profile) {
        Profile = profile;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

}