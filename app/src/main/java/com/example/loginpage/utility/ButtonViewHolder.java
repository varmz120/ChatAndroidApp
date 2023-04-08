package com.example.loginpage.utility;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginpage.R;
import com.example.loginpage.ThreadActivity;
import com.example.loginpage.databinding.AttachedButtonBinding;
import com.getstream.sdk.chat.adapter.MessageListItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;

import java.util.HashSet;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.channel.ChannelClient;
import io.getstream.chat.android.client.models.Message;
import io.getstream.chat.android.ui.message.list.adapter.BaseMessageItemViewHolder;
import io.getstream.chat.android.ui.message.list.adapter.MessageListItemPayloadDiff;

/**
 * @author saran
 * @date 25/2/2023
 */

class ButtonViewHolder extends BaseMessageItemViewHolder<MessageListItem.MessageItem> {
   AttachedButtonBinding binding;
   public Button upVoteButton;
   private final Database mDatabase;
   private static final String PREF_NAME = "upvote_pref";
   private static final String KEY_UPVOTED_IDS = "upvoted_ids";

   // method to get the set of upvoted IDs from shared preference
   private Set<String> getUpvotedIds() {
      SharedPreferences preferences = getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
      return preferences.getStringSet(KEY_UPVOTED_IDS, new HashSet<>());
   }

   // method to add an ID to the set of upvoted IDs in shared preference
   private void addUpvotedId(String id) {
      SharedPreferences preferences = getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
      Set<String> upvotedIds = preferences.getStringSet(KEY_UPVOTED_IDS, new HashSet<>());
      upvotedIds.add(id);
      preferences.edit().putStringSet(KEY_UPVOTED_IDS, upvotedIds).apply();
   }



   public ImageButton delete;

   public ChatClient client;

   public TextView message;

   public ImageButton emptyTick;
   public ImageView yellowTick;
   public ImageView blueCircle;
   public ImageView greenCircle;

   public ButtonViewHolder(@NonNull ViewGroup parentView, @NonNull AttachedButtonBinding binding, Database database) {
      super(binding.getRoot());
      this.binding = binding;
      this.upVoteButton = binding.getRoot().findViewById(R.id.upVoteButton);
      this.mDatabase = database;
      this.delete = binding.getRoot().findViewById(R.id.delete);
      this.message = binding.getRoot().findViewById(R.id.message);
      this.emptyTick = binding.getRoot().findViewById(R.id.emptyTick);
      this.yellowTick = binding.getRoot().findViewById(R.id.yellowTick);
      this.blueCircle = binding.getRoot().findViewById(R.id.blueCircle);
      this.greenCircle = binding.getRoot().findViewById(R.id.greenCircle);
   }

   @Override
   public void bindData(@NonNull MessageListItem.MessageItem messageItem, @Nullable MessageListItemPayloadDiff messageListItemPayloadDiff) {
      ChatClient client = ChatClient.instance();
      Message msg = messageItem.getMessage();
      binding.message.setText(msg.getText());
      String channelId = (String) msg.getExtraData().get("channel_id");
      String uid = client.getCurrentUser().getId();
      String allowStudent = (String) msg.getExtraData().get("allow_student");
      String allowTA = (String) msg.getExtraData().get("allow_ta");
      String[] roles = getContext().getResources().getStringArray(R.array.role);
      String Student = roles[0];
      String TA = roles[1];
      String Professor = roles[2];

      delete.setVisibility(View.GONE);
      yellowTick.setVisibility(View.GONE);
      blueCircle.setVisibility(View.GONE);
      greenCircle.setVisibility(View.GONE);
      emptyTick.setVisibility(View.VISIBLE);

      mDatabase.getTickPressed(channelId, msg.getId(),"profApproved").onSuccessTask(dataSnapshot -> {
         if (dataSnapshot.exists()) {
            Object profPressed=dataSnapshot.getValue();
            if(profPressed.toString().equals("true")){
               emptyTick.setVisibility(View.GONE);
               blueCircle.setVisibility(View.VISIBLE);


            }

         }
         return null;

      });
      mDatabase.getTickPressed(channelId, msg.getId(),"taApproved").onSuccessTask(dataSnapshot -> {
         if (dataSnapshot.exists()) {
            Object taPressed=dataSnapshot.getValue();
            if(taPressed.toString().equals("true")){
               emptyTick.setVisibility(View.GONE);
               greenCircle.setVisibility(View.VISIBLE);

            }
         }
         return null;

      });
      mDatabase.getTickPressed(channelId, msg.getId(),"studentApproved").onSuccessTask(dataSnapshot -> {
         if (dataSnapshot.exists()) {
            Object studentPressed=dataSnapshot.getValue();
            if(studentPressed.toString().equals("true")){
               emptyTick.setVisibility(View.GONE);
               yellowTick.setVisibility(View.VISIBLE);

            }
            else {
               System.out.println("it is indeed"+studentPressed.toString());
            }

         }
         return null;

      });



      View.OnClickListener ticklistener = new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            mDatabase.getRole(uid).onSuccessTask(dataSnapshot -> {
               if (dataSnapshot.exists()) {
                  String userRole = dataSnapshot.getValue().toString();
                  boolean permissionGrantedProf = userRole.equals("Professor");
                  boolean permissionGrantedTA = userRole.equals("TA");
                  boolean permissionQuestionOwner = msg.getUser().getId().equals(uid);

                  if (permissionGrantedProf) {
                     emptyTick.setVisibility(View.GONE);
                     blueCircle.setVisibility(View.VISIBLE);
                     mDatabase.tickPressed(channelId,msg.getId(),"profApproved");
                  }

                  if (permissionGrantedTA) {
                     emptyTick.setVisibility(View.GONE);
                     greenCircle.setVisibility(View.VISIBLE);
                     mDatabase.tickPressed(channelId,msg.getId(),"taApproved");
                  }

                  if (permissionQuestionOwner) {
                     emptyTick.setVisibility(View.GONE);
                     yellowTick.setVisibility(View.VISIBLE);
                     mDatabase.tickPressed(channelId,msg.getId(),"studentApproved");
                  }
               }
               return null;
            });

         }
      };

      emptyTick.setOnClickListener(ticklistener);
      yellowTick.setOnClickListener(ticklistener);
      greenCircle.setOnClickListener(ticklistener);
      blueCircle.setOnClickListener(ticklistener);






      binding.innerLayout.setOnLongClickListener(new View.OnLongClickListener() {
         @Override
         public boolean onLongClick(View view) {
            mDatabase.getRole(uid).onSuccessTask(dataSnapshot -> {
               if (dataSnapshot.exists()) {
                  String userRole = dataSnapshot.getValue().toString(); // Give userRole
                  System.out.println("USER ROLE FROM DATABASE: " + userRole);
                  boolean permissionGrantedProf = userRole.equals(Professor);
                  boolean permissionQuestionOwner = msg.getUser().getId().equals(uid);
                  if (permissionGrantedProf || permissionQuestionOwner) {

                     // Delete Button is visible after LONGCLICK
                     delete.setVisibility(View.VISIBLE);
                     // After 5s, the delete button fades away.
                     Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
                     // Problem: Animation does not activate after a second time.
                     animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }
                        @Override
                        public void onAnimationEnd(Animation animation) {
                           // Set the visibility of the delete button to GONE once the animation ends
                           delete.setVisibility(View.GONE);
                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                     });
                     delete.startAnimation(animation);
                  }
               }
               return null;
            });
            return true;
         }
      });


      binding.message.setOnLongClickListener(new View.OnLongClickListener() {
         @Override
         public boolean onLongClick(View view) {
            mDatabase.getRole(uid).onSuccessTask(dataSnapshot -> {
               if (dataSnapshot.exists()) {
                  String userRole = dataSnapshot.getValue().toString();
                  System.out.println("USER ROLE FROM DATABASE: " + userRole);
                  boolean permissionGrantedProf = userRole.equals(Professor);
                  boolean permissionQuestionOwner = msg.getUser().getId().equals(uid);
                  if (permissionGrantedProf || permissionQuestionOwner) {
                     // Delete Button is visible after LONGCLICK
                     delete.setVisibility(View.VISIBLE);
                     // After 5s, the delete button fades away.
                     Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
                     // Problem: Animation does not activate after a second time.
                     animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }
                        @Override
                        public void onAnimationEnd(Animation animation) {
                           // Set the visibility of the delete button to GONE once the animation ends
                           delete.setVisibility(View.GONE);
                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                     });
                     delete.startAnimation(animation);
                  }
               }
               return null;
            });
            return true;
         }
      });

      binding.delete.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            mDatabase.deleteMessage(channelId, msg.getId()).onSuccessTask(new SuccessContinuation<Void, Object>() {
               @NonNull
               @Override
               public Task<Object> then(Void unused) throws Exception {

                  client.channel(msg.getCid()).deleteMessage(msg.getId(), true).enqueue(result -> {
                     if (result.isSuccess()) {
                        Message deletedMessage = result.data();
                        Toast.makeText(getContext(), "Your message has been deleted.", Toast.LENGTH_SHORT).show();
                        System.out.println("The deleted message is: " + deletedMessage);
                     } else {
                        Toast.makeText(getContext(), "You cannot delete this message.", Toast.LENGTH_SHORT).show();
                        System.out.println("Message is not deleted for messageID: " + msg.getId());
                        System.out.println(result);
                     }
                  });
                  return null;
               }
            }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                  System.out.println("Error deleting message from database: " + e);
               }
            });
         }
      });

      // Must be below. WHY.
      mDatabase.getVoteCount(channelId, msg.getId()).onSuccessTask(dataSnapshot -> {
         if (dataSnapshot.exists()) {
            Object up_vote_count = dataSnapshot.getValue();
            binding.upVoteButton.setText(up_vote_count.toString());
         } else {
            binding.upVoteButton.setText("0");
         }
         return null;
      });

      binding.message.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            mDatabase.getRole(uid).onSuccessTask(dataSnapshot -> {
               if (dataSnapshot.exists()) {
                  String userRole = dataSnapshot.getValue().toString();
                  boolean permissionGrantedStudent = userRole.equals(Student) && allowStudent.equals("true");
                  boolean permissionGrantedTA = userRole.equals(TA) && allowTA.equals("true");
                  boolean permissionProf = userRole.equals(Professor);
                  if (permissionGrantedTA || permissionGrantedStudent || permissionProf) {
                     String messageId = msg.getId();
                     String newChannelId = channelId + "_" + messageId; // important to keep track of parent page for database
                     ChannelClient channelClient = client.channel("messaging", newChannelId); //uses client instance to make channel
                     Intent myintent = ThreadActivity.newIntent(getContext(), channelClient); //initialises intent
                     myintent.putExtra("messageid", newChannelId); //puts message id
                     view.getContext().startActivity(myintent); //starts activity
                     System.out.println(" Reply channel with ID: " + newChannelId + " started successfully ");
                  }
               }
               return null;
            });
         }
      });

      binding.upVoteButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            String messageId = msg.getId();

            // Contains the set of string values representing the IDs of the items that the user has upvoted.
            Set<String> upvotedIds = getUpvotedIds();
            if (!upvotedIds.contains(messageId)) {
               int current_votes = Integer.parseInt(upVoteButton.getText().toString());
               int added_votes = current_votes + 1;
               mDatabase.upVoteMessage(channelId, messageId, added_votes).onSuccessTask(new SuccessContinuation<Void, Object>() {
                  @NonNull
                  @Override
                  public Task<Object> then(Void unused) throws Exception {
                     System.out.println("UPVOTED SUCCESSFULLY!");
                     return null;
                  }
               });
               String new_votes = Integer.toString(added_votes);
               upVoteButton.setText(new_votes);
               upVoteButton.setEnabled(false); // disable the button
               addUpvotedId(messageId); // add the ID to the set of upvoted IDs
            } else {
               upVoteButton.setEnabled(false); // disable the button
            }
         }
      });
   }
}
