package legoEv3;

 import java.io.BufferedReader;
 import java.io.IOException;
 import java.io.InputStreamReader;
 import java.io.PrintStream;
 import java.net.HttpURLConnection;
 import java.net.MalformedURLException;
 import java.net.URL;

 /**　
  * 通信ユーティリティ
  * setUserSessionで登録されたセッションを使って通信を行う
  * 
  *
  */
 public class Http {
         private static String userAgent = "Nico-Lib-Java-hal ver1.0.0.0";
         private static int timeout = 15000;
         private static String userSession;

         public static String getUserAgent() {
                 return userAgent;
         }

         /**
          * ユーザーエージェントを設定します
          * ライブラリ利用時はツール名+バージョンなどといった形式で設定してください
          * @param userAgent
          */
         public static void setUserAgent(String userAgent) {
                 Http.userAgent = userAgent;
         }

         public static int getTimeout() {
                 return timeout;
         }

         public static void setTimeout(int timeout) {
                 Http.timeout = timeout;
         }

         public static String getUserSession() {
                 return userSession;
         }

         public static void setUserSession(String userSession) {
                 Http.userSession = userSession;
         }

         public static String Get(String url)
                 throws IOException, MalformedURLException
         {
                 HttpURLConnection http = makeConnection(new URL(url));

                 BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
                 StringBuilder builder = new StringBuilder();
         while (reader.ready()) {
             builder.append(reader.readLine());
             builder.append('\n');
         }
                 reader.close();
                 http.disconnect();

                 return builder.toString();
         }

         public static String Post(String url, String param, String referer)
                 throws IOException, MalformedURLException
         {
                 HttpURLConnection http = postConnection(url, param, referer);

                 BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
                 StringBuilder builder = new StringBuilder();
         while (reader.ready()) {
             builder.append(reader.readLine());
             builder.append('\n');
         }

                 reader.close();
                 http.disconnect();

                 return builder.toString();
         }

         public static HttpURLConnection postConnection(String url, String param, String referer) throws IOException{
                 HttpURLConnection http = makeConnection(new URL(url));
                 http.setRequestMethod("POST");
                 http.setDoOutput(true);

                 if(referer != null){
                         http.setRequestProperty("Referer", referer);
                 }

                 PrintStream out = new PrintStream(http.getOutputStream());
         out.print(param);
         out.close();

                 return http;
         }

         /**
          * 空文字列か例外が発生しなくなるまで最高でtryCount回取得を試みます
          * @param url
          * @param tryCount
          * @return res or ""
          * @throws IOException
          * @throws MalformedURLException
          */
         public static String TryGet(String url, int tryCount)
                 throws IOException, MalformedURLException
         {
                 String res;
                 IOException ioe = null;

                 do{
                         try {
                                 res = Get(url);
                                 if(!res.equals("")) return res;

                         } catch (MalformedURLException e){
                                 throw e;
                         } catch (IOException e) {
                                 ioe = e;
                         }

                         Logger.write(String.format("%s retry : %d", url, tryCount));

                 }while(0 < --tryCount);

                 if(ioe != null) throw ioe;
                 return "";
         }

         /**
          * 空文字列か例外が発生しなくなるまで最高でtryCount回取得を試みます
          * @param url
          * @param tryCount
          * @return res
          * @throws IOException
          * @throws MalformedURLException
          */
         public static String TryPost(String url, String param, String referer, int tryCount)
                 throws IOException, MalformedURLException
         {
                 String res;
                 IOException ioe = null;

                 do{
                         try {
                                 res = Post(url, param, referer);
                                 if(!res.equals("")) return res;

                         } catch (MalformedURLException e){
                                 throw e;
                         } catch (IOException e) {
                                 ioe = e;
                         }

                         Logger.write(String.format("%s retry : %d", url, tryCount));

                 }while(0 < --tryCount);

                 if(ioe != null) throw ioe;
                 return "";
         }

         private static HttpURLConnection makeConnection(URL url) throws IOException{

                 HttpURLConnection http = (HttpURLConnection)url.openConnection();
                 http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                 http.setRequestProperty("Accept-Language", "ja;q=0.7,en;q=0.3");
                 http.setReadTimeout(timeout);
                 http.setRequestProperty("User-Agent", userAgent);

                 if(userSession != null){
                          http.setRequestProperty("Cookie", "user_session=" + userSession);
                 }

                 return http;
         }



 }
