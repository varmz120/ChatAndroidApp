package com.example.loginpage.utility;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.loginpage.R;
import com.example.loginpage.ThreadActivity;
import com.example.loginpage.databinding.AttachedButtonBinding;
import com.getstream.sdk.chat.adapter.MessageListItem;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.channel.ChannelClient;
import io.getstream.chat.android.client.models.Message;
import io.getstream.chat.android.ui.message.list.adapter.BaseMessageItemViewHolder;
import io.getstream.chat.android.ui.message.list.adapter.MessageListItemPayloadDiff;

/**
 * @author saran
 * @date 25/2/2023
 */

class ButtonViewHolder extends BaseMessageItemViewHolder<MessageListItem.MessageItem> {
   AttachedButtonBinding binding;
   public Button upVoteButton;
   private final Database mDatabase;
   private static final String PREF_NAME = "upvote_pref";
   private static final String KEY_UPVOTED_IDS = "upvoted_ids";

   // method to get the set of upvoted IDs from shared preference
   private Set<String> getUpvotedIds() {
      SharedPreferences preferences = getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
      return preferences.getStringSet(KEY_UPVOTED_IDS, new HashSet<>());
   }

   // method to add an ID to the set of upvoted IDs in shared preference
   private void addUpvotedId(String id) {
      SharedPreferences preferences = getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
      Set<String> upvotedIds = preferences.getStringSet(KEY_UPVOTED_IDS, new HashSet<>());
      upvotedIds.add(id);
      preferences.edit().putStringSet(KEY_UPVOTED_IDS, upvotedIds).apply();
   }
   
   public ButtonViewHolder(@NonNull ViewGroup parentView, @NonNull AttachedButtonBinding binding, Database database){
      super(binding.getRoot());
      this.binding = binding;
      this.upVoteButton = binding.getRoot().findViewById(R.id.upVoteButton);
      this.mDatabase = database;
   }

   @Override
   public void bindData(@NonNull MessageListItem.MessageItem messageItem, @Nullable MessageListItemPayloadDiff messageListItemPayloadDiff) {
      Message msg = messageItem.getMessage();
      binding.message.setText(msg.getText());
      String channelId = (String) msg.getExtraData().get("channel_id");

      mDatabase.getVoteCount(channelId,msg.getId()).onSuccessTask(dataSnapshot -> {
         if(dataSnapshot.exists()){
            Object up_vote_count = dataSnapshot.getValue();
            binding.upVoteButton.setText(up_vote_count.toString());
         } else {
            binding.upVoteButton.setText("0");
         }
         return null;
      });


      binding.upVoteButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            String messageId = msg.getId();
            Set<String> upvotedIds = getUpvotedIds();
            if (!upvotedIds.contains(messageId)) {
               int current_votes = Integer.parseInt(upVoteButton.getText().toString());
               int added_votes = current_votes + 1;
               mDatabase.upVoteMessage(channelId, messageId, added_votes).onSuccessTask(new SuccessContinuation<Void, Object>() {
                  @NonNull
                  @Override
                  public Task<Object> then(Void unused) throws Exception {
                     System.out.println("UPVOTED SUCCESSFULLY!");
                     return null;
                  }
               });
               String new_votes = Integer.toString(added_votes);
               upVoteButton.setText(new_votes);
               upVoteButton.setEnabled(false); // disable the button
               addUpvotedId(messageId); // add the ID to the set of upvoted IDs
            } else {
               upVoteButton.setEnabled(false); // disable the button
            }
         }
      });
      binding.message.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            ChatClient client = ChatClient.instance(); //gets client instance
            String messageId = msg.getId();
            String newChannelId = channelId + "_" + messageId; // important to keep track of parent page for database
            ChannelClient channelClient = client.channel("messaging", newChannelId); //uses client instance to make channel
            Intent myintent = ThreadActivity.newIntent(getContext(),channelClient,mDatabase); //initialises intent
            myintent.putExtra("messageid",newChannelId); //puts message id
            view.getContext().startActivity(myintent); //starts activity
            System.out.println(" Reply channel with ID: " + newChannelId +" started successfully ");
         }


      });
   }


}
