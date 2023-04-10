package com.example.loginpage.utility;

import java.util.Comparator;

import io.getstream.chat.android.client.models.Message;

/**
 * @author saran
 * @date 10/4/2023
 */

class MessageComparator implements Comparator<Message> {
   @Override
   public int compare(Message message, Message t1) {
      int this_count = handleCount(message.getExtraData().get("vote_count"));
      int other_count = handleCount(t1.getExtraData().get("vote_count"));
      return Integer.compare(this_count,other_count);
   }
   private int handleCount(Object count){
      if(count instanceof Integer){
         return (int) count;
      }
      Double countD = (Double) count;
      return countD.intValue();
   }

}
