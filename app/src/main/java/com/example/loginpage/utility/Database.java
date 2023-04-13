package com.example.loginpage.utility;


import android.util.Log;

import com.example.loginpage.constants.DatabaseConstants;
import com.example.loginpage.constants.ExtraData;
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
   private FirebaseDatabase database;
   private DatabaseReference baseReference;
   private static Database sDatabase;
   private DatabaseReference channelReference;

   public static Database getInstance() {
      if (sDatabase == null) {
         sDatabase = new Database();
      }
      return sDatabase;
   }

   private Database() {
      connect();
   }


   public void storeDetails(String userId, String username, String selectedRole) {
      baseReference.child(DatabaseConstants.USERS).child(userId).child("username").setValue(username);
      baseReference.child(DatabaseConstants.USERS).child(userId).child("role").setValue(selectedRole);
   }

   public Task<Void> sendMessage(String channelId, Message message) {
      // writing task
      return channelReference.child(channelId).child(DatabaseConstants.MESSAGES).child(message.getId()).setValue(message);
   }

   public Task<DataSnapshot> getRole(String uid) {
      return baseReference.child(DatabaseConstants.USERS).child(uid).child("role").get();
   }

   public Task<DataSnapshot> getQuestionText(String channelId, String messageId) {
      // reading task
      return channelReference.child(channelId).child(DatabaseConstants.MESSAGES).child(messageId).child("text").get();
   }
   public Task<Void>tickPressed(String channelId, String messageId,String user){
      return getExtraDataForMessage(channelId, messageId).child(user).setValue("true");

   }
   public Task<Void>tickRemoved(String channelId, String messageId,String user){
      return getExtraDataForMessage(channelId, messageId).child(user).setValue("false");

   }
   public Task<DataSnapshot>getTickPressed(String channelId, String messageId,String user){
      return getExtraDataForMessage(channelId, messageId).child(user).get();

   }
   public Task<DataSnapshot>getReplyTickPressed(String channelId, String replyId,String user){
      return channelReference.child(channelId).child(DatabaseConstants.REPLIES).child(replyId).child(DatabaseConstants.EXTRA_DATA).child(user).get();

   }
   public Task<Void>ReplyTickPressed(String channelId, String replyId,String user){
      return channelReference.child(channelId).child(DatabaseConstants.REPLIES).child(replyId).child(DatabaseConstants.EXTRA_DATA).child(user).setValue("true");

   }
   public Task<Void>ReplyTickRemoved(String channelId, String replyId,String user){
      return channelReference.child(channelId).child(DatabaseConstants.REPLIES).child(replyId).child(DatabaseConstants.EXTRA_DATA).child(user).setValue("false");

   }


   public Task<Void> upVoteMessage(String channelId, String messageId, int votes) {
      return getExtraDataForMessage(channelId, messageId).child(ExtraData.VOTE_COUNT).setValue(votes);
   }

   public Task<Void> upVoteReply(String channelId, String replyId, int votes) {
      return channelReference.child(channelId).child(DatabaseConstants.REPLIES).child(replyId).child(DatabaseConstants.EXTRA_DATA).child(ExtraData.VOTE_COUNT).setValue(votes);
   }

   public Task<DataSnapshot> getVoteCount(String channelId, String messageId) {
      return getExtraDataForMessage(channelId, messageId).child(ExtraData.VOTE_COUNT).get();
   }

   public Task<DataSnapshot> getReplyCountForMessage(String channelId, String messageId) {
      return getExtraDataForMessage(channelId, messageId).child(ExtraData.REPLY_COUNT).get();
   }

   public Task<Void> updateReplyCountForMessage(String channelId, String messageId, int newCount) {
      return getExtraDataForMessage(channelId, messageId).child(ExtraData.REPLY_COUNT).setValue(newCount);
   }

   public DatabaseReference getExtraDataForMessage(String channelId, String messageId) {
      return channelReference.child(channelId).child(DatabaseConstants.MESSAGES).child(messageId).child(DatabaseConstants.EXTRA_DATA);
   }
   public Task<DataSnapshot> getExtraDataForMessage_diff(String channelId, String messageId) {
      return channelReference.child(channelId).child(DatabaseConstants.MESSAGES).child(messageId).child(DatabaseConstants.EXTRA_DATA).get();
   }
   public Task<DataSnapshot> getExtraDataForReplies_diff(String channelId, String replyId) {
      return channelReference.child(channelId).child(DatabaseConstants.REPLIES).child(replyId).child(DatabaseConstants.EXTRA_DATA).get();
   }
   public DatabaseReference getExtraDataForReply(String channelId, String replyId) {

      return channelReference.child(channelId).child(DatabaseConstants.REPLIES).child(replyId).child(DatabaseConstants.EXTRA_DATA);
   }

   public Task<DataSnapshot> getReplyUpVoteCount(String channelId, String replyId) {
      return channelReference.child(channelId).child(DatabaseConstants.REPLIES).child(replyId).child(DatabaseConstants.EXTRA_DATA).child(ExtraData.VOTE_COUNT).get();
   }

   public Task<Void> sendReply(String channelId_messageId, Message reply) {
      return channelReference.child(channelId_messageId).child(DatabaseConstants.REPLIES).child(reply.getId()).setValue(reply);
   }

   public Task<Void> deleteReply(String channelId_messageId, String replyId) {
      return channelReference.child(channelId_messageId).child(DatabaseConstants.REPLIES).child(replyId).removeValue();
   }
   public DatabaseReference getRepliesReference(String uid){
      return baseReference.child(DatabaseConstants.USERS).child(uid).child(DatabaseConstants.REPLIES);
   }
   public Task<Void> sendReplyToHistory(String uid, Message reply){
      return baseReference.child(DatabaseConstants.USERS).child(uid).child(DatabaseConstants.REPLIES).child(reply.getId()).setValue(reply);
   }

   public Task<Void> deleteMessage(String channelId, String messageId) {
      // if a message is deleted, it's corresponding replies will be deleted as well
      String correspondingReplyChannelId = channelId + "_" + messageId;
      channelReference.child(correspondingReplyChannelId).removeValue().addOnCompleteListener(data->{
         Log.i("Database","Corresponding reply channel: " + correspondingReplyChannelId + " for message: " + messageId + " is deleted");
      });
      return channelReference.child(channelId).child(DatabaseConstants.MESSAGES).child(messageId).removeValue();
   }
   public DatabaseReference getMessagesReference(String channelId){
      return channelReference.child(channelId).child(DatabaseConstants.MESSAGES);
   }

   public Task<Void> updateParentQuestionTick(String channelId,String messageId,String approval_from){
      return getExtraDataForMessage(channelId,messageId).child(approval_from).setValue("true");
   }

   public void connect() {
      try {
         String referenceId = "https://" + DatabaseConstants.DATABASE_NAME + "." + DatabaseConstants.DATABASE_REGION + ".firebasedatabase.app/";
         database = FirebaseDatabase.getInstance(referenceId);
         baseReference = database.getReference();
         channelReference = baseReference.child(DatabaseConstants.CHANNELS);
      } catch (Exception e){
         Log.e("Database","Error connecting to database: " + e);

      }
   }
}


