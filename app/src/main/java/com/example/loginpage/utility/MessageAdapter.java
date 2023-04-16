package com.example.loginpage.utility;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.loginpage.R;
import com.example.loginpage.ReplyActivity;
import com.example.loginpage.constants.ExtraData;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.channel.ChannelClient;
import io.getstream.chat.android.client.models.Message;

/**
 * @author saran
 * @date 15/4/2023
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.QuestionViewHolder> {
   private List<Message> messageList;

   public MessageAdapter(List<Message> messageList) {
      this.messageList = messageList;
   }

   @NonNull
   @Override
   public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.unanswered_question, parent, false);
      return new QuestionViewHolder(view);
   }

   @Override
   public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
      Message model = messageList.get(position);
      // only display messages that have no approvals from owner of the question
      holder.questionTextView.setText(model.getText());
      Object vote_count = (Object) model.getExtraData().get(ExtraData.VOTE_COUNT);
      holder.voteCountView.setText(vote_count.toString());
      holder.questionPosition.setText(String.format(Integer.toString(position)));
      // utilising the channelType: channelId_questionId structure of the CID string to create child reply room channel Id
      String replyRoomId = model.getCid()+"_"+model.getId();
      holder.chatRoomButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            ChannelClient channelClient = ChatClient.instance().channel(replyRoomId);
            view.getContext().startActivity(ReplyActivity.newIntent(view.getContext(),channelClient));
         }
      });
   }

   @Override
   public int getItemCount() {
      return messageList.size();
   }

   static class QuestionViewHolder extends RecyclerView.ViewHolder {
      // getting layout from the unanswered_question.xml
      TextView questionTextView, voteCountView, questionPosition;
      MaterialButton chatRoomButton;

      public QuestionViewHolder(@NonNull View itemView) {
         super(itemView);
         questionTextView = itemView.findViewById(R.id.unansweredQuestionText);
         voteCountView = itemView.findViewById(R.id.unansweredVoteCount);
         chatRoomButton = itemView.findViewById(R.id.unansweredChatRoom);
         questionPosition = itemView.findViewById(R.id.unansweredPosition);
      }
   }
}
