package com.example.loginpage.utility;

import android.os.Bundle;

import com.example.loginpage.constants.Environment;

import java.net.MalformedURLException;

import io.getstream.chat.android.client.ChatClient;
import io.getstream.client.Client;
import io.getstream.chat.android.client.channel.ChannelClient;

/**
 * @author saran
 * @date 3/4/2023
 */

public class BundleDeliveryMan {
   private static BundleDeliveryMan sBundleDeliveryMan;
   private Client mClient;

   private BundleDeliveryMan() throws MalformedURLException {connect();}

   public static BundleDeliveryMan getInstance() throws MalformedURLException {
      if(sBundleDeliveryMan == null){
         sBundleDeliveryMan = new BundleDeliveryMan();
      }
      return sBundleDeliveryMan;
   }
   public Bundle HomePageBundle(String uid){
      Bundle bundle = new Bundle();
      String userToken = mClient.frontendToken(uid).toString();
      bundle.putString("api_key", Environment.API_KEY);
      bundle.putString("userToken",userToken);
      bundle.putString("uid",uid);
      return bundle;
   }
   public ChannelClient QuestionsPageBundle(String channelType, String id){
      return ChatClient.instance().channel(channelType,id);
   }
   public Bundle HistoryPageBundle(String uid){
      Bundle b  = new Bundle();
      b.putString("uid",uid);
      return b;
   }
   public Bundle SettingsPageBundle(String uid){
      Bundle b = new Bundle();
      b.putString("uid",uid);
      return b;
   }
   private void connect() throws MalformedURLException {
      mClient = Client.builder(Environment.API_KEY,Environment.CID_KEY).build();
   }
}
