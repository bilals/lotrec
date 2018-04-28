
/*
  File: EdgeAppearanceCalculator.java 
  
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

//----------------------------------------------------------------------------
// $Revision: 9034 $
// $Date: 2006-12-01 13:52:46 -0800 (Fri, 01 Dec 2006) $
// $Author: mes $
//----------------------------------------------------------------------------
package cytoscape.visual;
//----------------------------------------------------------------------------
import java.util.Map;
import java.util.Properties;
import java.awt.Color;
import java.awt.Font;

import giny.model.Edge;
import cytoscape.visual.LineType;
import cytoscape.visual.Arrow;

import cytoscape.CyNetwork;
import cytoscape.visual.calculators.*;
import cytoscape.visual.parsers.*;
import cytoscape.visual.ui.VizMapUI;
//----------------------------------------------------------------------------
/**
 * This class calculates the appearance of an Edge. It holds a default value
 * and a (possibly null) calculator for each visual attribute.
 */
public class EdgeAppearanceCalculator extends AppearanceCalculator {
   
    EdgeAppearance defaultAppearance = new EdgeAppearance();

  /** Used _ONLY_ to support deprecated code - DO NOT USE OTHERWISE!!!! */
  private EdgeAppearance currentAppearance;
  /** Used _ONLY_ to support deprecated code - DO NOT USE OTHERWISE!!!! */
  private CyNetwork currentNetwork;
  /** Used _ONLY_ to support deprecated code - DO NOT USE OTHERWISE!!!! */
  private Edge currentEdge;


    public EdgeAppearanceCalculator() {
    	super();
    }
    
    /**
     * Copy constructor. Returns a default object if the argument is null.
     */
    public EdgeAppearanceCalculator(EdgeAppearanceCalculator toCopy) {
    	super(toCopy);
    }
    
    /**
     * Creates a new EdgeAppearanceCalculator and immediately customizes it
     * by calling applyProperties with the supplied arguments.
     */
    public EdgeAppearanceCalculator(String name, Properties eacProps,
                                    String baseKey, CalculatorCatalog catalog) {
        super(name, eacProps, baseKey, catalog, new EdgeAppearance() );
	defaultAppearance = (EdgeAppearance)tmpDefaultAppearance;
    }

    /**
     * Using the rules defined by the default values and calculators in this
     * object, compute an appearance for the requested Edge in the supplied
     * CyNetwork. A new EdgeApperance object will be created.
     */
    public EdgeAppearance calculateEdgeAppearance(Edge edge, CyNetwork network) {
        EdgeAppearance appr = (EdgeAppearance)defaultAppearance.clone(); 
        calculateEdgeAppearance(appr, edge, network);
 	
	currentAppearance = appr;
	currentEdge = edge;
	currentNetwork = network;

        return appr;
    }
    
    /**
     * Using the rules defined by the default values and calculators in this
     * object, compute an appearance for the requested Edge in the supplied
     * CyNetwork. The supplied EdgeAppearance object will be changed to hold
     * the new values.
     */
    public void calculateEdgeAppearance(EdgeAppearance appr, Edge edge, CyNetwork network) {
    	appr.copy(defaultAppearance); // set default values
    	for (Calculator c : calcs)
		c.apply(appr,edge,network);

	appr.applyBypass(edge);
    }
    
    public EdgeAppearance getDefaultAppearance() {
    	return defaultAppearance;
    }

    public void setDefaultAppearance(EdgeAppearance appr) {
    	defaultAppearance = appr;
    }

    public String getDescription() {
    	return getDescription("EdgeAppearanceCalculator",defaultAppearance);
    }
    
    public void applyProperties(String name, Properties eacProps, String baseKey,
                                CalculatorCatalog catalog) {
	applyProperties(defaultAppearance,name,eacProps,baseKey,catalog);

    }
    
    public Properties getProperties(String baseKey) {
    	return getProperties(defaultAppearance,baseKey);
    }

    protected void copyDefaultAppearance(AppearanceCalculator toCopy) {
        defaultAppearance = (EdgeAppearance)(((EdgeAppearanceCalculator)toCopy).getDefaultAppearance().clone());
    }
  protected boolean isValidCalculator(Calculator c) {
          if ( c instanceof EdgeCalculator )
                  return true;
          else
                  return false;
  }

    /** @deprecated Use getCalculator(type) instead. This method will be removed Sept. 2007. */
    public EdgeColorCalculator getEdgeColorCalculator() {
    	return (EdgeColorCalculator)getCalculator(VizMapUI.EDGE_COLOR);
    }
    /** @deprecated Use getCalculator(type) instead. This method will be removed Sept. 2007. */
    public EdgeLineTypeCalculator getEdgeLineTypeCalculator() {
    	return (EdgeLineTypeCalculator)getCalculator(VizMapUI.EDGE_LINETYPE);
    }
    /** @deprecated Use getCalculator(type) instead. This method will be removed Sept. 2007. */
    public EdgeArrowCalculator getEdgeSourceArrowCalculator() {
    	return (EdgeArrowCalculator)getCalculator(VizMapUI.EDGE_SRCARROW);
    }
    /** @deprecated Use getCalculator(type) instead. This method will be removed Sept. 2007. */
    public EdgeArrowCalculator getEdgeTargetArrowCalculator() {
    	return (EdgeArrowCalculator)getCalculator(VizMapUI.EDGE_TGTARROW);
    }
    /** @deprecated Use getCalculator(type) instead. This method will be removed Sept. 2007. */
    public EdgeLabelCalculator getEdgeLabelCalculator() {
    	return (EdgeLabelCalculator)getCalculator(VizMapUI.EDGE_LABEL);
    }
    /** @deprecated Use getCalculator(type) instead. This method will be removed Sept. 2007. */
    public EdgeFontFaceCalculator getEdgeFontFaceCalculator() {
    	return (EdgeFontFaceCalculator)getCalculator(VizMapUI.EDGE_FONT_FACE);
    }
    /** @deprecated Use getCalculator(type) instead. This method will be removed Sept. 2007. */
    public EdgeFontSizeCalculator getEdgeFontSizeCalculator() {
    	return (EdgeFontSizeCalculator)getCalculator(VizMapUI.EDGE_FONT_SIZE);
    }
    /** @deprecated Use getCalculator(type) instead. This method will be removed Sept. 2007. */
    public EdgeToolTipCalculator getEdgeToolTipCalculator() {
    	return (EdgeToolTipCalculator)getCalculator(VizMapUI.EDGE_TOOLTIP);
    }

    /** @deprecated Use setDefaultAppearance() instead. This method will be removed Sept. 2007. */
    public void setDefaultEdgeColor(Color c) { defaultAppearance.setColor(c); }
    /** @deprecated Use setDefaultAppearance() instead. This method will be removed Sept. 2007. */
    public void setDefaultEdgeLineType(LineType lt) { defaultAppearance.setLineType(lt); }
    /** @deprecated Use setDefaultAppearance() instead. This method will be removed Sept. 2007. */
    public void setDefaultEdgeSourceArrow(Arrow a) { defaultAppearance.setSourceArrow(a); }
    /** @deprecated Use setDefaultAppearance() instead. This method will be removed Sept. 2007. */
    public void setDefaultEdgeTargetArrow(Arrow a) { defaultAppearance.setTargetArrow(a); }
    /** @deprecated Use setDefaultAppearance() instead. This method will be removed Sept. 2007. */
    public void setDefaultEdgeLabel(String s) { defaultAppearance.setLabel(s); }
    /** @deprecated Use setDefaultAppearance() instead. This method will be removed Sept. 2007. */
    public void setDefaultEdgeFont(Font f) { defaultAppearance.setFont(f); }
    /** @deprecated Use setDefaultAppearance() instead. This method will be removed Sept. 2007. */
    public void setDefaultEdgeFontFace(Font f) { defaultAppearance.setFont(f); }
    /** @deprecated Use setDefaultAppearance() instead. This method will be removed Sept. 2007. */
    public void setDefaultEdgeFontSize(float f) { defaultAppearance.setFontSize(f); }
    /** @deprecated Use setDefaultAppearance() instead. This method will be removed Sept. 2007. */
    public void setDefaultEdgeToolTip(String s) { defaultAppearance.setToolTip(s); }

    /** @deprecated Use getDefaultAppearance() instead. This method will be removed Sept. 2007. */
    public Color getDefaultEdgeColor() {return defaultAppearance.getColor();}
    /** @deprecated Use getDefaultAppearance() instead. This method will be removed Sept. 2007. */
    public LineType getDefaultEdgeLineType() {return defaultAppearance.getLineType();}
    /** @deprecated Use getDefaultAppearance() instead. This method will be removed Sept. 2007. */
    public Arrow getDefaultEdgeSourceArrow() {return defaultAppearance.getSourceArrow();}
    /** @deprecated Use getDefaultAppearance() instead. This method will be removed Sept. 2007. */
    public Arrow getDefaultEdgeTargetArrow() {return defaultAppearance.getTargetArrow();}
    /** @deprecated Use getDefaultAppearance() instead. This method will be removed Sept. 2007. */
    public String getDefaultEdgeLabel() {return defaultAppearance.getLabel();}
    /** @deprecated Use getDefaultAppearance() instead. This method will be removed Sept. 2007. */
    public Font getDefaultEdgeFont() {return defaultAppearance.getFont();}
    /** @deprecated Use getDefaultAppearance() instead. This method will be removed Sept. 2007. */
    public Font getDefaultEdgeFontFace() {return defaultAppearance.getFont();}
    /** @deprecated Use getDefaultAppearance() instead. This method will be removed Sept. 2007. */
    public float getDefaultEdgeFontSize() {return defaultAppearance.getFontSize();}
    /** @deprecated Use getDefaultAppearance() instead. This method will be removed Sept. 2007. */
    public String getDefaultEdgeToolTip() {return defaultAppearance.getToolTip();}
    /** @deprecated Use getDefaultAppearance() instead. This method will be removed Sept. 2007. */

    /** @deprecated Use calculateEdgeAppearance() instead. This method will be removed Sept. 2007. */
    public Color calculateEdgeColor(Edge edge, CyNetwork network) {
	doCalc(edge,network);
	return currentAppearance.getColor();
    }
    /** @deprecated Use calculateEdgeAppearance() instead. This method will be removed Sept. 2007. */
    public LineType calculateEdgeLineType(Edge edge, CyNetwork network) {
	doCalc(edge,network);
	return currentAppearance.getLineType();
    }
    /** @deprecated Use calculateEdgeAppearance() instead. This method will be removed Sept. 2007. */
    public Arrow calculateEdgeSourceArrow(Edge edge, CyNetwork network) {
	doCalc(edge,network);
	return currentAppearance.getSourceArrow();
    }
    /** @deprecated Use calculateEdgeAppearance() instead. This method will be removed Sept. 2007. */
    public Arrow calculateEdgeTargetArrow(Edge edge, CyNetwork network) {
	doCalc(edge,network);
	return currentAppearance.getTargetArrow();
    }
    /** @deprecated Use calculateEdgeAppearance() instead. This method will be removed Sept. 2007. */
    public String calculateEdgeLabel(Edge edge, CyNetwork network) {
	doCalc(edge,network);
	return currentAppearance.getLabel();
    }
    /** @deprecated Use calculateEdgeAppearance() instead. This method will be removed Sept. 2007. */
    public Font calculateEdgeFont(Edge edge, CyNetwork network) {
	doCalc(edge,network);
	return currentAppearance.getFont();
    }
    /** @deprecated Use calculateEdgeAppearance() instead. This method will be removed Sept. 2007. */
    public String calculateEdgeToolTip(Edge edge, CyNetwork network) {
	doCalc(edge,network);
	return currentAppearance.getToolTip();
    }

    /** @deprecated Use setCalculator(calc) instead. This method will be removed Sept. 2007. */
    public void setEdgeColorCalculator(EdgeColorCalculator c) {setCalculator(c);}
    /** @deprecated Use setCalculator(calc) instead. This method will be removed Sept. 2007. */
    public void setEdgeLineTypeCalculator(EdgeLineTypeCalculator c) {setCalculator(c);}
    /** @deprecated Use setCalculator(calc) instead. This method will be removed Sept. 2007. */
    public void setEdgeSourceArrowCalculator(EdgeArrowCalculator c) {
    	c.set(VizMapUI.EDGE_SRCARROW,"edgeSourceArrowCalculator","Edge Source Arrow");
    	setCalculator(c);
    }
    /** @deprecated Use setCalculator(calc) instead. This method will be removed Sept. 2007. */
    public void setEdgeTargetArrowCalculator(EdgeArrowCalculator c) {
    	c.set(VizMapUI.EDGE_TGTARROW,"edgeSourceTargetCalculator","Edge Target Arrow");
    	setCalculator(c);
    }
    /** @deprecated Use setCalculator(calc) instead. This method will be removed Sept. 2007. */
    public void setEdgeLabelCalculator(EdgeLabelCalculator c) {setCalculator(c);}
    /** @deprecated Use setCalculator(calc) instead. This method will be removed Sept. 2007. */
    public void setEdgeFontFaceCalculator(EdgeFontFaceCalculator c) {setCalculator(c);}
    /** @deprecated Use setCalculator(calc) instead. This method will be removed Sept. 2007. */
    public void setEdgeFontSizeCalculator(EdgeFontSizeCalculator c) {setCalculator(c);}
    /** @deprecated Use setCalculator(calc) instead. This method will be removed Sept. 2007. */
    public void setEdgeToolTipCalculator(EdgeToolTipCalculator c) {setCalculator(c);}

  /** Used _ONLY_ to support deprecated code - DO NOT USE for anything else!!!! */
  private void doCalc(Edge edge, CyNetwork network) {
        if ( edge != currentEdge && network != currentNetwork )
                calculateEdgeAppearance(edge,network);
  }

}

