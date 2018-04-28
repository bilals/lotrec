package lotrec.dataStructure.tableau.action;

/* ADDED 00/12/10 */

import lotrec.dataStructure.expression.*;
import lotrec.process.AbstractAction;
import lotrec.process.Action;
import lotrec.process.ProcessException;
import lotrec.dataStructure.tableau.*;

import java.util.Vector;
import java.util.Enumeration;
import java.io.*;
import lotrec.process.EventMachine;

public class OracleAction extends AbstractAction {
    private static final String exchangeOracleFile = "oracleExchange";
    private SchemeVariable nodeScheme;
    private String oracle;

    public OracleAction(SchemeVariable nodeScheme, String oracle) {
        this.nodeScheme = nodeScheme;
        this.oracle = oracle;
    }

    private Expression[] sendToOracle(Expression[] expressions) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(exchangeOracleFile));
            for(int k = 0; k < expressions.length; k++) {
                Expression e = expressions[k];
                String s = e.toString();
                out.write(s, 0, s.length());
                out.newLine();
            }
            out.close();
            try {
                Runtime.getRuntime().exec(oracle).waitFor();
            } catch(InterruptedException exception) {
            }

            return new lotrec.dataStructure.tableau.action.util.BasicParser(new FileReader(exchangeOracleFile)).recognizeAll();
        } catch(IOException exception) {
            throw new RuntimeException("Cannot interact with oracle: " + exception);
        }
    }

    @Override
    public Object apply(EventMachine em, Object modifier) {
        InstanceSet instanceSet = (InstanceSet)modifier;
        TableauNode n = (TableauNode)instanceSet.get(nodeScheme);
        if(n == null) throw new ProcessException(this.getClass().getSimpleName()+" in rule " + em.getWorkerName() + ":\n" +
                "cannot apply action without instance for node");

        // all the expressions are sent to the oracle
        Vector toSend = new Vector();
        for(Enumeration enumr = n.getMarkedExpressionsEnum(); enumr.hasMoreElements();) {
            Expression e = ((MarkedExpression)enumr.nextElement()).expression;
            toSend.add(e);
        }
        Expression[] expr = new Expression[toSend.size()];
        toSend.toArray(expr);

        Expression[] fromOracle = sendToOracle(expr);

        // the oracle returns expressions to add
        for(int k = 0; k < fromOracle.length; k++) {
            Expression e = fromOracle[k];
            if(!n.contains(e)) n.add(new MarkedExpression(e));
        }

        return instanceSet;
    }
}
