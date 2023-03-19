package com.example.loginpage.utility;

/**
 * @author saran
 * @date 13/3/2023
 */

public class ColorLogger {
   private final String RED = "\\u001B[31m\t";
   private final String YELLOW = "\\u001B[33m";
   private final String GREEN = "\u001B[32m";
   private final String RESET = "\u001B[0m";
   private final String extensions = "--------------------------";

   public void logError(String s){
      System.out.println(RED + s + RESET);
   }
   public void logWarn(String s){
      s = extensions + s + extensions;
      System.out.println(YELLOW + s + RESET);
   }
   public void logSuccess(String s){
      s = extensions + s + extensions;
      System.out.println(GREEN + s + RESET);
   }
}
