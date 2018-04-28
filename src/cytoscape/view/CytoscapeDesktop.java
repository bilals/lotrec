/*
 File: CytoscapeDesktop.java 
 
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.SwingPropertyChangeSupport;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeVersion;
import cytoscape.view.cytopanels.BiModalJSplitPane;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelImp;
import cytoscape.view.cytopanels.CytoPanelState;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.ui.VizMapUI;

/**
 * The CytoscapeDesktop is the central Window for working with Cytoscape
 */

public class CytoscapeDesktop extends JFrame implements PropertyChangeListener {

	protected long lastPluginRegistryUpdate;
	protected int returnVal;
	// --------------------//
	// Static variables

	public static final String NETWORK_VIEW_FOCUSED = "NETWORK_VIEW_FOCUSED";
	public static final String NETWORK_VIEW_FOCUS = "NETWORK_VIEW_FOCUS";
	public static final String NETWORK_VIEW_CREATED = "NETWORK_VIEW_CREATED";
	public static final String NETWORK_VIEW_DESTROYED = "NETWORK_VIEW_DESTROYED";

	// state variables
	public static final String VISUAL_STYLE = "VISUAL_STYLE";
	public static final String VIZMAP_ENABLED = "VIZMAP_ENABLED";

	/**
	 * Cytoscape UndoManager
	 */
	public static cytoscape.util.UndoManager undo;

	/**
	 * Displays all network views in TabbedPanes ( like Mozilla )
	 */
	public static final int TABBED_VIEW = 0;

	/**
	 * Displays all network views in JInternalFrames, using the mock desktop
	 * interface. ( like MS Office )
	 */
	public static final int INTERNAL_VIEW = 1;

	/**
	 * Displays all network views in JFrames, so each Network has its own
	 * window. ( like the GIMP )
	 */
	public static final int EXTERNAL_VIEW = 2;

	// --------------------//
	// Member varaibles

	/**
	 * The type of view, should be considered final
	 */
	protected int VIEW_TYPE;

	protected VisualStyle defaultVisualStyle;

	/**
	 * The network panel that sends out events when a network is selected from
	 * the Tree that it contains.
	 */
	protected NetworkPanel networkPanel;

	/**
	 * The CyMenus object provides access to the all of the menus and toolbars
	 * that will be needed.
	 */
	protected CyMenus cyMenus;

	/**
	 * The NetworkViewManager can support three types of interfaces.
	 * Tabbed/InternalFrame/ExternalFrame
	 */
	protected NetworkViewManager networkViewManager;

	/**
	 * The HelpBroker provides access to JavaHelp
	 */
	protected CyHelpBroker cyHelpBroker;

	// --------------------//
	// Event Support

	/**
	 * provides support for property change events
	 */
	protected SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport(
			this);

	/**
	 * The GraphViewController for all NetworkViews that we know about
	 */
	protected GraphViewController graphViewController;

	// --------------------//
	// VizMap Variables

	/**
	 * Provides Operations for Mapping Data Attributes of CyNetworks to
	 * CyNetworkViews
	 */
	protected VisualMappingManager vizMapper;

	/**
	 * user interface to the {@link VisualMappingManager VisualMappingManager}
	 * {@link #vizMapper vizMapper}.
	 */
	protected VizMapUI vizMapUI;

	protected String currentNetworkID;
	protected String currentNetworkViewID;

	// --------------------//
	// CytoPanel Variables
	protected CytoPanelImp cytoPanelWest;
	protected CytoPanelImp cytoPanelEast;
	protected CytoPanelImp cytoPanelSouth;

	// create cytopanel with tabs along the top for manual layout
	protected CytoPanelImp cytoPanelSouthWest = new CytoPanelImp(SwingConstants.SOUTH_WEST,
			JTabbedPane.TOP, CytoPanelState.HIDE);

	// Status Bar
	protected JLabel statusBar;

	// kono@ucsd.edu
	// Association between network and VS.
	protected HashMap vsAssociationMap;

	protected JPanel main_panel;

	// This is used to keep track of the visual style combo box in EXTERNAL_VIEW
	// mode only. Basically, we need to remove the styleBox and create a new one
	// each time a session is loaded. To do this, we need to know which
	// component
	// to remove.
	protected JComboBox currentStyleBox = null;

	// This is used to keep track of the visual style combo box in every other
	// mode
	// This is the index of the box in the toolbar. We use this so that we can
	// add and remove the stylebox from the same place.
	protected int styleBoxIndex = -1;

	// ----------------------------------------//
	// Constructors
	// ----------------------------------------//

	/**
	 * The Default constructor uses a TabbedView
	 */
	public CytoscapeDesktop() {
		this(TABBED_VIEW);
	}

	/**
	 * Create a CytoscapeDesktop that conforms the given view type.
	 * 
	 * @param view_type
	 *            one of the ViewTypes
	 */
	public CytoscapeDesktop(int view_type) {
		super("Cytoscape Desktop (New Session)");
		this.VIEW_TYPE = view_type;//TABBED_VIEW;//
		initialize();
	}

	protected void initialize() {

		// ///////////TODO: REMOVE
		this.VIEW_TYPE = INTERNAL_VIEW;//TABBED_VIEW Doesn't work at all!!! Bilal

		setIconImage(Toolkit.getDefaultToolkit().getImage(
				getClass().getResource("/cytoscape/images/c16.png")));

		// initialize Help system with Cytoscape help set - define
		// context-sensitive
		// help as we create components
		cyHelpBroker = new CyHelpBroker();

		main_panel = new JPanel();

		main_panel.setLayout(new BorderLayout());
		// enable context-sensitive help generally
		// getHelpBroker().enableHelpKey(getRootPane(), "intro", null);

		// enable context-sensitive help for main panel
		// getHelpBroker().enableHelp(main_panel, "intro", null);

		// ------------------------------//
		// Set up the Panels, Menus, and Event Firing

		networkPanel = new NetworkPanel(this);
		// enable context-sensitive help for networkPanel
		// getHelpBroker().enableHelp(networkPanel, "network-view-manager",
		// null);

		cyMenus = new CyMenus();
		// enable context-sensitive help for menus/menubar
		// getHelpBroker().enableHelp(cyMenus.getMenuBar(), "menus", null);

		networkViewManager = new NetworkViewManager(this);

		// Listener Setup
		// ----------------------------------------
		// |----------|
		// | CyMenus |
		// |----------|
		// |
		// |
		// |-----| |---------| |------| |-------|
		// | N P |------| Desktop |----| NVM |--| Views |
		// |-----| |---------| |------| |-------|
		// |
		// |
		// |-----------|
		// | Cytoscape |
		// |-----------|

		// The CytoscapeDesktop listens to NETWORK_VIEW_CREATED events,
		// and passes them on, The NetworkPanel listens for them
		// The Desktop also keeps Cytoscape up2date, but NOT via events
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(
				this);

		// The Networkviewmanager listens to the CytoscapeDesktop to know when
		// to
		// put new NetworkViews in the userspace and to get passed focus events
		// from
		// the NetworkPanel. The CytoscapeDesktop also listens to the NVM
		this.getSwingPropertyChangeSupport().addPropertyChangeListener(
				networkViewManager);
		networkViewManager.getSwingPropertyChangeSupport()
				.addPropertyChangeListener(this);

		// The NetworkPanel listens to the CytoscapeDesktop for
		// NETWORK_CREATED_EVENTS a
		// as well as for passing focused events from the Networkviewmanager.
		// The
		// CytoscapeDesktop also listens to the NetworkPanel
		this.getSwingPropertyChangeSupport().addPropertyChangeListener(
				networkPanel);
		networkPanel.getSwingPropertyChangeSupport().addPropertyChangeListener(
				this);
	
		// add a listener for node bypass

        //Bilal Change//
		// Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(new VizMapBypassNetworkListener());
        //End Bilal Change//

		// initialize undo manager
		undo = new cytoscape.util.UndoManager(cyMenus);

		// initialize Menus
		cyMenus.initializeMenus();

		// initialize Help Menu
		cyMenus.initializeHelp(cyHelpBroker.getHelpBroker());

		// create the CytoscapeDesktop
		BiModalJSplitPane masterPane = setupCytoPanels(networkPanel,
				networkViewManager);
		// note - proper networkViewManager has been properly selected in
		// setupCytoPanels()
		if (VIEW_TYPE == TABBED_VIEW || VIEW_TYPE == INTERNAL_VIEW) {
			main_panel.add(masterPane, BorderLayout.CENTER);
			main_panel.add(cyMenus.getToolBar(), BorderLayout.NORTH);
			initStatusBar(main_panel);
			setJMenuBar(cyMenus.getMenuBar());
		}
		// not sure if this is correct
		else if (VIEW_TYPE == EXTERNAL_VIEW) {
			main_panel.add(masterPane);
			cyMenus.getToolBar().setOrientation(JToolBar.VERTICAL);
			main_panel.add(cyMenus.getToolBar(), BorderLayout.EAST);
			setJMenuBar(cyMenus.getMenuBar());
		}

		// Set up the VizMapper
		setupVizMapper();

		final CytoscapeDesktop thisWindow = this;

		// don't automatically close window. Let Cytoscape.exit(returnVal)
		// handle this,
		// based upon user confirmation.
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				Cytoscape.exit(returnVal);
			}

			public void windowClosed() {

			}
		});

		// show the Desktop
		setContentPane(main_panel);
		pack();
		if (VIEW_TYPE != EXTERNAL_VIEW)
			setSize(800, 700);
                //Bilal Change//
//		setVisible(true);
//		toFront();
                //End Bilal Change//
                
		/*
		 * Enable the bird's eye view.
		 * 
		 * kono: no longer necessary.  called after CYTOSCAPE_INITIALIZED signal.
		 */
//		final CyMenus fCyMenus = cyMenus;
//		Runnable birdViewEnabler = new Runnable() {
//			public void run() {
//				try {
//					Thread.sleep(5000);
//				} catch (InterruptedException e) {
//					return;
//				}
//				SwingUtilities.invokeLater(new Runnable() {
//					public void run() {
//						cyMenus.networkOverviewAction
//								.actionPerformed(new ActionEvent(
//										cyMenus.networkOverviewItem, 1001,
//										"Show Network Overview"));
//					}
//				});
//			}
//		};
//		(new Thread(birdViewEnabler)).start();
	}

	private void initStatusBar(JPanel panel) {
		statusBar = new JLabel();
		statusBar.setBorder(new EmptyBorder(0, 7, 5, 7));
		statusBar.setForeground(new Color(75, 75, 75));
		panel.add(statusBar, BorderLayout.SOUTH);
		setStatusBarMsg("Welcome to Cytoscape "
				+ CytoscapeVersion.version
				+ "              Right-click + drag  to  ZOOM             Middle-click + drag  to  PAN");
	}

	/**
	 * Sets the Status Bar Message.
	 * 
	 * @param msg
	 *            Status Bar Message.
	 */
	public void setStatusBarMsg(String msg) {
		statusBar.setText(msg);
	}

	/**
	 * Clears the Status Bar Message.
	 */
	public void clearStatusBar() {
		// By using mutiple white spaces, layout for the statusBar is preserved.
		statusBar.setText("   ");
	}

	public NetworkPanel getNetworkPanel() {
		return networkPanel;
	}

	public HelpBroker getHelpBroker() {
		return cyHelpBroker.getHelpBroker();
	}

	public HelpSet getHelpSet() {
		return cyHelpBroker.getHelpSet();
	}

	// ----------------------------------------//
	// Common Desktop Variables

	public void addEdit(javax.swing.undo.UndoableEdit edit) {
		// if ( undo == null )
		// undo = new cytoscape.util.UndoManager();
		undo.addEdit(edit);
	}

	/**
	 * Return the view type for this CytoscapeDesktop
	 */
	public int getViewType() {
		return VIEW_TYPE;
	}

	public CyMenus getCyMenus() {
		return cyMenus;
	}

	public void setNewNetwork(CyNetwork newNetwork) {
	}

	/**
	 * Returns the visual mapping manager that controls the appearance of nodes
	 * and edges in this display.
	 * 
	 * @deprecated Use Cytoscape.getVisualMappingManager() instead. This will be
	 *             removed at 03/2007 (possibly sooner).
	 */
	public VisualMappingManager getVizMapManager() {
		return vizMapper;
	}

	/**
	 * returns the top-level UI object for the visual mapper.
	 */
	public VizMapUI getVizMapUI() {
		return vizMapUI;
	}

	/**
	 * Create the VizMapper and the UI for it.
	 */
	public void setupVizMapper() {

		this.vizMapper = Cytoscape.getVisualMappingManager();

		// create the VizMapUI
		vizMapUI = new VizMapUI(vizMapper, this);
		vizMapUI.setName("vizMapUI");

		// In order for the VizMapper to run when the StyleSelector is
		// run, it needs to listen to the selector.
		vizMapper.addChangeListener(vizMapUI.getStyleSelector());

		// Add the StyleSelector to the ToolBar
		// TODO: maybe put this somewhere else to make it easier to make
		// vertical ToolBars.

		JComboBox styleBox = vizMapUI.getStyleSelector().getToolbarComboBox();
		Dimension newSize = new Dimension(150, (int) styleBox
				.getPreferredSize().getHeight());
		styleBox.setMaximumSize(newSize);
		styleBox.setPreferredSize(newSize);

		if (VIEW_TYPE == EXTERNAL_VIEW) {
			if (currentStyleBox != null)
				main_panel.remove(currentStyleBox);

			main_panel.add(styleBox, BorderLayout.SOUTH);
			currentStyleBox = styleBox;

		} else {
			JToolBar toolBar = cyMenus.getToolBar();

			// first time
			if (styleBoxIndex == -1) {
				toolBar.add(styleBox);
				styleBoxIndex = toolBar.getComponentCount() - 1;
				toolBar.addSeparator();

				// subsequent times
			} else {
				toolBar.remove(styleBoxIndex);
				toolBar.add(styleBox, styleBoxIndex);
			}
		}


	}

	// ----------------------------------------//
	// Focus Management

	/**
	 * @param style
	 *            the NEW VisualStyle
	 * @return the OLD VisualStyle
	 */
	public VisualStyle setVisualStyle(VisualStyle style) {

		VisualStyle old_style = (VisualStyle) vizMapUI.getStyleSelector()
				.getToolbarComboBox().getSelectedItem();

		vizMapper.setVisualStyle(style);
		vizMapUI.getStyleSelector().getToolbarComboBox().setSelectedItem(style);

		return old_style;
	}

	protected void updateFocus(String network_id) {

		// System.out.println( "CD: setting focus to: "+network_id );

		// deal with the old Network
		VisualStyle old_style = (VisualStyle) vizMapUI.getStyleSelector()
				.getToolbarComboBox().getSelectedItem();

		CyNetworkView old_view = Cytoscape.getCurrentNetworkView();
		if (old_view != null) {
			old_view.putClientData(VISUAL_STYLE, old_style);
			old_view.putClientData(VIZMAP_ENABLED, new Boolean(old_view
					.getVisualMapperEnabled()));
		}

		// set the current Network/View
		Cytoscape.setCurrentNetwork(network_id);
		if (Cytoscape.setCurrentNetworkView(network_id)) {

			// deal with the new Network
			CyNetworkView new_view = Cytoscape.getCurrentNetworkView();
			VisualStyle new_style = (VisualStyle) new_view
					.getClientData(VISUAL_STYLE);
			Boolean vizmap_enabled = ((Boolean) new_view
					.getClientData(VIZMAP_ENABLED));

			if (new_style == null)
				new_style = defaultVisualStyle;

			if (vizmap_enabled == null)
				vizmap_enabled = new Boolean(true);

			vizMapper.setNetworkView(new_view);
			if (new_style != null) {
				vizMapper.setVisualStyle(new_style);
				vizMapUI.getStyleSelector().getToolbarComboBox()
						.setSelectedItem(new_style);
			}

			cyMenus.setNodesRequiredItemsEnabled();
			cyMenus.setVisualMapperItemsEnabled(vizmap_enabled.booleanValue());
			if (vizmap_enabled.booleanValue()) {
				new_view.redrawGraph(false, false);
			}
		}
	}

	public void setFocus(String network_id) {
		pcs.firePropertyChange(new PropertyChangeEvent(this,
				NETWORK_VIEW_FOCUSED, null, network_id));
		pcs.firePropertyChange(new PropertyChangeEvent(this,
				NETWORK_VIEW_FOCUS, null, network_id));
	}

	/**
	 * TO keep things clearer there is one GraphView Controller per
	 * CytoscapeDesktop
	 */
	public GraphViewController getGraphViewController() {
		if (graphViewController == null)
			graphViewController = new GraphViewController();

		return graphViewController;
	}

	public SwingPropertyChangeSupport getSwingPropertyChangeSupport() {
		return pcs;
	}

	public void propertyChange(PropertyChangeEvent e) {

		if (e.getPropertyName() == NETWORK_VIEW_CREATED) {

			// add the new view to the GraphViewController
			getGraphViewController().addGraphView(
					(CyNetworkView) e.getNewValue());
			// pass on the event
			pcs.firePropertyChange(e);

			networkPanel.focusNetworkNode(((CyNetworkView) e.getNewValue())
					.getIdentifier());
			networkPanel.fireFocus(((CyNetworkView) e.getNewValue())
					.getIdentifier());
		}

		else if (e.getPropertyName() == NETWORK_VIEW_FOCUSED) {
			// get focus event from NetworkViewManager

			updateFocus(e.getNewValue().toString());
			pcs.firePropertyChange(e);
		}

		else if (e.getPropertyName() == NETWORK_VIEW_FOCUS) {
			// get Focus from NetworkPanel

			updateFocus(e.getNewValue().toString());
			pcs.firePropertyChange(e);
		}

		else if (e.getPropertyName() == Cytoscape.NETWORK_CREATED) {
			// fire the event so that the NetworkPanel can catch it
			pcs.firePropertyChange(e);
		}

		else if (e.getPropertyName() == Cytoscape.NETWORK_DESTROYED) {
			// fire the event so that the NetworkPanel can catch it
			pcs.firePropertyChange(e);
			// Check new session or not

			if (Cytoscape.getNetworkSet().size() == 0
					&& Cytoscape.getSessionstate() != Cytoscape.SESSION_OPENED) {
				String message = "Do you want to create a new session?.\n(All attributes will be lost!)";

				int result = JOptionPane.showConfirmDialog(this, message,
						"Create New Session?", JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE, null);
				if (result == JOptionPane.YES_OPTION) {
					Cytoscape.createNewSession();
				}
			}
		} else if (e.getPropertyName() == NETWORK_VIEW_DESTROYED) {
			// remove the view from the GraphViewController
			getGraphViewController().removeGraphView(
					(CyNetworkView) e.getNewValue());
			// pass on the event
			pcs.firePropertyChange(e);
		} else if (e.getPropertyName() == Cytoscape.CYTOSCAPE_INITIALIZED) {
			/*
			 * Create bird's eye view
			 */
			cyMenus.networkOverviewAction
					.actionPerformed(new ActionEvent(
							cyMenus.networkOverviewItem, 1001,
							"Show Network Overview"));
		}

	}

	// ---------------------------------------------------------------------------//
	// Cytopanels - Public and Protected methods

	/**
	 * Gets a cytoPanel given a Compass direction.
	 * 
	 * @param compassDirection
	 *            Compass Direction (SwingConstants.{SOUTH,EAST,WEST}).
	 * @return CytoPanel The CytoPanel that lives in the region specified by
	 *         compass direction.
	 */
	public CytoPanel getCytoPanel(int compassDirection) {

		// return appropriate cytoPanel based on compass direction
		switch (compassDirection) {
		case SwingConstants.SOUTH:
			return (CytoPanel) cytoPanelSouth;
		case SwingConstants.EAST:
			return (CytoPanel) cytoPanelEast;
		case SwingConstants.WEST:
			return (CytoPanel) cytoPanelWest;
		case SwingConstants.SOUTH_WEST:
			return (CytoPanel) cytoPanelSouthWest;			
		}

		// houston we have a problem
		throw new IllegalArgumentException("Illegal Argument:  "
				+ compassDirection
				+ ".  Must be one of:  SwingConstants.{SOUTH,EAST,WEST,SOUTH_WEST}.");
	}

	/**
	 * Create the CytoPanels UI.
	 * 
	 * @param networkPanel
	 *            to load on left side of right bimodal.
	 * @param networkViewManager
	 *            to load on left side (CytoPanel West).
	 * @return BiModalJSplitPane Object.
	 */
	protected BiModalJSplitPane setupCytoPanels(NetworkPanel networkPanel,
			NetworkViewManager networkViewManager) {

		// bimodals that our Cytopanels Live within
		BiModalJSplitPane topRightPane = createTopRightPane(networkViewManager);
		BiModalJSplitPane rightPane = createRightPane(topRightPane);
		BiModalJSplitPane masterPane = createMasterPane(networkPanel, rightPane);

		return masterPane;
	}

	/**
	 * Creates the TopRight Pane.
	 * 
	 * @param networkViewManager
	 *            to load on left side of top right bimodal.
	 * @return BiModalJSplitPane Object.
	 */
	protected BiModalJSplitPane createTopRightPane(
			NetworkViewManager networkViewManager) {

		// create cytopanel with tabs along the top
		cytoPanelEast = new CytoPanelImp(SwingConstants.EAST, JTabbedPane.TOP,
				CytoPanelState.HIDE);

		// determine proper network view manager component
		Component networkViewComp = null;
		if (VIEW_TYPE == TABBED_VIEW) {
//                    System.out.println("TABBED_VIEW");
			networkViewComp = (Component) networkViewManager.getTabbedPane();
		} else if (VIEW_TYPE == INTERNAL_VIEW) {                    
//                    System.out.println("INTERNAL_VIEW");
			networkViewComp = (Component) networkViewManager.getDesktopPane();
		} else if (VIEW_TYPE == EXTERNAL_VIEW) {
			// do nothing
		}
                
                //Bilal change
                nteworksView = networkViewComp;
                //End Bilal change
                

		// create the split pane - we show this on startup
		BiModalJSplitPane splitPane = new BiModalJSplitPane(this,
				JSplitPane.HORIZONTAL_SPLIT, BiModalJSplitPane.MODE_HIDE_SPLIT,
				networkViewComp, cytoPanelEast);

		// set the cytopanelcontainer
		cytoPanelEast.setCytoPanelContainer(splitPane);

		// set the resize weight - left component gets extra space
		splitPane.setResizeWeight(1.0);

		// outta here
		return splitPane;
	}

	/**
	 * Creates the Right Panel.
	 * 
	 * @param topRightPane
	 *            TopRightPane Object.
	 * @return BiModalJSplitPane Object
	 */
	protected BiModalJSplitPane createRightPane(BiModalJSplitPane topRightPane) {

		// create cytopanel with tabs along the bottom
		cytoPanelSouth = new CytoPanelImp(SwingConstants.SOUTH,
				JTabbedPane.BOTTOM, CytoPanelState.HIDE);

		// create the split pane - hidden by default
		BiModalJSplitPane splitPane = new BiModalJSplitPane(this,
				JSplitPane.VERTICAL_SPLIT, BiModalJSplitPane.MODE_HIDE_SPLIT,
				topRightPane, cytoPanelSouth);

		// set the cytopanel container
		cytoPanelSouth.setCytoPanelContainer(splitPane);

		// set resize weight - top component gets all the extra space.
		splitPane.setResizeWeight(1.0);

		// outta here
		return splitPane;
	}

	/**
	 * Creates the Master Split Pane.
	 * 
	 * @param networkPanel
	 *            to load on left side of CytoPanel (cytoPanelWest).
	 * @param rightPane
	 *            BiModalJSplitPane Object.
	 * @return BiModalJSplitPane Object.
	 */
	protected BiModalJSplitPane createMasterPane(NetworkPanel networkPanel,
			BiModalJSplitPane rightPane) {

		// create cytopanel with tabs along the top
		cytoPanelWest = new CytoPanelImp(SwingConstants.WEST, JTabbedPane.TOP,
				CytoPanelState.DOCK);
		
		// add the network panel to our tab
		String tab1Name = new String("Network");
		cytoPanelWest
				.add(tab1Name, new ImageIcon(getClass().getResource(
						"/cytoscape/images/class_hi.gif")), networkPanel,
						"Cytoscape Network List");

		// create the split pane - hidden by default
		BiModalJSplitPane splitPane = new BiModalJSplitPane(this,
				JSplitPane.HORIZONTAL_SPLIT, BiModalJSplitPane.MODE_SHOW_SPLIT,
				cytoPanelWest, rightPane);

		// set the cytopanel container
		cytoPanelWest.setCytoPanelContainer(splitPane);

		// outta here
		return splitPane;
	}

	// End Cytopanels - Public and Protected methods
	// ---------------------------------------------------------------------------//

	/**
	 * Gets the NetworkView Manager.
	 * 
	 * @return NetworkViewManager Object.
	 */
	public NetworkViewManager getNetworkViewManager() {
		return this.networkViewManager;
	}

	public static int parseViewType(String vt) {
		int type = -1;
		if (vt == null || vt.equals("tabbed"))
			type = TABBED_VIEW;
		else if (vt.equals("internal"))
			type = INTERNAL_VIEW;
		else if (vt.equals("external"))
			type = EXTERNAL_VIEW;
		else {
			System.out.println("Couldn't parse view type: " + vt
					+ " -- using TABBED");
			type = TABBED_VIEW;
		}
		return type;
	}
        
    //Bilal Change//
    public JPanel getMain_panel() {
        return main_panel;
    }
    Component nteworksView;
    public Component getNteworksView(){
        return nteworksView;
    }    
    //End Bilal Change//
}
