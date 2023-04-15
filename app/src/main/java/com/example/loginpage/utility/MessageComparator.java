package com.example.loginpage.utility;

import com.example.loginpage.constants.ExtraData;

import java.util.Comparator;

import io.getstream.chat.android.client.models.Message;

/**
 * @author saran
 * @date 15/4/2023
 */

public class MessageComparator implements Comparator<Message> {

   @Override
   public int compare(Message message, Message t1) {
      int vote_count_this = handleVoteCount(message.getExtraData().get(ExtraData.VOTE_COUNT));
      int vote_count_that = handleVoteCount(t1.getExtraData().get(ExtraData.VOTE_COUNT));
      // reverse comparison logic to produce an array in descending order
      return Integer.compare(vote_count_that,vote_count_this);
   }
   /** Function to handle different types of vote_count objects stored in the Database as well as the
    * on the Client cloud server. */
   private int handleVoteCount(Object vote_count){
      // function
      if(vote_count instanceof Long){
         Long vc = (Long) vote_count;
         return vc.intValue();
      }
      return Integer.parseInt(vote_count.toString());
   }
}
