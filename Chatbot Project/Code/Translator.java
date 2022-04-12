
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

// this program by fault it wil translate text to english 


public class Translator {

static String thetranslatedtext;// the tranlasted text 
static String source;// the source of the user input 


   public Translator (){  }



  public void translate(String word) throws Exception {

    // connect the google api
//  String url = "https://translate.googleapis.com/language/translate/v2?target=en&key="
//               + "AIzaSyApag4W3awsC2-yPCsQS5SpTSOlxjrxt1Y&q=" +
//               word;

 String url = "https://translate.googleapis.com/language/translate/v2?&target=en&key="
              + "AIzaSyApag4W3awsC2-yPCsQS5SpTSOlxjrxt1Y&q=" +
             
              URLEncoder.encode(word,"utf-8");
                      

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
         gettheresult(response.toString());
    
  } 

  
  public static void main(String[] args) throws Exception {
         Translator s1= new Translator ();
    
          s1.translate("hello");

         String [] inputinfo= s1.returnArray( ) ;
         System.out.println(inputinfo[0]);
         System.out.println(inputinfo[1]);


  }


public static void  gettheresult(String jsonstring) throws ParseException{

    // below it is the example of  json file return from the api 
    // { "data": {  "translations": [ {  "translatedText": "Hello", "detectedSourceLanguage": "fr" } ] }}

  JSONParser parser = new JSONParser();  
  // convert the string into json object 
   JSONObject json = (JSONObject) parser.parse(jsonstring);  
   // navigating the inside the json object to get the target value 
   JSONObject object = (JSONObject) json.get("data");  
   JSONArray object1=  (JSONArray) object.get("translations");
   JSONObject attribute = (JSONObject) object1.get(0);
     thetranslatedtext= attribute.get("translatedText").toString();
     source=attribute.get("detectedSourceLanguage").toString();
    
    // String []  info= new String [2]; 
    // info [0]=thetranslatedtext;
    // info [1]=  source ;
    // return info;

  //  System.out.println("translatedText is " +  thetranslatedtext);
   //  return gettranslatedtest();  // to do  return the arraylist 


}

  public static String  gettranslatedtext(){
       return  thetranslatedtext;
  }

  public static String  getthesource(){
    return  source;
}


public String [] returnArray( )  
{  
   String []  info= new String [2]; 
    info [0]=gettranslatedtext();
    info [1]=  getthesource();
    return info;


}  


}


    // below it is the example of  json file return from the api
    // { "data": {  "translations": [ {  "translatedText": "Hello",   "detectedSourceLanguage": "fr"   } ] }}
  // JSONParser parser = new JSONParser();  
  // // convert the string into json object 
  //  JSONObject json = (JSONObject) parser.parse(result);  
  //  // navigating the inside the json object to get the target value 
  //  JSONObject object = (JSONObject) json.get("data");  
  //  JSONArray object1=  (JSONArray) object.get("translations");
  //  JSONObject attribute = (JSONObject) object1.get(0);
  //  thetranslatedtext= attribute.get("translatedText").toString() ;
  // System.out.println("translatedText is " +  thetranslatedtext);
  // System.out.println("the source language is "+ attribute.get("detectedSourceLanguage"));


