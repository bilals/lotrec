package cytoscape.ding;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.layout.LayoutAlgorithm;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.CyEdgeView;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CyNodeView;
import cytoscape.view.FlagAndSelectionHandler;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.ui.VizMapUI;
import ding.view.DGraphView;
import giny.view.EdgeView;
import giny.view.NodeView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

// AJK: 05/19/06 BEGIN
//     for context menus
import ding.view.NodeContextMenuListener;
import ding.view.EdgeContextMenuListener;
// AJK: 05/19/06 END


public class DingNetworkView extends DGraphView implements CyNetworkView
{

  private String title;
  private boolean vizmapEnabled = true;
  private HashMap clientData = new HashMap();
  private VisualStyle vs;

  public DingNetworkView(CyNetwork network,
                         String title)
  {
    super(network);
    this.title = title;
    final int[] nodes = network.getNodeIndicesArray();
    final int[] edges = network.getEdgeIndicesArray();
    for (int i = 0; i < nodes.length; i++) {
      addNodeView(nodes[i]); 
    }
    for (int i = 0; i < edges.length; i++) {
      addEdgeView(edges[i]); 
    }
    new FlagAndSelectionHandler(((CyNetwork) getNetwork()).getFlagger(), this);
  }

  public void setVisualStyle(String vsName)
  {
    vs = Cytoscape.getVisualMappingManager().getCalculatorCatalog().getVisualStyle(vsName);
  }

  public VisualStyle getVisualStyle()
  {
    return vs;
  }

  public CyNetwork getNetwork()
  {
    return (CyNetwork) getGraphPerspective();
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public String getTitle()
  {
    return title;
  }

  public void redrawGraph(boolean layout, boolean vizmap)
  {
    // I think we forgot to add an important method here:
    //
    // public int returnSumOfOnePlusOne()
    // {
    //   return 2;
    // }

    // Just copying this line from the old implementation.
    Cytoscape.getVisualMappingManager().applyAppearances();
    updateView();
  }

  public CyNetworkView getView()
  {
    return this;
  }

  public VisualMappingManager getVizMapManager()
  {
    // Believe it or not, this is the correct f***ing implementation.
    return null;
  }

  public VizMapUI getVizMapUI()
  {
    // Believe it or not, this is the correct f***ing implementation.
    return null;
  }

  public void toggleVisualMapperEnabled()
  {
    vizmapEnabled = !vizmapEnabled;
  }

  public void setVisualMapperEnabled(boolean state)
  {
    vizmapEnabled = state;
  }

  public boolean getVisualMapperEnabled()
  {
    return vizmapEnabled;
  }

  public void putClientData(String data_name, Object data)
  {
    clientData.put(data_name, data);
  }

  public Collection getClientDataNames()
  {
    return clientData.keySet();
  }

  public Object getClientData(String data_name)
  {
    return clientData.get(data_name);
  }

  public boolean setSelected(CyNode[] nodes)
  {
    return setSelected(convertToViews(nodes));
  }

  private NodeView[] convertToViews(CyNode[] nodes)
  {
    NodeView[] views = new NodeView[nodes.length];
    for (int i = 0; i < nodes.length; i++) {
      views[i] = getNodeView(nodes[i]); }
    return views;
  }

  public boolean setSelected(NodeView[] node_views)
  {
    for (int i = 0; i < node_views.length; i++) {
      node_views[i].select(); }
    return true;
  }

  public boolean applyVizMap(CyEdge edge)
  {
    return applyVizMap(getEdgeView(edge));
  }

  public boolean applyVizMap(EdgeView edge_view)
  {
    return applyVizMap
      (edge_view,
       (VisualStyle) getClientData(CytoscapeDesktop.VISUAL_STYLE));
  }

  public boolean applyVizMap(CyNode node)
  {
    return applyVizMap(getNodeView(node));
  }

  public boolean applyVizMap(NodeView node_view)
  {
    return applyVizMap
      (node_view,
       (VisualStyle) getClientData(CytoscapeDesktop.VISUAL_STYLE));
  }

  public boolean applyVizMap(CyEdge edge, VisualStyle style)
  {
    return applyVizMap(getEdgeView(edge), style);
  }

  public boolean applyVizMap(EdgeView edge_view, VisualStyle style)
  {
    VisualStyle old_style = Cytoscape.getDesktop().setVisualStyle(style);
    Cytoscape.getVisualMappingManager().vizmapEdge(edge_view, this);
    Cytoscape.getDesktop().setVisualStyle(old_style);
    return true;
  }

  public boolean applyVizMap(CyNode node, VisualStyle style)
  {
    return applyVizMap(getNodeView(node), style);
  }

  public boolean applyVizMap(NodeView node_view, VisualStyle style)
  {
    VisualStyle old_style = Cytoscape.getDesktop().setVisualStyle(style);
    Cytoscape.getVisualMappingManager().vizmapNode(node_view, this);
    Cytoscape.getDesktop().setVisualStyle(old_style);
    return true;
  }

  public boolean setSelected(CyEdge[] edges)
  {
    return setSelected(convertToViews(edges));
  }

  private EdgeView[] convertToViews(CyEdge[] edges)
  {
    EdgeView[] views = new EdgeView[edges.length];
    for (int i = 0; i < edges.length; i++) {
      views[i] = getEdgeView(edges[i]); }
    return views;
  }

  public boolean setSelected(EdgeView[] edge_views)
  {
    for (int i = 0; i < edge_views.length; i++) {
      edge_views[i].select(); }
    return true;
  }

  public void applyVizmapper(VisualStyle style)
  {
    VisualStyle old_style = Cytoscape.getDesktop().setVisualStyle(style);
    redrawGraph(false, true);
  }

  public void applyLayout(LayoutAlgorithm layout)
  {
    layout.doLayout();
  }

  public void applyLockedLayout(LayoutAlgorithm layout,
                                CyNode[] nodes,
                                CyEdge[] edges)
  {
    layout.lockNodes(convertToViews(nodes));
    layout.doLayout();
  }

  public void applyLayout(LayoutAlgorithm layout,
                          CyNode[] nodes,
                          CyEdge[] edges)
  {
    layout.lockNodes(getInverseViews(convertToViews(nodes)));
    layout.doLayout();
  }

  private NodeView[] getInverseViews(NodeView[] given)
  {
    // This code, like most all of the code in this class, is copied from
    // PhoebeNetworkView.  Zum kotzen.
    NodeView[] inverse = new NodeView[getNodeViewCount() - given.length];
    List node_views = getNodeViewsList();
    int count = 0;
    Iterator i = node_views.iterator();
    Arrays.sort(given);
    while (i.hasNext()) {
      NodeView view = (NodeView) i.next();
      if (Arrays.binarySearch(given, view) < 0) {
        inverse[count] = view;
        count++; } }
    return inverse;
  }

  public List getNodeViewsList()
  {
    ArrayList list = new ArrayList(getNodeViewCount());
    int[] gp_indices = getGraphPerspective().getNodeIndicesArray();
    for (int i = 0; i < gp_indices.length; i++) {
      list.add(getNodeView(gp_indices[i])); }
    return list;
  }

  public void applyLockedLayout(LayoutAlgorithm layout,
                                CyNodeView[] nodes,
                                CyEdgeView[] edges)
  {
    layout.lockNodes(nodes);
    layout.doLayout();
  }

  public void applyLayout(LayoutAlgorithm layout,
                          CyNodeView[] nodes,
                          CyEdgeView[] edges)
  {
    layout.lockNodes(getInverseViews(nodes));
    layout.doLayout();
  }

  public void applyLockedLayout(LayoutAlgorithm layout,
                                int[] nodes,
                                int[] edges)
  {
    layout.lockNodes(convertToNodeViews(nodes));
    layout.doLayout();
  }

  private NodeView[] convertToNodeViews(int[] nodes)
  {
    NodeView[] views = new NodeView[nodes.length];
    for (int i = 0; i < nodes.length; i++) {
      views[i] = getNodeView(nodes[i]); }
    return views;
  }

  public void applyLayout(LayoutAlgorithm layout,
                          int[] nodes,
                          int[] edges)
  {
    layout.lockNodes(getInverseViews(convertToNodeViews(nodes)));
    layout.doLayout();
  }
  // AJK: 05/19/06 BEGIN
  //   for context menus
    @Override
  public void addNodeContextMenuListener (NodeContextMenuListener l)
  {
	  super.addNodeContextMenuListener(l);
  }

    @Override
  public void removeNodeContextMenuListener (NodeContextMenuListener l)  {
	  super.removeNodeContextMenuListener(l);
  }

    @Override
  public void addEdgeContextMenuListener(EdgeContextMenuListener l)  {
	  super.addEdgeContextMenuListener(l);
  }
 
    @Override
  public void removeEdgeContextMenuListener(EdgeContextMenuListener l)  {
	  super.removeEdgeContextMenuListener(l);
  }
  // AJK: 05/19/06 END

}
