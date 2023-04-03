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
import com.example.loginpage.utility.Database;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import io.getstream.client.Client;
import io.getstream.core.http.Token;

import java.net.MalformedURLException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private Database mDatabase = Database.getInstance();
    //new
    private EditText Name;
    private EditText Password;
    private TextView Info;
    private Button Login;
    private Button Register;
    private ImageView Profile;
    private int counter = 5;
    private FirebaseAuth mAuth;
    private String role;
    private String api_key = "c6ys6m7794gr";

    private String secret_key = "4mx3y6jmz23j3y347me4kpar2kwrttf9br3d86tu4sf4e84ya6j3vpqpqm7u5968";
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
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("TAG", "signInWithEmail:success");
                                    // Creating Firebase User
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    //storing uid for streamUser to use later on
                                    String uid = user.getUid();
                                    //retrieve and set role from Database
                                    mDatabase.getRole(uid).addOnSuccessListener(dataSnapshot -> {
                                        if(dataSnapshot.exists()){
                                            role = dataSnapshot.getValue().toString();
                                            Log.d("TAG",role);
                                        }else{
                                            role = "user";
                                            Log.d("TAG",role);}
                                    });
                                    //Generating JWT token
                                    Token userToken = clientRef.frontendToken(uid);
                                    // creating intent to pass information for creating user on to HomePage.java
                                    Intent intent = new Intent(MainActivity.this,HomePage.class);
                                    intent.putExtra("userToken", userToken.toString());
                                    intent.putExtra("uid",uid);
                                    intent.putExtra("role",role);
                                    intent.putExtra("api_key",api_key);
                                    startActivity(intent);

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("TAG", "signInWithEmail:failure", task.getException());
                                    Toast.makeText(MainActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        //Move to the register page
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);

                startActivity(intent);
            }
        });
    }


//    private boolean validated(String userUsername, String userPassword) {
//        System.out.println(userUsername);
//        System.out.println(userPassword);
//        if ((Objects.equals(userUsername, "")) && (Objects.equals(userPassword, ""))) {
//            System.out.println("Validated!");
//            return true;
//        }
//        else {
//            counter--;
//            Info.setText("Number of attempts remaining: " + String.valueOf(counter));
//            if (counter == 0) {
//                Login.setEnabled(false);
//            }
//        }
//        return false;
//
//    }

}