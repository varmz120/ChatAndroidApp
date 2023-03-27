package com.example.loginpage.utility;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.getstream.chat.android.client.channel.ChannelClient;
import io.getstream.chat.android.client.models.Attachment;
import io.getstream.chat.android.client.models.Message;
import io.getstream.chat.android.ui.message.input.MessageInputView;
import kotlin.Pair;

/**
 * @author saran
 * @date 27/3/2023
 */

public class CustomMessageSend implements MessageInputView.MessageSendHandler {
   Database mDatabase;
   ChannelClient classChannel;
   public CustomMessageSend(Database database, ChannelClient channelClient){
      mDatabase = database;
      classChannel = channelClient;
   }
   @Override
   public void sendMessage(@NonNull String s, @Nullable Message message) {
      Message message1 = construct_message(s);
      classChannel.sendMessage(message1).enqueue(result -> {
         if(result.isSuccess()){
            System.out.println("Message with text: " + message1.getText() + " was sent successfully");
         } else {
            System.out.println("Error sending message with text " + message1.getText());
         }
      });
   }
   private Message construct_message(String s){
      Message message = new Message();
      message.setId(random_id());
      message.setText(s);
      message.setCid(classChannel.getCid());
      HashMap<String,Object> extraData = new HashMap<>();
      extraData.put("up_votes",0);
      message.setExtraData(extraData);
      return message;
   }
   private String random_id(){
      byte[] array = new byte[8]; // length is bounded by 8
      new Random().nextBytes(array);
      return new String(array, StandardCharsets.UTF_8);
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
