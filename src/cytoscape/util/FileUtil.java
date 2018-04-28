
/*
  File: FileUtil.java 
  
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
  
  The Cytoscape Consortium is: 
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies
  
  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.
  
  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute 
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute 
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute 
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.
  
  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package cytoscape.util;

import java.awt.FileDialog;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;

import javax.swing.JFileChooser;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.task.TaskMonitor;

/**
 * Provides a platform-dependent way to open files. Mainly
 * because Mac would prefer that you use java.awt.FileDialog
 * instead of the Swing FileChooser.
 */
public abstract class FileUtil {

  public static int LOAD = FileDialog.LOAD;
  public static int SAVE = FileDialog.SAVE;
  public static int CUSTOM = LOAD + SAVE;
  
  /**
   * A string that defines a simplified java regular expression for a URL.
   * This may need to be updated to be more precise.
   */
  public static final String urlPattern = "^(jar\\:)?(\\w+\\:\\/+\\S+)(\\!\\/\\S*)?$";


  /**
   * Returns a File object, this method should be used instead
   * of rolling your own JFileChooser.
   *
   * @return the location of the selcted file
   * @param title the title of the dialog box
   * @param load_save_custom a flag for the type of file dialog
   */
 public static File getFile ( String title, 
                              int load_save_custom ) {
   return getFile( title,
                   load_save_custom,
                   new CyFileFilter[] {},
                   null,
                   null );
 }

 /**
   * Returns a File object, this method should be used instead
   * of rolling your own JFileChooser.
   *
   * @return the location of the selcted file
   * @param title the title of the dialog box
   * @param load_save_custom a flag for the type of file dialog
   * @param filters an array of CyFileFilters that let you filter
   *                based on extension
   */
 public static File getFile ( String title, 
                                int load_save_custom,
                              CyFileFilter[] filters ) {
   return getFile( title,
            load_save_custom,
            filters,
            null,
            null );
 }

  /**
   * Returns a File object, this method should be used instead
   * of rolling your own JFileChooser.
   *
   * @return the location of the selcted file
   * @param title the title of the dialog box
   * @param load_save_custom a flag for the type of file dialog
   * @param filters an array of CyFileFilters that let you filter
   *                based on extension
   * @param start_dir an alternate start dir, if null the default
   *                  cytoscape MUD will be used
   * @param custom_approve_text if this is a custom dialog, then
   *                            custom text should be on the approve
   *                            button.
   */
  public static File getFile ( String title, 
                                int load_save_custom,
                                CyFileFilter[] filters,
                                String start_dir,
                                String custom_approve_text ) {
    File[] result = getFiles(title,
    		load_save_custom,
    		filters,
    		start_dir,
    		custom_approve_text,
    		false);
    
    return (result == null || result.length <= 0) ? null : result[0];
  }

  /**
   * Returns an array of File objects, this method should be used instead
   * of rolling your own JFileChooser.
   *
   * @return the location of the selcted file
   * @param title the title of the dialog box
   * @param load_save_custom a flag for the type of file dialog
   * @param filters an array of CyFileFilters that let you filter
   *                based on extension
   */
  public static File[] getFiles ( String title, 
          int load_save_custom,
        CyFileFilter[] filters ) {
	   return getFiles( title,
	            load_save_custom,
	            filters,
	            null,
	            null,
	            true);	  
  }

  /**
   * Returns a list of File objects, this method should be used instead
   * of rolling your own JFileChooser.
   *
   * @return and array of selected files, or null if none are selected
   * @param title the title of the dialog box
   * @param load_save_custom a flag for the type of file dialog
   * @param filters an array of CyFileFilters that let you filter
   *                based on extension
   * @param start_dir an alternate start dir, if null the default
   *                  cytoscape MUD will be used
   * @param custom_approve_text if this is a custom dialog, then
   *                            custom text should be on the approve
   *                            button.
   */
  public static File[] getFiles ( String title, 
                                int load_save_custom,
                                CyFileFilter[] filters,
                                String start_dir,
                                String custom_approve_text) {
	  return getFiles (title, 
              load_save_custom,
              filters,
              start_dir,
              custom_approve_text,
              true);
  }
  
 /**
   * Returns a list of File objects, this method should be used instead
   * of rolling your own JFileChooser.
   *
   * @return and array of selected files, or null if none are selected
   * @param title the title of the dialog box
   * @param load_save_custom a flag for the type of file dialog
   * @param filters an array of CyFileFilters that let you filter
   *                based on extension
   * @param start_dir an alternate start dir, if null the default
   *                  cytoscape MUD will be used
   * @param custom_approve_text if this is a custom dialog, then
   *                            custom text should be on the approve
   *                            button.
   * @param multiselect Enable selection of multiple files (Macs are
   *                    still limited to a single file because we use
   *                    FileDialog there -- is this fixed in Java 1.5?)
   */
  public static File[] getFiles ( String title, 
                                int load_save_custom,
                                CyFileFilter[] filters,
                                String start_dir,
                                String custom_approve_text,
                                boolean multiselect) {

    File start = null;
    if ( start_dir == null ) {
      start = CytoscapeInit.getMRUD();
    } else {
      start = new File( start_dir );
    }

    String osName = System.getProperty("os.name" );
    //System.out.println( "Os name: "+osName );
    if ( osName.startsWith( "Mac" ) ) {
    
      // this is a Macintosh, use the AWT style file dialog

      FileDialog chooser = new FileDialog( Cytoscape.getDesktop(),
                                           title,
                                           load_save_custom );

      // we can only set the one filter; therefore, create a special
      // version of CyFileFilter that contains all extensions
      CyFileFilter fileFilter = new CyFileFilter();
      for (int i = 0; i < filters.length; i++) {
        Iterator iter;
        for (iter = filters[i].getExtensionSet().iterator(); iter.hasNext(); ) {
          fileFilter.addExtension((String) iter.next());
        }
      }
      fileFilter.setDescription("All network files");
      chooser.setFilenameFilter( fileFilter );

      chooser.setVisible(true);
      
      if ( chooser.getFile() != null ) {
        File[] result = new File[1];
        result[0] = new File(chooser.getDirectory()+"/"+ chooser.getFile() );
        if (chooser.getDirectory() != null) {
            CytoscapeInit.setMRUD( new File( chooser.getDirectory() ) );
        }
        return result;
      }
      return null;
      
    } else {
      // this is not a mac, use the Swing based file dialog
      JFileChooser chooser = new JFileChooser( start );
      
      // set multiple selection, if applicable
      chooser.setMultiSelectionEnabled(multiselect);
        
      // set the dialog title
      chooser.setDialogTitle( title );

      // add filters
      for ( int i = 0; i < filters.length; ++i ) {
        chooser.addChoosableFileFilter( filters[i] );
      }

      File[] result = null;
      File   tmp    = null;
      
      // set the dialog type
      if ( load_save_custom == LOAD ) {
        if ( chooser.showOpenDialog( Cytoscape.getDesktop() ) == JFileChooser.APPROVE_OPTION ) {
          if (multiselect)
             result = chooser.getSelectedFiles();
          else if ((tmp = chooser.getSelectedFile()) != null) {
        	  result = new File[1];
        	  result[0] = tmp;
          }
        }
      } else if ( load_save_custom == SAVE ) {
        if ( chooser.showSaveDialog( Cytoscape.getDesktop() ) == JFileChooser.APPROVE_OPTION ) {
            if (multiselect)
                result = chooser.getSelectedFiles();
             else if ((tmp = chooser.getSelectedFile()) != null) {
           	  result = new File[1];
           	  result[0] = tmp;
             }
        }
      } else {
        if ( chooser.showDialog( Cytoscape.getDesktop(), custom_approve_text ) == JFileChooser.APPROVE_OPTION ) {
            if (multiselect)
                result = chooser.getSelectedFiles();
             else if ((tmp = chooser.getSelectedFile()) != null) {
           	  result = new File[1];
           	  result[0] = tmp;
             }
        }
      }

      if ( result != null && start_dir == null )
      CytoscapeInit.setMRUD( chooser.getCurrentDirectory() );

      return result;
    }

  }

  public static InputStream getInputStream(String name) {
  	return getInputStream(name,null);
  }

  public static InputStream getInputStream(String name, TaskMonitor monitor) {
  	InputStream in = null;
	try {
		if ( name.matches( urlPattern ) ) { 
			URL u = new URL(name);
			in = u.openStream();
		} else
			in = new FileInputStream(name);
	} catch (IOException ioe) {
		ioe.printStackTrace();
		if ( monitor != null )
			monitor.setException(ioe, ioe.getMessage());
	}
	return in;
  }

  public static String getInputString(String filename) {
	try {
		String lineSep = System.getProperty("line.separator");
		StringBuffer sb = new StringBuffer();
		String line = null;
		BufferedReader br = new BufferedReader( new InputStreamReader( getInputStream( filename ) ) );
		while ((line = br.readLine()) != null)
			sb.append (line + lineSep);
		return sb.toString();
		
	} catch (IOException ioe) {
		ioe.printStackTrace();
	}
	System.out.println("couldn't create string from '" + filename + "'");
	return null;
  }

}
