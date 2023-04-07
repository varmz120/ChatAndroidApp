package com.example.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.example.loginpage.utility.BundleDeliveryMan;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import java.net.MalformedURLException;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author saran
 * @date 6/4/2023
 */

public class SettingActivity extends AppCompatActivity {
    private MaterialButton changeUsernameButton;
    private final BundleDeliveryMan mDeliveryMan = BundleDeliveryMan.getInstance();
    private MaterialButton changePasswordButton;
    private MaterialButton historyButton;
    private String newEmail;
    private String newPassword;


    public SettingActivity() throws MalformedURLException {
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle userData = getIntent().getExtras();
        String uid = userData.getString("uid");
        setContentView(R.layout.activity_settings);
        changeUsernameButton = findViewById(R.id.changeUsernameButton);
        changePasswordButton  = findViewById(R.id.changePasswordButton);
        historyButton = findViewById(R.id.historyButton);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newPassword = ((EditText) findViewById(R.id.newpassword)).getText().toString();
                //get user object
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                // if email EditText field empty, show error, else attempt to update
                if (TextUtils.isEmpty(newPassword)) {
                    Toast.makeText(SettingActivity.this, "Please enter a new password.", Toast.LENGTH_LONG).show();
                } else {
                    //update password and check for any other errors
                    user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SettingActivity.this, "Password successfully changed!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(SettingActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
        changeUsernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: JK Firebase change username functionality
                newEmail = ((EditText)findViewById(R.id.newemail)).getText().toString();
                Log.d("TAG",newEmail);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                //if EditText field empty, show error, else attempt to update
                if (TextUtils.isEmpty(newEmail)) {
                    // Show an error message if the new email field is empty
                    Toast.makeText(SettingActivity.this, "Please enter a new email.", Toast.LENGTH_LONG).show();
                } else {
                    user.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(SettingActivity.this,"Username successfully changed!",Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(SettingActivity.this,""+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle historyPageBundle = mDeliveryMan.HistoryPageBundle(uid);
                Intent goToHistory = new Intent(SettingActivity.this,HistoryActivity.class);
                goToHistory.putExtras(historyPageBundle);
                startActivity(goToHistory);
            }
        });
    }
}