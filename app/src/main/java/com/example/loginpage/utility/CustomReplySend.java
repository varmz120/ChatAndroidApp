package com.example.loginpage.utility;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;


import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.channel.ChannelClient;
import io.getstream.chat.android.client.models.Message;


/**
 * @author saran
 * @date 31/3/2023
 */

public class CustomReplySend extends CustomMessageSend{
   private final Database mDatabase;
   private final ChannelClient classChannel;
   private final String parentQuestionPageId;
   private final String parentMessageId;

   public CustomReplySend(ChannelClient channelClient, Database database){
      this.mDatabase = database;
      this.classChannel = channelClient;
      String[] ids = channelClient.getChannelId().split("_");
      parentQuestionPageId = ids[0];
      parentMessageId = ids[1];
   }
   @Override
   public void sendMessage(@NonNull String s, @Nullable Message message) {
      ChatClient client = ChatClient.instance();
      Message reply = construct_message(s,client);
      mDatabase.sendReply(classChannel.getChannelId(),reply).onSuccessTask(new SuccessContinuation<Void, Object>() {
         @NonNull
         @Override
         public Task<Object> then(Void unused) throws Exception {
            mDatabase.sendReplyToHistory(reply.getUser().getId(),reply).onSuccessTask(result->{
               System.out.println("Successfully sent Reply: " + reply.getId());
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
               client.sendMessage(classChannel.getChannelType(),classChannel.getChannelId(),reply,true).enqueue(result -> {
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

}
