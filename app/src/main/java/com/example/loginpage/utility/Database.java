package com.example.loginpage.utility;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * @author saran
 * @date 13/3/2023
 */

public class Database {
   private final String databaseName = "d-project-8fd93-default-rtdb";
   private final String databaseRegion = "asia-southeast1";
   private ColorLogger logger;
   private FirebaseDatabase database;
   public Database(){
      connect();
      logger = new ColorLogger();
   }
   public void write(Object o){
      try{
         if(database == null) throw new Exception("Database object is null! Unable to write");
         DatabaseReference databaseReference = database.getReference("message");
         databaseReference.setValue(o);
      } catch (Exception e){
         logger.logError("Error writing to database: "+ e);
      }
   }
   public void connect(){
      try{
         String referenceId = "https://d-project-8fd93-default-rtdb.asia-southeast1.firebasedatabase.app/";
         database = FirebaseDatabase.getInstance(referenceId);
      } catch (Exception e){
         logger.logError("Error connecting to database: " + e);
      }
   }

}
