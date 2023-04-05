package com.example.loginpage.utility;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.loginpage.databinding.ItemCommandBinding;

import androidx.annotation.NonNull;
import io.getstream.chat.android.ui.suggestion.list.adapter.SuggestionListItem;
import io.getstream.chat.android.ui.suggestion.list.adapter.SuggestionListItemViewHolderFactory;
import io.getstream.chat.android.ui.suggestion.list.adapter.viewholder.BaseSuggestionItemViewHolder;

public final class CustomSuggestionListViewHolderFactory extends SuggestionListItemViewHolderFactory {
    @NonNull
    @Override
    public BaseSuggestionItemViewHolder<SuggestionListItem.CommandItem> createCommandViewHolder(@NonNull ViewGroup parentView) {
        return new CustomCommandViewHolder(ItemCommandBinding.inflate(LayoutInflater.from(parentView.getContext()), parentView, false));
    }
}
