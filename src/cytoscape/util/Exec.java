
/*
  File: Exec.java 
  
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

// Exec.java


// exec a child process, and get its stdout & stderr
//---------------------------------------------------------------------------
// rcs:  $Revision: 7760 $ $Date: 2006-06-26 09:28:49 -0700 (Mon, 26 Jun 2006) $
//---------------------------------------------------------------------------
package cytoscape.util;
//---------------------------------------------------------------------------
import java.lang.Runtime;
import java.lang.Process;
import java.io.*;

import java.lang.Runtime;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.Vector;
import java.util.Enumeration;
//---------------------------------------------------------------------------
public class Exec {
  String [] cmd;
  Vector stdoutResults;
  Vector stderrResults;
  String stringToSendToStandardInput;
  boolean runInBackground = false;
  String stdout, stderr;
//---------------------------------------------------------------------------
public Exec  ()
{
  this (null);
}
//---------------------------------------------------------------------------
public Exec  (String [] cmd)
{
  this.cmd = cmd;
  stdoutResults = new Vector (100);   // just guessing...
  stderrResults = new Vector (10);
}
//---------------------------------------------------------------------------
public void setStandardInput (String input)
{
  stringToSendToStandardInput = input;
}
//---------------------------------------------------------------------------
public void setRunInBackground (boolean newValue)
{
  runInBackground = newValue;
  int length = cmd.length;
  String [] revisedCmd = new String [length + 1];
  
  for (int i=0; i < length; i++)
    revisedCmd [i] = cmd [i];
  
  revisedCmd [length] = " &";

  cmd = revisedCmd;
  
} // setRunInBackground
//---------------------------------------------------------------------------
public String getCmd ()
{
  StringBuffer sb = new StringBuffer ();
  for (int i=0; i < cmd.length; i++) {
    sb.append (cmd [i]);
    sb.append (" ");
    }

  return sb.toString ();

} // getCmd
//---------------------------------------------------------------------------
public int run ()
{
  int execExitValue = -1;  // be pessimistic

  StringBuffer cmdSB = new StringBuffer ();
  for (int i=0; i < cmd.length; i++) {
    cmdSB.append (cmd [i]);
    cmdSB.append (" ");
    }

  try {
    Runtime runtime =  Runtime.getRuntime ();
    // System.out.println (" --> just before exec: \n\t" + getCmd ());
    //Process process = runtime.exec (cmd);
    Process process = runtime.exec (cmdSB.toString ());
    BufferedReader stdoutReader = 
      new BufferedReader (new InputStreamReader (process.getInputStream()));
    BufferedReader stderrReader = 
      new BufferedReader (new InputStreamReader (process.getErrorStream()));

    if (stringToSendToStandardInput != null) {
        // A PrintStream adds functionality to another output stream, namely the
        // ability to print representations of various data values
        // conveniently. Two other features are provided as well. Unlike other
        // output streams, a PrintStream never throws an IOException; instead,
        // exceptional situations merely set an internal flag that can be tested
        // via the checkError method. Optionally, a PrintStream can be created so
        // as to flush automatically; this means that the flush method is
        // automatically invoked after a byte array is written, one of
        // the println methods is invoked, or a newline character or
        // byte ('\n') is written.
      PrintStream stdinWriter = new PrintStream (process.getOutputStream(),true);
      stdinWriter.print (stringToSendToStandardInput);
      stdinWriter.close ();
      }

    try {
      execExitValue = process.waitFor ();
      }
    catch (InterruptedException e) {
      e.printStackTrace ();
      }

    String stdoutResult;
    while ((stdoutResult = stdoutReader.readLine()) != null) {
      stdoutResults.addElement (stdoutResult);
      }

    String stderrResult;
    while ((stderrResult = stderrReader.readLine()) != null) {
      stderrResults.addElement (stderrResult);
      }
    }// try
  catch (IOException e) {
    e.printStackTrace ();
    }

  return execExitValue;

} // run
//---------------------------------------------------------------------------
public int runThreaded () throws Exception
{
  int execExitValue = -1;  // be pessimistic

  StringBuffer cmdSB = new StringBuffer ();
  for (int i=0; i < cmd.length; i++) {
    cmdSB.append (cmd [i]);
    cmdSB.append (" ");
    }

  Runtime runtime =  Runtime.getRuntime ();
  Process process = runtime.exec (cmdSB.toString ());

  final BufferedReader stdoutReader = new BufferedReader (new InputStreamReader (process.getInputStream()));
  final BufferedReader stderrReader = new BufferedReader (new InputStreamReader (process.getErrorStream()));
  final StringBuffer stdoutSB = new StringBuffer ();
  final StringBuffer stderrSB = new StringBuffer ();

  Thread stdoutReadingThread = new Thread () {
    public void run () {
      String s;
      try { 
         while ((s = stdoutReader.readLine()) != null) {
           stdoutSB.append (s + "\n");
           } // while
         } // trey
       catch (Exception exc0) {
         System.out.println ("--- error: " + exc0.getMessage ());
         exc0.printStackTrace ();
         } // catch
      }; // run
     }; // thread

  Thread stderrReadingThread = new Thread () {
    public void run () {
      String s;
      try { 
         while ((s = stderrReader.readLine()) != null) {
           stderrSB.append (s + "\n");
           } // while
         } // try
       catch (Exception exc1) {
         System.out.println ("--- error: " + exc1.getMessage ());
         exc1.printStackTrace ();
         } // catch
      }; // run
     }; // thread

  stdoutReadingThread.start ();
  stderrReadingThread.start ();
  execExitValue = process.waitFor ();

  stdout = stdoutSB.toString ();
  stderr = stderrSB.toString ();

  return execExitValue;

} // runThreaded
//---------------------------------------------------------------------------
public Vector getStdout ()
{
  return stdoutResults;
}
//---------------------------------------------------------------------------
public Vector getStderr ()
{
  return stderrResults;
}
//---------------------------------------------------------------------------
public String getStdoutAsString () 
{
  return stdout;
}
//---------------------------------------------------------------------------
public String getStderrAsString () 
{
  return stderr;
}
//---------------------------------------------------------------------------
} // Exec.java


