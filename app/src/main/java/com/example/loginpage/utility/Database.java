package com.example.loginpage.utility;


import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import io.getstream.chat.android.client.models.Message;

/**
 * @author saran
 * @date 13/3/2023
 */

public class Database {
   private final String databaseName = "d-project-8fd93-default-rtdb";
   private final String databaseRegion = "asia-southeast1";
   private FirebaseDatabase database;
   private DatabaseReference baseReference;
   private static Database sDatabase;
   private DatabaseReference channelReference;
   private final String CHANNELS = "Channels";
   private final String MESSAGES = "messages";
   private final String REPLIES = "Replies";
   private final String EXTRA_DATA = "extraData";
   private final String USERS = "users";
   private final String VOTE_COUNT = "vote_count";
   private final String REPLY_COUNT = "RC";

   public static Database getInstance() {
      if (sDatabase == null) {
         sDatabase = new Database();
      }
      return sDatabase;
   }

   private Database() {
      connect();
   }

   public Task<Boolean> checkChannel(String channelId) {
      TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();

      DatabaseReference channelRef = channelReference.child(channelId);

      channelRef.addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            // If the dataSnapshot exists, then the channel exists
            boolean channelExists = dataSnapshot.exists();
            taskCompletionSource.setResult(channelExists);
         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {
            // Handle the error case here
            taskCompletionSource.setException(databaseError.toException());
         }
      });

      return taskCompletionSource.getTask();
   }

   public void storeDetails(String userId, String username, String selectedRole) {
      baseReference.child(USERS).child(userId).child("username").setValue(username);
      baseReference.child(USERS).child(userId).child("role").setValue(selectedRole);
   }

   public Task<Void> sendMessage(String channelId, Message message) {
      // writing task
      return channelReference.child(channelId).child(MESSAGES).child(message.getId()).setValue(message);
   }

   public Task<DataSnapshot> getRole(String uid) {
      return baseReference.child("users").child(uid).child("role").get();
   }

   public Task<DataSnapshot> getQuestionText(String channelId, String messageId) {
      // reading task
      return channelReference.child(channelId).child(MESSAGES).child(messageId).child("text").get();
   }

   public Task<Void> upVoteMessage(String channelId, String messageId, int votes) {
      return getExtraDataForMessage(channelId, messageId).child(VOTE_COUNT).setValue(votes);
   }

   public Task<Void> upVoteReply(String channelId, String replyId, int votes) {
      return channelReference.child(channelId).child(REPLIES).child(replyId).child(EXTRA_DATA).child(VOTE_COUNT).setValue(votes);
   }

   public Task<DataSnapshot> getVoteCount(String channelId, String messageId) {
      return getExtraDataForMessage(channelId, messageId).child(VOTE_COUNT).get();
   }

   public Task<DataSnapshot> getReplyCountForMessage(String channelId, String messageId) {
      return getExtraDataForMessage(channelId, messageId).child(REPLY_COUNT).get();
   }

   public Task<Void> updateReplyCountForMessage(String channelId, String messageId, int newCount) {
      return getExtraDataForMessage(channelId, messageId).child(REPLY_COUNT).setValue(newCount);
   }

   public DatabaseReference getExtraDataForMessage(String channelId, String messageId) {
      return channelReference.child(channelId).child(MESSAGES).child(messageId).child(EXTRA_DATA);
   }

   public Task<DataSnapshot> getReplyUpVoteCount(String channelId, String replyId) {
      return channelReference.child(channelId).child(REPLIES).child(replyId).child(EXTRA_DATA).child(VOTE_COUNT).get();
   }

   public Task<Void> setChannel(String channelId) {
      return channelReference.child(channelId).setValue("");
   }

   public Task<Void> sendReply(String channelId_messageId, Message reply) {
      return channelReference.child(channelId_messageId).child(REPLIES).child(reply.getId()).setValue(reply);
   }

   public Task<Void> deleteReply(String channelId_messageId, String replyId) {
      return channelReference.child(channelId_messageId).child(REPLIES).child(replyId).removeValue();
   }
   public DatabaseReference getRepliesReference(String uid){
      return baseReference.child(USERS).child(uid).child(REPLIES);
   }
   public Task<Void> sendReplyToHistory(String uid, Message reply){
      return baseReference.child(USERS).child(uid).child(REPLIES).child(reply.getId()).setValue(reply);
   }

   public Task<Void> deleteMessage(String channelId, String messageId) {
      // if a message is deleted, it's corresponding replies will be deleted as well
      String correspondingReplyChannelId = channelId + "_" + messageId;
      channelReference.child(correspondingReplyChannelId).removeValue().addOnCompleteListener(data->{
         Log.i("Database","Corresponding reply channel: " + correspondingReplyChannelId + " for message: " + messageId + " is deleted");
      });
      return channelReference.child(channelId).child(MESSAGES).child(messageId).removeValue();
   }
   public DatabaseReference getMessagesReference(String channelId){
      return channelReference.child(channelId).child(MESSAGES);
   }

   public void connect() {
      try {
         String referenceId = "https://d-project-8fd93-default-rtdb.asia-southeast1.firebasedatabase.app/";
         database = FirebaseDatabase.getInstance(referenceId);
         baseReference = database.getReference();
         channelReference = baseReference.child(CHANNELS);
      } catch (Exception e){
         Log.e("Database","Error connecting to database: " + e);

      }
   }
}


