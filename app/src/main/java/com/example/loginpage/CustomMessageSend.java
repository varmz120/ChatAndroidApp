package com.example.loginpage;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.channel.ChannelClient;
import io.getstream.chat.android.client.models.Attachment;
import io.getstream.chat.android.client.models.Message;
import io.getstream.chat.android.ui.message.input.MessageInputView;
import kotlin.Pair;

/**
 * @author saran
 * @date 10/3/2023
 */

class CustomMessageSend implements MessageInputView.MessageSendHandler {
   private ChannelClient classChannel;
   private ArrayList<Message> mMessageArrayList;
   CustomMessageSend(ChannelClient channelClient){
      mMessageArrayList = new ArrayList<>();
   }

   @Override
   public void sendMessage(@NonNull String s, @Nullable Message message) {
      Message newMessage = constructMessage(s);
      mMessageArrayList.add(newMessage);
      mClient().sendMessage(classChannel.getChannelType(),classChannel.getChannelId(),newMessage).execute();
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
   private ChatClient mClient(){
      return ChatClient.instance();
   }
   private Message constructMessage(String str){
      Message m = new Message();
      m.setText(str);
      m.setCid(classChannel.getCid());
      m.setId(Integer.toString(mMessageArrayList.size()));
      HashMap<String,Object> extraData = new HashMap<>();
      extraData.put("current_votes",(double)0.0);
      m.setExtraData(extraData);
      return m;
   }
}
