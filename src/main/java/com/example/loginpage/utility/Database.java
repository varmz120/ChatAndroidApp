package com.example.loginpage.utility;


import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import io.getstream.chat.android.client.models.Message;

/**
 * @author saran
 * @date 13/3/2023
 */

public class Database {
   private final String databaseName = "d-project-8fd93-default-rtdb";
   private final String databaseRegion = "asia-southeast1";
   private FirebaseDatabase database;
   private final String MESSAGES = "messages";
   private final String UP_VOTES = "up_votes";
   private final String USERS_IN_UPVOTES = "users";
   public Database(){
      connect();
   }
   public void sendMessage(String channelId, Message message){
      // writing task
      try{
         if(database == null) throw new Exception("Database object is null! Unable to send message");
         DatabaseReference databaseReference = database.getReference();
         databaseReference.child(channelId).child(MESSAGES).child(message.getId()).setValue(message).onSuccessTask((dataSnapShot)->{
            System.out.println("Send message: " + dataSnapShot);
            return null;
         });
      } catch (Exception e){
         System.out.println("Error sending message with ID" + message.getId() + " to database: "+ e);
      }
   }
   public Task<DataSnapshot> getMessage(String channelId, String messageId){
      // reading task
      DatabaseReference dr = database.getReference();
      return dr.child(channelId).child(MESSAGES).child(messageId).get();
   }
   public void upVoteMessage(String messageId, String userId){
      // writing task
      try{
         if(database == null) throw new Exception("Database object is null! Unable to send message");
         DatabaseReference databaseReference = database.getReference();
         databaseReference.child(UP_VOTES).child(messageId).setValue(userId);
      }catch (Exception e){
         System.out.println("ERROR UPVOTING A MESSAGE WITH ID " + messageId + " --> " + e);
      }
   }
   public int getVoteCount(String messageId){
      DatabaseReference databaseReference = database.getReference();
      return ((String[]) Objects.requireNonNull(databaseReference.child(UP_VOTES).child(messageId).get().getResult().getValue())).length;
   }
   public void connect(){
      try{
         String referenceId = "https://d-project-8fd93-default-rtdb.asia-southeast1.firebasedatabase.app/";
         database = FirebaseDatabase.getInstance(referenceId);
      } catch (Exception e){
         System.out.println("Error connecting to database: " + e);
      }
   }

}
