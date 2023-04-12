package com.example.loginpage.customviews;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;


import com.example.loginpage.databinding.AttachedButtonBinding;
import com.example.loginpage.utility.Database;

import com.getstream.sdk.chat.adapter.MessageListItem;

import java.util.List;

import androidx.annotation.NonNull;
import io.getstream.chat.android.client.models.Message;
import io.getstream.chat.android.ui.message.list.MessageListView;
import io.getstream.chat.android.ui.message.list.adapter.BaseMessageItemViewHolder;
import io.getstream.chat.android.ui.message.list.adapter.MessageListItemViewHolderFactory;
import io.getstream.chat.android.ui.message.list.adapter.MessageListListenerContainer;

/**
 * @author ryner
 * @date 28/3/2023
 */
public class CustomReplyViewHolderFactory extends MessageListItemViewHolderFactory{
    private int BUTTON_VIEW_HOLDER_TYPE = 0;
    private List<Message>msgList;
    private Database database;

    public CustomReplyViewHolderFactory(Database database){
        this.database = database;
    }
    @Override
    public int getItemViewType(@NonNull MessageListItem item){
        if(item instanceof MessageListItem.MessageItem){
            return BUTTON_VIEW_HOLDER_TYPE;
        }
        return super.getItemViewType(item);
    }
    @Override
    public BaseMessageItemViewHolder<? extends MessageListItem> createViewHolder(@NonNull ViewGroup parentView, int viewType){
        if (viewType == BUTTON_VIEW_HOLDER_TYPE) {
            return new ReplyViewHolder(parentView, AttachedButtonBinding.inflate(LayoutInflater.from(parentView.getContext()), parentView, false));
        }
        return super.createViewHolder(parentView, viewType);
    }

}
