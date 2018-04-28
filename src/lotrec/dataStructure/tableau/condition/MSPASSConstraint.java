package lotrec.dataStructure.tableau.condition;

import java.util.Enumeration;
import lotrec.dataStructure.expression.*;
import lotrec.process.*;
import lotrec.dataStructure.tableau.*;
import java.io.*;

/**
Represents a restriction that will test if the modifier defines a recognizable tableau pattern.
<p>The modifier will not be completed. This class stands as a constraint, usually in the end of a restriction chain.
<p>The modifier must contain the instance of the two nodes schemes. The pass succeeds if the first node (represented by the first scheme) contains the second node (represented by the second scheme).
<p>In case of success, the modifier will be passed thru the chain. It is a recursive process.
@author David Fauthoux
 */
public class MSPASSConstraint extends Restriction {

    private SchemeVariable nodeScheme;
    private Expression expression;
    private int Time;

    /**
    Creates a contains constraint, ready to be included in a restriction chain.
    The contains constraint can be represented by "N0 contains N1" or by "N1 C N0" ('C' representing the mathematical inclusion)
    @param bigNodeScheme the scheme representing the node N0
    @param smallNodeScheme the scheme representing the node N1
     */
    public MSPASSConstraint(SchemeVariable nodeScheme, Expression expression, int Time) {
        this.nodeScheme = nodeScheme;
        this.expression = expression;
        this.Time = Time;
    }

    public void attemptToApply(Action action, Object modifier, ActionStocking actionStocking,EventMachine eventMachine) throws ProcessException {
        InstanceSet instanceSet = (InstanceSet) modifier;
        TableauNode Node = (TableauNode) instanceSet.get(nodeScheme);
        if (Node == null) {
            throw new ProcessException(toString() + " : cannot attempt to apply without  instance for Node ");
        }

        for (Enumeration enumr = Node.getMarkedExpressionsEnum(); enumr.hasMoreElements();) {
            Expression e = ((MarkedExpression) enumr.nextElement()).expression;
            InstanceSet newInstanceSet = expression.matchWith(e, instanceSet);
            if (newInstanceSet != null) {
                if (MSPASSProve(e)) {
                    continueAttemptToApply(action, newInstanceSet, actionStocking,eventMachine);
                    if (eventMachine.isApplyOnOneOccurence() && !actionStocking.isEmpty()) {
                        return;
                    }
                }
            }
        }
        return;

    }

    boolean MSPASSProve(Expression expression) {
        String formula = getFormula(expression);

        try {
            FileWriter fr = new FileWriter("run.bat");
            fr.write("pl -f ml2dfg.pl -g 'translate([]," + formula + ")'   -t halt");
            fr.close();

            String command1 = "chmod a+x run.bat";
            String command2 = "sh run.bat";
            String command3 = "/opt/spass/SPASS problem.dfg", result, mspassResult = "";


            Process proc = Runtime.getRuntime().exec(command1);
            proc.waitFor();

            proc = Runtime.getRuntime().exec(command2);
            proc.waitFor();

            proc = Runtime.getRuntime().exec(command3);
            proc.waitFor();

            BufferedInputStream bis = new BufferedInputStream(proc.getInputStream());
            int i = bis.read();
            while (i != -1) {
                mspassResult = mspassResult.concat(String.valueOf((char) i));
                i = bis.read();
            }
            bis.close();

            int index = mspassResult.indexOf("SPASS beiseite");

            System.out.println("Index: " + index);

            result = new String(mspassResult.substring(index + 16, index + 27));

            System.out.println(result);

            if (!result.equals("Proof found")) {
                return false;
            }
        } catch (Exception e) {
            System.out.println("There is a problem: " + e.toString());
        }


        return true;
    }

    private String getFormula(Expression expression) {
        String formula = expression.toMSPASS();

        System.out.println(formula);
        return formula;
    }
}
