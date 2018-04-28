/*
 File: ValueDisplayer.java 
 
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

package cytoscape.visual.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.text.DecimalFormat;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.plaf.metal.MetalButtonUI;

import cytoscape.visual.Arrow;
import cytoscape.visual.LineType;
import cytoscape.visual.ShapeNodeRealizer;
import cytoscape.visual.LabelPosition;
import cytoscape.util.CyColorChooser;

/**
 * Given an Object, figures out the class of the object and creates a JButton
 * suitable for displaying the value of that object. When the button is pressed,
 * it should pop up a user interface to change the value.
 * 
 * The class interested in the selection of the ValueDisplayer should add an
 * ItemListener to the button. The ItemListener is triggered when the user's
 * selection changes.
 */
public class ValueDisplayer extends JButton {
	/**
	 * Formatting for numeric types.
	 */
	public static DecimalFormat formatter = new DecimalFormat("0.0####");

	/**
	 * Display and get input for a color
	 */
	public static final byte COLOR = 0;

	/**
	 * Display and get input for a linetype
	 */
	public static final byte LINETYPE = 1;

	/**
	 * Display and get input for an arrowhead
	 */
	public static final byte ARROW = 2;

	/**
	 * Display and get input for a string
	 */
	public static final byte STRING = 3;

	/**
	 * Display and get input for a double
	 */
	public static final byte DOUBLE = 4;

	/**
	 * Display and get input for node shape
	 */
	public static final byte NODESHAPE = 5;

	/**
	 * Display and get input for an int
	 */
	public static final byte INT = 6;

	/**
	 * Display and get input for a font
	 */
	public static final byte FONT = 7;

	/**
	 * Display and get input for label position 
	 */
	public static final byte LABEL_POSITION = 8;

	/**
	 * Holds the type of UI this ValueDisplayer will pop up.
	 */
	protected byte dispType;

	/**
	 * Holds the object inputted by the user.
	 */
	private Object inputObj = null;

	/**
	 * Input dialog title
	 */
	private String title;

	/**
	 * Parent dialog
	 */
	private JDialog parent;

	/**
	 * ActionListener that triggers input UI
	 */
	private ActionListener inputListener;

	/**
	 * Enable/disable mouse listeners
	 */
	private boolean enabled;

	/**
	 * Default font used to initialize things.
	 */
	private static Font defaultDisplayFont = new Font(null, Font.PLAIN, 1);

	/**
	 * Provided for convenience.
	 * 
	 * @see #getValue
	 * @return User-selected object displayed by this ValueDisplayer
	 */
	public Object getSelectedItem() {
		return this.getValue();
	}

	/**
	 * Returns an object representing the user input. The return value is always
	 * an object type. It may be a String, Number, Arrow, LineType, or Byte
	 * depending on what type the ValueDisplayer was initialized with.
	 * 
	 * @return User-selected object displayed by this ValueDisplayer
	 */
	public Object getValue() {
		return inputObj;
	}

	/**
	 * Returns the ActionListener that will pop up the input UI when triggered.
	 * Attach this to a component to trigger input on a click.
	 */
	public ActionListener getInputListener() {
		return inputListener;
	}

	/**
	 * Returns the type of input this ValueDisplayer displays/gets input for
	 */
	public byte getType() {
		return dispType;
	}

	/**
	 * Set the ValueDisplayer active/inactive.
	 * 
	 * @param b
	 *            true to enable, false to disable
	 */
	public void setEnabled(boolean b) {
		this.enabled = b;
		super.setEnabled(b);
	}

	/**
	 * This private constructor is used to create all ValueDisplayers. Use the
	 * static method getDisplayFor (@link #getDisplayFor) to get a new
	 * ValueDisplayer.
	 */
	private ValueDisplayer(JDialog parent, String labelText, String title,
			byte dispType) {
		super(labelText);
		setBorderPainted(false);

		this.parent = parent;
		this.dispType = dispType;
		this.title = title;
	}

	private ValueDisplayer(JDialog parent, String title, byte dispType) {
		// can't find proper icon/label until later, so set label to null for
		// now
		this(parent, null, title, dispType);
	}

	public static ValueDisplayer getDisplayForLabelPosition(JDialog parent,
			String title, LabelPosition c) {
		ValueDisplayer v = new ValueDisplayer(parent, c.shortString(), title, LABEL_POSITION);
		v.setInputLabelPositionListener();
		v.inputObj = c;
		return v;
	}

	private void setInputLabelPositionListener() {
		this.inputListener = new LabelPositionListener(this);
		addActionListener(this.inputListener);
	}

	public static ValueDisplayer getDisplayForColor(JDialog parent,
			String title, Color c) {
		String dispString = "   "; // just display the color
		ValueDisplayer v = new ValueDisplayer(parent, dispString, title, COLOR);
		// gbader Jul12/04 - force the Metal L&F here, since the windows L&F
		// doesn't color the button
		v.setUI(new MetalButtonUI());
		if (c != null) {
			v.setOpaque(true);
			v.setBackground(c);
			v.inputObj = c;
		} else {
			v.setOpaque(false);
			v.setBackground(null);
			v.inputObj = null;
		}
		v.setInputColorListener();
		return v;
	}

	private void setInputColorListener() {
		this.inputListener = new ColorListener(this);
		addActionListener(this.inputListener);
	}

	/**
	 * This method fires the itemListeners. Item listeners are notified only
	 * when a new selection of the underlying value of the ValueDisplayer is
	 * made.
	 * 
	 * Typically this should only be called by listeners that underlie the
	 * internal structure of the ValueDisplayer
	 */
	protected void fireItemSelected() {
		this.fireItemStateChanged(new ItemEvent(this,
				ItemEvent.ITEM_STATE_CHANGED, inputObj, ItemEvent.SELECTED));
	}

	/**
	 * Externally sets the object displayed. Ensure that the class is the same.
	 * Does not fire an itemSelected event.
	 * 
	 * @throws ClassCastException
	 *             if caller attempts to set an object different from what was
	 *             being represented.
	 */
	public void setObject(Object o) throws ClassCastException {
		inputObj = o;
		if (o instanceof Icon) {
			setIcon((Icon) o);
		} else if (o instanceof LabelPosition) {
			setText(((LabelPosition)o).shortString());
		} else if (o instanceof Color) {
			setBackground((Color) o);
		} else if (o instanceof Font) {
			Font f = (Font) o;
			setSelectedFont(f);
		} else { // anything else must be a Double, Integer, or String
			setText(o.toString());
		}
		// fireItemSelected();
	}

	// internal class ColorListener
	private class ColorListener extends AbstractAction {
		ValueDisplayer parent;

		ColorListener(ValueDisplayer parent) {
			super("ValueDisplayer ColorListener");
			this.parent = parent;
		}

		public void actionPerformed(ActionEvent e) {
			if (enabled) {
				Color tempColor = CyColorChooser.showDialog(parent.parent,
						parent.title, (Color) parent.inputObj);
				if (tempColor != null) {
					parent.inputObj = tempColor;
					parent.setBackground(tempColor);
					parent.fireItemSelected();
				}
			}
		}
	}

	private class LabelPositionListener extends AbstractAction {
		ValueDisplayer parent;
		LabelPositionListener(ValueDisplayer parent) {
			super("ValueDisplayer LabelPositionListener");
			this.parent = parent;
		}
		public void actionPerformed(ActionEvent e) {
			if (enabled) {
				LabelPosition pos = PopupLabelPositionChooser.showDialog(
						parent.parent, (LabelPosition)parent.inputObj);
				if ( pos != null ) {
					parent.inputObj = pos;
					setText(pos.shortString());
					parent.fireItemSelected();
				}
			}
		}
	}


	private static ValueDisplayer getDisplayForFont(JDialog parent,
			String title, Font startFont) {
		ValueDisplayer v = new ValueDisplayer(parent, title, FONT);
		v.setSelectedFont(startFont);
		v.setInputFontListener();
		return v;
	}

	private void setSelectedFont(Font f) {
		this.inputObj = f;
		setFont(f);
		setText(f.getFontName());
	}

	private void setInputFontListener() {
		this.inputListener = new FontListener(this);
		addActionListener(this.inputListener);
	}

	private class FontListener extends AbstractAction {
		ValueDisplayer parent;

		FontListener(ValueDisplayer parent) {
			super("ValueDisplayer FontListener");
			this.parent = parent;
		}

		public void actionPerformed(ActionEvent e) {
			Font f = PopupFontChooser.showDialog(parent.parent, (Font)parent.inputObj);
			if ( f != null ) {
				parent.setSelectedFont(f);
				parent.fireItemSelected();
			}
		}
	}

	private static ValueDisplayer getDisplayForIcons(JDialog parent,
			String title, Object startObj, byte type) {
		ValueDisplayer v = new ValueDisplayer(parent, title, type);
		// has to be done this way because of static call
		v.setInputIconListener(title, title, startObj, parent, type);
		return v;
	}

	private void setInputIconListener(String title, String objectName,
			Object startObject, JDialog parentDialog, byte type) {

		IconSupport is = new IconSupport(startObject,type);

		if (startObject != null) {
			this.setContentAreaFilled(false);
			this.setIcon(is.getCurrentIcon());
			this.inputObj = is.getIconType(is.getCurrentIcon());
		}

		this.inputListener = new IconListener(title, objectName, is, parentDialog, this);
		addActionListener(this.inputListener);
	}

	// internal class IconListener. Calls PopupIconChooser to get an icon from
	// the user.
	private class IconListener extends AbstractAction {
		private PopupIconChooser chooser;
		private ValueDisplayer parent;
		private IconSupport is;

		IconListener(String title, String objectName, IconSupport is , JDialog parentDialog,
				ValueDisplayer parent) {
			super("ValueDisplayer IconListener");
			this.chooser = new PopupIconChooser(title, objectName, is.getIcons(),
					is.getCurrentIcon(), parentDialog);
			this.parent = parent;
			this.is = is;
		}

		public void actionPerformed(ActionEvent e) {
			if (enabled) {
				ImageIcon icon = chooser.showDialog();
				if (icon != null) {
					parent.setContentAreaFilled(false);
					parent.setIcon(icon);
					parent.inputObj = is.getIconType(icon);
					parent.fireItemSelected();
				}
			}
		}
	}

	private void addStringListener(String prompt, byte type) {
		this.inputListener = new StringListener(prompt, type);
		addActionListener(this.inputListener);
	}

	private static ValueDisplayer getDisplayForString(JDialog parent,
			String title, String init) {
		ValueDisplayer v = new ValueDisplayer(parent, init, title, STRING);
		v.addStringListener("Input a string:", STRING);
		return v;
	}

	private static ValueDisplayer getDisplayForDouble(JDialog parent,
			String title, double init) {
		ValueDisplayer v = new ValueDisplayer(parent, formatter.format(init),
				title, DOUBLE);
		v.addStringListener("Input a double:", DOUBLE);
		return v;
	}

	private static ValueDisplayer getDisplayForInt(JDialog parent,
			String title, int init) {
		ValueDisplayer v = new ValueDisplayer(parent, Integer.toString(init),
				title, INT);
		v.addStringListener("Input an integer:", INT);
		return v;
	}

	// StringListener for String, Double, Int types
	private class StringListener extends AbstractAction {
		private byte type;
		private String prompt;

		StringListener(String prompt, byte type) {
			super("ValueDisplayer StringListener");
			this.prompt = prompt;
			this.type = type;
		}

		public void actionPerformed(ActionEvent e) {
			if (enabled) {
				Object o = PopupStringChooser.showDialog(parent,title,prompt,inputObj,type); 
				if ( o != null ) { 
					inputObj = o;
					setText(inputObj.toString());
					fireItemSelected();
				}
			}
		}
	}

	/**
	 * Get a blank or default display/input pair for a given type of input.
	 * 
	 * @param parent
	 *            The parent dialog for the returned ValueDisplayer
	 * @param title
	 *            Title to display for input dialog
	 * @param type
	 *            Type of input, one of {@link #COLOR}, {@link #LINETYPE},
	 *            {@link #NODESHAPE}, {@link #ARROW}, {@link #STRING},
	 *            {@link #DOUBLE}, {@link #INT}, {@link #FONT}
	 * 
	 * @return ValueDisplayer initialized for given input
	 * @throws ClassCastException
	 *             if you didn't pass in a known type
	 */
	public static ValueDisplayer getBlankDisplayFor(JDialog parent,
			String title, byte type) {
		switch (type) {
		case COLOR:
			return getDisplayForColor(parent, title, null);
		case LINETYPE:
			return getDisplayForIcons(parent, title, null, LINETYPE);
		case NODESHAPE:
			return getDisplayForIcons(parent, title, new Byte(
					ShapeNodeRealizer.ELLIPSE), NODESHAPE);
		case ARROW:
			return getDisplayForIcons(parent, title, Arrow.NONE, ARROW);
		case STRING:
			return getDisplayForString(parent, title, null);
		case DOUBLE:
			return getDisplayForDouble(parent, title, 0);
		case INT:
			return getDisplayForInt(parent, title, 0);
		case FONT:
			return getDisplayForFont(parent, title, defaultDisplayFont);
		case LABEL_POSITION:
			return getDisplayForLabelPosition(parent, title, new LabelPosition());
		default:
			throw new ClassCastException(
					"ValueDisplayer didn't understand type flag " + type);
		}
	}

	/**
	 * Get a blank or default display/input pair for the given sample object,
	 * which itself is ignored.
	 * 
	 * @param parent
	 *            The parent dialog for the returned ValueDisplayer
	 * @param title
	 *            Title to display for input dialog
	 * @param o
	 * 
	 * 
	 * @return ValueDisplayer initialized for given input
	 * @throws ClassCastException
	 *             if you didn't pass in a known type
	 */
	public static ValueDisplayer getBlankDisplayFor(JDialog parent,
			String title, Object sampleObj) {
		if (sampleObj instanceof Color) {
			return getDisplayForColor(parent, title, null);
		} else if (sampleObj instanceof LineType) {
			return getDisplayForIcons(parent, title, null, LINETYPE);
		} else if (sampleObj instanceof Byte) {
			return getDisplayForIcons(parent, title, null, NODESHAPE);
		} else if (sampleObj instanceof Arrow) {
			return getDisplayForIcons(parent, title, null, ARROW);
		} else if (sampleObj instanceof String) {
			return getDisplayForString(parent, title, null);
		} else if (sampleObj instanceof Number) {
			if (sampleObj instanceof Float || sampleObj instanceof Double) {
				return getDisplayForDouble(parent, title, 0);
			} else {
				return getDisplayForInt(parent, title, 0);
			}
		} else if (sampleObj instanceof Font) {
			return getDisplayForFont(parent, title, defaultDisplayFont);
		} else if (sampleObj instanceof LabelPosition) {
			return getDisplayForLabelPosition(parent, title, new LabelPosition());
		} else {// don't know what to do this this
			throw new ClassCastException(
					"ValueDisplayer doesn't know how to display type "
							+ sampleObj.getClass().getName());
		}
	}

	/**
	 * Get a display/input pair initialized to a given type of input. If sending
	 * fonts, must send fonts as gotten from
	 * {@link java.awt.GraphicsEnvrionment#getAllFonts}
	 * 
	 * @param parent
	 *            The parent dialog for the returned ValueDisplayer
	 * @param o
	 *            Object to represent. Should be a {@link java.awt.Color Color},
	 *            {@link y.view.LineType LineType}, node shape (byte), arrow,
	 *            string, or number
	 * @return ValueDisplayer displaying the given object and accepting input
	 *         for given object
	 * @throws ClassCastException
	 *             if you didn't pass in a known type
	 */
	public static ValueDisplayer getDisplayFor(JDialog parent, String title,
			Object o) throws ClassCastException {
		if (o instanceof Color) {
			return getDisplayForColor(parent, title, (Color) o);
		} else if (o instanceof LineType) {
			return getDisplayForIcons(parent, title, o, LINETYPE);
		} else if (o instanceof Byte) {
			return getDisplayForIcons(parent, title, o, NODESHAPE);
		} else if (o instanceof Arrow) {
			return getDisplayForIcons(parent, title, o, ARROW);
		} else if (o instanceof String) {
			return getDisplayForString(parent, title, (String) o);
		} else if (o instanceof Number) {
			if (o instanceof Float || o instanceof Double) {
				return getDisplayForDouble(parent, title, ((Number) o)
						.doubleValue());
			} else {
				return getDisplayForInt(parent, title, ((Number) o).intValue());
			}
		} else if (o instanceof Font) {
			return getDisplayForFont(parent, title, (Font) o);
		} else if (o instanceof LabelPosition) {
			return getDisplayForLabelPosition(parent, title, (LabelPosition) o);
		} else {// don't know what to do this this
			throw new ClassCastException(
					"ValueDisplayer doesn't know how to display type "
							+ o.getClass().getName());
		}
	}
}
