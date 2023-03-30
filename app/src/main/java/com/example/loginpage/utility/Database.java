package com.example.loginpage.utility;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

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
         baseReference.child(channelId).child(MESSAGES).child(message.getId()).setValue(message).onSuccessTask((dataSnapShot)->{
            System.out.println("Send message: " + dataSnapShot);
            return null;
         });
      } catch (Exception e){
         System.out.println("Error sending message with ID" + message.getId() + " to database: "+ e);
      }
   }
   public Task<DataSnapshot> getMessage(String channelId, String messageId){
      // reading task
      return baseReference.child(channelId).child(MESSAGES).child(messageId).get();
   }
   public Task<Void> upVoteMessage(String channelId, String messageId,int votes){
      return baseReference.child(channelId).child(MESSAGES).child(messageId).child("extraData").child("vote_count").setValue(votes);
   }
   public Task<DataSnapshot> getVoteCount(String channelId, String messageId){
      return baseReference.child(channelId).child(MESSAGES).child(messageId).child("extraData").child("vote_count").get();
   }
   public void connect(){
      try{
         String referenceId = "https://d-project-8fd93-default-rtdb.asia-southeast1.firebasedatabase.app/";
         database = FirebaseDatabase.getInstance(referenceId);
         baseReference = database.getReference();
      } catch (Exception e){
         System.out.println("Error connecting to database: " + e);
      }
   }

}
