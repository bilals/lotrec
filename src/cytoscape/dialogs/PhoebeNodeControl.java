
/*
  File: PhoebeNodeControl.java 
  
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

package cytoscape.dialogs;

import phoebe.*;
import giny.model.*;
import giny.view.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import java.util.List;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.colorchooser.*;

/**
 * An Adobe style node organizer.
 * Thanks KK!
 */
public class PhoebeNodeControl  {

  
  GraphView view;
  double Xmin, Ymin;
  double Xmax, Ymax;
  boolean setToCircle;
  double side;

  JSlider radius, rotation, twist;

  public PhoebeNodeControl ( GraphView view ) {

    JFrame frame = new JFrame( "Node Control" );
    JTabbedPane tabbed = new JTabbedPane();

    this.view = view;

    //Add Control tabs
    tabbed.addTab( "Align", createAlignTab() );
    tabbed.addTab( "Circular", createCircularTab() );

    frame.getContentPane().add( tabbed );
    frame.pack();
    frame.setVisible( true );
  }

 

  public JComponent createAlignTab () {

        
    JPanel panel = new JPanel();
    panel.setLayout( new GridLayout( 0,1 ) );

    JPanel align = new JPanel();
    align.setBorder( new TitledBorder( "Alignment" ) );
    JPanel dist = new JPanel();
    dist.setBorder( new TitledBorder( "Distribution" ) );

    align.setLayout( new BorderLayout() );
    dist.setLayout( new BorderLayout() );

    JPanel aButtons = new JPanel();
    JPanel bButtons = new JPanel();
    JPanel dButtons = new JPanel();

    aButtons.add( createHorizontalAlignLeft() );
    aButtons.add( createHorizontalAlignCenter() );
    aButtons.add( createHorizontalAlignRight() );
    bButtons.add( createVerticalAlignTop() );
    bButtons.add( createVerticalAlignCenter() );
    bButtons.add( createVerticalAlignBottom() );

    align.add( aButtons, BorderLayout.NORTH );
    align.add( bButtons, BorderLayout.SOUTH );

    dButtons.add( createHorizontalDistributeCenter() );
    dButtons.add( createVerticalDistributeCenter() );
  

    dist.add( dButtons );
    
    panel.add( align );
    panel.add( dist );

    return panel;
  }

  public JButton createHorizontalAlignLeft () {
    return 
      new JButton(
                  new AbstractAction( "HorizontalAlignLeft" ) {
                    public void actionPerformed ( ActionEvent e ) {
                      // Do this in the GUI Event Dispatch thread...
                      SwingUtilities.invokeLater( new Runnable() {
                          public void run() {
                            // do it.
                            
                            double h;
                            NodeView node_view;
                            Node node;
                            Iterator sel_nodes;
                            sel_nodes = view.getSelectedNodes().iterator();
                            h = ( ( NodeView )sel_nodes.next() ).getXPosition();
                            while ( sel_nodes.hasNext() ) {
                              node_view = ( NodeView )sel_nodes.next();
                              if ( node_view.getXPosition() < h ) {
                                h = node_view.getXPosition();
                              }
                            }
                            
                            sel_nodes = view.getSelectedNodes().iterator();
                            while ( sel_nodes.hasNext() ) {
                              ( ( NodeView )sel_nodes.next() ).setXPosition( h );
                            }
                            // done doing it
                          }
                        }
                                                  );
                    }
                  }
                  );
  }
  
  public JButton createHorizontalAlignRight () {
    return 
      new JButton(
                  new AbstractAction( "HorizontalAlignRight" ) {
                    public void actionPerformed ( ActionEvent e ) {
                      // Do this in the GUI Event Dispatch thread...
                      SwingUtilities.invokeLater( new Runnable() {
                          public void run() {
                            // do it.
                            double h;
                            NodeView node_view;
                            Node node;
                            Iterator sel_nodes;
                            sel_nodes = view.getSelectedNodes().iterator();
                            h = ( ( NodeView )sel_nodes.next() ).getXPosition();
                            while ( sel_nodes.hasNext() ) {
                              node_view = ( NodeView )sel_nodes.next();
                              if ( node_view.getXPosition() > h ) {
                                h = node_view.getXPosition();
                              }
                            }
                            
                            sel_nodes = view.getSelectedNodes().iterator();
                            while ( sel_nodes.hasNext() ) {
                              ( ( NodeView )sel_nodes.next() ).setXPosition( h );
                            }
                           
                            // done doing it
                          }
                        }
                                                  );
                    }
                  }
                  );

  }

  public JButton createHorizontalAlignCenter () {
    return 
      new JButton(
                  new AbstractAction( "HorizontalAlignCenter" ) {
                    public void actionPerformed ( ActionEvent e ) {
                      // Do this in the GUI Event Dispatch thread...
                      SwingUtilities.invokeLater( new Runnable() {
                          public void run() {
                            // do it.
                            
                            double min;
                            double max;
                            NodeView node_view;
                            Iterator sel_nodes;
                            sel_nodes = view.getSelectedNodes().iterator();
                            min = ( ( NodeView )sel_nodes.next() ).getXPosition();
                            max = min;
                            while ( sel_nodes.hasNext() ) {
                              node_view = ( NodeView )sel_nodes.next();
                              if ( node_view.getXPosition() > max ) {
                                max = node_view.getXPosition();
                              }
                              if ( node_view.getXPosition() < min ) {
                                min = node_view.getXPosition();
                              }

                            }
                            min = ( min + (max - min) / 2 );
                            sel_nodes = view.getSelectedNodes().iterator();
                            while ( sel_nodes.hasNext() ) {
                              ( ( NodeView )sel_nodes.next() ).setXPosition( min );
                            }                        }
                             
                            // done doing it
                        
                        }
                                                  );
                    }
                  }
                  );
    
  }

  public JButton createVerticalAlignBottom () {
return 
      new JButton(
                  new AbstractAction( "VerticalAlignBottom" ) {
                    public void actionPerformed ( ActionEvent e ) {
                      // Do this in the GUI Event Dispatch thread...
                      SwingUtilities.invokeLater( new Runnable() {
                          public void run() {
                            // do it.
                            double h;
                            NodeView node_view;
                            Node node;
                            Iterator sel_nodes;
                            sel_nodes = view.getSelectedNodes().iterator();
                            h = ( ( NodeView )sel_nodes.next() ).getYPosition();
                            while ( sel_nodes.hasNext() ) {
                              node_view = ( NodeView )sel_nodes.next();
                              if ( node_view.getYPosition() > h ) {
                                h = node_view.getYPosition();
                              }
                            }
                            
                            sel_nodes = view.getSelectedNodes().iterator();
                            while ( sel_nodes.hasNext() ) {
                              ( ( NodeView )sel_nodes.next() ).setYPosition( h );
                            }
                          
                            // done doing it
                          }
                        }
                                                  );
                    }
                  }
                  );

  }

  public JButton createVerticalAlignCenter () {
    return 
      new JButton(
                  new AbstractAction( "VerticalAlignCenter" ) {
                    public void actionPerformed ( ActionEvent e ) {
                      // Do this in the GUI Event Dispatch thread...
                      SwingUtilities.invokeLater( new Runnable() {
                          public void run() {
                            // do it.
                                 
                            double min;
                            double max;
                            NodeView node_view;
                            Iterator sel_nodes;
                            sel_nodes = view.getSelectedNodes().iterator();
                            min = ( ( NodeView )sel_nodes.next() ).getYPosition();
                            max = min;
                            while ( sel_nodes.hasNext() ) {
                              node_view = ( NodeView )sel_nodes.next();
                              if ( node_view.getYPosition() > max ) {
                                max = node_view.getYPosition();
                              }
                              if ( node_view.getYPosition() < min ) {
                                min = node_view.getYPosition();
                              }

                            }
                            min = ( min + (max - min) / 2 );
                            sel_nodes = view.getSelectedNodes().iterator();
                            while ( sel_nodes.hasNext() ) {
                              ( ( NodeView )sel_nodes.next() ).setYPosition( min );
                            }                        
                            
                             
                          
                            // done doing it
                          }
                        }
                                                  );
                    }
                  }
                  );
  
  }

  public JButton createVerticalAlignTop () {
    return 
      new JButton(
                  new AbstractAction( "VerticalAlignTop" ) {
                    public void actionPerformed ( ActionEvent e ) {
                      // Do this in the GUI Event Dispatch thread...
                      SwingUtilities.invokeLater( new Runnable() {
                          public void run() {
                            // do it.
                            double h;
                            NodeView node_view;
                            Node node;
                            Iterator sel_nodes;
                            sel_nodes = view.getSelectedNodes().iterator();
                            h = ( ( NodeView )sel_nodes.next() ).getYPosition();
                            while ( sel_nodes.hasNext() ) {
                              node_view = ( NodeView )sel_nodes.next();
                              if ( node_view.getYPosition() < h ) {
                                h = node_view.getYPosition();
                              }
                            }
                            
                            sel_nodes = view.getSelectedNodes().iterator();
                            while ( sel_nodes.hasNext() ) {
                              ( ( NodeView )sel_nodes.next() ).setYPosition( h );
                            }
                          
                            // done doing it
                          }
                        }
                                                  );
                    }
                  }
                  );

  }


  public JButton createVerticalDistributeCenter () {
   
 return 
      new JButton(
                  new AbstractAction( "VerticalDistributeBottom" ) {
                    public void actionPerformed ( ActionEvent e ) {
                      // Do this in the GUI Event Dispatch thread...
                      SwingUtilities.invokeLater( new Runnable() {
                          public void run() {
                            // do it.
                            
                            double min = 0;
                            double max = 0;
                            NodeView node_view = null;
                            List sel_nodes_list = view.getSelectedNodes();

                            if ( sel_nodes_list.size() == 0 )
                              return;

                            Iterator sel_nodes;
                            sel_nodes = sel_nodes_list.iterator();
                            min = ( ( NodeView )sel_nodes.next() ).getYPosition();
                            max = min;
                            while ( sel_nodes.hasNext() ) {
                              node_view = ( NodeView )sel_nodes.next();
                              
                              if ( node_view.getYPosition() > max ) {
                                max = node_view.getYPosition();
                              }
                              if ( node_view.getYPosition() < min ) {
                                min = node_view.getYPosition();
                              }
                            }
                            
                            double diff =  ( min + (max - min) / 2 ) / sel_nodes_list.size() + node_view.getHeight() ;
                            double loc = min + (max - min) / 2;

                            sel_nodes = sel_nodes_list.iterator();
                            while ( sel_nodes.hasNext() ) {
                              node_view = ( NodeView )sel_nodes.next();
                              node_view.setYPosition( loc );
                              loc += diff;
                            }
                            // done doing it
                          }
                        }
                                                  );
                    }
                  }
                  );


  }


  public JButton createHorizontalDistributeCenter () {
      return new JButton(
                  new AbstractAction( "HorizontalDistributeCenter" ) {
                    public void actionPerformed ( ActionEvent e ) {
                      // Do this in the GUI Event Dispatch thread...
                      SwingUtilities.invokeLater( new Runnable() {
                          public void run() {
                            // do it.
                            double min = 0;
                            double max = 0;
                            NodeView node_view = null;
                            List sel_nodes_list = view.getSelectedNodes();

                            if ( sel_nodes_list.size() == 0 )
                              return;

                            Iterator sel_nodes;
                            sel_nodes = sel_nodes_list.iterator();
                            min = ( ( NodeView )sel_nodes.next() ).getXPosition();
                            max = min;
                            while ( sel_nodes.hasNext() ) {
                              node_view = ( NodeView )sel_nodes.next();
                              
                              if ( node_view.getXPosition() > max ) {
                                max = node_view.getXPosition();
                              }
                              if ( node_view.getXPosition() < min ) {
                                min = node_view.getXPosition();
                              }
                            }
                            
                            double diff =  ( min + (max - min) / 2 ) / sel_nodes_list.size() + node_view.getWidth() ;
                            double loc = min + (max - min) / 2;

                            sel_nodes = sel_nodes_list.iterator();
                            while ( sel_nodes.hasNext() ) {
                              node_view = ( NodeView )sel_nodes.next();
                              node_view.setXPosition( loc );
                              loc += diff;
                            }
                        
                            // done doing it
                          }
                        }
                                                  );
                    }
                  }
                  );


  }


  protected JComponent createCircularTab () {

    radius = new JSlider( 0, 2000 );
    radius.addChangeListener( new SliderListener() );
    
    rotation = new JSlider( 0, 360 );
    rotation.addChangeListener( new SliderListener() );

    twist = new JSlider( 0, 50000 );
    twist.addChangeListener( new SliderListener() );
    
    rotation.setValue( 90 );
    radius.setValue( 100 );
    twist.setValue( 0 );

    JPanel panel = new JPanel();
    panel.setLayout( new GridLayout( 0,1 ) );
    JPanel rad = new JPanel();
    rad.setLayout( new BorderLayout() );
    rad.setBorder( new TitledBorder( "Radius" ) );
    rad.add( radius );

    JPanel rot = new JPanel();
    rot.setBorder( new TitledBorder( "Rotation" ) );
    rot.add( rotation );

    JPanel twt = new JPanel();
    twt.setBorder( new TitledBorder( "Twist" ) );
    twt.add( twist );

    panel.add(rad);
    panel.add(rot);
    panel.add(twt);

    return panel;    
  }

  protected void updateSliders () {

    double theta;
    double thetaIncr;
   
    int ra = radius.getValue();
    int ro = rotation.getValue();
    int tw = twist.getValue();
    double radius = ( new Integer(ra) ).doubleValue();
    double rotation = ( new Integer(ro )).doubleValue();
    double radians = Math.toRadians( tw );
    NodeView node_view;
   
    List sel_nodes_list = view.getSelectedNodes();
    if ( sel_nodes_list.size() == 0 ) {
      return;
    }
    
    Iterator sel_nodes;
    sel_nodes = sel_nodes_list.iterator();
    double maxX, maxY, minX, minY;
    node_view = ( NodeView )sel_nodes.next();
    maxY = node_view.getYPosition();
    minY = maxY;
    maxX = node_view.getXPosition();
    minX = maxX;
    while ( sel_nodes.hasNext() ) {
      node_view = ( NodeView )sel_nodes.next();
      
      if ( node_view.getXPosition() > maxX ) {
        maxX = node_view.getXPosition();
      }
      if ( node_view.getXPosition() < minX ) {
        minX = node_view.getXPosition();
      }
      if ( node_view.getYPosition() > maxY ) {
        maxY = node_view.getYPosition();
      }
      if ( node_view.getYPosition() < minY ) {
        minY = node_view.getYPosition();
      }
    }
    double midX = minX + ( ( maxX - minX ) /2 );
    double midY = minY + ( ( maxY - minY ) /2 );
      
    theta = Math.acos( ( ( maxX - minX ) /2 ) / radius );
    theta += Math.toRadians( rotation );
    thetaIncr = ( Math.PI - (2 * theta)  ) / sel_nodes_list.size() ;
    
    sel_nodes = sel_nodes_list.iterator();
    while ( sel_nodes.hasNext() ) {
      node_view = ( NodeView )sel_nodes.next();
      node_view.setXPosition( Math.cos(theta + Math.toRadians( radians )) * radius + midX );
      node_view.setYPosition( Math.sin(theta + Math.toRadians( radians )) * radius + midY );
      theta += thetaIncr;
    }
    
  }

  public  class SliderListener implements ChangeListener {

   public  SliderListener (  ) {
    }

   public   void stateChanged(ChangeEvent e) {
   
      updateSliders();
   
   }

  } // class SliderListener

}
 
