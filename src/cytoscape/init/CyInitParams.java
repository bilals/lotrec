package cytoscape.init;

import java.util.List;
import java.util.Properties;

/**
 * An interface that describes the initialization parameters needed 
 * by cytoscape.
 */
public interface CyInitParams {

   public Properties getProps();

   public Properties getVizProps();

   public List getGraphFiles();

   public List getEdgeAttributeFiles();

   public List getNodeAttributeFiles();

   public List getExpressionFiles();

   public List getPlugins();

   public String getSessionFile();

   public int getMode();

   public String[] getArgs();

   public static final int ERROR = 0;
   public static final int GUI = 1;
   public static final int TEXT = 2;
   public static final int LIBRARY = 3;
   public static final int EMBEDDED_WINDOW = 4;

}


