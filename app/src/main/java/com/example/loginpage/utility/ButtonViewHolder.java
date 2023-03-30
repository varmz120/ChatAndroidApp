package com.example.loginpage.utility;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.loginpage.R;
import com.example.loginpage.ThreadActivity;
import com.example.loginpage.databinding.AttachedButtonBinding;
import com.getstream.sdk.chat.adapter.MessageListItem;

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
   private ChatClient client;
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
      binding.upVoteButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            int current_votes = Integer.parseInt(upVoteButton.getText().toString());
            int added_votes = current_votes + 1;
            Map<String,Object> extraData = messageItem.getMessage().getExtraData();
            extraData.put("vote_count",added_votes);
            messageItem.getMessage().setExtraData(extraData);
            String new_votes = Integer.toString(added_votes);
            upVoteButton.setText(new_votes);
         }
      });
      binding.message.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {

            System.out.println("watch this");
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
