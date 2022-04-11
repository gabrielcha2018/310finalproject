
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class Translator {



  public static String translate(String word) throws Exception {



         String url = 
              "https://translate.googleapis.com/language/translate/v2?target=en&key=AIzaSyApag4W3awsC2-yPCsQS5SpTSOlxjrxt1Y&q=" +
              word;
        

// https://translation.googleapis.com/language/translate/v2?target=zh-CN&key=AIzaSyApag4W3awsC2-yPCsQS5SpTSOlxjrxt1Y&q=test

              

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

    
      return  parseResult(response.toString());
    
  } 

  private static String parseResult(String inputJson) throws Exception {


    // for(int i =0; i < inputJson.length();i++ )
    // System.out.println(i);
    // return "finsih"; 
    
// JSONParser parser = new JSONParser();  
// JSONObject json = (JSONObject) parser.parse(stringToParse);  

    // JSONObject obj = new JSONObject();
    // JSONObject jsonObject = obj.getJSONObject("main");
    // System.out.println(jsonObject.get("temp")); 
  }

  public static void main(String[] args) throws Exception {
    String result = translate("Bonjour");
    System.out.println(result);
  }


}


// JSONParser parser = new JSONParser();  
// JSONObject json = (JSONObject) parser.parse(stringToParse);  

// JSONArray array = new JSONArray(str);  
// for(int i=0; i < array.length(); i++)   
// {  
// JSONObject object = array.getJSONObject(i);  
// System.out.println(object.getString("No"));  
// System.out.println(object.getString("Name"));  
// }  
// }  
// }  

