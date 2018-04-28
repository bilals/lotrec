
/*
  File: CreditScreen.java 
  
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

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.Timer;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.Action;
import java.net.URL;
import cytoscape.util.shadegrown.WindowUtilities;


public abstract class CreditScreen  {

	public static void showCredits ( URL url, String lines ) {
		
		final JWindow window = new JWindow();
		final ScrollingLinesPanel panel = new ScrollingLinesPanel(url,lines);
		window.add( panel );
		window.pack();
		window.validate();
                window.setPreferredSize( panel.getPreferredSize() );
                window.requestFocusInWindow();
		WindowUtilities.centerWindowLocation( window );
		window.setAlwaysOnTop(true);
		window.setVisible( true );

		Action scrollText = new AbstractAction() {
			boolean shouldDraw = false;
			public void actionPerformed(ActionEvent e) {
				panel.incrementYPos();	
				window.repaint();
			}
		};

		final javax.swing.Timer timer = new javax.swing.Timer(100, scrollText);

		window.addMouseListener(
			new MouseListener () {
				public  void      mouseClicked(MouseEvent e) {
					window.dispose();
					timer.stop();
				}
				public void       mouseEntered(MouseEvent e) {}
				public void       mouseExited(MouseEvent e){}
				public void       mousePressed(MouseEvent e){}
				public void       mouseReleased(MouseEvent e) {}
			}
		);

		timer.start();
	}


	private static class ScrollingLinesPanel extends JPanel {

		int yPos;
		int xPos;
		ImageIcon background;
		String lines;

		public ScrollingLinesPanel(URL url, String lines) {
			super();
			background = new ImageIcon(url);
			this.lines = lines;
			yPos =  background.getIconHeight();
			xPos = (int)((float)background.getIconWidth()/2.0f);
			setOpaque( false );
			setPreferredSize( new Dimension(background.getIconWidth(), 
							background.getIconHeight()));
		}

		protected void paintComponent(Graphics g) {
			g.drawImage(background.getImage(), 0, 0, null);
			((Graphics2D)g).setPaint(Color.white);
			int end = lines.indexOf("\n");
			int begin = 0;
			int i = 1;
			int y = yPos;
			while ( end > 0) {
				String sub = lines.substring(begin,end);
				y = yPos +(12*i);
				if ( y > 80 )
					g.drawString(sub,xPos,y);
				begin = end+1;
				end = lines.indexOf("\n",begin);
				i++;
			}
			super.paintComponent(g);
		}

		public void incrementYPos() {
			yPos -= 2;
		}
	}
}
