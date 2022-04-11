
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class Translator {

static String text;

  public static String translate(String word) throws Exception {

 String url = "https://translate.googleapis.com/language/translate/v2?target=en&key="
              + "AIzaSyApag4W3awsC2-yPCsQS5SpTSOlxjrxt1Y&q=" +
              word;
                      

      URL obj = new URL(url);
      HttpURLConnection con = (HttpURLConnection) obj.openConnection();
      con.setRequestProperty("User-Agent", "Mozilla/5.0");

      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      String inputLine;
      StringBuffer response = new StringBuffer();

      while ((inputLine = in.readLine()) != null) {
          response.append(inputLine);
      }
      in.close();

    
      return  response.toString();
    
  } 



  public static void main(String[] args) throws Exception {
    text= "Bonjour";
    String result = translate(text);
    JSONParser parser = new JSONParser();  
   JSONObject json = (JSONObject) parser.parse(result);  
   JSONObject j1 = (JSONObject) json.get("data");  
   JSONArray a1=  (JSONArray) j1.get("translations");
   JSONObject tra = (JSONObject) a1.get(0);
  System.out.println("translatedText is " + tra.get("translatedText"));
  System.out.println("the source language is "+ tra.get("detectedSourceLanguage"));


  }


}




