
/*
  File: RandomColorListener.java 
  
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

package cytoscape.visual.mappings.discrete;

import cytoscape.visual.mappings.DiscreteMapping;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.util.*;

/**
 *  User wants to Seed the Discrete Mapper with Random Color Values.
 */
public class RandomColorListener implements ActionListener {
    private DiscreteMapping dm;
    private TreeSet mappedKeys;

    /**
     * Constructs a ValueChangeListener.
     */
    public RandomColorListener(DiscreteMapping dm, TreeSet mappedKeys) {
        this.dm = dm;
        this.mappedKeys = mappedKeys;
    }

    /**
     *  User wants to Seed the Discrete Mapper with Random Color Values.
     */
    public void actionPerformed (ActionEvent e) {
        Calendar cal = Calendar.getInstance();
        int seed = cal.get(Calendar.SECOND);
        Random rand = new Random(seed);
        Iterator iterator = mappedKeys.iterator();
        while (iterator.hasNext()) {
            Object key = iterator.next();
            int r = rand.nextInt(255);
            int g = rand.nextInt(255);
            int b = rand.nextInt(255);
            Color c1 = new Color(r,g,b);
            dm.putMapValue(key,c1);
        }
    }
}