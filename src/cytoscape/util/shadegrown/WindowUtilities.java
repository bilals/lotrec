
/*
  File: WindowUtilities.java 
  
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

package cytoscape.util.shadegrown;

import java.awt.Window;
import java.awt.Frame;
import java.awt.Component;
import java.awt.Container;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

import javax.swing.JDesktopPane;
import javax.swing.JWindow;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.Timer;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import java.util.Map;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * WindowUtilities keeps track of open windows and may close down the JVM when
 * all primary windows are closed (see {@link #setExitJVMWhenAllWindowsClose}
 * and {@link #addPrimaryWindow( Window )}).
 */
// TODO: add createDialog stuff...
public abstract class WindowUtilities
  implements WindowConstants {

  protected static JWindow splashWindow = null;
  protected static JComponent splashContent = null;
  // protected static javax.swing.Timer splashTimer = null;

 

  public static void centerWindowOnScreen ( Window window ) {
    centerWindowSize( window );
    centerWindowLocation( window );
    window.setVisible( true );
  } // static centerWindowOnScreen( Window )

  public static void centerWindowSize ( Window window ) {
    Dimension screen_size =
      Toolkit.getDefaultToolkit().getScreenSize();
    GraphicsConfiguration configuration =
      GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    Insets screen_insets =
      Toolkit.getDefaultToolkit().getScreenInsets( configuration );

    screen_size.width -= screen_insets.left;
    screen_size.width -= screen_insets.right;
    screen_size.height -= screen_insets.top;
    screen_size.height -= screen_insets.bottom;

    Dimension frame_size = window.getSize();
    frame_size.width = ( int )( screen_size.width * .75 );
    frame_size.height = ( int )( screen_size.height * .75 );
    window.setSize( frame_size );
  } // static centerWindowSize( Window )

 
  public static void centerWindowLocation ( Window window ) {
    Dimension screen_size =
      Toolkit.getDefaultToolkit().getScreenSize();
    GraphicsConfiguration configuration =
      GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    Insets screen_insets =
      Toolkit.getDefaultToolkit().getScreenInsets( configuration );

    screen_size.width -= screen_insets.left;
    screen_size.width -= screen_insets.right;
    screen_size.height -= screen_insets.top;
    screen_size.height -= screen_insets.bottom;

    Dimension frame_size = window.getSize();
    window.setLocation(
      ( ( screen_size.width / 2 ) -
        ( frame_size.width / 2 ) ) + screen_insets.left,
      ( ( screen_size.height / 2 ) -
        ( frame_size.height / 2 ) ) + screen_insets.top
    );
  } 

  public static void showSplash ( ImageIcon image, int milliseconds ) {
    showSplash( image, milliseconds, true );
  } 

  public static void showSplash (
    ImageIcon image,
    int milliseconds,
    boolean start_timer
  ) {
    showSplash( new JLabel( image ), milliseconds, start_timer );
  } 

  public static void showSplash ( JComponent content, int milliseconds ) {
    showSplash( content, milliseconds, true );
  } 

  public static void showSplash (
    JComponent content,
    int milliseconds,
    boolean start_timer
  ) {
    hideSplash();
    if( splashWindow == null ) {
      splashWindow = new JWindow();
    }
    splashContent = content;
    splashWindow.getContentPane().add( splashContent );
    splashWindow.pack();
    centerWindowLocation( splashWindow );
    splashWindow.setVisible( true );
    splashWindow.setAlwaysOnTop(true);


    splashContent.addMouseListener( 
                                   new MouseListener () {
                                     public  void 	mouseClicked(MouseEvent e) {
                                       hideSplash();
                                     }
                                     
                                     public void 	mouseEntered(MouseEvent e) {}
                                     
                                     public void 	mouseExited(MouseEvent e){}
                                     
                                     public void 	mousePressed(MouseEvent e){}
                                     
                                     public void 	mouseReleased(MouseEvent e) {}
                                   }
                                   );


  } 


  public static void hideSplash () {
    if( ( splashWindow != null ) && splashWindow.isVisible() ) {
      splashWindow.setVisible( false );
      if( splashContent != null ) {
        splashWindow.getContentPane().remove( splashContent );
        splashContent = null;
      }
    }
  } 

} 
