package lotrec.dataStructure.tableau.action;

import java.awt.event.ActionEvent;
import lotrec.dataStructure.expression.*;
import lotrec.dataStructure.tableau.*;
import java.io.*;
import javax.swing.*;
import lotrec.process.EventMachine;

/**
When applied, removes from memory a tableau containing a specified node.
@author David Fauthoux
 */
public class ProcessAction extends AbstractAction {
    //private SchemeVariable sourceNodeScheme;
    //private Vector schemes;

    private String command;
    private SchemeVariable sourceNodeScheme;

    /**
    Creates an action which will remove from memory the tableau containing the specified node.
    User only specifies the scheme representing this node and this class will find it in the instance set (modifier), when <i>apply</i> method is called.
    @param sourceNodeScheme the scheme representing the node to get the tableau to remove
    @param strategy the global strategy from where the tableau strategy will be remove
     */
    public ProcessAction(SchemeVariable sourceNodeScheme, String command) {
        this.command = command;
        this.sourceNodeScheme = sourceNodeScheme;
    }

    /**
    Finds the concrete node in the modifier, represented by sourceNodeScheme in the constructor, to get its tableau; and removes the tableau.
    @param modifier the instance set used in the restriction process
    @return the instance set completed with the destination schemes
     */
    public Object apply(EventMachine em, Object modifier) {
        try {
            Process proc =
                    Runtime.getRuntime().exec(command);
            //OutputStream os=proc.getOutputStream();
            //InputStream is=proc.getInputStream();
            InstanceSet instanceSet = (InstanceSet) modifier;
            TableauNode n = (TableauNode) instanceSet.get(sourceNodeScheme);
            InputStreamReader br = new InputStreamReader(proc.getInputStream());
            int c = br.read();
            while (c != -1) {
                System.out.print((char) c);
                c = br.read();
            }
            System.out.println("  is applied in the node " + n.toString());
        } catch (IOException e) {
            System.out.println("problem ");
            JOptionPane.showMessageDialog(new JPanel(),
                    e.toString(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        return modifier;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }
}
