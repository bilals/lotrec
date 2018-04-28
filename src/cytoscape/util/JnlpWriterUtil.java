package cytoscape.util;

/**
 * Utility script to create an up-to-date jnlp file for Cytoscape webstart.
 * 
 * It uses the plugin manifest files to figure out the CytoscapePlugin classes
 * or attempts to guess by looking for a class in the jar file with "Plugin" in the name.
 * 
 * Meant to use as a standalone script through an ant bulidfile.
 */

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Comment;
import org.jdom.output.XMLOutputter;
//import org.jdom.output.Format; //jdom vs 1.0, currently on 0.9

import java.util.HashMap;
import java.util.ArrayList;

import java.util.jar.JarFile;
import java.util.jar.Manifest;

import java.io.File;
import java.io.FileWriter;


public class JnlpWriterUtil
	{
	public Document Doc;
	public Element Root;
	public XMLOutputter Out; 

	protected String Filename;
	protected String CytoDir;
	protected String Url = "http://your.jnlp.location";
	protected String SaveDir;
	
	public String LibDir;
	public String PluginDir;
	
	protected HashMap<String,String> Options;
	
	public JnlpWriterUtil(String[] args)
		{
		this.Options = this.getOptions(args);
		this.Filename = Options.get("filename");
		this.CytoDir = Options.get("cyto_dir");
		this.SaveDir = Options.get("save_dir");
		
		if (Options.containsKey("url")) this.Url = Options.get("url");
		
		this.LibDir = this.CytoDir + "/lib";
		this.PluginDir = this.CytoDir + "/plugins";
		
		
		setupDoc();
		}
	
	private void setupDoc()
		{
		this.Doc = new Document();
		this.Root = new Element("jnlp");
		this.Doc.setRootElement(this.Root);
		
		if (!this.Options.containsKey("url")) 
			{
			Comment Codebase = new Comment("Replace the codebase URL with your own");
			Root.addContent(Codebase);
			}
		Root.setAttribute("codebase", Url);
		Root.setAttribute("href", Filename);
		this.Out = new XMLOutputter("  ", true );
		}
	
	private static void print(String s) { System.out.println(s); }
	
	/*
	 * Creates jnlp file, 
	 * 
	 */
	public static void main(String[] args) throws Exception
		{
		JnlpWriterUtil jutil = new JnlpWriterUtil(args);
		
		ArrayList<String> MainLibs = jutil.getLibJars();
		print("Adding "+MainLibs.size()+" total main jars");

		ArrayList<String> PluginLibs = jutil.getPluginJars();
		ArrayList<String> PluginClasses = jutil.getMainClass(PluginLibs, jutil.PluginDir);
		print("Adding "+PluginLibs.size()+" total plugin jars");
		
		jutil.createInfoTag();
		jutil.createResourcesTag();
		jutil.addJars("lib/", MainLibs, null);
		jutil.addJars( "plugins/", PluginLibs, "These are the plugins you wish to load, edit as necessary.");

		jutil.addArguments(PluginClasses);
		
		jutil.writeToFile();
		}

	/**
	 * @return jdom.Document as a string
	 */
	public String getString()
		{ return Out.outputString(this.Doc); }
	
	/**
	 * 
	 * @param Loc - directory to write xml file
	 * @throws java.io.IOException
	 */
	public void writeToFile() throws java.io.IOException
		{
		FileWriter writer = new FileWriter(this.SaveDir+"/"+Filename);
		Out.output(this.Doc, writer);
		}

	

	
	/*
	 * Adding the <information> tag
	 */
	public void createInfoTag()
		{
		Element Info = new Element("information");
		Info.addContent( new Element("title").setText("Cytoscape Webstart") );
		Info.addContent( new Element("vendor").setText("Cytoscape Collaboration") );
		Info.addContent( new Element("homepage").setAttribute("href", "http://cytoscape.org") );
		Info.addContent( new Element("offline-allowed") );

		this.Root.addContent( new Element("security").addContent( new Element("all-permissions")) );

		this.Root.addContent(Info);
		}

	/*
	 * Adding the <resource> tag
	 */
	public void createResourcesTag()
		{
		Element Resources = new Element("resources");
		Element JSE = new Element("j2se");
		JSE.setAttribute("version", "1.5+");
		JSE.setAttribute("max-heap-size", "1024M");
		Resources.addContent(JSE);
		Resources.addContent( new Comment("All lib jars that cytoscape requires to run should be in this list") );
		Resources.addContent( new Element("jar").setAttribute("href", "cytoscape.jar") );
		
		this.Root.addContent(Resources);
		}

	/**
	 * 
	 * @param Prefix (append to beginning of jar file name)
	 * @param Jars
	 * @param Comment
	 */
	public void addJars( String Prefix, ArrayList<String> Jars, String Comment )
		{
		Element Resources = this.Root.getChild("resources");
		if (Comment != null) Resources.addContent( new Comment(Comment) );
		
		for(int i=0; i<Jars.size(); i++)
			{
			Resources.addContent( new Element("jar").setAttribute("href", Prefix+Jars.get(i)) );
			}
		}


	public ArrayList<String> getLibJars()
		{ return this.getJarList(this.LibDir); }
	
	public ArrayList<String> getPluginJars()
		{ return this.getJarList(this.PluginDir); }
	
	/**
	 * @param Dir
	 * @return ArrayList<String> of jars listed in given directory
	 */
	private ArrayList<String> getJarList(String Dir)
		{
		File JarDir = new File(Dir);
		if (!JarDir.exists()) 
			{
			System.err.println("Required directory '"+JarDir.getAbsolutePath()+"' does not exist");
			System.exit(-1);
			}
		
		ArrayList<String> JarFiles = new ArrayList<String>();
		for(File Current: JarDir.listFiles())
			{
			if (Current.isFile() && Current.getName().endsWith(".jar"))
				{ JarFiles.add(Current.getName()); }
			}
		return JarFiles;
		}

	/**
	 * @param JarFiles
	 * @param JarDir
	 * @return ArrayList<String> of the CytoscapePlugin classes that could be determined from each plugin
	 */
	public ArrayList<String> getMainClass(ArrayList<String> JarFiles, String JarDir)
		{
		ArrayList<String> PluginMainClass = new ArrayList<String>();
		
		for(int i=0; i<JarFiles.size(); i++)
			{
			try
				{
				JarFile jf = new JarFile(JarDir+"/"+JarFiles.get(i));
				Manifest m = jf.getManifest();
				if ( m != null ) 
					{
					String className = m.getMainAttributes().getValue("Cytoscape-Plugin");
					if ( className != null ) 
						{
						// add to list
						PluginMainClass.add(className);
						continue;
						}
					}
				}
			catch (Exception E) { E.printStackTrace(); }
			}
		return PluginMainClass;
		}
	
	/**
	 * @param args
	 * @return HashMap<String, String> of the command line options
	 */
	private HashMap<String, String> getOptions(String[] args)
		{
		String Usage = "Usage: java "+this+" [parameters]\n" +
										"Option    : Description          Required\n" +
										"-filename : Name of jnlp file    yes\n" +
										"-cyto_dir : Cytoscape directory  yes\n" +
										"-url      : Webstart url         no\n" +
										"-save_dir : Save to dir          yes\n"
										;
		
		HashMap<String, String> Opts = new HashMap<String,String>();

		if (args.length < 2) 
			{
			System.err.println("Too few arguments ("+args.length+"). "+Usage);
			System.exit(-1);
			}
		
		for(int i=0; i<args.length; i++)
			{
			if ( args[i].equals("-filename") ) Opts.put("filename", args[i+1]);
			if ( args[i].equals("-cyto_dir") ) Opts.put("cyto_dir", args[i+1]);
			if ( args[i].equals("-url") ) 		 Opts.put("url", args[i+1]);
			if ( args[i].equals("-save_dir") ) Opts.put("save_dir", args[i+1]);
			}

		if (!Opts.containsKey("filename") || 
				!Opts.containsKey("cyto_dir") ||
				!Opts.containsKey("save_dir"))
			{ 
			System.err.println("Required arguments missing. "+Usage);
			System.exit(-1);
			}

		return Opts;
		}

	/**
	 * 
	 * @param Args
	 * These are all plugin arguments at the moment, only specifies the -p tag between each.
	 */
	public void addArguments(ArrayList<String> Args)
		{
		Element Application = this.Root.getChild("application-desc");
		if (Application == null)
			{ 
			this.Root.addContent( 
					new Comment("This starts-up Cytoscape, specify your plugins to load, and other command line arguments.  Plugins not specified here will not be loaded."));

			Application = new Element("application-desc").setAttribute("main-class", "cytoscape.CyMain"); 
			this.Root.addContent(Application);
			}
		
		for(int i=0; i<Args.size(); i++)
			{
			Application.addContent( new Element("argument").setText("-p") );
			Application.addContent( new Element("argument").setText(Args.get(i)) ); 
			}
		}

	
	
	}
