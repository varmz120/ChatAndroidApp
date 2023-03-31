package com.example.loginpage.utility;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.loginpage.R;
import com.example.loginpage.databinding.AttachedButtonBinding;
import com.getstream.sdk.chat.adapter.MessageListItem;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.models.Message;
import io.getstream.chat.android.ui.message.list.adapter.BaseMessageItemViewHolder;
import io.getstream.chat.android.ui.message.list.adapter.MessageListItemPayloadDiff;

/**
 * @author ryner
 * @date 25/2/2023
 */

class ReplyViewHolder extends BaseMessageItemViewHolder<MessageListItem.MessageItem> {
    AttachedButtonBinding binding;
    public Button upVoteButton;
    private final Database database;
    private ChatClient client;
    public ReplyViewHolder(@NonNull ViewGroup parentView, @NonNull AttachedButtonBinding binding, Database database){
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
    }


}
