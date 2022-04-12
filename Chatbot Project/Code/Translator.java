
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


// this program by default it will translate text to english 


public class Translator {

static String thetranslatedtext;// the tranlasted text 
static String source;// the source of the user input 


// constructor
  public Translator (){  }


// defalut translate to english, it will auto detect the input language  
  public void translate(String query) throws Exception {

 String url = "https://translate.googleapis.com/language/translate/v2?&target=en"
                 +"&key=AIzaSyApag4W3awsC2-yPCsQS5SpTSOlxjrxt1Y&q="
                 + URLEncoder.encode(query,"utf-8");
                  connect( url); 
            
            } 

  

  public void connect(String url) throws Exception {
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

public static void  gettheresult(String jsonstring) throws ParseException{

    //if i type Bonjour
    // below it is the example of  json file return from the api 
    // { "data": {  "translations": [ {  "translatedText": "Hello", "detectedSourceLanguage": "fr" } ] }}

     JSONParser parser = new JSONParser();  
     // convert the string into json object 
    JSONObject json = (JSONObject) parser.parse(jsonstring);  
    // navigating the inside the json object to get the target value 
    JSONObject object = (JSONObject) json.get("data");  
    JSONArray object1=  (JSONArray) object.get("translations");
     JSONObject attribute = (JSONObject) object1.get(0);

     // getting the translated text
     thetranslatedtext= attribute.get("translatedText").toString();
      // getting the source language
     source=attribute.get("detectedSourceLanguage").toString();

  }
    



  public static String  gettranslatedtext(){  return  thetranslatedtext; }

  public static String  getthesource(){   return  source;   }


public String [] returnArray( )  
{  
   String []  info= new String [2]; 
    info [0]=gettranslatedtext();
    info [1]=  getthesource();
    return info;
}  

// // testing 
// public static void main(String[] args) throws Exception {
   
//      Translator s1= new Translator ();
//       s1.translate("Bonjour");
//       String [] inputinfo= s1.returnArray( );
//      System.out.println(inputinfo[0]);
//      System.out.println(inputinfo[1]);
// }


}



