package com.example.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.loginpage.utility.BundleDeliveryMan;
import com.example.loginpage.utility.Database;
import com.example.loginpage.utility.HistoryAdapter;
import com.example.loginpage.utility.LoadingDialogFragment;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;

import java.net.MalformedURLException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.getstream.chat.android.client.models.Message;

/**
 * @author saran
 * @date 7/4/2023
 */

public class HistoryActivity extends AppCompatActivity {
    private HistoryAdapter mHistoryAdapter;
    private RecyclerView mRecyclerView;
    private Button backButton;
    private final BundleDeliveryMan mDeliveryMan = BundleDeliveryMan.getInstance();
    private final Database mDatabase = Database.getInstance();

    public LoadingDialogFragment loadingDialogFragment = new LoadingDialogFragment();

    public HistoryActivity() throws MalformedURLException {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        String uid = getIntent().getExtras().getString("uid");
        DatabaseReference replyReference = mDatabase.getRepliesReference(uid);
        mRecyclerView = findViewById(R.id.recycleViewForReply);
        backButton = findViewById(R.id.historyBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!loadingDialogFragment.isAdded()) {
                    loadingDialogFragment.show(getSupportFragmentManager(), "loader");
                }
                Bundle homePageBundle = mDeliveryMan.SettingsPageBundle(uid);
                Intent goToHomePage = new Intent(HistoryActivity.this,SettingActivity.class);
                goToHomePage.putExtras(homePageBundle);
                startActivity(goToHomePage);
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        FirebaseRecyclerOptions<Message> options = new FirebaseRecyclerOptions.Builder<Message>().setQuery(replyReference,Message.class).build();
        mHistoryAdapter = new HistoryAdapter(options);
        mRecyclerView.setAdapter(mHistoryAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mHistoryAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHistoryAdapter.startListening();
    }
}
