package com.example.loginpage.customviews;

import android.content.Context;

import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.models.Message;

/**
 * @author saran
 * @date 12/4/2023
 */

public interface ChannelSender {
 void sendMessage(String s);
 Message construct_message(String s, ChatClient client);
 String random_id();
 void setUpListeners(Context context);
 void init(Context context);
 void setDatabase();
}
