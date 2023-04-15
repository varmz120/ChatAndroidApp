package com.example.loginpage;

import android.os.Bundle;
import android.util.Log;

import com.example.loginpage.utility.Database;
import com.example.loginpage.utility.MessageAdapter;
import com.example.loginpage.utility.MessageComparator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.getstream.chat.android.client.models.Message;

/**
 * @author saran
 * @date 15/4/2023
 */
/** Allows professors to sort enter a new Activity where they are able to see messages that filtered and sorted based on whether the owner of the question is has approved the answers given and the upvote count on the question*/
public class FilteredActivity extends AppCompatActivity {
   private MessageAdapter mFilteredAdapter;
   private RecyclerView mRecyclerView;
   private final Database mDatabase = Database.getInstance();

   public FilteredActivity(){}
   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_filtered);
      String channelId = getIntent().getStringExtra("channel_id");
      List<Message> messages = new ArrayList<>();
      Query q = mDatabase.filteredQuestions(channelId);
      ValueEventListener valueEventListener = new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot snapshot) {
            for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
               Message message = messageSnapshot.getValue(Message.class);
               messages.add(message);
            }
            Collections.sort(messages, new MessageComparator());
            mRecyclerView = findViewById(R.id.recycleViewForUnansweredQuestions);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(FilteredActivity.this));
            mFilteredAdapter = new MessageAdapter(messages);
            mRecyclerView.setAdapter(mFilteredAdapter);
         }
         @Override
         public void onCancelled(@NonNull DatabaseError error) {
            Log.e("FilteredActivity: ","On cancelled for dataChange when retrieving message");
         }
      };
      q.addValueEventListener(valueEventListener);
   }

}
