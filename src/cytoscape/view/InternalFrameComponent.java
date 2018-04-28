/*
File: InternalFrameComponent.java

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
package cytoscape.view;

// imports
import ding.view.DGraphView;
import ding.view.DingCanvas;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.Printable;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;

/**
 * This class manages the JLayeredPane that resides in
 * each internal frame of cytoscape.  Its intended to be the
 * class which encapsulates the multiple canvases that are created 
 * by the DGraphView class.
 */
public class InternalFrameComponent extends JComponent implements Printable {

    /**
     * z-order enumeration
     */
    private static enum ZOrder {

        BACKGROUND_PANE, NETWORK_PANE, FOREGROUND_PANE;

        int layer() {
            if (this == BACKGROUND_PANE) {
                return -30000;
            }
            if (this == NETWORK_PANE) {
                return 0;
            }
            if (this == FOREGROUND_PANE) {
                return 301;
            }
            return 0;
        }
    }
    /**
     * ref to the JInternalFrame's JLayeredPane
     */
    private JLayeredPane layeredPane;
    /**
     * ref to background canvas
     */
    private DingCanvas backgroundCanvas;
    /**
     * ref to network canvas
     */
    private DingCanvas networkCanvas;
    /**
     * ref to foreground canvas
     */
    private DingCanvas foregroundCanvas;

    /**
     * Constructor.
     *
     * @param layeredPane JLayedPane
     * @param dGraphView dGraphView
     */
    public InternalFrameComponent(JLayeredPane layeredPane, DGraphView dGraphView) {

        // init members
        this.layeredPane = layeredPane;
        this.backgroundCanvas = dGraphView.getCanvas(DGraphView.Canvas.BACKGROUND_CANVAS);
        this.networkCanvas = dGraphView.getCanvas(DGraphView.Canvas.NETWORK_CANVAS);
        this.foregroundCanvas = dGraphView.getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS);

        // set default ordering
        initLayeredPane();
    }

    /**
     * Our implementation of Component setBounds().  If we don't do this, the
     * individual canvas do not get rendered.
     *
     * @param x int
     * @param y int
     * @param width int
     * @param height int
     */
    public void setBounds(int x, int y, int width, int height) {

        // call reshape on each innercanvas
        backgroundCanvas.setBounds(x, y, width, height);
        networkCanvas.setBounds(x, y, width, height);
        foregroundCanvas.setBounds(x, y, width, height);
    }

    /**
     * Our implementation of the Printable interface.
     *
     * @param graphics Graphics (context into which the page is drawn)
     * @param pageFormat PageFormat (size and orientation of the page being drawn)
     * @param pageIndex int (the zero based index of the page being drawn)
     *
     * @return PAGE_EXISTS if teh page is rendered or NO_SUCH_PAGE if pageIndex specifies non-existent page
     * @throws PrinterException
     */
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex == 0) {
            ((Graphics2D) graphics).translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            //TODO look at whether we should be clipping like this
            graphics.clipRect(0, 0, backgroundCanvas.getWidth(), backgroundCanvas.getHeight());
            backgroundCanvas.print(graphics);
            networkCanvas.print(graphics);
            foregroundCanvas.print(graphics);
            return PAGE_EXISTS;
        } else {
            return NO_SUCH_PAGE;
        }
    }

    /**
     * Places the canvas on the layeredPane in the following manner:
     * top - bottom: foreground, network, background
     */
    private void initLayeredPane() {

        // remove all canvases from layered pane
        layeredPane.removeAll();

        // foreground followed by network followed by background
        layeredPane.add(backgroundCanvas, new Integer(ZOrder.BACKGROUND_PANE.layer()));
        layeredPane.add(networkCanvas, new Integer(ZOrder.NETWORK_PANE.layer()));
        layeredPane.add(foregroundCanvas, new Integer(ZOrder.FOREGROUND_PANE.layer()));
    }
}
