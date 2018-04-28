
/*
  File: ExpressionData.java 
  
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

//ExpressionData.java


//--------------------------------------------------------------------
// $Revision: 8901 $
// $Date: 2006-11-21 08:39:28 -0800 (Tue, 21 Nov 2006) $
// $Author: mcreech $
//--------------------------------------------------------------------
package cytoscape.data;

//--------------------------------------------------------------------

import cytoscape.data.readers.TextFileReader;
import cytoscape.data.readers.TextJarReader;
import cytoscape.task.TaskMonitor;
import cytoscape.util.FileUtil;

import java.io.*;
import java.util.*;
import cytoscape.Cytoscape;
import cytoscape.CyNode;

//--------------------------------------------------------------------

/**
 * This class provides a reader for the common file format for expression data
 * and an interface to access the data.
 * <P>
 * <p/> <p/> There are variations in the file format used; the following
 * assumptions about the file format are considered valid. Attempting to read a
 * file that does not satisfy these assumptions is not guaranteed to work.
 * <P>
 * <p/> 1. A token is a consecutive sequence of alphanumeric characters
 * separated by whitespace.<BR>
 * 2. The file consists of an arbitrary number of lines, each of which contains
 * the same number of tokens (except for possibly the first line) and has a
 * total length less than 8193 characters.<BR>
 * 3. The first line of the file is a header line with one of the following
 * three formats:
 * <P>
 * <p/> <text> <text> cond1 cond2 ... condN cond1 cond2 ... condN NumSigConds
 * <P>
 * <p/> <text> <text> cond1 cond2 ... condN
 * <P>
 * <p/> <\t><\t>RATIOS<\t><\t>...LAMBDAS
 * <P>
 * <p/> Here cond1 through condN are the names of the conditions. In the first
 * case, the two sequences of condition names must match exactly in order and
 * lexicographically; each name among cond1 ... condN must be unique. In the
 * second case, each name must be unique, but need only appear once. The last
 * label, NumSigConds, is optional.<BR>
 * The third case is the standard header for a MTX file. The numer of '\t'
 * characters between the words "RATIOS" and "LAMBDAS" is equal to the number of
 * ratio columns in the file (which must be equal to the number of lambda
 * columns).
 * <P>
 * <p/> 4. Each successive line represents the measurements for a partcular
 * gene, and has one of the following two formats, depending on the header:
 * <P>
 * <p/> <FNAME> <CNAME> E E ... E S S ... S I
 * <P>
 * <p/> <FNAME> <CNAME> E E ... E
 * <P>
 * <p/> where <FNAME> is the formal name of the gene, <CNAME> is the common
 * name, the E's are tokens, parsable as doubles, representing the expression
 * level change for each condition, the S's are tokens parsable as doubles
 * representing the statistical significance of the expression level change, and
 * I is an optional integer giving the number of conditions in which the
 * expression level change was significant for this gene.
 * <P>
 * <p/> The first format is used in conjuction with the first or third header
 * formats. The second format is used in conjunction with the second header
 * format.
 * <P>
 * <p/> 5. An optional last line can be included with the following form:
 * <P>
 * <p/> NumSigGenes: I I ... I
 * <P>
 * <p/> where there are N I's, each an integer representing the number of
 * significant genes in that condition.
 * <P>
 */
public class ExpressionData implements Serializable {
    private TaskMonitor taskMonitor;
    public static final int MAX_LINE_SIZE = 8192;

    /**
     * Significance value: PVAL.
     */
    public static final int PVAL = 0;

    /**
     * Significance value: LAMBA.
     */
    public static final int LAMBDA = 1;

    /**
     * Significance value: NONE.
     */
    public static final int NONE = 2;

    /**
     * Significance value: UNKNOWN.
     */
    public static final int UNKNOWN = 3;

    private static final String DEFAULT_KEY_ATTRIBUTE = "ID";

    protected int significanceType = 3;

    /*
     * the key attribute name is the attribute by which the expression data is 
     * matched to the node name.  For instance, this might be a commercial
     * probe set ID.
     */
    String keyAttributeName = DEFAULT_KEY_ATTRIBUTE;
    private boolean mappingByAttribute = false;

    String filename;
    int numGenes;
    int numConds;
    int extraTokens;
    boolean haveSigValues;

    Vector geneNames;
    Vector geneDescripts;
    Vector condNames;
    Hashtable geneNameToIndex;
    Hashtable condNameToIndex;
    double minExp;
    double maxExp;
    double minSig;
    double maxSig;
    Vector allMeasurements;

    /**
     * Constructor. Creates an empty Expression Data object with no data.
     */
    public ExpressionData() {
        filename = null;
	keyAttributeName = DEFAULT_KEY_ATTRIBUTE;
        numGenes = 0;
        numConds = 0;
        extraTokens = 0;
        haveSigValues = false;
        this.initDataStructures();
    }

    /**
     * Constructor. Loads the specified filename into memory.
     *
     * @param filename Name of Expression Data File.
     * @throws IOException Error opening/parsing the expression data file.
     */
    public ExpressionData(String filename) throws IOException {
        this.filename = null;
        numGenes = 0;
        numConds = 0;
        extraTokens = 0;
        haveSigValues = false;
        this.initDataStructures();
        this.loadData(filename, DEFAULT_KEY_ATTRIBUTE);
    }


    /**
     * Constructor. Loads the specified filename into memory.
     *
     * @param filename Name of Expression Data File.
     * @throws IOException Error opening/parsing the expression data file.
     */
    public ExpressionData(String filename, String keyAttributeName) throws IOException {
        this.filename = null;
        numGenes = 0;
        numConds = 0;
        extraTokens = 0;
        haveSigValues = false;
        this.initDataStructures();
        this.loadData(filename, keyAttributeName);
    }

    /**
     * Constructor. Loads the specified file into memory, and reports its
     * progress to the specified TaskMonitor Object. This option is useful for
     * displaying a progress bar to the end-user, while expression data is being
     * parsed.
     *
     * @param filename    Name of Expression Data File.
     * @param taskMonitor TaskMonitor for reporting/monitoring progress.
     * @throws IOException Error opening/parsing the expression data file.
     */
    public ExpressionData(String filename, TaskMonitor taskMonitor)
            throws IOException {
        this.taskMonitor = taskMonitor;
        this.filename = null;
        numGenes = 0;
        numConds = 0;
        extraTokens = 0;
        haveSigValues = false;
        this.initDataStructures();
        this.loadData(filename, DEFAULT_KEY_ATTRIBUTE);
    }



    /**
     * Constructor. Loads the specified file into memory, and reports its
     * progress to the specified TaskMonitor Object. This option is useful for
     * displaying a progress bar to the end-user, while expression data is being
     * parsed.
     *
     * @param filename    Name of Expression Data File.
     * @param keyAttributeName  Identifies an attribute to use in mapping
     *                          the data to the nodes.
     * @param taskMonitor TaskMonitor for reporting/monitoring progress.
     * @throws IOException Error opening/parsing the expression data file.
     */
    public ExpressionData(String filename, String keyAttributeName, TaskMonitor taskMonitor)
            throws IOException {
        this.taskMonitor = taskMonitor;
        this.filename = null;
        numGenes = 0;
        numConds = 0;
        extraTokens = 0;
        haveSigValues = false;
        this.initDataStructures();
        this.loadData(filename, keyAttributeName);
    }

    /**
     * Gets the Name of the Expression Data File.
     *
     * @return File String, as it was originally passed to the constructor, or
     *         null, if no filename is available.
     */
    public String getFileName() {
        return filename;
    }

    /**
     * Gets the File representation of the Expression Data Object. This File
     * object can be queried for a full path to the file, etc.
     *
     * @return File Object.
     */
    public File getFullPath() {
        File file = new File(filename);
        return file.getAbsoluteFile();
    }

    /**
     * Initializes all data structures.
     */
    private void initDataStructures() {
        /*
         * on overflow, capacity of vector will be increased by "expand"
         * elements all at once; much more efficient when we don't know how many
         * thousand genes are left in the file
         */
        int expand = 1000;
        if (geneNames != null) {
            geneNames.clear();
        }
        geneNames = new Vector(0, expand);
        if (geneDescripts != null) {
            geneDescripts.clear();
        }
        geneDescripts = new Vector(0, expand);
        if (condNames != null) {
            condNames.clear();
        }
        condNames = new Vector();
        if (geneNameToIndex != null) {
            geneNameToIndex.clear();
        }
        geneNameToIndex = new Hashtable();
        if (condNameToIndex != null) {
            condNameToIndex.clear();
        }
        condNameToIndex = new Hashtable();
        minExp = Double.MAX_VALUE;
        maxExp = Double.MIN_VALUE;
        minSig = Double.MAX_VALUE;
        maxSig = Double.MIN_VALUE;
        if (allMeasurements != null) {
            allMeasurements.clear();
        }
        allMeasurements = new Vector(0, expand);
    }

    /**
     * Loads the Specified File into memory.
     *
     * @param filename Name of Expression Data File.
     * @return always returns true, indicating succesful load.
     * @throws IOException Error loading / parsing the Expression Data File.
     */
    public boolean loadData(String filename, String keyAttributeName) throws IOException {

	Hashtable attributeToId = new Hashtable();

        if (filename == null)
            return false;

	boolean mappingByKeyAttribute = false;

	if (!keyAttributeName.equals(DEFAULT_KEY_ATTRIBUTE)) {
	    mappingByKeyAttribute = true;
	    attributeToId = getAttributeToIdList(keyAttributeName);
	}

        String rawText = FileUtil.getInputString(filename);
        String[] lines = rawText.split(System.getProperty("line.separator"));

        int lineCount = 0;
        String headerLine = lines[lineCount++];
        if (headerLine == null || headerLine.length() == 0)
            return false;

        if (isHeaderLineMTXHeader(headerLine)) {
            // for sure we know that the file contains lambdas
            this.significanceType = this.LAMBDA;
            headerLine = lines[lineCount++];
        }

        boolean expectPvals = doesHeaderLineHaveDuplicates(headerLine);
        if (this.significanceType != this.LAMBDA && !expectPvals) {
            // we know that we don't have a lambda header and we don't
            // have significance values
            this.significanceType = this.NONE;
        }
        StringTokenizer headerTok = new StringTokenizer(headerLine);
        int numTokens = headerTok.countTokens();

        // if we expect p-values, 4 is the minimum number.
        // if we don't, 3 is the minimum number. Ergo:
        // either way, we need 3, and if we expectPvals, we need 4.
        if ((numTokens < 3) || ((numTokens < 4) && expectPvals)) {
            StringBuffer msg = new StringBuffer("Invalid header format in data file.");
            msg.append("\nNumber of tokens parsed: " + numTokens);
            for (int i = 0; i < numTokens; i++) {
                msg.append("\nToken " + i + ": " + headerTok.nextToken());
            }
            throw new IOException(msg.toString());
        }

        double tmpF = numTokens / 2.0;
        int tmpI = (int) Math.rint(tmpF);
        int numberOfConditions;
        int haveExtraTokens = 0;
        if (expectPvals) {
            if (tmpI == tmpF) {// missing numSigConds field
                numberOfConditions = (numTokens - 2) / 2;
                haveExtraTokens = 0;
            } else {
                numberOfConditions = (numTokens - 3) / 2;
                haveExtraTokens = 1;
            } // else
        } else {
            numberOfConditions = numTokens - 2;
        }

        /* eat the first two tokens from the header line */
        headerTok.nextToken();
        headerTok.nextToken();
        /* the next numConds tokens are the condition names */
        Vector cNames = new Vector(numberOfConditions);
        for (int i = 0; i < numberOfConditions; i++)
            cNames.add(headerTok.nextToken());
        /*
         * the next numConds tokens should duplicate the previous list of
         * condition names
         */
        if (expectPvals) {
            for (int i = 0; i < numberOfConditions; i++) {
                String title = headerTok.nextToken();
                if (!(title.equals(cNames.get(i)))) {
                    StringBuffer msg = new StringBuffer();
                    msg.append("Expecting both ratios and p-values.\n");
                    msg.append("Condition name mismatch in header line"
                            + " of data file " + filename + ": "
                            + cNames.get(i) + " vs. " + title);
                    throw new IOException(msg.toString());
                } // if !title
            } // for i
        } // if expectPvals

        /* OK, we have a reasonable header; clobber all old information */
        this.filename = filename;
        this.numConds = numberOfConditions;
        this.extraTokens = haveExtraTokens;
        this.haveSigValues = expectPvals;
        /* wipe old data */
        initDataStructures();
        /* store condition names */
        condNames = cNames;
        for (int i = 0; i < numConds; i++) {
            condNameToIndex.put(condNames.get(i), new Integer(i));
        }

        /* parse rest of file line by line */
        if (taskMonitor != null) {
            taskMonitor.setStatus("Reading in Data...");
        }
        for (int ii = lineCount; ii < lines.length; ii++) {

            if (taskMonitor != null) {
                double percentComplete = ((double) ii / lines.length) * 100.0;
                taskMonitor.setPercentCompleted((int) percentComplete);
            }

            parseOneLine(lines[ii], ii, expectPvals, mappingByKeyAttribute, attributeToId);
        }

        /* save numGenes and build hash of gene names to indices */
        this.numGenes = geneNames.size();
        for (int i = 0; i < geneNames.size(); i++) {
	    if (geneNames.get(i) != null) {
		geneNameToIndex.put(geneNames.get(i), new Integer(i));
	    }
        }

        /* trim capacity of data structures for efficiency */
        geneNames.trimToSize();
        geneDescripts.trimToSize();
        allMeasurements.trimToSize();
        return true;
    }


    private Object getAttributeValue(byte type, String id, String att) {
	if (type == CyAttributes.TYPE_INTEGER)
	    return Cytoscape.getNodeAttributes().getIntegerAttribute(id, att);
	else if (type == CyAttributes.TYPE_FLOATING)
	    return Cytoscape.getNodeAttributes().getDoubleAttribute(id, att);
	else if (type == CyAttributes.TYPE_BOOLEAN)
	    return Cytoscape.getNodeAttributes().getBooleanAttribute(id, att);
	else if (type == CyAttributes.TYPE_STRING)
	    return Cytoscape.getNodeAttributes().getStringAttribute(id, att);
	else if (type == CyAttributes.TYPE_SIMPLE_LIST)
	    return Cytoscape.getNodeAttributes().getListAttribute(id, att);
	else if (type == CyAttributes.TYPE_SIMPLE_MAP)
	    return Cytoscape.getNodeAttributes().getMapAttribute(id, att);
	return null;
    }


    private Hashtable getAttributeToIdList(String keyAttributeName) {

	Hashtable attributeToIdList = new Hashtable();
	List allNodes = Cytoscape.getCyNodesList();
	byte attributeType = Cytoscape.getNodeAttributes().getType(keyAttributeName);
	for (Iterator ii = allNodes.iterator(); ii.hasNext(); ) {
	    CyNode node = (CyNode) ii.next();
	    String nodeName = node.getIdentifier();
	    Object attrValue = getAttributeValue(attributeType, nodeName, 
						 keyAttributeName);
	    if (attrValue != null) {
		String attributeValue = getAttributeValue(attributeType, 
						      nodeName, 
						      keyAttributeName).toString();
		if (attributeValue != null) {
		    if (!attributeToIdList.contains(attributeValue)) {
			ArrayList newGeneList = new ArrayList();
			newGeneList.add(nodeName);
			attributeToIdList.put(attributeValue, newGeneList);
		    } else {
			ArrayList genesThisAttribute
			    = (ArrayList) attributeToIdList.get(attributeValue);
			genesThisAttribute.add(nodeName);
		    }
		}
	    }
	}
	return(attributeToIdList);
    }


    private boolean doesHeaderLineHaveDuplicates(String hline) {
        boolean retval = false;

        StringTokenizer headerTok = new StringTokenizer(hline);
        int numTokens = headerTok.countTokens();
        if (numTokens < 3) {
            retval = false;
        } else {

            headerTok.nextToken();
            headerTok.nextToken();

            HashMap names = new HashMap();
            while ((!retval) && headerTok.hasMoreTokens()) {
                String title = headerTok.nextToken();
                Object titleObject = (Object) title;
                if (names.get(titleObject) == null) {
                    names.put(titleObject, titleObject);
                } else {
                    retval = true;
                }
            }
        }

        return retval;
    }

    private boolean isHeaderLineNull(String hline, BufferedReader input,
                                     String filename) throws IOException {
        if (hline == null) {
            throw new IOException("Could not read header line from data file: "
                    + filename);
        }
        return false;
    }

    // added by iliana on 11.25.2002
    // it is convenient for users to load their MTX files as they are
    // the current code requires them to remove the first line
    private boolean isHeaderLineMTXHeader(String hline) {
        boolean b = false;
        String pattern = "\t+RATIOS\t+LAMBDAS";
        b = hline.matches(pattern);
        return b;
    }

    private String readOneLine(BufferedReader f) {
        String s = null;
        try {
            s = f.readLine();
        } catch (IOException e) {
        }
        return s;
    }

    private void parseOneLine(String oneLine, int lineCount, boolean sig_vals, 
			      boolean mappingByAttribute, Hashtable attributeToId)
            throws IOException {

	// 
	// Step 1: divide the line into input tokens, and parse through
	// the input tokens.
	//
        StringTokenizer strtok = new StringTokenizer(oneLine);
        int numTokens = strtok.countTokens();

        if (numTokens == 0) {
            return;
        }
        /* first token is gene name (or identifying attribute), or NumSigGenes */
        String firstToken = strtok.nextToken();
        if (firstToken.startsWith("NumSigGenes")) {
            return;
        }
        if ((sig_vals && (numTokens < 2 * numConds + 2))
                || ((!sig_vals) && numTokens < numConds + 2)) {
            throw new IOException("Warning: parse error on line " + lineCount
                    + "  tokens read: " + numTokens);
        }
	String geneDescript = strtok.nextToken();
        String[] expData = new String[numConds];
        for (int i = 0; i < numConds; i++) {
            expData[i] = strtok.nextToken();
        }
        String[] sigData = new String[numConds];
        if (sig_vals) {
            for (int i = 0; i < numConds; i++) {
                sigData[i] = strtok.nextToken();
            }
        } else {
            for (int i = 0; i < numConds; i++) {
                sigData[i] = expData[i];
            }
        }


	ArrayList gNames = new ArrayList();
	if (mappingByAttribute) {
	    if (attributeToId.containsKey(firstToken)) {
		gNames = (ArrayList) attributeToId.get(firstToken);
	    }
	} else {
	    gNames = new ArrayList();
	    gNames.add(firstToken);
	}

	for (int ii = 0; ii < gNames.size(); ii++) {
	    geneNames.add(gNames.get(ii));
	    /* store descriptor token */
	    geneDescripts.add(geneDescript);
	    Vector measurements = new Vector(numConds);
	    for (int jj = 0; jj < numConds; jj++) {

		mRNAMeasurement m = new mRNAMeasurement(expData[jj], 
							sigData[jj]);
		measurements.add(m);
		double ratio = m.getRatio();
		double signif = m.getSignificance();
		if (ratio < minExp) {
		    minExp = ratio;
		}
		if (ratio > maxExp) {
		    maxExp = ratio;
		}
		if (signif < minSig) {
		    minSig = signif;
		}
		if (signif > maxSig) {
		    maxSig = signif;
		    if (this.significanceType != this.LAMBDA && sig_vals
                        && maxSig > 1) {
			this.significanceType = this.LAMBDA;
		    }
		}
	    }

	    if (this.significanceType != this.LAMBDA 
		&& sig_vals && minSig > 0) {
		// We are probably not looking at lambdas, since no 
		// significance value was > 1
		// and the header is not a LAMBDA header
		this.significanceType = this.PVAL;
	    }

	    allMeasurements.add(measurements);
	}
    }// parseOneLine

    /**
     * Converts all lambdas to p-values. Lambdas are lost after this call.
     */
    public void convertLambdasToPvals() {
        Iterator it = this.allMeasurements.iterator();
        while (it.hasNext()) {
            Vector v = (Vector) it.next();
            Iterator it2 = v.iterator();
            while (it2.hasNext()) {
                mRNAMeasurement m = (mRNAMeasurement) it2.next();
                double pval = ExpressionData.getPvalueFromLambda(m
                        .getSignificance());
                m.setSignificance(pval);
            }// while it2
        }// while it
    }// convertPValsToLambdas

    /**
     * Gets a PValue of the specified lambda value.
     *
     * @return a very close approximation of the pvalue that corresponds to the
     *         given lambda value
     */
    static public double getPvalueFromLambda(double lambda) {
        double x = StrictMath.sqrt(lambda) / 2.0;
        double t = 1.0 / (1.0 + 0.3275911 * x);
        double erfc = StrictMath.exp(-(x * x))
                * (0.254829592 * t + -0.284496736 * StrictMath.pow(t, 2.0)
                + 1.421413741 * StrictMath.pow(t, 3.0) + -1.453152027
                * StrictMath.pow(t, 4.0) + 1.061405429 * StrictMath
                .pow(t, 5.0));
        erfc = erfc / 2.0;
        if (erfc < 0 || erfc > 1) {
            // P-value must be >= 0 and <= 1
            throw new IllegalStateException("The calculated pvalue for lambda = " + lambda + " is "
                    + erfc);
        }
        return erfc;
    }// getPvalueFromLambda

    /**
     * Gets the Significance Type.
     *
     * @return one of NONE, UNKNOWN, PVAL, LAMBDA
     */
    public int getSignificanceType() {
        return this.significanceType;
    }

    /**
     * Returns a text description of this data object.
     *
     * @return Text Decription of this Data Object.
     */
    public String getDescription() {
        String lineSep = System.getProperty("line.separator");
        StringBuffer sb = new StringBuffer();
        File file = new File(filename);

        sb.append("Data read from: " + file.getName() + lineSep);
        sb.append(lineSep);
        sb.append("Number of genes = " + getNumberOfGenes() + lineSep);
        sb
                .append("Number of conditions = " + getNumberOfConditions()
                + lineSep);
        sb.append("Significance values: ");
        if (this.haveSigValues) {
            sb.append("yes");
        } else {
            sb.append("no");
        }
        sb.append(lineSep).append(lineSep);
        sb.append("MinExp: " + minExp + "    MaxExp: " + maxExp + lineSep);
        if (this.haveSigValues) {
            sb.append("MinSig: " + minSig + "    MaxSig: " + maxSig + lineSep);
            String sigType = null;
            if (this.significanceType == this.UNKNOWN) {
                sigType = "unknown";
            } else if (this.significanceType == this.LAMBDA) {
                sigType = "lambda values";
            } else if (this.significanceType == this.PVAL) {
                sigType = "p-values";
            } else if (this.significanceType == this.NONE) {
                sigType = "none";
            }
            sb.append("Type of significance: " + sigType + lineSep);
        }
        return sb.toString();
    }

    /**
     * Sets a List of Gene Names. This clobbers the old list of gene names, if
     * it exists.
     *
     * @param newNames Vector of String Objects.
     */
    public void setGeneNames(Vector newNames) {
        geneNames = newNames;
        geneNameToIndex.clear();
        for (int i = 0; i < geneNames.size(); i++) {
            geneNameToIndex.put(geneNames.get(i), new Integer(i));
        }
    }

    /**
     * Gets an Array of GeneDescriptors.
     *
     * @return Array of String Objects.
     */
    public String[] getGeneDescriptors() {
        return (String[]) geneDescripts.toArray(new String[0]);
    }

    /**
     * Gets a Vector Gene Descriptors.
     *
     * @return Vector of String Objects.
     */
    public Vector getGeneDescriptorsVector() {
        return geneDescripts;
    }

    /**
     * Sets a List of Gene Descriptors. This clobbers the old list of gene
     * descriptors, if it exists.
     *
     * @param newDescripts Vector of String Objects.
     */
    public void setGeneDescriptors(Vector newDescripts) {
        geneDescripts = newDescripts;
    }

    /**
     * Gets an Array of All Experimental Conditions.
     *
     * @return Array of String Objects.
     */
    public String[] getConditionNames() {
        return (String[]) condNames.toArray(new String[0]);
    }

    /**
     * Gets the index value of the specified experimenal conditon.
     *
     * @param condition Name of experimental condition.
     * @return index value of the specified experimenal conditon.
     */
    public int getConditionIndex(String condition) {
        return ((Integer) this.condNameToIndex.get(condition)).intValue();
    }

    /**
     * Gets the Gene Descriptor for the specified gene.
     *
     * @param gene Gene Name.
     * @return Gene Descriptor String.
     */
    public String getGeneDescriptor(String gene) {
        Integer geneIndex = (Integer) geneNameToIndex.get(gene);
        if (geneIndex == null) {
            return null;
        }

        return (String) geneDescripts.get(geneIndex.intValue());
    }

    /**
     * Indicates whether the expression data has significance values.
     *
     * @return true or false.
     */
    public boolean hasSignificanceValues() {
        return haveSigValues;
    }

    /**
     * Gets all Measurements.
     *
     * @return A Vector of Vectors. The embedded Vector contains mRNAMeasurement
     *         Objects.
     */
    public Vector getAllMeasurements() {
        return allMeasurements;
    }

    /**
     * Gets a List of All Gene Names. Same as getGeneNamesVector(), except this
     * method returns an Array of String Objects.
     *
     * @return Array of Strings.
     */
    public String[] getGeneNames() {
        return (String[]) geneNames.toArray(new String[0]);
    }

    /**
     * Gets a List of All Gene Names. Same as getGeneNames(), except this method
     * returns a Vector of String Objects.
     *
     * @return Vector of String Objects.
     */
    public Vector getGeneNamesVector() {
        return geneNames;
    }

    /**
     * Gets Total Number of Experimental Conditions. This corresponds to the
     * number of condition columns in the original expression data file.
     *
     * @return total number of experimental conditions.
     */
    public int getNumberOfConditions() {
        return numConds;
    }

    /**
     * Gets Total Number of Genes. This corresponds to the number of rows of
     * data in the original expression data file.
     *
     * @return total number of genes.
     */
    public int getNumberOfGenes() {
        return numGenes;
    }

    /**
     * Returns a 2D Matrix of Extreme Values. The matrix is set to the
     * following:
     * <p/>
     * <PRE>
     * <p/>
     * maxVals[0][0] = minExp; maxVals[0][1] = maxExp; maxVals[1][0] = minSig;
     * maxVals[0][1] = maxSig;
     * <p/>
     * </PRE>
     *
     * @return a 2D Matrix of double values.
     */
    public double[][] getExtremeValues() {
        double[][] maxVals = new double[2][2];
        maxVals[0][0] = minExp;
        maxVals[0][1] = maxExp;
        maxVals[1][0] = minSig;
        maxVals[1][1] = maxSig;
        return maxVals;
    }

    /**
     * Gets a Vector of all Measurements associated with the specified gene.
     *
     * @param gene Gene Name.
     * @return Vector of mRNAMeasurement Objects.
     */
    public Vector getMeasurements(String gene) {
	if (gene == null) {
	    return null;
	}
        Integer geneIndex = (Integer) geneNameToIndex.get(gene);
        if (geneIndex == null) {
            return null;
        }

        Vector measurements = (Vector) (this.getAllMeasurements().get(geneIndex
                .intValue()));
        return measurements;
    }

    /**
     * Gets Single Measurement Value for the specified gene at the specified
     * condition.
     *
     * @param gene      Gene Name.
     * @param condition Condition Name (corresponds to column heading in original
     *                  expression data file.)
     * @return an mRNAMeasurement Object.
     */
    public mRNAMeasurement getMeasurement(String gene, String condition) {
        Integer condIndex = (Integer) condNameToIndex.get(condition);
        if (condIndex == null) {
            return null;
        }

        Vector measurements = this.getMeasurements(gene);
        if (measurements == null) {
            return null;
        }

        mRNAMeasurement returnVal = (mRNAMeasurement) measurements
                .get(condIndex.intValue());
        return returnVal;
    }

    /**
     * Copies ExpressionData data structure into CyAttributes data structure.
     *
     * @param nodeAttribs Node Attributes Object.
     * @param taskMonitor Task Monitor. Can be null.
     */
    public void copyToAttribs(CyAttributes nodeAttribs, TaskMonitor taskMonitor) {
        String[] condNames = getConditionNames();
        for (int condNum = 0; condNum < condNames.length; condNum++) {
            String condName = condNames[condNum];
            String eStr = condName + "exp";
            String sStr = condName + "sig";
            for (int i = 0; i < geneNames.size(); i++) {
                String canName = (String) geneNames.get(i);

                /*
                GaryBader - Nov.25.2005 - common name information should only come from annotation files
                Doing it this way is not modular and is hard to maintain
                // Set common name into geneDescripts
                nodeAttribs.setAttribute(canName, Semantics.COMMON_NAME,
                        geneDescripts.get(i).toString());
                */

                mRNAMeasurement mm = getMeasurement(canName, condName);
                if (mm != null) {
                    nodeAttribs.setAttribute(canName, eStr, new Double(mm
                            .getRatio()));
                    nodeAttribs.setAttribute(canName, sStr, new Double(mm
                            .getSignificance()));
                }

                // Report on Progress to the Task Monitor.
                if (taskMonitor != null) {
                    int currentCoordinate = condNum * geneNames.size() + i;
                    int matrixSize = condNames.length * geneNames.size();
                    double percent = ((double) currentCoordinate / matrixSize) * 100.0;
                    taskMonitor.setPercentCompleted((int) percent);
                }
            }
        }
    }
}
