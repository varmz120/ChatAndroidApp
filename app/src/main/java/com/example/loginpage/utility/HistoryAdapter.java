package com.example.loginpage.utility;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.loginpage.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.getstream.chat.android.client.models.Message;

/**
 * @author saran
 * @date 7/4/2023
 */


public class HistoryAdapter extends FirebaseRecyclerAdapter<Message,HistoryAdapter.replyViewHolder> {
    private final Database mDatabase = Database.getInstance();
    public HistoryAdapter(@NonNull FirebaseRecyclerOptions<Message> options){
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull replyViewHolder holder, int position, @NonNull Message model) {
        holder.Reply.setText(model.getText());
        String[] channelId_messageId = model.getCid().split(":")[1].split("_");
        String channelId = channelId_messageId[0];
        String messageId = channelId_messageId[1];
        mDatabase.getQuestionText(channelId,messageId).onSuccessTask(dataSnapshot -> {
            if(dataSnapshot.exists()){
                String questionText = dataSnapshot.getValue().toString();
                holder.parentQuestion.setText(questionText);
            } else if (!dataSnapshot.exists()) {
                holder.parentQuestion.setText("Unable to find parent question");
            }
            return null;
        });
    }


    @NonNull
    @Override
    public replyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reply, parent, false);
        return new replyViewHolder(view);
    }
    static class replyViewHolder extends RecyclerView.ViewHolder {
        TextView parentQuestion, Reply;
        public replyViewHolder(@NonNull View itemView)
        {
            super(itemView);
            parentQuestion = itemView.findViewById(R.id.parentQuestion);
            Reply = itemView.findViewById(R.id.replyText);
        }
    }
}
