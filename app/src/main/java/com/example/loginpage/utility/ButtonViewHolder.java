package com.example.loginpage.utility;

import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;

import com.example.loginpage.R;
import com.example.loginpage.databinding.AttachedButtonBinding;
import com.getstream.sdk.chat.adapter.MessageListItem;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.api.models.QueryChannelRequest;
import io.getstream.chat.android.client.call.Call;
import io.getstream.chat.android.client.channel.ChannelClient;
import io.getstream.chat.android.client.models.Channel;
import io.getstream.chat.android.client.models.Message;
import io.getstream.chat.android.client.utils.Result;
import io.getstream.chat.android.ui.message.list.adapter.BaseMessageItemViewHolder;
import io.getstream.chat.android.ui.message.list.adapter.MessageListItemPayloadDiff;

/**
 * @author saran
 * @date 25/2/2023
 */

class ButtonViewHolder extends BaseMessageItemViewHolder<MessageListItem.MessageItem> {
   AttachedButtonBinding binding;
   public Button upVoteButton;
   private Database mDatabase;
   public ButtonViewHolder(@NonNull ViewGroup parentView, @NonNull AttachedButtonBinding binding,@NonNull Database database){
      super(binding.getRoot());
      this.binding = binding;
      this.mDatabase = database;
      this.upVoteButton = binding.getRoot().findViewById(R.id.upVoteButton);
   }
   @Override
   public void bindData(@NonNull MessageListItem.MessageItem messageItem, @Nullable MessageListItemPayloadDiff messageListItemPayloadDiff) {
      Message msg = messageItem.getMessage();
      binding.message.setText(msg.getText());
      mDatabase.getVoteCount("message_room",msg.getId()).onSuccessTask(dataSnapshot -> {
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
            //int current_votes = Integer.parseInt(upVoteButton.getText().toString());
            int current_votes = Integer.parseInt(upVoteButton.getText().toString());
            //int current_votes = (int) double_type_votes;
            int added_votes = current_votes + 1;
            mDatabase.upVoteMessage("message_room",msg.getId(),added_votes).onSuccessTask(new SuccessContinuation<Void, Object>() {

               @NonNull
               @Override
               public Task<Object> then(Void unused) throws Exception {
                  System.out.println("UPVOTED SUCCESSFULLY!");
                  return null;
               }
            });

            //msg.getExtraData().put("up_votes",added_votes);
            String new_votes = Integer.toString(added_votes);
            upVoteButton.setText(new_votes);
         }
      });
   }


}
