package com.example.loginpage.utility;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.loginpage.R;
import com.example.loginpage.databinding.AttachedButtonBinding;
import com.getstream.sdk.chat.adapter.MessageListItem;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;

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
    private final Database mDatabase;
    private ChatClient client;
    public ReplyViewHolder(@NonNull ViewGroup parentView, @NonNull AttachedButtonBinding binding, Database database){
        super(binding.getRoot());
        this.binding = binding;
        this.upVoteButton = binding.getRoot().findViewById(R.id.upVoteButton);
        this.mDatabase = database;
    }

    @Override
    public void bindData(@NonNull MessageListItem.MessageItem messageItem, @Nullable MessageListItemPayloadDiff messageListItemPayloadDiff) {
        Message msg = messageItem.getMessage();
        binding.message.setText(msg.getText());
        String channelId_messageId = (String) msg.getExtraData().get("channel_id");
        mDatabase.getReplyUpVoteCount(channelId_messageId,msg.getId()).onSuccessTask(dataSnapshot -> {
            if(dataSnapshot.exists()){
                Object count = dataSnapshot.getValue();
                binding.upVoteButton.setText(count.toString());
            } else {
                binding.upVoteButton.setText("0");
            }
            return null;
        });
        binding.upVoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int current_votes = Integer.parseInt(upVoteButton.getText().toString());
                //int current_votes = (int) double_type_votes;
                int added_votes = current_votes + 1;

                mDatabase.upVoteReply(channelId_messageId,msg.getId(),added_votes).onSuccessTask(new SuccessContinuation<Void, Object>() {
                    @NonNull
                    @Override
                    public Task<Object> then(Void unused) throws Exception {
                        System.out.println("REPLY UPVOTE SUCCESSFUL!");
                        return null;
                    }
                });

                String new_votes = Integer.toString(added_votes);
                upVoteButton.setText(new_votes);
            }
        });
    }


}
