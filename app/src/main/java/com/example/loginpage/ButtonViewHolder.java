package com.example.loginpage;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.loginpage.databinding.AttachedButtonBinding;
import com.getstream.sdk.chat.adapter.MessageListItem;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.getstream.chat.android.ui.message.list.adapter.BaseMessageItemViewHolder;
import io.getstream.chat.android.ui.message.list.adapter.MessageListItemPayloadDiff;

/**
 * @author saran
 * @date 25/2/2023
 */

class ButtonViewHolder extends BaseMessageItemViewHolder<MessageListItem.MessageItem> {
   AttachedButtonBinding binding;
   public Button upVoteButton;
   public ButtonViewHolder(@NonNull ViewGroup parentView, @NonNull AttachedButtonBinding binding){
      super(binding.getRoot());
      this.binding = binding;
      this.upVoteButton = binding.getRoot().findViewById(R.id.upVoteButton);
   }
   @Override
   public void bindData(@NonNull MessageListItem.MessageItem messageItem, @Nullable MessageListItemPayloadDiff messageListItemPayloadDiff) {
      binding.message.setText(messageItem.getMessage().getText());
      binding.upVoteButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            int current_votes = Integer.parseInt(upVoteButton.getText().toString());
            current_votes++;
            HashMap<String,Object> extraData = new HashMap<>();
            extraData.put("current_votes",current_votes);
            messageItem.getMessage().setExtraData(extraData);
            String new_votes = Integer.toString(current_votes);
            upVoteButton.setText(new_votes);
         }
      });
   }

}
