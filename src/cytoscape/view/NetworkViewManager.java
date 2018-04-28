
/*
File: NetworkViewManager.java

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

import java.awt.Component;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.event.SwingPropertyChangeSupport;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import ding.view.DGraphView;
import ding.view.DingCanvas;
import ding.view.InnerCanvas;
import giny.view.NodeView;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class NetworkViewManager
        implements
        PropertyChangeListener,
        InternalFrameListener,
        WindowFocusListener,
        ChangeListener {

    private java.awt.Container container;
    private Map networkViewMap;
    private Map componentMap;
    private Map internalFrameComponentMap;
    private int viewCount = 0;
    protected CytoscapeDesktop cytoscapeDesktop;
    protected int VIEW_TYPE;
    protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(this);
    protected static int frame_count = 0;

    /**
     * Constructor for overiding the default Desktop view type
     */
    public NetworkViewManager(CytoscapeDesktop desktop, int view_type) {
        this.cytoscapeDesktop = desktop;
        VIEW_TYPE = view_type;
        initialize();
    }

    public NetworkViewManager(CytoscapeDesktop desktop) {
        this.cytoscapeDesktop = desktop;
        VIEW_TYPE = cytoscapeDesktop.getViewType();
        initialize();
    }

    protected void initialize() {

        if (VIEW_TYPE == CytoscapeDesktop.TABBED_VIEW) {
            //create a Tabbed Style NetworkView manager
            container = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
            ((JTabbedPane) container).addChangeListener(this);
        } else if (VIEW_TYPE == CytoscapeDesktop.INTERNAL_VIEW) {
            container = new JDesktopPane();
        //container.addComponentListener( this );
        } else if (VIEW_TYPE == CytoscapeDesktop.EXTERNAL_VIEW) {
            container = null;
        }

        // add Help hooks
        cytoscapeDesktop.getHelpBroker().enableHelp(container,
                "network-view-manager", null);

        networkViewMap = new HashMap();
        componentMap = new HashMap();
        internalFrameComponentMap = new HashMap();
    }

    public SwingPropertyChangeSupport getSwingPropertyChangeSupport() {
        return pcs;
    }

    public JTabbedPane getTabbedPane() {
        if (VIEW_TYPE == CytoscapeDesktop.TABBED_VIEW) {
            return (JTabbedPane) container;
        }
        return null;
    }

    public JDesktopPane getDesktopPane() {
        if (VIEW_TYPE == CytoscapeDesktop.INTERNAL_VIEW) {
            return (JDesktopPane) container;
        }
        return null;
    }

    /**
     * Given a CyNetwork, returns the InternalFrameComponent that wraps it.
     *
     * @param view CyNetworkView
     * @return InternalFrameComponent
     * @throws IllegalArgumentException
     */
    public InternalFrameComponent getInternalFrameComponent(CyNetworkView view)
            throws IllegalArgumentException {

        // check args
        if (view == null) {
            throw new IllegalArgumentException("NetworkViewManager.getInternalFrameComponent(), argument is null");
        }

        // outta here
        return (InternalFrameComponent) internalFrameComponentMap.get(view.getIdentifier());
    }

    public void updateNetworkTitle(CyNetwork network) {

        Object frame = networkViewMap.get(network.getIdentifier());
        if (frame instanceof JInternalFrame) {
            ((JInternalFrame) frame).setTitle(network.getTitle());
            ((JInternalFrame) frame).repaint();
        } else if (frame instanceof JFrame) {
            ((JFrame) frame).setTitle(network.getTitle());
            ((JFrame) frame).repaint();
        } else if (frame instanceof Component) {
            ((Component) frame).setName(network.getTitle());
            ((Component) frame).repaint();
        }
    }

    //------------------------------//
    // Fire Events when a Managed Network View gets the Focus
    /**
     * For Tabbed Panes
     */
    public void stateChanged(ChangeEvent e) {
        String network_id = (String) componentMap.get(((JTabbedPane) container).getSelectedComponent());

        if (network_id == null) {
            return;
        }



        firePropertyChange(CytoscapeDesktop.NETWORK_VIEW_FOCUSED,
                null,
                network_id);
    }

    /**
     * For Internal Frames
     */
    public void internalFrameActivated(InternalFrameEvent e) {
        String network_id = (String) componentMap.get(e.getInternalFrame());

        if (network_id == null) {
            return;
        }



        firePropertyChange(CytoscapeDesktop.NETWORK_VIEW_FOCUSED,
                null,
                network_id);
    }

    public void internalFrameClosed(InternalFrameEvent e) {
    }

    public void internalFrameClosing(InternalFrameEvent e) {
    }

    public void internalFrameDeactivated(InternalFrameEvent e) {
    }

    public void internalFrameDeiconified(InternalFrameEvent e) {
    }

    public void internalFrameIconified(InternalFrameEvent e) {
    }

    public void internalFrameOpened(InternalFrameEvent e) {
        internalFrameActivated(e);
    }

    /**
     * For Exteernal Frames
     */
    public void windowGainedFocus(WindowEvent e) {



        String network_id = (String) componentMap.get(e.getWindow());

        // System.out.println( " Window Gained Focus: "+ network_id );

        if (network_id == null) {
            return;
        }



        firePropertyChange(CytoscapeDesktop.NETWORK_VIEW_FOCUSED,
                null,
                network_id);

    }

    public void windowLostFocus(WindowEvent e) {
    }

    /**
     * This handles all of the incoming PropertyChangeEvents.  If you are going to have
     * multiple NetworkViewManagers, then this method should be extended such that the
     * desired behaviour is achieved, assuming of course that you want your
     * NetworkViewManagers to behave differently.
     */
    public void propertyChange(PropertyChangeEvent e) {
        // handle events

        // handle focus event
        if (e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_FOCUS) {
            String network_id = (String) e.getNewValue();
            e = null;
            unsetFocus(); // in case the newly focused network doesn't have a view
            setFocus(network_id);

            // AJK: 01/14/07 BEGIN
            //    hack to add transfer handlers to canvas
            InnerCanvas canvas = ((DGraphView) Cytoscape.getCurrentNetworkView()).getCanvas();
            if (this.getDesktopPane() != null) {
                canvas.addTransferComponent(this.getDesktopPane());
            } else if (this.getTabbedPane() != null) {
                canvas.addTransferComponent(this.getTabbedPane());
            }


        //
        } // handle putting a newly created CyNetworkView into a Container
        else if (e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_CREATED) {
            CyNetworkView new_view = (CyNetworkView) e.getNewValue();
            createContainer(new_view);
            e = null;
        } // handle a NetworkView destroyed
        else if (e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_DESTROYED) {
            CyNetworkView view = (CyNetworkView) e.getNewValue();
            removeView(view);
            e = null;
        }

    }

    /**
     * Fires a PropertyChangeEvent
     */
    public void firePropertyChange(String property_type,
            Object old_value,
            Object new_value) {



        pcs.firePropertyChange(new PropertyChangeEvent(this,
                property_type,
                old_value,
                new_value));
    }

    /**
     * Used to unset the focus of all the views. This is for the situation
     * when a network is focused but the network doesn't have a view.
     */
    protected void unsetFocus() {
        for (Iterator i = networkViewMap.values().iterator(); i.hasNext();) {

            if (VIEW_TYPE == CytoscapeDesktop.TABBED_VIEW) {
                // do nothing - one tab always needs to be shown,
                // so it may as well be the old one.
            } else if (VIEW_TYPE == CytoscapeDesktop.INTERNAL_VIEW) {
                try {
                    JInternalFrame f = (JInternalFrame) i.next();
                    f.setSelected(false);
                } catch (PropertyVetoException pve) {
                    System.out.println("Couldn't unset focus for internal frame.");
                }
            } else if (VIEW_TYPE == CytoscapeDesktop.EXTERNAL_VIEW) {
                // can't really do anything here either
                // While we can transfer focus, we can't just remove it
                // (as far as I can tell) and we don't necessarily know
                // where to transfer it to.
            }
        }
    }

    /**
     * Sets the focus of the passed network, if possible
     * The Network ID corresponds to the CyNetworkView.getNetwork().getIdentifier()
     */
    protected void setFocus(String network_id) {

        if (networkViewMap.containsKey(network_id)) {
            // there is a NetworkView for this network
            if (VIEW_TYPE == CytoscapeDesktop.TABBED_VIEW) {
                try {
                    ((JTabbedPane) container).setSelectedComponent((Component) networkViewMap.get(network_id));
                } catch (Exception e) {
                    //  e.printStackTrace();
//           System.err.println( "Network View unable to be focused" );
                }
            } else if (VIEW_TYPE == CytoscapeDesktop.INTERNAL_VIEW) {
                try {
                    ((JInternalFrame) networkViewMap.get(network_id)).setIcon(false);
                    ((JInternalFrame) networkViewMap.get(network_id)).show();
                    ((JInternalFrame) networkViewMap.get(network_id)).setSelected(true);

                } catch (Exception e) {
                    System.err.println("Network View unable to be focused");
                }
            } else if (VIEW_TYPE == CytoscapeDesktop.EXTERNAL_VIEW) {
                try {
                    ((JFrame) networkViewMap.get(network_id)).requestFocus();
                //( ( JFrame )networkViewMap.get( network_id ) ).setVisible( true );
                } catch (Exception e) {
                    System.err.println("Network View unable to be focused");
                }
            }
        }
    }

    protected void removeView(CyNetworkView view) {

        if (VIEW_TYPE == CytoscapeDesktop.TABBED_VIEW) {
            try {
                ((JTabbedPane) container).remove((Component) networkViewMap.get(view.getNetwork().getIdentifier()));
            } catch (Exception e) {
                // possible error
            }
        } else if (VIEW_TYPE == CytoscapeDesktop.INTERNAL_VIEW) {
            try {
                ((JInternalFrame) networkViewMap.get(view.getNetwork().getIdentifier())).dispose();
            } catch (Exception e) {
                System.err.println("Network View unable to be killed");
            }
        } else if (VIEW_TYPE == CytoscapeDesktop.EXTERNAL_VIEW) {
            try {
                ((JFrame) networkViewMap.get(view.getNetwork().getIdentifier())).dispose();
            } catch (Exception e) {
                System.err.println("Network View unable to be killed");
            }
        }

        networkViewMap.remove(view.getNetwork().getIdentifier());

    }

    /**
     * Contains a CyNetworkView according to the view type of this NetworkViewManager
     */
    protected void createContainer(final CyNetworkView view) {


        if (networkViewMap.containsKey(view.getNetwork().getIdentifier())) {
            // already contains
            return;
        }

        if (VIEW_TYPE == CytoscapeDesktop.TABBED_VIEW) {
            // put the CyNetworkViews Component into the Tabbed Pane
            ((JTabbedPane) container).addTab(view.getNetwork().getTitle(), view.getComponent());

            networkViewMap.put(view.getNetwork().getIdentifier(), view.getComponent());
            componentMap.put(view.getComponent(), view.getNetwork().getIdentifier());
        } else if (VIEW_TYPE == CytoscapeDesktop.INTERNAL_VIEW) {
            // create a new InternalFrame and put the CyNetworkViews Component into it
            JInternalFrame iframe = new JInternalFrame(view.getTitle(),
                    true, true, true, true);
            iframe.addInternalFrameListener(new InternalFrameAdapter() {

                public void internalFrameClosing(InternalFrameEvent e) {
                    Cytoscape.destroyNetworkView(view);
                }
            });
            ((JDesktopPane) container).add(iframe);
            // code added to support layered canvas for each CyNetworkView
            if (view instanceof DGraphView) {
                InternalFrameComponent internalFrameComp =
                        new InternalFrameComponent(iframe.getLayeredPane(), (DGraphView) view);
                iframe.setContentPane(internalFrameComp);
                internalFrameComponentMap.put(view.getNetwork().getIdentifier(), internalFrameComp);
            } else {
                System.out.println("NetworkViewManager.createContainer() - DGraphView not found!");
                iframe.getContentPane().add(view.getComponent());
            }
            iframe.pack();
            iframe.setSize(400, 400);
//            iframe.setSize(container.getSize());


// maximize the frame if the specified property is set
            try {
                String max = CytoscapeInit.getProperties().getProperty("maximizeViewOnCreate");
                if (max != null && Boolean.parseBoolean(max)) {
                    iframe.setMaximum(true);
                }

            } catch (PropertyVetoException pve) {
                pve.printStackTrace();
            }

            iframe.setVisible(true);
            iframe.addInternalFrameListener(this);

            networkViewMap.put(view.getNetwork().getIdentifier(), iframe);
            componentMap.put(iframe, view.getNetwork().getIdentifier());
        } else if (VIEW_TYPE == CytoscapeDesktop.EXTERNAL_VIEW) {
            // create a new JFrame and put the CyNetworkViews Component into it

            JFrame frame = new JFrame(view.getNetwork().getTitle());
            frame.getContentPane().add(view.getComponent());
            frame.pack();
            frame.setSize(400, 400);
            frame.setVisible(true);
            componentMap.put(frame, view.getNetwork().getIdentifier());
            networkViewMap.put(view.getNetwork().getIdentifier(), frame);
            frame.addWindowFocusListener(this);
            frame.setJMenuBar(cytoscapeDesktop.getCyMenus().getMenuBar());
        }

//        firePropertyChange(CytoscapeDesktop.NETWORK_VIEW_FOCUSED,
//                null,
//                view.getNetwork().getIdentifier());

    }
}
