package com.example.loginpage.customviews;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
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
import com.example.loginpage.databinding.AttachedButtonBinding;
import com.example.loginpage.utility.Database;
import com.getstream.sdk.chat.adapter.MessageListItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.models.Message;
import io.getstream.chat.android.ui.message.list.adapter.BaseMessageItemViewHolder;
import io.getstream.chat.android.ui.message.list.adapter.MessageListItemPayloadDiff;

/**
 * @author ryner
 * @date 25/2/2023
 */

class ReplyViewHolder extends BaseMessageItemViewHolder<MessageListItem.MessageItem> {
    AttachedButtonBinding binding;
    public Button upVoteButton;
    private final Database mDatabase = Database.getInstance();

    public TextView message;
    public ImageButton delete;
    public ImageButton emptyTick;
    public ImageView pinkTick;
    public ImageView maroonCircle;
    public ImageView redCircle;
    private static final String PREF_NAME = "upvote_pref";
    private static final String KEY_UPVOTED_IDS = "upvoted_ids";
    private boolean emptyTickClicked = false;
    private Set<String> getUpvotedIds() {
        SharedPreferences preferences = getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getStringSet(KEY_UPVOTED_IDS, new HashSet<>());
    }

    // method to add an ID to the set of upvoted IDs in shared preference
    private void addUpvotedId(String userId, String messageId) {
        SharedPreferences preferences = getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> upvotedIds = preferences.getStringSet(KEY_UPVOTED_IDS, new HashSet<>());
        String upvotedId = userId + ":" + messageId;
        upvotedIds.add(upvotedId);
        preferences.edit().putStringSet(KEY_UPVOTED_IDS, upvotedIds).apply();
    }

    public ReplyViewHolder(@NonNull ViewGroup parentView, @NonNull AttachedButtonBinding binding){
        super(binding.getRoot());
        this.binding = binding;
        this.upVoteButton = binding.getRoot().findViewById(R.id.upVoteButton);
        this.delete = binding.getRoot().findViewById(R.id.delete);
        this.message = binding.getRoot().findViewById(R.id.message);
        this.emptyTick = binding.getRoot().findViewById(R.id.emptyTick);
        this.pinkTick = binding.getRoot().findViewById(R.id.pinkTick);
        this.maroonCircle = binding.getRoot().findViewById(R.id.maroonCircle);
        this.redCircle = binding.getRoot().findViewById(R.id.redCircle);
    }

    @Override
    public void bindData(@NonNull MessageListItem.MessageItem messageItem, @Nullable MessageListItemPayloadDiff messageListItemPayloadDiff) {
        ChatClient client = ChatClient.instance();
        Message msg = messageItem.getMessage();
        binding.message.setText(msg.getText());
        String channelId_messageId = (String) msg.getExtraData().get("channel_id");
        String channelId = (String) msg.getExtraData().get("channel_id");
        String uid = client.getCurrentUser().getId();
        String allowStudent = (String) msg.getExtraData().get("allow_student");
        String allowTA = (String) msg.getExtraData().get("allow_ta");
        String[] roles = getContext().getResources().getStringArray(R.array.role);
        String Student = roles[0];
        String TA = roles[1];
        String Professor = roles[2];
        delete.setVisibility(View.GONE);
        pinkTick.setVisibility(View.GONE);
        maroonCircle.setVisibility(View.GONE);
        redCircle.setVisibility(View.GONE);
        emptyTick.setVisibility(View.VISIBLE);
        mDatabase.getExtraDataForReply(channelId_messageId, msg.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    try {
                        JSONObject jsonObject = new JSONObject(snapshot.getValue().toString());
                        String taApproved = jsonObject.getString("taApproved");
                        String studentApproved = jsonObject.getString("studentApproved");
                        String profApproved = jsonObject.getString("profApproved");
                        if(taApproved.equals("true") || studentApproved.equals("true") || profApproved.equals("true")){
                            emptyTick.setVisibility(View.GONE);
                        }
                        else {emptyTick.setVisibility(View.VISIBLE);}
                        if(profApproved.equals("true")){
                            redCircle.setVisibility(View.VISIBLE);
                        }
                        else{redCircle.setVisibility(View.GONE);}
                        if(taApproved.equals("true")){
                            maroonCircle.setVisibility(View.VISIBLE);
                        }
                        else{maroonCircle.setVisibility(View.GONE);}
                        if(studentApproved.equals("true")){
                            pinkTick.setVisibility(View.VISIBLE);
                        }
                        else{pinkTick.setVisibility(View.GONE);}



                        String vote_count = jsonObject.getString("vote_count");
                        binding.upVoteButton.setText(vote_count);

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    System.out.println("SNAPSHOT DOES NOT EXIST: " + snapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mDatabase.getReplyTickPressed(channelId_messageId,msg.getId(),"profApproved").onSuccessTask(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                Object profPressed=dataSnapshot.getValue();
                if(profPressed.toString().equals("true")){
                    emptyTick.setVisibility(View.GONE);
                    redCircle.setVisibility(View.VISIBLE);


                }

            }
            return null;

        });
        mDatabase.getReplyTickPressed(channelId_messageId, msg.getId(),"taApproved").onSuccessTask(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                Object taPressed=dataSnapshot.getValue();
                if(taPressed.toString().equals("true")){
                    emptyTick.setVisibility(View.GONE);
                    maroonCircle.setVisibility(View.VISIBLE);

                }
            }
            return null;

        });
        mDatabase.getReplyTickPressed(channelId_messageId, msg.getId(),"studentApproved").onSuccessTask(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                Object studentPressed=dataSnapshot.getValue();
                if(studentPressed.toString().equals("true")){
                    emptyTick.setVisibility(View.GONE);
                    pinkTick.setVisibility(View.VISIBLE);

                }
                else {
                    Log.i("ReplyViewHolder","it is indeed"+studentPressed.toString());
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
                        boolean permissionGrantedProf = userRole.equals(Professor);
                        boolean permissionGrantedTA = userRole.equals(TA);
                        boolean permissionQuestionOwner = msg.getUser().getId().equals(uid);
                        mDatabase.getExtraDataForReplies_diff(channelId_messageId, msg.getId()).onSuccessTask(dataSnap -> {
                            JSONObject jsonObject = new JSONObject(dataSnap.getValue().toString());
                            String taApproved = jsonObject.getString("taApproved");
                            String studentApproved = jsonObject.getString("studentApproved");
                            String profApproved = jsonObject.getString("profApproved");
                            System.out.println(profApproved);
                            if (permissionQuestionOwner) {
                                // send a channel event that ticks this message using the UI components below
                                ownerTick(channelId,msg,studentApproved);
                                //eventSender(LIVESTREAM,channelId,CustomEvents.OWNER_TICK);
                            }

                            else if (permissionGrantedProf) {
                                profTick(channelId,msg,profApproved);
                                //eventSender(LIVESTREAM,channelId, CustomEvents.PROF_TICK);
                            }

                            else if (permissionGrantedTA) {
                                TATick(channelId,msg,taApproved);
                                //eventSender(LIVESTREAM,channelId,CustomEvents.TA_TICK);
                            }
                            return null;


                        });
                    }
                    return null;
                });

            }
        };

        emptyTick.setOnClickListener(ticklistener);
        pinkTick.setOnClickListener(ticklistener);
        maroonCircle.setOnClickListener(ticklistener);
        redCircle.setOnClickListener(ticklistener);

        binding.innerLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mDatabase.getRole(uid).onSuccessTask(dataSnapshot -> {
                    if (dataSnapshot.exists()) {
                        String userRole = dataSnapshot.getValue().toString(); // Give userRole
                        Log.i("ReplyViewHolder","User role from database: " + userRole);
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
                        Log.i("ReplyViewHolder","User role from database: " + userRole);
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
                                Log.i("ReplyViewHolder","The deleted message is: " + deletedMessage);
                            } else {
                                Toast.makeText(getContext(), "You cannot delete this message.", Toast.LENGTH_SHORT).show();
                                Log.e("ReplyViewHolder","Message is not deleted for messageID: " + msg.getId());
                                Log.e("ReplyViewHolder",String.valueOf(result));
                            }
                        });
                        return null;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ReplyViewHolder","Error deleting message from database: " + e);
                    }
                });
            }
        });

        mDatabase.getReplyUpVoteCount(channelId_messageId,msg.getId()).onSuccessTask(dataSnapshot -> {
            if(dataSnapshot.exists()){
                Object count = dataSnapshot.getValue();
                binding.upVoteButton.setText(count.toString());
            } else {
                binding.upVoteButton.setText("0");
            }
            return null;
        });
        binding.upVoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageId = msg.getId();
                String userId = uid;
                Set<String> upvotedIds = getUpvotedIds();
                String upvotedId = userId + ":" + messageId;
                if (!upvotedIds.contains(upvotedId)){
                    int current_votes = Integer.parseInt(upVoteButton.getText().toString());
                    //int current_votes = (int) double_type_votes;
                    int added_votes = current_votes + 1;

                    mDatabase.upVoteReply(channelId_messageId,msg.getId(),added_votes).onSuccessTask(new SuccessContinuation<Void, Object>() {
                        @NonNull
                        @Override
                        public Task<Object> then(Void unused) throws Exception {
                            Log.i("ReplyViewHolder","Reply upvote is successful");
                            return null;
                        }
                    });

                    String new_votes = Integer.toString(added_votes);
                    upVoteButton.setText(new_votes);
                    upVoteButton.setEnabled(false); // disable the button
                    addUpvotedId(userId,messageId); // add the ID to the set of upvoted IDs
                }
                else{
                    upVoteButton.setEnabled(false); // disable the button
                }

            }
        });


    }
    private void ownerTick(String channelId, Message msg){
        if (!emptyTickClicked) {
            emptyTick.setVisibility(View.GONE);
            pinkTick.setVisibility(View.VISIBLE);
            mDatabase.ReplyTickPressed(channelId, msg.getId(), "studentApproved");
        }
        else {
            emptyTick.setVisibility(View.VISIBLE);
            pinkTick.setVisibility(View.GONE);
            mDatabase.ReplyTickRemoved(channelId, msg.getId(), "studentApproved");
        }

        emptyTickClicked = !emptyTickClicked;
    }
    private void ownerTick(String channelId, Message msg,String studentApproved){
        if (studentApproved.equals("false")) {
            emptyTick.setVisibility(View.GONE);
            pinkTick.setVisibility(View.VISIBLE);
            mDatabase.ReplyTickPressed(channelId, msg.getId(), "studentApproved");
        }
        else {
            emptyTick.setVisibility(View.VISIBLE);
            pinkTick.setVisibility(View.GONE);
            mDatabase.ReplyTickRemoved(channelId, msg.getId(), "studentApproved");
        }

        emptyTickClicked = !emptyTickClicked;
    }
    private void profTick(String channelId, Message msg,String profApproved){
        if (profApproved.equals("false")) {
            emptyTick.setVisibility(View.GONE);
            redCircle.setVisibility(View.VISIBLE);
            mDatabase.ReplyTickPressed(channelId, msg.getId(), "profApproved");
        }
        else {
            emptyTick.setVisibility(View.VISIBLE);
            redCircle.setVisibility(View.GONE);
            mDatabase.ReplyTickRemoved(channelId, msg.getId(), "profApproved");
        }
        emptyTickClicked = !emptyTickClicked;

    }
    private void TATick(String channelId, Message msg,String taApproved){
        if (taApproved.equals("false")) {
            emptyTick.setVisibility(View.GONE);
            maroonCircle.setVisibility(View.VISIBLE);
            mDatabase.ReplyTickPressed(channelId, msg.getId(), "taApproved");
        }
        else {
            emptyTick.setVisibility(View.VISIBLE);
            maroonCircle.setVisibility(View.GONE);
            mDatabase.ReplyTickRemoved(channelId, msg.getId(), "taApproved");
        }
        emptyTickClicked = !emptyTickClicked;

    }

}
