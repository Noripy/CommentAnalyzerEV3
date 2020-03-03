 package legoEv3;

 /**
  * ログをとるためのクラス
  * 
  *
  */
 public class Logger {
         public static void write(Object data){
                 System.err.println(data);
         }

         public static void writeException(Exception e){
                 writeException(e, false);
         }

         public static void writeException(Exception e, boolean fatal){
                 e.printStackTrace();
         }
 }
