
/*
  File: NewSlider.java 
  
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

// NewSlider.java


//---------------------------------------------------------------------------------------
// $Revision: 7760 $
// $Date: 2006-06-26 09:28:49 -0700 (Mon, 26 Jun 2006) $
// $Author: mes $
//---------------------------------------------------------------------------------------
package cytoscape.dialogs;
//---------------------------------------------------------------------------------------
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.text.*;
import java.lang.Math;

public class NewSlider extends JPanel{
    private int minimum,maximum,current, sigFigs;
    private JLabel myTitle, myMin, myMax;
    private MJTextField myTextbox;
    private String title;
    private MJSlider mySlider;   
    String attributeName;
    public NewSlider(){ 
	sigFigs = 0;
	minimum = returnInt(0);
	maximum = returnInt(100);
	current = returnInt(50);
	title = new String(" ");
	attributeName = "";
	setup();
    }
    public NewSlider(String id,double min, double max, double cur){
	sigFigs = 0;//ORDER IS IMPORTANT
	title = new String(id);
	minimum = returnInt(min);
	maximum = returnInt(max);
	current = returnInt(cur);
	setup();
    }
    public NewSlider(String id, double min, double max, double cur, int decimal){
	sigFigs = decimal;
	title = new String(id);
	minimum = returnInt(min);
	maximum = returnInt(max);
	current = returnInt(cur);
	setup();
    }
    public NewSlider(String attribute,String id,double min, double max, double cur){
	sigFigs = 0;//ORDER IS IMPORTANT
	title = new String(id);
	minimum = returnInt(min);
	maximum = returnInt(max);
	current = returnInt(cur);
	attributeName = attribute;
	setup();
    }
    public NewSlider(String attribute,String id, double min, double max, double cur, int decimal){
	sigFigs = decimal;
	title = new String(id);
	minimum = returnInt(min);
	maximum = returnInt(max);
	current = returnInt(cur);
	attributeName = attribute;
	setup();
    }

    public void setup(){
	//order is important!  the slider uses integers, the textboxes
	//use doubles
          // safeguards that ought not to be needed, but nonetheless are
          // (pshannon, 2002/02/26)
        if (current < minimum) current = minimum;
        if (current > maximum) current = maximum;
	mySlider  = new MJSlider(minimum,maximum,current);
	if (sigFigs == 0)
	    myTextbox = new MJTextField ((int)returnDouble(current).doubleValue(), 6);
	else
	    myTextbox = new MJTextField (returnDouble(current).doubleValue(),6);
	if (sigFigs == 0){
	    myMin     = new JLabel(String.valueOf((int)returnDouble(minimum).doubleValue()),JLabel.LEFT);
	    myMax     = new JLabel(String.valueOf((int)returnDouble(maximum).doubleValue()),JLabel.RIGHT);
	}	    
	else{
	    myMin     = new JLabel(String.valueOf(returnDouble(minimum)),JLabel.LEFT);
	    myMax     = new JLabel(String.valueOf(returnDouble(maximum)),JLabel.RIGHT);
	}
	myTitle   = new JLabel (title);
	JLabel myFiller  = new JLabel (" ");


	//holds the title and slider value
	JPanel firstSubPanel = new JPanel();
        firstSubPanel.add(myTitle, BorderLayout.CENTER);
	firstSubPanel.add(myTextbox);

	//mixes text box with max/min boxes
	JPanel secondSubPanel = new JPanel();
	secondSubPanel.setLayout( new GridLayout(0,2) );
	secondSubPanel.add(myMin);
	secondSubPanel.add(myMax);

	//integrates both previous panels
	JPanel sumOneTwoPanel = new JPanel();
	sumOneTwoPanel.setLayout( new GridLayout(0,1) );
	sumOneTwoPanel.add(firstSubPanel);
	sumOneTwoPanel.add(mySlider);
	sumOneTwoPanel.add(secondSubPanel);

        this.add(sumOneTwoPanel);
	Dimension tempDim = this.getPreferredSize();
	//commented out 1-15-02: setting preferred size seemed to mess up the look on one cpu,
	//                       whereas it was fine on another.  
	//	this.setPreferredSize(new Dimension((int)(tempDim.getWidth()-10), (int)(tempDim.getHeight()-17)));
	this.setAlignmentX(CENTER_ALIGNMENT);
	this.setAlignmentY(CENTER_ALIGNMENT);
	//	this.setBorder(BorderFactory.createLineBorder(Color.black));
    }
    public double getDoubleValue(){
	return (returnDouble(current).doubleValue());
    }
    
    //getIntegerValue requires the programmer to know if he's dealing 
    //in integers or not.  If the slider is using doubles, an integer
    //will nevertheless be returned.
    public int getIntegerValue(){
	return ((int)(returnDouble(current).doubleValue()));
    }

    private int returnInt(double original) {
	return (int)( original*(Math.pow(10.0,(double)sigFigs)) );
    }

    private Double returnDouble(int original){
	Double returnVal = new Double( original/Math.pow(10.0,(double)sigFigs));
	return returnVal;
    }
    //*******************************************//
    public class MJTextField extends JTextField
	implements ActionListener{
	
	MJTextField(double dub, int columns){
	    super(String.valueOf(dub), columns);
	    addActionListener(this);
	}
	MJTextField(int tempInt, int columns){
	    super(String.valueOf(tempInt),columns);
	    addActionListener(this);
	}
	public void actionPerformed(ActionEvent e){
	    String str = this.getText();
	    Double temp = new Double(str);
	    int test = returnInt(temp.doubleValue());
	    if (minimum<=test && test<=maximum){
		mySlider.setValue(test);
	    }
	    else{
		this.setText( String.valueOf(returnDouble(mySlider.getValue())) );
	    }
	}
    }
    //*******************************************//
    public class MJSlider extends JSlider
	implements ChangeListener{ 
	String test;
	MJSlider(double x, double y, double currentValue){
	    super ((int)x,(int)y,(int)currentValue);
	    setMajorTickSpacing((maximum-minimum)/4);
	    setPaintTicks (true);
	    addChangeListener(this);
	} 
	public void stateChanged(ChangeEvent e){
	    JSlider source = (JSlider)e.getSource();
	    current = source.getValue();
	    String test = myTitle.getText();
	    if (sigFigs == 0){
		int tempInt= returnInt(source.getValue());
		myTextbox.setText(String.valueOf(tempInt));
	    }
	    else{
		Double tempDub = returnDouble(source.getValue());
		myTextbox.setText(String.valueOf(tempDub));
	    }
	}
    }
}//class NewSlider


