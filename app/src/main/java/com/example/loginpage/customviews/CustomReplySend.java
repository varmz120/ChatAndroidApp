package com.example.loginpage.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.loginpage.R;
import com.example.loginpage.utility.Database;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;


import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.channel.ChannelClient;
import io.getstream.chat.android.client.models.Attachment;
import io.getstream.chat.android.client.models.Message;
import io.getstream.chat.android.ui.message.input.MessageInputView;
import kotlin.Pair;


/**
 * @author saran
 * @date 31/3/2023
 */

public class CustomReplySend extends ConstraintLayout implements MessageInputView.MessageSendHandler {
   private final Database mDatabase = Database.getInstance();
   public static ChannelClient classChannel;

   public CustomReplySend(@NonNull Context context) {
      super(context);
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      setUpListeners(context);
   }

   public CustomReplySend(@NonNull Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      setUpListeners(context);
   }

   public CustomReplySend(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      setUpListeners(context);
   }

   public CustomReplySend(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
      super(context, attrs, defStyleAttr, defStyleRes);
      setUpListeners(context);
   }
   private void setUpListeners(Context context){
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      View view = inflater.inflate(R.layout.custom_reply_input, this, true);
      EditText inputField = view.findViewById(R.id.replyInputField);
      ImageButton sendButton = view.findViewById(R.id.replySendButton);
      sendButton.setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View view) {
            String messageString = inputField.getText().toString();
            String checkMessage = messageString.trim();
            if(!checkMessage.equals("")){
               sendMessage(messageString,null);
            }
         }
      });
   }


   @Override
   public void sendMessage(@NonNull String s, @Nullable Message message) {
      ChatClient client = ChatClient.instance();
      String[] ids = classChannel.getChannelId().split("_");
      String parentQuestionPageId = ids[0];
      String parentMessageId = ids[1];
      Message reply = construct_message(s,client);
      mDatabase.sendReply(classChannel.getChannelId(),reply).onSuccessTask(new SuccessContinuation<Void, Object>() {
         @NonNull
         @Override
         public Task<Object> then(Void unused) throws Exception {
            mDatabase.sendReplyToHistory(reply.getUser().getId(),reply).onSuccessTask(result->{
               Log.i("CustomReplySend","Successfully sent Reply: " + reply.getId());
               return null;
            });
            return null;
         }
      });
      mDatabase.getReplyCountForMessage(parentQuestionPageId,parentMessageId).onSuccessTask(dataSnapshot -> {
         if(dataSnapshot.exists()){
            int count = ((Long) dataSnapshot.getValue()).intValue();
            count += 1;
            mDatabase.updateReplyCountForMessage(parentQuestionPageId,parentMessageId,count).onSuccessTask(dataSnapshot2->{
               Log.i("CustomReplySend","Updated reply count of message " + parentMessageId + " successfully");
               classChannel.sendMessage(reply).enqueue(result -> {
                  if(result.isSuccess()){
                     Log.i("CustomReplySend","Reply with text: " + reply.getText() + " was sent successfully");
                  } else {
                     Log.i("CustomReplySend","Error sending reply on client with text " + reply.getText());
                  }
               });
               return null;
            });
         }
         Log.e("CustomReplySend","Error getting reply count for message " + parentMessageId);
         return null;
      }).addOnFailureListener(new OnFailureListener() {
         @Override
         public void onFailure(@NonNull Exception e) {
            Log.e("CustomReplySend","Error getting reply count: "+e);
         }
      });

   }
   private Message construct_message(String s, ChatClient client){
      Message message = new Message();
      message.setId(random_id());
      message.setText(s);
      message.setCid(classChannel.getCid());
      message.setUser(Objects.requireNonNull(client.getCurrentUser()));
      HashMap<String,Object> extraData = new HashMap<>();
      extraData.put("vote_count",0);
      extraData.put("channel_id",classChannel.getChannelId());
      extraData.put("profApproved","false");
      extraData.put("taApproved","false");
      extraData.put("studentApproved","false");
      message.setExtraData(extraData);
      return message;
   }
   // TODO: Hash message properly
   private String random_id(){
      String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
              + "0123456789"
              + "abcdefghijklmnopqrstuvxyz";
      StringBuilder sb = new StringBuilder(8);
      for (int i = 0; i < 8; i++) {
         int index
                 = (int)(AlphaNumericString.length()
                 * Math.random());
         sb.append(AlphaNumericString
                 .charAt(index));
      }
      return sb.toString();
   }

   @Override
   public void dismissReply() {

   }

   @Override
   public void editMessage(@NonNull Message message, @NonNull String s) {

   }

   @Override
   public void sendMessageWithAttachments(@NonNull String s, @NonNull List<? extends Pair<? extends File, String>> list, @Nullable Message message) {

   }

   @Override
   public void sendMessageWithCustomAttachments(@NonNull String s, @NonNull List<Attachment> list, @Nullable Message message) {

   }

   @Override
   public void sendToThread(@NonNull Message message, @NonNull String s, boolean b) {

   }

   @Override
   public void sendToThreadWithAttachments(@NonNull Message message, @NonNull String s, boolean b, @NonNull List<? extends Pair<? extends File, String>> list) {

   }

   @Override
   public void sendToThreadWithCustomAttachments(@NonNull Message message, @NonNull String s, boolean b, @NonNull List<Attachment> list) {

   }
}
