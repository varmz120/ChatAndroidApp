package com.example.loginpage;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author saran
 * @date 6/4/2023
 */

class SettingActivity extends AppCompatActivity {
    private MaterialButton changeUsernameButton;
    private MaterialButton changePasswordButton;
    private MaterialButton historyButton;
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
                // TODO: JK Firebase change password functionality
            }
        });
        changeUsernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: JK Firebase change username functionality
            }
        });
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Sarang history functionality
            }
        });
    }
}
