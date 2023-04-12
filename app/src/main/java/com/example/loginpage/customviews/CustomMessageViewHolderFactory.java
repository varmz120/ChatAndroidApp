package com.example.loginpage.customviews;

import android.view.LayoutInflater;
import android.view.ViewGroup;


import com.example.loginpage.customviews.ButtonViewHolder;
import com.example.loginpage.databinding.AttachedButtonBinding;
import com.example.loginpage.utility.Database;
import com.getstream.sdk.chat.adapter.MessageListItem;

import androidx.annotation.NonNull;

import io.getstream.chat.android.ui.message.list.adapter.BaseMessageItemViewHolder;
import io.getstream.chat.android.ui.message.list.adapter.MessageListItemViewHolderFactory;

/**
 * @author saran
 * @date 25/2/2023
 */

public class CustomMessageViewHolderFactory extends MessageListItemViewHolderFactory {
   private final int BUTTON_VIEW_HOLDER_TYPE = 1;
   private final Database mDatabase;
   
   public int getItemViewType(@NonNull MessageListItem item){
      if(item instanceof MessageListItem.MessageItem){
         return BUTTON_VIEW_HOLDER_TYPE;
      }
      return super.getItemViewType(item);
   }
   @Override
   public BaseMessageItemViewHolder<? extends MessageListItem> createViewHolder(@NonNull ViewGroup parentView, int viewType){
      if (viewType == BUTTON_VIEW_HOLDER_TYPE) {
         return new ButtonViewHolder(parentView, AttachedButtonBinding.inflate(LayoutInflater.from(parentView.getContext()), parentView, false));
      }
      return super.createViewHolder(parentView, viewType);
   }
   public CustomMessageViewHolderFactory(Database database){
      this.mDatabase = database;
   }

}
