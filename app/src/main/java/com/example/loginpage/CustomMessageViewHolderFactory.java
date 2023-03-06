package com.example.loginpage;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.loginpage.databinding.AttachedButtonBinding;
import com.getstream.sdk.chat.adapter.MessageListItem;

import androidx.annotation.NonNull;
import io.getstream.chat.android.ui.message.list.adapter.BaseMessageItemViewHolder;
import io.getstream.chat.android.ui.message.list.adapter.MessageListItemViewHolderFactory;

/**
 * @author saran
 * @date 25/2/2023
 */

class CustomMessageViewHolderFactory extends MessageListItemViewHolderFactory {
   private int BUTTON_VIEW_HOLDER_TYPE = 1;
   public int getItemViewType(@NonNull MessageListItem item){
      if(item instanceof MessageListItem.MessageItem){
         return BUTTON_VIEW_HOLDER_TYPE;
      }
      return super.getItemViewType(item);
   }
   public BaseMessageItemViewHolder<? extends MessageListItem> createViewHolder(@NonNull ViewGroup parentView, int viewType){
      if (viewType == BUTTON_VIEW_HOLDER_TYPE) {
         return new ButtonViewHolder(parentView, AttachedButtonBinding.inflate(LayoutInflater.from(parentView.getContext()), parentView, false));
      }
      return super.createViewHolder(parentView, viewType);
   }


}
