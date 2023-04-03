package com.example.loginpage.utility;

import android.os.Bundle;

import java.net.MalformedURLException;

import io.getstream.client.Client;
import io.getstream.core.http.Token;

/**
 * @author saran
 * @date 3/4/2023
 */

public class BundleDeliveryMan {
   private static BundleDeliveryMan sBundleDeliveryMan;
   private Client mClient;
   private final String api_key = "c6ys6m7794gr";

   private final String secret_key = "4mx3y6jmz23j3y347me4kpar2kwrttf9br3d86tu4sf4e84ya6j3vpqpqm7u5968";
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
      bundle.putString("api_key",api_key);
      bundle.putString("userToken",userToken);
      bundle.putString("uid",uid);
      return bundle;
   }
   public String deliverAPI(){
      return api_key;
   }
   private void connect() throws MalformedURLException {
      mClient = Client.builder(api_key,secret_key).build();
   }
}
