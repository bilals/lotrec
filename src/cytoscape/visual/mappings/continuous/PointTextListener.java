
/*
  File: PointTextListener.java 
  
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
// $Revision: 7760 $
// $Date: 2006-06-26 09:28:49 -0700 (Mon, 26 Jun 2006) $
// $Author: mes $
//----------------------------------------------------------------------------
package cytoscape.visual.mappings.continuous;

import cytoscape.visual.mappings.ContinuousMapping;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.NumberFormat;

/**
 * Listens for a User Initiated Change in any of the JTextFields
 * in the Continuous Mapper UI.
 */
public class PointTextListener extends FocusAdapter {
    private JTextField textField;
    private int index;
    private ContinuousMapping cm;
    private JDialog parent;

    /**
     * Constructor.
     */
    public PointTextListener(JTextField textField,
            ContinuousMapping cm, JDialog parent, int index) {
        this.textField = textField;
        this.cm = cm;
        this.parent = parent;
        this.index = index;
    }

    /**
     * User now clicks outside of Text Box.  Now validate that
     * the new data is acceptable.
     * @param e
     */
    public void focusLost(FocusEvent e) {
        //  Get Old Data Point (Before User Change)
        ContinuousMappingPoint point = cm.getPoint(index);
        try {
            validate(point);
        } catch (IllegalArgumentException arge) {
            //  If new point is no good, revert back to old point.
            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setMaximumFractionDigits(6);
            String textValue = numberFormat.format(point.getValue());
            textField.setText(textValue);
            JOptionPane.showMessageDialog(parent, arge.getMessage() +
                    " Please try again.", "Data Validation Error",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Validates new data field.  If the new data is not acceptable,
     * we revert back to existing data.
     */
    private void validate(ContinuousMappingPoint point) {
        //  Get New Data Point (After User Change)
        double newValue = getNewValue();

        //  Check Previous/Next Points (if available)
        checkPreviousPoint(newValue);
        checkNextPoint(newValue);

        //  If all goes well, update the point.
        point.setValue(newValue);
    }

    /**
     * Gets new value from the Text Box.
     * @return double value.
     * @throws IllegalArgumentException if this is not a number.
     */
    private double getNewValue() {
        String number = textField.getText();
        double newValue;
        try {
            newValue = Double.parseDouble(number);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("This is not a number:  "
                    + number + ".");
        }
        return newValue;
    }

    /**
     * Validates New Point against Next Point in List.
     * @throws IllegalArgumentException if this is not a valid value.
     */
    private void checkNextPoint(double newValue) {
        try {
            ContinuousMappingPoint nextPoint = cm.getPoint(index + 1);
            double nextValue = nextPoint.getValue();
            if (newValue > nextValue) {
                throw new IllegalArgumentException(newValue
                        + " must be greater than " +
                        "next point:  " + nextValue + ".");
            }
        } catch (IndexOutOfBoundsException e) {
            //  There is no next point;  skip test.
        }
    }

    /**
     * Validates New Point against Previous Point in List.
     * @throws IllegalArgumentException if this is not a valid value.
     */
    private boolean checkPreviousPoint(double newValue) {
        boolean errorFlag = false;
        try {
            ContinuousMappingPoint prevPoint = cm.getPoint(index - 1);
            double previousValue = prevPoint.getValue();
            if (newValue < previousValue) {
                throw new IllegalArgumentException(newValue
                        + " must be less than " +
                        "previous point:  " + previousValue + ".");
            }
        } catch (IndexOutOfBoundsException e) {
            //  There is no previous point;  skip test.
        }
        return errorFlag;
    }
}