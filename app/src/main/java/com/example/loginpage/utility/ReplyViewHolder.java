package com.example.loginpage.utility;

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
import com.getstream.sdk.chat.adapter.MessageListItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
                        boolean permissionGrantedProf = userRole.equals(Professor);
                        boolean permissionGrantedTA = userRole.equals(TA);
                        boolean permissionQuestionOwner = msg.getUser().getId().equals(uid);

                        if (permissionGrantedProf) {
                            emptyTick.setVisibility(View.GONE);
                            redCircle.setVisibility(View.VISIBLE);
                            mDatabase.ReplyTickPressed(channelId_messageId,msg.getId(),"profApproved");
                        }

                        if (permissionGrantedTA) {
                            emptyTick.setVisibility(View.GONE);
                            maroonCircle.setVisibility(View.VISIBLE);
                            mDatabase.ReplyTickPressed(channelId_messageId,msg.getId(),"taApproved");
                        }

                        if (permissionQuestionOwner) {
                            emptyTick.setVisibility(View.GONE);
                            pinkTick.setVisibility(View.VISIBLE);
                            mDatabase.ReplyTickPressed(channelId_messageId,msg.getId(),"studentApproved");
                        }
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
                Set<String> upvotedIds = getUpvotedIds();
                if (!upvotedIds.contains(messageId)){
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
                    addUpvotedId(messageId); // add the ID to the set of upvoted IDs
                }
                else{
                    upVoteButton.setEnabled(false); // disable the button
                }

            }
        });
    }


}
