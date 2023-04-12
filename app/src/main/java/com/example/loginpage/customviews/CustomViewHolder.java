package com.example.loginpage.customviews;

import android.view.View;

import com.getstream.sdk.chat.adapter.MessageListItem;

import androidx.annotation.NonNull;
import io.getstream.chat.android.client.models.Message;
import io.getstream.chat.android.ui.message.list.adapter.BaseMessageItemViewHolder;

/**
 * @author saran
 * @date 13/4/2023
 */

public abstract class CustomViewHolder extends BaseMessageItemViewHolder<MessageListItem.MessageItem> {

    public CustomViewHolder(@NonNull View itemView) {
        super(itemView);
    }
    abstract void setUpDatabaseStateListener(String channelId, Message message);
    abstract void upVoteButtonListener(String channelId, String uid, Message message);
    abstract void deleteButtonListener(String channelId, Message message);
    abstract void setMessageListener(String uid, Message message);
    abstract void setUpInnerLayoutListener(String uid, Message message);
    abstract void setUpTickListeners(String channelId_messageId, String uid, Message msg);
    abstract void setUpInitialState(String channelId, Message message);
    void setUpOverallState(String channelId, String uid, Message message){
        setUpInitialState(channelId,message);

        setUpDatabaseStateListener(channelId,message);
        upVoteButtonListener(channelId,uid,message);
        deleteButtonListener(channelId,message);
        setMessageListener(uid,message);
        setUpInnerLayoutListener(uid,message);
        setUpTickListeners(channelId,uid,message);

    };

}
