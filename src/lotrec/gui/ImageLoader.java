
package lotrec.gui;

import java.net.URL;

public class ImageLoader {
    
    public static String IMAGES_PATH = "/lotrec/images/";
    public static String IMAGES_PATH_WITHOUT_BCKSLASH = "images/";
    
    public ImageLoader() {
    }
    
    public static javax.swing.ImageIcon getImageIcon(String imageName, java.awt.Component cmp){
        try{
            URL url = cmp.getClass().getResource(IMAGES_PATH + imageName);
            return new javax.swing.ImageIcon(url);
        }catch(Exception e){}
        return null;
    }
    
}
