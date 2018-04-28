package cytoscape.ding;

import cytoscape.render.stateful.GraphLOD;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import cytoscape.CytoscapeInit;
import cytoscape.Cytoscape;
import java.util.regex.Pattern; 
import java.util.regex.Matcher; 

/**
 * An instance of this class defines the level of detail that goes into
 * a single rendering of a graph.  This class is meant to be subclassed; its
 * methods are meant to be overridden; nonetheless, sane defaults are
 * used in the default method implementations.<p>
 * To understand the significance of each method's return value, it makes
 * sense to become familiar with the API cytoscape.render.immed.GraphGraphics.
 */
public class CyGraphLOD extends GraphLOD implements PropertyChangeListener
{
      	protected int coarseDetailThreshold; 
	protected int nodeBorderThreshold;
	protected int nodeLabelThreshold;
	protected int edgeArrowThreshold;
	protected int edgeLabelThreshold;

	public CyGraphLOD() {
		init();
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(this);

	}

	public void propertyChange(PropertyChangeEvent e) {
          if ( e.getPropertyName() == Cytoscape.PREFERENCES_UPDATED ) {
            init();
            java.util.Map networkViewMap = cytoscape.Cytoscape.getNetworkViewMap();
            java.util.Iterator foo = networkViewMap.values().iterator();
            while (foo.hasNext()) {
              ((ding.view.DGraphView) foo.next()).setGraphLOD(this); } }
	}

	protected void init() {

		coarseDetailThreshold =  getInt("render.coarseDetailThreshold",2000);
		nodeBorderThreshold = getInt("render.nodeBorderThreshold",200);
		nodeLabelThreshold = getInt("render.nodeLabelThreshold",100);
		edgeArrowThreshold = getInt("render.edgeArrowThreshold",300);
		edgeLabelThreshold = getInt("render.edgeLabelThreshold",120);
/*
		System.out.println("(re)initializing level of detail (LOD)");
		System.out.println("  coarseDetailThreshold: " + coarseDetailThreshold);
		System.out.println("  nodeBorderThreshold: " + nodeBorderThreshold);
		System.out.println("  nodeLabelThreshold: " + nodeLabelThreshold);
		System.out.println("  edgeArrowThreshold: " + edgeArrowThreshold);
		System.out.println("  edgeLabelThreshold: " + edgeLabelThreshold);
*/

		//Cytoscape.getCurrentNetworkView().updateView();
	}

	protected int getInt(String key, int defaultValue) {
		String val = CytoscapeInit.getProperties().getProperty(key);
		if ( val == null )
			return defaultValue;

		int ret = defaultValue;
		try {
			ret = Integer.parseInt(val);
		} catch ( Exception e) { e.printStackTrace(); }

		return ret;
	}

	protected boolean getBoolean(String key, boolean defaultValue) {
		String val = CytoscapeInit.getProperties().getProperty(key);
		if ( val == null )
			return defaultValue;

		boolean ret = defaultValue;
		try {
			if ( Pattern.compile("true",Pattern.CASE_INSENSITIVE).matcher(val).matches() )
				ret = true;
			else
				ret = false;
		} catch ( Exception e) { e.printStackTrace(); }

		return ret;
	}


  /**
   * Determines whether or not to render all edges in a graph, no edges, or
   * only those edges which touch a visible node.  By default
   * this method returns zero, which leads the rendering engine to render
   * only those edges that touch at least one visible node.  If a positive
   * value is returned, all edges in the graph will be rendered.  If a negative
   * value is returned, no edges will be rendered.  This is the first
   * method called on an instance of GraphLOD by the rendering engine;
   * the renderEdgeCount parameter passed to other methods
   * will have a value which reflects the decision made by the return value
   * of this method call.<p>
   * Note that rendering all edges leads to a dramatic performance decrease
   * when rendering large graphs.
   * @param visibleNodeCount the number of nodes visible in the current
   *   viewport; note that a visible node is not necessarily a rendered node,
   *   because visible nodes with zero width or height are not rendered.
   * @param totalNodeCount the total number of nodes in the graph that is
   *   being rendered.
   * @param totalEdgeCount the total number of edges in the graph that is
   *   being rendered.
   * @return zero if only edges touching a visible node are to be rendered,
   *   positive if all edges are to be rendered, or negative if no edges
   *   are to be rendered.
   */
  public byte renderEdges(final int visibleNodeCount,
                          final int totalNodeCount,
                          final int totalEdgeCount)
  {
    if (totalEdgeCount >= Math.min(edgeArrowThreshold, edgeLabelThreshold)) {
      return (byte) 0; }
    else {
      return (byte) 1; }
  }

  /**
   * Determines whether or not to render a graph at full detail.
   * By default this method returns true if and only if the sum of rendered
   * nodes and rendered edges is less than 1200.<p>
   * The following table describes the difference between full and low
   * rendering detail in terms of what methods on an instance of
   * GraphGraphics get called:
   * <blockquote><table border="1" cellpadding="5" cellspacing="0">
   *   <tr>  <td></td>
   *         <th>full detail</th>
   *         <th>low detail</th>                                          </tr>
   *   <tr>  <th>nodes</th>
   *         <td>drawNodeFull()</td>
   *         <td>drawNodeLow()</td>                                       </tr>
   *   <tr>  <th>edges</th>
   *         <td>drawEdgeFull()</td>
   *         <td>drawEdgeLow()</td>                                       </tr>
   *   <tr>  <th>node labels</th>
   *         <td>drawTextFull()</td>
   *         <td>not rendered</td>                                        </tr>
   *   <tr>  <th>edge labels</th>
   *         <td>drawTextFull()</td>
   *         <td>not rendered</td>                                        </tr>
   *   <tr>  <th>custom node graphics</th>
   *         <td>drawCustomGraphicFull()</td>
   *         <td>not rendered</td>                                        </tr>
   * </table></blockquote>
   * @param renderNodeCount the number of nodes that are about to be rendered.
   * @param renderEdgeCount the number of edges that are about to be rendered.
   * @return true for full detail, false for low detail.
   */
  public boolean detail(final int renderNodeCount, final int renderEdgeCount)
  {
    return renderNodeCount + renderEdgeCount < coarseDetailThreshold;
  }

  /**
   * Determines whether or not to render node borders.  By default this
   * method returns true if and only if the number of rendered nodes
   * is less than 200.<p>
   * It is only possible to draw node borders at the full detail
   * level.  If low detail is chosen, the output of this method is ignored.
   * @param renderNodeCount the number of nodes that are about to be rendered.
   * @param renderEdgeCount the number of edges that are about to be rendered.
   * @return true if and only if node borders are to be rendered.
   * @see #detail(int, int)
   */
  public boolean nodeBorders(final int renderNodeCount,
                             final int renderEdgeCount)
  {
    return renderNodeCount < nodeBorderThreshold;
  }

  /**
   * Determines whether or not to render node labels.  By default this method
   * returns true if and only if the number of rendered nodes is less than
   * 60.<p>
   * Node labels are only rendered at the full detail level.  If low detail is
   * chosen, the output of this method is ignored.
   * @param renderNodeCount the number of nodes that are about to be rendered.
   * @param renderEdgeCount the number of edges that are about to be rendered.
   * @return true if and only if node labels are to be rendered.
   * @see #detail(int, int)
   */
  public boolean nodeLabels(final int renderNodeCount,
                            final int renderEdgeCount)
  {
    return renderNodeCount < nodeLabelThreshold;
  }

  /**
   * Determines whether or not to render custom graphics on nodes.
   * By default this method returns true if and only if the number of rendered
   * nodes is less than 60.<p>
   * Custom node graphics are only rendered at the full detail level.  If low
   * detail is chosen, the output of this method is ignored.
   * @param renderNodeCount the number of nodes that are about to be rendered.
   * @param renderEdgeCount the number of edges that are about to be rendered.
   * @return true if and only if custom node graphics are to be rendered.
   * @see #detail(int, int)
   */
  public boolean customGraphics(final int renderNodeCount,
                                final int renderEdgeCount)
  {
    return renderNodeCount < nodeBorderThreshold;
  }

  /**
   * Determines whether or not to render edge arrows.  By default this
   * method returns true if and only if the number of rendered edges is less
   * than 300.<p>
   * It is only possible to draw edge arrows at the full detail
   * level.  If low detail is chosen, the output of this method is ignored.
   * @param renderNodeCount the number of nodes that are about to be rendered.
   * @param renderEdgeCount the number of edges that are about to be rendered.
   * @return true if and only if edge arrows are to be rendered.
   * @see #detail(int, int)
   */
  public boolean edgeArrows(final int renderNodeCount,
                            final int renderEdgeCount)
  {
    return renderEdgeCount < edgeArrowThreshold;
  }

  /**
   * Determines whether or not to honor dashed edges.  By default this
   * method always returns true.  If false is returned, edges that
   * claim to be dashed will be rendered as solid.<p>
   * It is only possible to draw dashed edges at the full detail
   * level.  If low detail is chosen, the output of this method is ignored.
   * Note that drawing dashed edges is computationally expensive;
   * the default implementation of this method does not make a very
   * performance-minded decision if a lot of edges happen to be dashed.
   * @param renderNodeCount the number of nodes that are about to be rendered.
   * @param renderEdgeCount the number of edges that are about to be rendered.
   * @return true if and only if dashed edges are to be honored.
   * @see #detail(int, int)
   */
  public boolean dashedEdges(final int renderNodeCount,
                             final int renderEdgeCount)
  {
     return true;
  }

  /**
   * Determines whether or not to honor edge anchors.  By default this
   * method always returns true.  If false is returned, edges that
   * claim to have edge anchors will be rendered as simple straight
   * edges.<p>
   * It is only possible to draw poly-edges at the full detail
   * level.  If low detail is chosen, the output of this method is ignored.
   * @param renderNodeCount the number of nodes that are about to be rendered.
   * @param renderEdgeCount the number of edges that are about to be rendered.
   * @return true if and only if edge anchors are to be honored.
   * @see #detail(int, int)
   */
  public boolean edgeAnchors(final int renderNodeCount,
                             final int renderEdgeCount)
  {
    return true;
  }

  /**
   * Determines whether or not to render edge labels.  By default this method
   * returns true if and only if the number of rendered edges is less than
   * 80.<p>
   * Edge labels are only rendered at the full detail level.  If low detail is
   * chosen, the output of this method is ignored.
   * @param renderNodeCount the number of nodes that are about to be rendered.
   * @param renderEdgeCount the number of edges that are about to be rendered.
   * @return true if and only if edge labels are to be rendered.
   * @see #detail(int, int)
   */
  public boolean edgeLabels(final int renderNodeCount,
                            final int renderEdgeCount)
  {
    return renderEdgeCount < edgeLabelThreshold;
  }

  /**
   * Determines whether or not to draw text as shape when rendering node and
   * edge labels.  By default this method always returns false.<p>
   * This method affects the boolean parameter drawTextAsShape in the method
   * call GraphGraphics.drawTextFull().  If neither node nor edge labels are
   * rendered then the output of this method is ignored.
   * @param renderNodeCount the number of nodes that are about to be rendered.
   * @param renderEdgeCount the number of edges that are about to be rendered.
   * @return true if and only if rendered label text should be drawn as
   *   primitive shapes.
   * @see #nodeLabels(int, int)
   * @see #edgeLabels(int, int)
   */
  public boolean textAsShape(final int renderNodeCount,
                             final int renderEdgeCount)
  {
    return false;
  }

}
