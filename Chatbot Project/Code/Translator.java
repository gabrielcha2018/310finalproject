
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


// defalut translate to english 
  public void translate(String word) throws Exception {

 String url = "https://translate.googleapis.com/language/translate/v2?&target=en&key="
 + "AIzaSyApag4W3awsC2-yPCsQS5SpTSOlxjrxt1Y&q=" +

 URLEncoder.encode(word,"utf-8");
 connect( url); 
            
            } 

  // // tranlste to other language   //still working on it 
  // public void customizetranslate(String word, String customize ) throws Exception {

  //   String customizeurl = 
  //   "https://translate.googleapis.com/translate_a/single?" +
  //   "client=gtx" +
  //   "&sl=en"+
  //   "&tl=" + customize +
  //   "&dt=t&q=" + word;  
  //     connect (customizeurl);
      
  // } 

              

  


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

  //  JSONObject obj = new JSONObject(jsonstring);
  //  JSONArray jsonArray = obj.getJSONArray(someField);

  // for(int i=0;i<jsonArray.length();i++){
  //     System.out.println("array is " + jsonArray.get(i));

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

// // testing 
// public static void main(String[] args) throws Exception {
//   Translator s1= new Translator ();

//    s1.translate("你好");

//    String [] inputinfo= s1.returnArray( ) ;
//   // System.out.println(inputinfo[0]);
//   // System.out.println(inputinfo[1]);

//   // s1.customizetranslate("hello", "zh-CN");

//   System.out.println(inputinfo[0]);
//   System.out.println(inputinfo[1]);



// }


}



