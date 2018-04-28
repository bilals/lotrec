
package lotrec.resources;

import java.util.Locale;

public class ResourcesProvider {
    
    private static java.util.Locale currentLocale;
    
    public ResourcesProvider() {
    }
    
    public static java.util.Locale getCurrentLocale(){
        return currentLocale;
    }
    
    public static void setCurrentLocale(java.util.Locale userCurrentLocale){
        currentLocale = userCurrentLocale;
        Locale.setDefault(currentLocale);
    }   
}
