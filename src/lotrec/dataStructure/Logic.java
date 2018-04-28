/*
 * Logic.java
 *
 * Created on 7 mars 2007, 15:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package lotrec.dataStructure;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;
import lotrec.dataStructure.expression.Connector;
import lotrec.dataStructure.expression.MarkedExpression;
import lotrec.dataStructure.tableau.Rule;
import lotrec.parser.Verifier;
import lotrec.process.Strategy;

/**
 *
 * @author said
 */
public class Logic implements Serializable {
    //The def source will be either :
    //1- the String Object of a texte
    //2- Or, the name of a file
    //This should be changed with the introduction of Session management :s
    //private String definitionSource;

    private String name;
    private String description;
//    private String author;
//    private String lastUpdate;
    private Vector<Connector> connectors;
    private Vector<Rule> rules;
    private Vector<Strategy> strategies;
    private Vector<TestingFormula> testingFormulae;
    private String mainStrategyName;

    /** Creates a new instance of Logic */
    public Logic() {
        this.setName("Untitled Logic");
        this.setDescription("No special descitpion..");
        this.setConnectors(new Vector());
        this.setRules(new Vector());
        this.setStrategies(new Vector());
        this.setTestingFormulae(new Vector());
        this.setMainStrategyName("Default strategy name is not given or not correct");
    }

    public static Logic getNewEmptyLogic() {
        Logic logic = new Logic();
        logic.setName("New Logic");
        logic.addDefaultStrategy();
        return logic;
    }

    public void addDefaultStrategy() {
        Strategy str = new Strategy();
        str.setWorkerName("DefaultStrategy");
        str.setCode("");
//        str.setUsability("complete");
        str.setComment("A default empty strategy that does nothing.");
//                +"\n"+
//                "In LoTREC, one needs the following 3 inputs to be able to build a tableau:\n" +
//                "1- logic,\n" +
//                "2- complete strategy for it,\n" +
//                "3- formula.\n" +
//                "The logic must have at least a name and a complete strategy, but could have no connectors, no rules.\n" +
//                "The complete strategy could be a minimum of name and with the usability 'complete'.\n" +
//                "The formula could be a minimum of one UpperCase letter representing an arbitrary constant.");
        addStrategy(str);
        setMainStrategyName(str.getWorkerName());
    }

    public boolean isUsedConnector(Connector c) {
        for (Rule rule : rules) {
            if (rule.isUsed(c)) {
                return true;
            }
        }
        for (TestingFormula tf : testingFormulae) {
            if (tf.isUsed(c)) {
                return true;
            }
        }
        return false;
    }

    public boolean isRuleCalledInStrategies(String ruleName) {
        for (Strategy str : strategies) {
            if (str.isCallingRule(ruleName)) {
                return true;
            }
        }
        return false;
    }

    public boolean isStrategyCalledInOthers(Strategy str) {
        for (Strategy otherStr : strategies) {
            if (!otherStr.getWorkerName().equals(str.getWorkerName()) &&
                    otherStr.isCallingStrategy(str)) {
                return true;
            }
        }
        return false;
    }

    public void replaceStrategyCalls(Strategy oldStr, Strategy newStr) {
        for (Strategy otherStr : strategies) {
            if (!otherStr.getWorkerName().equals(oldStr.getWorkerName())) {
                otherStr.replaceStrategyCalls(oldStr, newStr);
                Verifier.replaceStrategyNameInOtherStrategyCode(
                        oldStr.getWorkerName(), newStr.getWorkerName(),
                        otherStr);
            }
        }
    }

    public void replaceRuleCalls(String oldRuleName, Rule newRule) {
        for (Strategy str : strategies) {
            str.replaceRuleCalls(oldRuleName, newRule);
            Verifier.replaceRuleNameInStrategyCode(
                    oldRuleName, newRule.getName(),
                    str);
        }
    }

    public boolean isStrategyName(String identifier) {
        for (Strategy str : strategies) {
            if (str.getWorkerName().equals(identifier)) {
                return true;
            }
        }
        return false;
    }

    public boolean isOtherStrategyName(Strategy str, String newIdentifier) {
        for (Strategy otherStr : strategies) {
            if (!otherStr.getWorkerName().equals(str.getWorkerName()) &&
                    otherStr.getWorkerName().equals(newIdentifier)) {
                return true;
            }
        }
        return false;
    }

    public boolean isOtherRuleName(String oldName, String newIdentifier) {
        for (Rule rule : rules) {
            if (!rule.getName().equals(oldName) &&
                    rule.getName().equals(newIdentifier)) {
                return true;
            }
        }
        return false;
    }

    public boolean isRuleName(String identifier) {
        for (Rule rule : rules) {
            if (rule.getName().equals(identifier)) {
                return true;
            }
        }
        return false;
    }

    public boolean isRoutineName(String identifier) {
        if (identifier.equals("allRules") ||
                identifier.equals("firstRule") ||
                identifier.equals("repeat")) {
            return true;
        }
        return false;
    }

    public void removeConnector(Connector connector) {
        this.getConnectors().remove(connector);
    }

    public void addConnector(Connector connector) {
        this.getConnectors().add(connector);
    }

    public void removeRule(Rule rule) {
        this.getRules().remove(rule);
    }

    public void addRule(Rule rule) {
        this.getRules().add(rule);
    }

    public void addRule(int index, Rule rule) {
        this.getRules().add(index, rule);
    }

    public void removeStrategy(Strategy strategy) {
        this.getStrategies().remove(strategy);
    }

    public void addStrategy(Strategy strategy) {
        this.getStrategies().add(strategy);
    }

    public void removeTestingFormula(TestingFormula testingFormula) {
        this.getTestingFormulae().remove(testingFormula);
    }

    public void addTestingFormula(TestingFormula testingFormula) {
        this.getTestingFormulae().add(testingFormula);
    }

    public String getName() {
        return name;
    }

    public void setName(String displayName) {
        this.name = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Vector<Connector> getConnectors() {
        return connectors;
    }

    public void setConnectors(Vector connectors) {
        this.connectors = connectors;
    }

    public Vector<Rule> getRules() {
        return rules;
    }

    public void setRules(Vector rules) {
        this.rules = rules;
    }

    public Vector<Strategy> getStrategies() {
        return strategies;
    }

//    public Vector<Strategy> getCompleteStrategies() {
//        Vector<Strategy> completeStrategies = new Vector();
//        for (int i = 0; i < strategies.size(); i++) {
//            if (((Strategy) strategies.get(i)).getUsability().equals("complete")) {
//                completeStrategies.add((Strategy) strategies.get(i));
//            }
//        }
//        return completeStrategies;
//    }
    public void setStrategies(Vector strategies) {
        this.strategies = strategies;
    }

    public Vector<TestingFormula> getTestingFormulae() {
        return testingFormulae;
    }

    public void setTestingFormulae(Vector testingFormulae) {
        this.testingFormulae = testingFormulae;
    }

    public Connector getConnector(String connName) {
        for (Enumeration enumr = connectors.elements(); enumr.hasMoreElements();) {
            Connector c = (Connector) enumr.nextElement();
            if (c.getName().equals(connName)) {
                return c;
            }
        }
        return null;
    }

    public Rule getRule(String ruleName) {
        for (Enumeration enumr = this.rules.elements(); enumr.hasMoreElements();) {
            Rule r = (Rule) enumr.nextElement();
            if (r.getName().equals(ruleName)) {
                return r;
            }
        }
        return null;
    }

    public Strategy getStrategy(String stratName) {
        for (int i = 0; i < this.getStrategies().size(); i++) {
            Strategy s = (Strategy) getStrategies().get(i);
            if (s.getWorkerName().equals(stratName)) {
                return s;
            }
        }
        return null;
    }

    public TestingFormula getTestingFormula(String code) {
        for (int i = 0; i < this.getTestingFormulae().size(); i++) {
            TestingFormula tf = (TestingFormula) getTestingFormulae().get(i);
            if (tf.getCode().equals(code)) {
                return tf;
            }
        }
        return null;
    }

    public MarkedExpression getTFFormula(String code) {
        for (int i = 0; i < this.getTestingFormulae().size(); i++) {
            TestingFormula tf = (TestingFormula) getTestingFormulae().get(i);
            if (tf.getCode().equals(code)) {
                return tf.getFormula();
            }
        }
        return null;
    }

    @Override
    public String toString() {
        String logicDef = new String();
        logicDef = "Logic name is: " + this.name;
        logicDef = logicDef + "\nLogic description is: " + this.getDescription();
//        logicDef = logicDef + "\nLogic author(s): " + this.getAuthor();
//        logicDef = logicDef + "\nLogic last update: " + this.getLastUpdate();
        if (this.getConnectors() != null) {
            logicDef = logicDef + "\n\nConnectors set:";
            for (int i = 0; i < this.getConnectors().size(); i++) {
                logicDef = logicDef + "\n  Connector num[" + (i + 1) + "]: " + getConnectors().get(i);
            }
        } else {
            logicDef = logicDef + "\n\nThere's no connector defined!";
        }
        if (this.getRules() != null) {
            logicDef = logicDef + "\n\nRules set:";
            for (int i = 0; i < this.getRules().size(); i++) {
                logicDef = logicDef + "\n  Rule num[" + (i + 1) + "]: " + getRules().get(i);
            }
        } else {
            logicDef = logicDef + "\n\nThere's no rule defined!";
        }
        if (this.getStrategies() != null) {
            logicDef = logicDef + "\n\nStrategies set:";
            for (int i = 0; i < this.getStrategies().size(); i++) {
                logicDef = logicDef + "\n  Strategy num[" + (i + 1) + "] name: " + getStrategies().get(i);
//                logicDef = logicDef + "\n  Strategy num["+ (i+1) + "] code: " + ((Strategy)getStrategies().get(i)).getCode();
            }
            logicDef = logicDef + "\n\nMain strategy name : " + this.getMainStrategyName();
        } else {
            logicDef = logicDef + "\n\nThere's no strategy defined!";
        }
        if (this.getTestingFormulae() != null) {
            logicDef = logicDef + "\n\nTesting formulae set:";
            for (int i = 0; i < this.getTestingFormulae().size(); i++) {
                logicDef = logicDef + "\n  Testing formula num[" + (i + 1) + "]: " + getTestingFormulae().get(i);
            }
        } else {
            logicDef = logicDef + "\n\nThere's no testing formula defined!";
        }
        return logicDef;
    }

    public String getMainStrategyName() {
        return mainStrategyName;
    }

    public void setMainStrategyName(String mainStrategyName) {
        this.mainStrategyName = mainStrategyName;
    }

//    public String getAuthor() {
//        return author;
//    }
//
//    public void setAuthor(String author) {
//        this.author = author;
//    }
//
//    public String getLastUpdate() {
//        return lastUpdate;
//    }
//
//    public void setLastUpdate(String lastUpdate) {
//        this.lastUpdate = lastUpdate;
//    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Logic) {
            Logic l = (Logic) o;
            return this.getName().equals(l.getName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
}
