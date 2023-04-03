package com.example.loginpage.utility;

import java.io.File;
import java.util.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.channel.ChannelClient;
import io.getstream.chat.android.client.models.Attachment;
import io.getstream.chat.android.client.models.ChannelInfo;
import io.getstream.chat.android.client.models.Message;
import io.getstream.chat.android.ui.message.input.MessageInputView;
import kotlin.Pair;

/**
 * @author saran
 * @date 31/3/2023
 */
public class CustomMessageSend implements MessageInputView.MessageSendHandler {
   Database mDatabase;
   ChannelClient classChannel;
   public CustomMessageSend(){}
   public CustomMessageSend(Database database, ChannelClient channelClient){
      mDatabase = database;
      classChannel = channelClient;
   }
   @Override
   public void sendMessage(@NonNull String s, @Nullable Message message) {
      ChatClient client = ChatClient.instance();
      Message message1 = construct_message(s,client);
      mDatabase.sendMessage(classChannel.getChannelId(),message1);
      client.sendMessage(classChannel.getChannelType(),classChannel.getChannelId(),message1,true).enqueue(result -> {
         if(result.isSuccess()){
            System.out.println("Message with text: " + message1.getText() + " was sent successfully");
         } else {
            System.out.println("Error sending message with text " + message1.getText());
         }
      });
   }
   private Message construct_message(String s,ChatClient client){
      Message message = new Message();
      message.setId(random_id());
      message.setText(s);
      message.setCid(classChannel.getCid());
      message.setUser(Objects.requireNonNull(client.getCurrentUser()));
      HashMap<String,Object> extraData = new HashMap<>();
      extraData.put("vote_count",0);
      extraData.put("reply_count",0);
      extraData.put("channel_id",classChannel.getChannelId());
      message.setExtraData(extraData);
      return message;
   }
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
      message.setText(s);
   }



   @Override
   public void sendMessageWithAttachments(@NonNull String s, @NonNull List<? extends Pair<? extends File, String>> list, @Nullable Message message) {
      System.out.println("SENDING ATTACHMENTS");
   }

   @Override
   public void sendMessageWithCustomAttachments(@NonNull String s, @NonNull List<Attachment> list, @Nullable Message message) {
      System.out.println("SENDING CUSTOM ATTACHMENTS");
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
