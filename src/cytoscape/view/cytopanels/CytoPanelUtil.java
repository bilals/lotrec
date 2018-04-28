
/*
  File: CytoPanelUtil.java 
  
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

//     
// $Id: CytoPanelUtil.java 7760 2006-06-26 16:28:49Z mes $
//------------------------------------------------------------------------------

// our package
package cytoscape.view.cytopanels;

// imports
import java.awt.Point;
import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.SwingConstants;

/**
 * Contains methods to assist with various
 * tasks performed by the CytoPanel API.
 *
 * @author Ethan Cerami, Ben Gross
 */
public class CytoPanelUtil {

	/**
	 * String used to compare against os.name System property - 
	 * to determine if we are running on Windows platform.
     */
	static final String WINDOWS = "windows";

    /**
     * Inset between owner frame and floating window.
     */
    private static final int INSET = 5;

    /**
     * Gets Location of External Frame, based on current UI Dimensions.
     *
     * @param screenDimension  Current Screen Dimensions.
     * @param containerBounds  Container Bounds Rectangle.
     * @param frameDimension   Current Frame Dimensions.
     * @param compassDirection Compass Direction, SwingConstants.
     * @return Point Object.
     */
    public static Point getLocationOfExternalFrame(Dimension screenDimension,
            Rectangle containerBounds, Dimension frameDimension,
            int compassDirection, boolean outputDiagnostics) {
        if (outputDiagnostics) {
            outputDiagnostics(screenDimension,
							  containerBounds,
							  frameDimension,
							  compassDirection);
        }

        //  Get Location and Dimension of Container
        Point containerLocation = containerBounds.getLocation();
        int containerWidth = (int) containerBounds.getWidth();
        int containerHeight = (int) containerBounds.getHeight();

        //  Get Screen Dimensions
        int screenWidth = (int) screenDimension.getWidth();
        int screenHeight = (int) screenDimension.getHeight();

        //  Initialize Point
        Point p = new Point(containerLocation.x, containerLocation.y);

        //  Set Point Based on Compass Direction
        if (compassDirection == SwingConstants.WEST) {
            p.x = containerLocation.x - INSET -
                    (int) frameDimension.getWidth();
        } else if (compassDirection == SwingConstants.EAST) {
            p.x = containerLocation.x + INSET + (int) containerWidth;
        } else if (compassDirection == SwingConstants.SOUTH) {
            p.y = containerLocation.y + INSET + (int) containerHeight;
        }

        //  Remove any negative coordinates
        p.x = Math.max(0, p.x);
        p.y = Math.max(0, p.y);

        if (p.x + frameDimension.getWidth() > screenWidth) {
            //  Adjust for right most case
            p.x = screenWidth - (int) frameDimension.getWidth();
        }
        if (p.y + frameDimension.getHeight() > screenHeight) {
            //  Adjust for bottom-most case
            p.y = screenHeight - (int) frameDimension.getHeight();
        }
        return p;
    }

	/**
	 * Determines if we are running on Windows platform.
	 */
	public boolean isWindows() {
		String os = System.getProperty("os.name");
		return os.regionMatches(true, 0, WINDOWS, 0, WINDOWS.length());
	}

    /**
     * Outputs Diagnostics Related to Screen/Frame Dimensions.
     */
    private static void outputDiagnostics(Dimension screenDimension,
										  Rectangle containerBounds,
										  Dimension preferredSizeOfPanel,
										  int compassDirection) {
        System.err.println("Compass Direction:  " + compassDirection);
        System.err.println("Screen Dimension:  " + screenDimension);
        System.err.println("Container Bounds:  " + containerBounds.toString());
        System.err.println("Preferred Size of Panel:  " +
						   preferredSizeOfPanel.toString());
    }
}
