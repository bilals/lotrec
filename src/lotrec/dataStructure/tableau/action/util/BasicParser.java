package lotrec.dataStructure.tableau.action.util;

import java.io.*;
import java.util.Enumeration;
import java.util.Vector;
import lotrec.dataStructure.expression.*;

public class BasicParser {

    protected StreamTokenizer tokenizer;
    protected Vector connectors;
    protected Vector expressions;

    public BasicParser(Reader reader) {
        connectors = new Vector();
        expressions = new Vector();
        tokenizer = new StreamTokenizer(reader);
        tokenizer.wordChars('_','_');
        tokenizer.quoteChar('\"');
        tokenizer.slashSlashComments(true);
        tokenizer.slashStarComments(true);
    }

    public Expression[] recognizeAll() {
        while(recognize());
        Expression[] e = new Expression[expressions.size()];
        expressions.toArray(e);
        return e;
    }

    protected Connector findConnector(String name) {
        for(Enumeration enumr = connectors.elements(); enumr.hasMoreElements();) {
            Connector c = (Connector)enumr.nextElement();
            if(c.getName().equals(name)) return c;
        }
        throw new IllegalArgumentException("Cannot find connector : "+name);
    }

    protected String readStringToken() {
        try {
            int ttype = tokenizer.nextToken();
            if(ttype == StreamTokenizer.TT_EOF) return null;//throw new IllegalArgumentException("End of file. Need another token...");
            if(ttype == StreamTokenizer.TT_NUMBER) throw new IllegalArgumentException("Why "+(int)tokenizer.nval+" ?");
            if((ttype == StreamTokenizer.TT_WORD) || (ttype == '\"')) {
                System.out.println("Read : " + tokenizer.sval);
                return tokenizer.sval;
            }
            return readStringToken();
        } catch(IOException e) {
            throw new IllegalArgumentException("End of file. Need another token...");
        }
    }

    protected int readIntToken() {
        try {
            int ttype = tokenizer.nextToken();
            if(ttype == StreamTokenizer.TT_EOF) throw new IllegalArgumentException("End of file. Need a number...");
            if(ttype == StreamTokenizer.TT_WORD) throw new IllegalArgumentException("Why "+tokenizer.sval+" ?");
            if(ttype == StreamTokenizer.TT_NUMBER) {
                System.out.println("Read : " + (int)tokenizer.nval);
                return (int)tokenizer.nval;
            }
            return readIntToken();
        } catch(IOException e) {
            throw new IllegalArgumentException("End of file. Need another token...");
        }
    }

    protected boolean recognize() {
        String s = readStringToken();
        if(s == null) return false;

        if(s.equals("connector")) {
            recognizeConnector();
        }
        else if(s.equals("expression")) {
            expressions.add(recognizeExpression());
        }
        return true;
    }

    protected Connector recognizeConnector() {
        Connector connector = new Connector();
        connector.setName(readStringToken());
        connector.setArity(readIntToken());
        connector.setAssociative(new Boolean(readStringToken()).booleanValue());
        connector.setOutString(readStringToken());
        connector.setPriority(readIntToken());
        System.out.println("Connector found : "+connector);
        connectors.add(connector);
        return connector;
    }

    protected Expression recognizeExpression() {
        String s = readStringToken();
        
        if(s.equals("constant")) {
            return recognizeConstant();
        }
        else if(s.equals("variable")) {
            return recognizeVariable();
        }
        Connector connector = findConnector(s);
        ExpressionWithSubExpressions expression = new ExpressionWithSubExpressions(connector);
        for(int i = 0; i < connector.getArity(); i++) expression.setExpression(recognizeExpression(), i);
        return expression;
    }

    protected ConstantExpression recognizeConstant() {
        return new ConstantExpression(readStringToken());
    }

    protected VariableExpression recognizeVariable() {
        return new VariableExpression(readStringToken());
    }
}