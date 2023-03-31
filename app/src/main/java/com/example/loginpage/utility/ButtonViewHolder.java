package com.example.loginpage.utility;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.loginpage.R;
import com.example.loginpage.ThreadActivity;
import com.example.loginpage.databinding.AttachedButtonBinding;
import com.getstream.sdk.chat.adapter.MessageListItem;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;

import java.util.Map;

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
   private final Database database;
   
   public ButtonViewHolder(@NonNull ViewGroup parentView, @NonNull AttachedButtonBinding binding, Database database){
      super(binding.getRoot());
      this.binding = binding;
      this.upVoteButton = binding.getRoot().findViewById(R.id.upVoteButton);
      this.database = database;
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
      binding.message.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {

            System.out.println(messageItem.getMessage().getId());

            ChatClient client = ChatClient.instance(); //gets client instance
            ChannelClient channelClient = client.channel("messaging", messageItem.getMessage().getId()); //uses client instance to make channel
            Intent myintent = ThreadActivity.newIntent(getContext(),channelClient,database); //initialises intent
            myintent.putExtra("messageid",messageItem.getMessage().getId()); //puts message id
            view.getContext().startActivity(myintent); //starts activity
            System.out.println(" Channel started successfully ");
         }


      });
   }


}
