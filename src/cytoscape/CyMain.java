
/*
  File: CyMain.java 
  
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

package cytoscape;

import java.util.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.awt.Dimension;
import javax.swing.*;
import javax.swing.event.*;

import cytoscape.init.CyInitParams;
import cytoscape.util.FileUtil;

import com.jgoodies.plaf.FontSizeHints;
import com.jgoodies.plaf.LookUtils;
import com.jgoodies.plaf.Options;
import com.jgoodies.plaf.plastic.Plastic3DLookAndFeel;
import cytoscape.Cytoscape;
import org.apache.commons.cli.*; 

/**
 * This is the main startup class for Cytoscape. This parses the command line
 * and implements CyInitParams so that it can be used to initialize cytoscape.
 */
public class CyMain implements CyInitParams {

	protected String[] args;
	protected Properties props; 
	protected String[] graphFiles; 
	protected String[] plugins;
	protected Properties vizmapProps;
	protected String sessionFile;
	protected String[] nodeAttrFiles;
	protected String[] edgeAttrFiles;
	protected String[] expressionFiles;
	protected int mode; 
	protected org.apache.commons.cli.Options options; 


	public static void main(String args []) throws Exception {
		CyMain app = new CyMain(args);
	}

	public CyMain ( String [] args ) throws Exception {

		props = null;
		graphFiles = null;
		plugins = null;
		vizmapProps = null;
		sessionFile = null;
		nodeAttrFiles = null;
		edgeAttrFiles = null;
		expressionFiles = null;
		this.args = args;
		mode = CyInitParams.ERROR;
		options = new org.apache.commons.cli.Options();
		//for (String asdf: args)
		//	System.out.println("arg: '" + asdf + "'");

		parseCommandLine(args);
		CytoscapeInit initializer = new CytoscapeInit();

		if ( !initializer.init(this) ) {
			printHelp();
			Cytoscape.exit(1);
		}
	}        

	protected void parseCommandLine(String args[]) {

		// create the options
		options.addOption("h", "help", false, "Print this message.");
		options.addOption("v", "version", false, "Print the version number.");
// commented out until we actually support doing anything in headless mode
//		options.addOption("H", "headless", false, "Run in headless (no gui) mode.");

		options.addOption(OptionBuilder
	                                .withLongOpt("session")
	                                .withDescription( "Load a cytoscape session (.cys) file.")
	                                .withValueSeparator(' ')
	                                .withArgName("file")
	                                .hasArg() // only allow one session!!!
					.create("s"));

		options.addOption(OptionBuilder
	                                .withLongOpt("network")
	                                .withDescription( "Load a network file (any format).")
	                                .withValueSeparator(' ')
	                                .withArgName("file")
	                                .hasArgs()
					.create("N"));

		options.addOption(OptionBuilder
	                                .withLongOpt("edge-attrs")
	                                .withDescription( "Load an edge attributes file (edge attribute format).")
	                                .withValueSeparator(' ')
	                                .withArgName("file")
	                                .hasArgs()
					.create("e"));
		options.addOption(OptionBuilder
	                                .withLongOpt("node-attrs")
	                                .withDescription( "Load a node attributes file (node attribute format).")
	                                .withValueSeparator(' ')
	                                .withArgName("file")
	                                .hasArgs()
					.create("n"));
		options.addOption(OptionBuilder
	                                .withLongOpt("matrix")
	                                .withDescription( "Load a node attribute matrix file (table).")
	                                .withValueSeparator(' ')
	                                .withArgName("file")
	                                .hasArgs()
					.create("m"));


		options.addOption(OptionBuilder
	                                .withLongOpt("plugin")
	                                .withDescription( "Load a plugin jar file, directory of jar files, plugin class name, or plugin jar URL.")
	                                .withValueSeparator(' ')
	                                .withArgName("file")
	                                .hasArgs()
					.create("p"));

		options.addOption(OptionBuilder
	                                .withLongOpt("props")
	                                .withDescription( "Load cytoscape properties file (Java properties format) or individual property: -P name=value.")
					// the null value here is so that properties can have spaces in them
	                                .withValueSeparator('\0') 
	                                .withArgName("file")
	                                .hasArgs()
					.create("P"));
		options.addOption(OptionBuilder
	                                .withLongOpt("vizmap")
	                                .withDescription( "Load vizmap properties file (Java properties format).")
	                                .withValueSeparator(' ')
	                                .withArgName("file")
	                                .hasArgs()
					.create("V"));

		// try to parse the cmd line
		CommandLineParser parser = new PosixParser();
		CommandLine line = null;

		try {
			line = parser.parse(options, args);
		} catch (ParseException e) {
			System.err.println("Parsing command line failed: " + e.getMessage());
			printHelp();
			System.exit(1);
		}

		// use what is found on the command line to set values
		if ( line.hasOption("h") ) {
			printHelp();
			System.exit(0); 
		}

		if ( line.hasOption("v") ) {
			CytoscapeVersion version = new CytoscapeVersion();
			System.out.println(version.getVersion());
			System.exit(0); 
		}

		if ( line.hasOption("H") ) {
			mode = CyInitParams.TEXT;
		} else {
			mode = CyInitParams.GUI;
			setupLookAndFeel();
		}

		if ( line.hasOption("P") ) 
			props = createProperties( line.getOptionValues("P") );	
		else
			props = createProperties( new String[0] );

		if ( line.hasOption("N") ) 
			graphFiles = line.getOptionValues("N");	

		if ( line.hasOption("p") ) 
			plugins = line.getOptionValues("p");	

		if ( line.hasOption("V") ) 
			vizmapProps = createProperties( line.getOptionValues("V") );	
		else
			vizmapProps = createProperties( new String[0] );

		if ( line.hasOption("s") ) 
			sessionFile = line.getOptionValue("s");	
	
		if ( line.hasOption("n") ) 
			nodeAttrFiles = line.getOptionValues("n");	

		if ( line.hasOption("e") ) 
			edgeAttrFiles = line.getOptionValues("e");	

		if ( line.hasOption("m") ) 
			expressionFiles = line.getOptionValues("m");	
	}

	protected void setupLookAndFeel() {
		
		UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
		UIManager.put(Options.USE_SYSTEM_FONTS_APP_KEY, Boolean.TRUE);
		Options.setGlobalFontSizeHints(FontSizeHints.MIXED);
		Options.setDefaultIconSize(new Dimension(18, 18));

		try {
			if ( LookUtils.isWindowsXP() ) {
				// use XP L&F
				UIManager.setLookAndFeel( Options.getSystemLookAndFeelClassName() );
			} else if ( System.getProperty("os.name").startsWith( "Mac" ) ) {
				// do nothing, I like the OS X L&F
			} else {
				// this is for for *nix
				// I happen to like this color combo, there are others
				// jgoodies
				Plastic3DLookAndFeel laf = new Plastic3DLookAndFeel();
				laf.setTabStyle( Plastic3DLookAndFeel.TAB_STYLE_METAL_VALUE );
				laf.setHighContrastFocusColorsEnabled(true);
				laf.setMyCurrentTheme( new com.jgoodies.plaf.plastic.theme.ExperienceBlue() );
				UIManager.setLookAndFeel( laf );
			}
		} catch (Exception e) {
			System.err.println("Can't set look & feel:" + e);
		}
	} 

	protected void printHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java -Xmx512M -jar cytoscape.jar [OPTIONS]", options);
	}


	public Properties getProps() {
		return props;
	}

	public Properties getVizProps() {
		return vizmapProps;
	}

	private Properties createProperties(String[] potentialProps) {

		//for ( String asdf: potentialProps)
		//	System.out.println("prop: '" + asdf + "'");

		Properties props = new Properties();
		Properties argProps = new Properties();

		Matcher propPattern = Pattern.compile("^((\\w+\\.*)+)\\=(.+)$").matcher(""); 

		for ( int i = 0; i < potentialProps.length; i++ ) {

			propPattern.reset(potentialProps[i]);	

			// check to see if the string is a key value pair
			if ( propPattern.matches() ) {
				argProps.setProperty(propPattern.group(1),propPattern.group(3));

			// otherwise assume it's a file/url
			} else {
				try {
				InputStream in = FileUtil.getInputStream( potentialProps[i] );

				if ( in != null )
					props.load(in);
				else
					System.out.println("Couldn't load property: " + potentialProps[i]);
				} catch (IOException e) { 
					System.out.println("Couldn't load property: " + potentialProps[i
]);
					e.printStackTrace(); 
				}
			}
		}

		// Transfer argument properties into the full properties.
		// We do this so that anything specified on the command line
		// overrides anything specified in a file.
		props.putAll(argProps);

		return props;
	}

	public List getGraphFiles() {
		return createList( graphFiles );
	}

	public List getEdgeAttributeFiles() {
		return createList( edgeAttrFiles );
	}

	public List getNodeAttributeFiles() {
		return createList( nodeAttrFiles );
	}

	public List getExpressionFiles() {
		return createList( expressionFiles );
	}

	public List getPlugins() {
		return createList( plugins );
	}

	public String getSessionFile() {
		return sessionFile; 
	}

	public int getMode() {
		return mode;
	}

	public String[] getArgs() {
		return args;
	}

	private List createList(String[] vals) {
		if ( vals == null )
			return new ArrayList();
		ArrayList a = new ArrayList(vals.length);
		for ( int i = 0; i < vals.length; i++ )
			a.add(i,vals[i]);

		return a;
	}
}

