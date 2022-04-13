import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
 
public class Staticmaps {

    Staticmaps(){}

public void showimage(){

        JFrame map = new JFrame("Google Maps");
 
        try {
           

            // connect the api and showing best buy kelowna location 
            String imageUrl = "https://maps.googleapis.com/maps/api/staticmap?autoscale=2&size=400x400&maptype=roadmap&key=AIzaSyBA_Wq9JJlr-8P7RuX5D781fQQkoiqxtKs"+
           "&format=png&visual_refresh=true&markers=size:mid%7Ccolor:0xff0000%7Clabel:1%7Ckelowna+best+buy";
        
            String destinationFile = "image.jpg";
 
        
            // read the map image from Google
            // then save it to a local file: image.jpg
            URL url = new URL(imageUrl);
            InputStream is = url.openStream();
            OutputStream os = new FileOutputStream(destinationFile);
 
            byte[] b = new byte[2048];
            int length;
 
            while ((length = is.read(b)) != -1) {
                os.write(b, 0, length);
            }
 
            is.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
 
        // create a GUI component that loads the image: image.jpg
        // pop out the gui window and showing the image 
        ImageIcon imageIcon = new ImageIcon((new ImageIcon("image.jpg"))
                .getImage().getScaledInstance(630, 600,
                        java.awt.Image.SCALE_SMOOTH));
        map.add(new JLabel(imageIcon));
 
        // show the GUI window
        map.setVisible(true);
        map.pack();

    }
    //     public static void main(String[] args) throws IOException {
    //         Staticmaps s1 = new  Staticmaps();
    //         s1.showimage();
           
            
    // }
}


