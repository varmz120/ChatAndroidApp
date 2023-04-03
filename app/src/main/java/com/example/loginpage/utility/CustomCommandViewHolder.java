package com.example.loginpage.utility;

import com.example.loginpage.databinding.ItemCommandBinding;

import androidx.annotation.NonNull;
import io.getstream.chat.android.client.models.Command;
import io.getstream.chat.android.ui.suggestion.list.adapter.SuggestionListItem;
import io.getstream.chat.android.ui.suggestion.list.adapter.viewholder.BaseSuggestionItemViewHolder;

/**
 * @author saran
 * @date 2/4/2023
 */

public final class CustomCommandViewHolder extends BaseSuggestionItemViewHolder<SuggestionListItem.CommandItem> {

    ItemCommandBinding binding;
    private final String GIPHY = "giphy";
    private final String MUTE = "mute";

    public CustomCommandViewHolder(ItemCommandBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    @Override
    public void bindItem(@NonNull SuggestionListItem.CommandItem item) {
        String given_command = item.getCommand().getName();
        System.out.println(item);
        if(given_command.equals("giphy")){
            given_command = "Students";
        } else if (given_command.equals("mute")){
            given_command = "TA";
        } else {
            given_command = "Both";
        }
        binding.commandNameTextView.setText(given_command);
    }
}

