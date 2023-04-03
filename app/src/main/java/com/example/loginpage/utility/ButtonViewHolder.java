package com.example.loginpage.utility;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.loginpage.R;
import com.example.loginpage.ThreadActivity;
import com.example.loginpage.databinding.AttachedButtonBinding;
import com.getstream.sdk.chat.adapter.MessageListItem;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.channel.ChannelClient;
import io.getstream.chat.android.client.models.Message;
import io.getstream.chat.android.ui.message.list.adapter.BaseMessageItemViewHolder;
import io.getstream.chat.android.ui.message.list.adapter.MessageListItemPayloadDiff;

/**
 * @author saran
 * @date 25/2/2023
 */

class ButtonViewHolder extends BaseMessageItemViewHolder<MessageListItem.MessageItem> {
   AttachedButtonBinding binding;
   public Button upVoteButton;
   private final Database mDatabase;

   public Button delete;

   public ChatClient client;


   
   public ButtonViewHolder(@NonNull ViewGroup parentView, @NonNull AttachedButtonBinding binding, Database database){
      super(binding.getRoot());
      this.binding = binding;
      this.upVoteButton = binding.getRoot().findViewById(R.id.upVoteButton);
      this.mDatabase = database;
      this.delete = binding.getRoot().findViewById(R.id.delete);
   }

   @Override
   public void bindData(@NonNull MessageListItem.MessageItem messageItem, @Nullable MessageListItemPayloadDiff messageListItemPayloadDiff) {
      ChatClient client = ChatClient.instance();
      Message msg = messageItem.getMessage();
      binding.message.setText(msg.getText());
      String channelId = (String) msg.getExtraData().get("channel_id");
      String uid = client.getCurrentUser().getId();
      String allowStudent = (String) msg.getExtraData().get("allow_student");
      String allowTA = (String) msg.getExtraData().get("allow_ta");
      String[] roles =getContext().getResources().getStringArray(R.array.role);
      String Student = roles[0]; String TA = roles[1]; String Professor = roles[2];
      delete.setVisibility(View.GONE);
     //gets client instance
      mDatabase.getVoteCount(channelId,msg.getId()).onSuccessTask(dataSnapshot -> {
         if(dataSnapshot.exists()){
            Object up_vote_count = dataSnapshot.getValue();
            binding.upVoteButton.setText(up_vote_count.toString());
         } else {
            binding.upVoteButton.setText("0");
         }
         return null;
      });
      binding.innerLayout.setOnLongClickListener(new View.OnLongClickListener() {
         @Override
         public boolean onLongClick(View view) {
            mDatabase.getRole(uid).onSuccessTask(dataSnapshot -> {
               if (dataSnapshot.exists()) {
                  String userRole = dataSnapshot.getValue().toString();
                  System.out.println("USER ROLE FROM DATABASE: " + userRole);
                  boolean permissionGrantedProf = userRole.equals(Professor);
                  boolean permissionQuestionOwner = msg.getUser().getId().equals(uid);
                  if (permissionGrantedProf || permissionQuestionOwner) {
                     delete.setVisibility(View.VISIBLE);
                  }
               }
               return  null;
                    });
            return true;
         }
      });
      
      binding.upVoteButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            //int current_votes = Integer.parseInt(upVoteButton.getText().toString());
            int current_votes = Integer.parseInt(upVoteButton.getText().toString());
            //int current_votes = (int) double_type_votes;
            int added_votes = current_votes + 1;
            mDatabase.upVoteMessage(channelId,msg.getId(),added_votes).onSuccessTask(new SuccessContinuation<Void, Object>() {

               @NonNull
               @Override
               public Task<Object> then(Void unused) throws Exception {
                  System.out.println("UPVOTED SUCCESSFULLY!");
                  return null;
               }
            });

            //msg.getExtraData().put("up_votes",added_votes);
            String new_votes = Integer.toString(added_votes);
            upVoteButton.setText(new_votes);
         }
      });
      binding.message.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            mDatabase.getRole(uid).onSuccessTask(dataSnapshot -> {
               if(dataSnapshot.exists()){
                  String userRole = dataSnapshot.getValue().toString();
                  boolean permissionGrantedStudent = userRole.equals(Student) && allowStudent.equals("true");
                  boolean permissionGrantedTA = userRole.equals(TA) && allowTA.equals("true");
                  if(permissionGrantedTA || permissionGrantedStudent){
                     String messageId = msg.getId();
                     String newChannelId = channelId + "_" + messageId; // important to keep track of parent page for database
                     ChannelClient channelClient = client.channel("messaging", newChannelId); //uses client instance to make channel
                     Intent myintent = ThreadActivity.newIntent(getContext(),channelClient,mDatabase); //initialises intent
                     myintent.putExtra("messageid",newChannelId); //puts message id
                     view.getContext().startActivity(myintent); //starts activity
                     System.out.println(" Reply channel with ID: " + newChannelId +" started successfully ");
                  }
               }
               return null;
            });
         }

      });

      binding.delete.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            mDatabase.deleteMessage(channelId,msg.getId()).onSuccessTask(new SuccessContinuation<Void, Object>() {
               @NonNull
               @Override
               public Task<Object> then(Void unused) throws Exception {
                  client.deleteMessage(messageItem.getMessage().getId(),true).enqueue(result -> {
                     if (result.isSuccess()){
                        Message deletedMessage = result.data();
                        System.out.println("The deleted message is: "+deletedMessage);
                     }
                     else{
                        System.out.println("Message is not deleted");
                        System.out.println(result);
                     }
                  });
                  return null;
               }
            });
         }
      });
   }


}
