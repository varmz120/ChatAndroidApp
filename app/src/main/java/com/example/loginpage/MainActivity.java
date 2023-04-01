package com.example.loginpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.loginpage.utility.JwtGenerator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.getstream.chat.android.client.ChatClient;
import io.getstream.client.Client;
import io.getstream.core.http.Token;
import io.getstream.chat.android.client.models.User;
import io.getstream.chat.android.offline.plugin.configuration.Config;
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory;

import java.net.MalformedURLException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    //new
    ChatClient chatClient;
    private EditText Name;
    private EditText Password;
    private TextView Info;
    private Button Login;
    private Button Generate;
    private Button Register;
    private ImageView Profile;
    private int counter = 5;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private String userRole;
    private String api_key = "akxdpvv55dsv";
    private String secret_key = "shk4bq5vqttmrfush2e98d9d83n7bz5cwj8ws4dtxe9xby3nw8hgsr5vjmr4qcms";
    Client clientRef = Client.builder(api_key,secret_key).build();

    public MainActivity() throws MalformedURLException {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }

    private void init() {

        Generate = (Button) findViewById(R.id.Generate);
        Name = (EditText) findViewById(R.id.ETUsername);
        Password = (EditText) findViewById(R.id.ETPassword);
        Info = (TextView) findViewById(R.id.TVAttemptsLeft);
        Login = (Button) findViewById(R.id.LoginButton);
        Profile = (ImageView) findViewById(R.id.imageView);
        Register = (Button) findViewById(R.id.Register);
        Info.setText("Number of attempts remaining: 5");

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = Name.getText().toString();
                String password = Password.getText().toString();
                mAuth = FirebaseAuth.getInstance();
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    boolean backGroundSyncEnable = true;
                                    boolean userPresence = true;
                                    Config config = new Config(backGroundSyncEnable, userPresence);
                                    StreamOfflinePluginFactory offlinePlugin = new StreamOfflinePluginFactory(config, getApplicationContext());
                                    chatClient = new ChatClient.Builder(api_key, getApplicationContext()).withPlugin(offlinePlugin).build();

                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("TAG", "signInWithEmail:success");
                                    // Creating Firebase User
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    // Get the id of the user
                                    String firebaseUserId = user.getUid();
                                    // Send the Firebase user ID to your server to obtain a JWT token for GetStream Chat
                                    //Token userToken = client.frontendToken(firebaseUserId);
                                    //Log.d("TAG", userToken.toString());
                                    //storing uid for streamUser to use later on
                                    String uid = firebaseUserId;
                                    // create new user
                                    User streamUser = new User();
                                    streamUser.setId(uid);
                                    //retrieve and set role from Database
                                    mDatabase.child("users").child(uid).child("role").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                        @Override
                                        public void onSuccess(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                streamUser.setRole(dataSnapshot.getValue(String.class));
                                            } else {
                                                Log.i("firebase", "Role not found");
                                            }
                                        }
                                    });
                                    Token userToken = clientRef.frontendToken(uid);
//                                  Log.d("TAG", userToken.toString());
                                    chatClient.connectUser(streamUser, userToken.toString()).enqueue(result -> {
                                        if (result.isSuccess()) {
                                            // Logged in
                                            User userRes = result.data().getUser();
                                            String connectionId = result.data().getConnectionId();
                                            Log.d("TAG","Sucessful login");
                                            Intent intent = new Intent(MainActivity.this,HomePage.class);
                                            startActivity(intent);
                                        } else {
                                            // Handle result.error()
                                            Log.d("TAG",result.error().toString());
                                            Log.d("TAG",userToken.toString());
                                        }
                                    });

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("TAG", "signInWithEmail:failure", task.getException());
                                    Toast.makeText(MainActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }

                            ;
                        });
            }
        });

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, com.example.loginpage.Register.class);
                startActivity(intent);
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