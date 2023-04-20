package com.example.loginpage.customviews;

import android.view.View;

import com.example.loginpage.utility.Database;
import com.getstream.sdk.chat.adapter.MessageListItem;

import androidx.annotation.NonNull;
import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.models.Message;
import io.getstream.chat.android.ui.message.list.adapter.BaseMessageItemViewHolder;

/**
 * @author saran
 * @date 13/4/2023
 */
/** Template Method Design Pattern*/
public abstract class CustomViewHolder extends BaseMessageItemViewHolder<MessageListItem.MessageItem> {
    private final Database mDatabase = Database.getInstance();
    private final ChatClient client = ChatClient.instance();
    private final String PREF_NAME = "upvote_pref";
    private final String KEY_UPVOTED_IDS = "upvoted_ids";
    String getPrefName(){return PREF_NAME;}
    String getKeyUpvotedIds(){return KEY_UPVOTED_IDS;}
    public CustomViewHolder(@NonNull View itemView) {
        super(itemView);
    }
    abstract void setUpDatabaseStateListener(String channelId, Message message,Database database);
    abstract void upVoteButtonListener(String channelId, String uid, Message message,Database database);
    abstract void deleteButtonListener(String channelId, Message message,Database database, ChatClient client);
    abstract void setMessageListener(String uid, Message message,Database database);
    abstract void setUpInnerLayoutListener(String uid, Message message,Database database);
    abstract void setUpTickListeners(String channelId_messageId, String uid, Message msg,Database database);
    abstract void setUpInitialState(String channelId, Message message,Database database);
    void setUpOverallState(String channelId, Message message){
        String uid = client.getCurrentUser().getId();
        setUpInitialState(channelId,message,mDatabase);
        setUpDatabaseStateListener(channelId,message,mDatabase);
        upVoteButtonListener(channelId,uid,message,mDatabase);
        deleteButtonListener(channelId,message,mDatabase,client);
        setMessageListener(uid,message,mDatabase);
        setUpInnerLayoutListener(uid,message,mDatabase);
        setUpTickListeners(channelId,uid,message,mDatabase);

    };

}
