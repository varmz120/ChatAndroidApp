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

import com.example.loginpage.utility.BundleDeliveryMan;
import com.example.loginpage.utility.Database;
import com.example.loginpage.utility.LoadingDialogFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.offline.plugin.configuration.Config;
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory;

import java.net.MalformedURLException;

public class MainActivity extends AppCompatActivity {
    private final Database mDatabase = Database.getInstance();
    //new
    private EditText Name;
    private EditText Password;
    private TextView Info;
    private Button Login;
    private Button Register;
    private ImageView Profile;

    private final BundleDeliveryMan mBundleDeliveryMan = BundleDeliveryMan.getInstance();
    private FirebaseAuth mAuth;

    public LoadingDialogFragment loadingDialogFragment = new LoadingDialogFragment();



    public MainActivity() throws MalformedURLException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        Name = (EditText) findViewById(R.id.ETUsername);
        Password = (EditText) findViewById(R.id.ETPassword);
        Login = (Button) findViewById(R.id.LoginButton);
        Register = (Button) findViewById(R.id.Register);
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = Name.getText().toString();
                String password = Password.getText().toString();
                mAuth = FirebaseAuth.getInstance();
                try {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        if (!loadingDialogFragment.isAdded()) {
                                            loadingDialogFragment.show(getSupportFragmentManager(), "loader");
                                        }
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("TAG", "signInWithEmail:success");
                                        // Creating Firebase User
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        //storing uid for streamUser to use later on
                                        String uid = user.getUid();
                                        // creating intent to pass information for creating user on to HomePage.java
                                        start_client();
                                        Intent intent = new Intent(MainActivity.this, HomePage.class);
                                        Bundle bundle = mBundleDeliveryMan.HomePageBundle(uid);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("TAG", "signInWithEmail:failure", task.getException());
                                        Toast.makeText(MainActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }catch(IllegalArgumentException e){
                    Toast.makeText(getApplicationContext(), "Verify that you have entered valid non-null values!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Move to the register page
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!loadingDialogFragment.isAdded()) {
                    loadingDialogFragment.show(getSupportFragmentManager(), "loader");
                }
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);

                startActivity(intent);
            }
        });
    }
    private void start_client(){
        try {
            boolean backGroundSyncEnable = true;
            boolean userPresence = true;
            Config config = new Config(backGroundSyncEnable, userPresence);
            StreamOfflinePluginFactory offlinePlugin = new StreamOfflinePluginFactory(config, getApplicationContext());
            new ChatClient.Builder(mBundleDeliveryMan.deliverAPI(), getApplicationContext()).withPlugin(offlinePlugin).build();
            Log.i("MainActivity","Successfully connected to client");
        }
        catch (Exception e){
            Log.e("MainActivity","Error connecting to client object"+e);
        }
    }


}