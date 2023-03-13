package com.example.loginpage;

import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;

import com.example.loginpage.databinding.AttachedButtonBinding;
import com.getstream.sdk.chat.adapter.MessageListItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
   public ButtonViewHolder(@NonNull ViewGroup parentView, @NonNull AttachedButtonBinding binding,@Nullable List<Message> msgList){
      super(binding.getRoot());
      this.binding = binding;
      this.upVoteButton = binding.getRoot().findViewById(R.id.upVoteButton);
   }
   @Override
   public void bindData(@NonNull MessageListItem.MessageItem messageItem, @Nullable MessageListItemPayloadDiff messageListItemPayloadDiff) {
      Message msg = messageItem.getMessage();
      binding.message.setText(msg.getText());
      System.out.println(msg.getExtraData() + " printing extra data for message: " + msg.getText());
      binding.upVoteButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            int current_votes = Integer.parseInt(upVoteButton.getText().toString());
            current_votes++;
            HashMap<String,Object> extraData = new HashMap<>();
            extraData.put("current_votes",current_votes);
            messageItem.getMessage().setExtraData(extraData);
            String new_votes = Integer.toString(current_votes);
//            channelClient.get().enqueue((result -> {
//               Channel channel = result.data();
//               List<Message> msg_list = channel.getMessages();
//               for(Message m: msg_list){
//                  //client.deleteMessage(m.getId(),true).execute();
//                  if(m.getId().equals(mId)) m.getExtraData().put("current_votes",(double) finalCurrent_votes);
//               }
//
//            }));
            upVoteButton.setText(new_votes);
         }
      });
   }


}
