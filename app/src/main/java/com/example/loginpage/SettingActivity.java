package com.example.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.loginpage.utility.BundleDeliveryMan;
import com.google.android.material.button.MaterialButton;

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
                Bundle historyPageBundle = mDeliveryMan.HistoryPageBundle(uid);
                Intent goToHistory = new Intent(SettingActivity.this,HistoryActivity.class);
                goToHistory.putExtras(historyPageBundle);
                startActivity(goToHistory);
            }
        });
    }
}
