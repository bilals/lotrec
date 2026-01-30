/*
 * Engine.java
 *
 * Created on 27 mars 2007, 17:45
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package lotrec.engine;

import java.util.ArrayList;
import java.util.Vector;

import lotrec.dataStructure.Logic;
import lotrec.dataStructure.expression.MarkedExpression;
import lotrec.dataStructure.graph.Wallet;
import lotrec.dataStructure.tableau.Tableau;
import lotrec.dataStructure.tableau.TableauNode;
import lotrec.dataStructure.tableau.action.DuplicateAction;
import lotrec.gui.MainFrame;
import lotrec.process.EventMachine;
import lotrec.process.Routine;
import lotrec.process.Strategy;
import lotrec.process.AbstractWorker;

/**
 * This Class should be instanciated, and doesn't work correctly with static methods!!!
 * @author said
 */
public class Engine extends Thread {

    public static int NOP_WHEN_HAVING_OPEN_TABLEAU = 0;
    public static int STOP_WHEN_HAVING_OPEN_TABLEAU = 1;
    public static int PAUSE_WHEN_HAVING_OPEN_TABLEAU = 2;
    private int openTableauAction = NOP_WHEN_HAVING_OPEN_TABLEAU;
    protected Vector<Strategy> strategiesList;
    private Logic logic;
    private Strategy strategy;
    private MarkedExpression formula;
    private ArrayList<String> rulesNames;
    private boolean shouldStop = false;
    private boolean shouldPause = false;
    private String ruleNameToBePaused;
    private ArrayList rulesBreakPoints;// = new ArrayList();// set of EventMachines'levels to be paused
    private Wallet currentWallet;
    private EngineStatus status;
    private EngineListener listener;
    @Deprecated
    private MainFrame mainFrame; // Kept for backward compatibility via getMainFrame()
    private boolean runningBySteps = false;
    private EngineTimer engineTimer;
    private int appliedRules;
    private int totalAppliedRules;
    private Benchmarker benchmarker;

    /** Creates a new instance of Engine */
    public Engine() {
    }

    /**
     * Creates an Engine with the given EngineListener for headless/testable execution.
     *
     * @param chosenLogic the logic definition to use
     * @param chosenStrategy the strategy to apply
     * @param chosenFormula the formula to analyze
     * @param listener the listener for engine events (use HeadlessEngineListener for testing)
     */
    public Engine(Logic chosenLogic, Strategy chosenStrategy, MarkedExpression chosenFormula, EngineListener listener) {
        this.listener = listener;
        rulesNames = new ArrayList();
        this.logic = chosenLogic;
        strategy = chosenStrategy;
        strategiesList = new Vector<Strategy>();
        strategy.setRelatedTableau(new Tableau("empty"));
        this.add(strategy);
        this.formula = chosenFormula;
        engineTimer = new EngineTimer();
        appliedRules = 0;
        totalAppliedRules = 0;
        benchmarker = new Benchmarker();
    }

    /**
     * Creates an Engine with GUI support via MainFrame.
     *
     * @param chosenLogic the logic definition to use
     * @param chosenStrategy the strategy to apply
     * @param chosenFormula the formula to analyze
     * @param mainFrame the main application frame (for GUI updates)
     * @deprecated Use {@link #Engine(Logic, Strategy, MarkedExpression, EngineListener)} instead
     */
    @Deprecated
    public Engine(Logic chosenLogic, Strategy chosenStrategy, MarkedExpression chosenFormula, MainFrame mainFrame) {
        this(chosenLogic, chosenStrategy, chosenFormula, new SwingEngineListener(mainFrame));
        this.mainFrame = mainFrame; // Keep for backward compatibility via getMainFrame()
    }

    public void add(Strategy strategy) {
        strategiesList.add(strategy);
    }

    public void remove(Strategy strategy) {
        strategiesList.remove(strategy);
    }

    public String getStrategiesListInfo() {
        String result = "Strategies list contains complete strategies for tableaux: [";
        boolean empty = true;
        for (Strategy str : strategiesList) {
            empty = false;
            result = result + str.getRelatedTableau().getName() + " ; ";
        }
        StringBuffer sb = new StringBuffer(result);
        sb.delete(sb.lastIndexOf(" ; "), sb.length());
        result = sb.toString() + "]";
        if (empty) {
            result = "Strategies list contains no strategy (i.e. no worker)!!";
        }
        return result;
    }

    public int getOpenTableauAction() {
        return openTableauAction;
    }

    public void setOpenTableauAction(int openTableauAction) {
        this.openTableauAction = openTableauAction;
    }

    public Logic getLogic() {
        return logic;
    }

    public void setLogic(Logic logic) {
        this.logic = logic;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public MarkedExpression getFormula() {
        return formula;
    }

    public void setFormula(MarkedExpression formula) {
        this.formula = formula;
    }

    public void setTotalAppliedRules(int totalAppliedRules) {
        this.totalAppliedRules = totalAppliedRules;
    }

    private void addToDispatcher(Tableau tableau, AbstractWorker aw) {
        aw.setEngine(this);
        aw.setRelatedTableau(tableau);
        if (aw instanceof Routine) {
            for (AbstractWorker w : ((Routine) aw).getWorkers()) {
                addToDispatcher(tableau, w);
            }
        } else if (aw instanceof EventMachine) {
            tableau.addProcessListener((EventMachine) aw);
            rulesNames.add(aw.getWorkerName());
        }
    }
    //Old one used by Lotrec class
//    public Wallet buildTableaux() {
//        Wallet resultsWallet = new Wallet();
//        currentWallet = new Wallet();
//        Tableau tableau = new Tableau("tab");
////        tableau.setEngine(this);
//        addToDispatcher(tableau, this.strategy);
//        tableau.setStrategy(this.strategy);
////        resultsWallet.add(tableau);
//        currentWallet.add(tableau);
//        TableauNode.initialiseForName();
//        TableauNode rootNode = new TableauNode("root");
//        tableau.add(rootNode);
//        rootNode.add(this.getFormula());
//        //THE FOLLOWING SHOULD BE DELETED        
//        DuplicateAction.setStepCount(-1);
//        return resultsWallet;
//    }

    public void buildTableaux() {
        currentWallet = new Wallet();
        Tableau tableau = new Tableau("premodel");
//        tableau.setEngine(this);
        addToDispatcher(tableau, this.strategy);
        tableau.setStrategy(this.strategy);
        currentWallet.add(tableau);
        TableauNode.initialiseForName();
        if (this.formula != null) {
            TableauNode firstNode = new TableauNode();
            tableau.add(firstNode);
            firstNode.add(this.formula);
        }
        //--------
//        TableauNode historyNode = new TableauNode("history");                  
//        TableauNode succNode = new TableauNode("succ");   
//        tableau.add(historyNode);  
//        tableau.add(succNode);  
//        TableauEdge eRootSucc = new TableauEdge(firstNode, succNode, new ConstantExpression("SUCC"));
//        TableauEdge eRootHistory = new TableauEdge(firstNode, historyNode, new ConstantExpression("HIS"));
//        firstNode.link(eRootHistory);
//        firstNode.link(eRootSucc);        
//        firstNode.add(new MarkedExpression(new ConstantExpression("Root")));   
//        succNode.add(this.getFormula());
        //--------
        //THE FOLLOWING SHOULD BE DELETED        
        DuplicateAction.setStepCount(-1);
    }

    @Override
    public void run() {
        startBuild();
        getEngineTimer().start();
        try {
            this.applyStrategies();
        } catch (Exception ex) {
            if (listener != null) {
                listener.onRuntimeError(ex.getMessage());
            }
        }
        getEngineTimer().stop();
        updateElapsedTime();
        endBuild();
    }

    public void applyStrategies() {
        if (this.isRunningBySteps()) {
            // In GUI mode, pause before first step; in headless mode, skip step-by-step
            if (mainFrame != null) {
                mainFrame.getTableauxPanel().makePause();// Engine.pauseWork(); is called there
                System.out.println("Engine is paused before first step to enable editing the initial premodel...");
                synchronized (this) {
                    if (shouldPause()) {
                        makePause();
                        while (shouldPause()) {
                            try {
                                wait();
                            } catch (InterruptedException ex) {
                                System.err.println("Exception during Engine.wait(): " + ex);
                            }
                        }
                        makeResume();
                    }
                }
            }
        }

        //Exceptionnally done with this "Enumeration" style of "for" loop,
        //because eventually, the strategies vector may change at runtime,
        //after applying a Tableau DuplicateAction.
        while (!strategiesList.isEmpty()) {
            Strategy str;
            // Should be used for width-first search
            str = strategiesList.firstElement();
            // Should be used for depth-first search
//            str = strategiesList.lastElement();

//        for (Enumeration enumr = strategiesList.elements(); enumr.hasMoreElements();) {
//            Strategy str = (Strategy) enumr.nextElement();
            // test for STOP
            synchronized (this) {
                if (this.shouldStop()) {
                    return;
                }
            }
            if (!str.isQuiet()) {
                System.out.println(" ");
                System.out.println("The global strategy starts working on tableau " +
                        str.getRelatedTableau().getName() + " ..");
                str.work();
                System.out.println("The global strategy stops working on tableau " +
                        str.getRelatedTableau().getName() + " ..");
                System.out.println(" ");
            }
            if (this.openTableauAction == Engine.STOP_WHEN_HAVING_OPEN_TABLEAU && !str.getRelatedTableau().isClosed()) {
                stopWork();
                System.out.println("Engine is stopped, cause an open tableau is found..");
                break;
            }
            if (this.openTableauAction == Engine.PAUSE_WHEN_HAVING_OPEN_TABLEAU && !str.getRelatedTableau().isClosed()) {
                // In GUI mode, pause when open tableau found; in headless mode, skip pause
                if (mainFrame != null) {
                    mainFrame.getTableauxPanel().makePause();// Engine.pauseWork(); is called there
                    System.out.println("Engine is paused, cause an open tableau is found..");
                    synchronized (this) {
                        if (shouldPause()) {
                            makePause();
                            while (shouldPause()) {
                                try {
                                    wait();
                                } catch (InterruptedException ex) {
                                    System.err.println("Exception during Engine.wait(): " + ex);
                                }
                            }
                            makeResume();
                        }
                    }
                }
            }
            // Should be use when we want to reduce
            // the amount of memory allocated for the current premodels in the wallet
//            currentWallet.remove(str.getRelatedTableau());
            strategiesList.remove(str);
        }
    }

    public void reInitializeStopPause() {
        shouldStop = false;
        shouldPause = false;
    }

    public void startBuild() {
        reInitializeStopPause();
        if (listener != null) {
            listener.onBuildStart();
        }
        if (isRunningBySteps()) {
            setStatus(EngineStatus.STEPRUNNING);
        } else {
            setStatus(EngineStatus.NORMAL);
        }
    }

    public void endBuild() {
        boolean wasStopped = shouldStop;
        if (wasStopped) {
            setStatus(EngineStatus.STOPPED);
        } else {
            setStatus(EngineStatus.FINISHED);
        }
        reInitializeStopPause();
        if (listener != null) {
            listener.onBuildEnd(wasStopped);
        }
    }

    public void endNextStep() {
//        btnNextStep.setEnabled(true);
    }

    public synchronized void stopWork() {
        // Should be sure that there's no pause!!!
        shouldStop = true;
        setStatus(EngineStatus.SHOULDSTOP);
    }

    public synchronized void pauseWork() {
//        this.ruleNameToBePaused = "ANY";
        shouldPause = true;
        setStatus(EngineStatus.SHOULDPAUSE);
    }

//    public synchronized void pauseWorkAt(String ruleNameToBePaused) {
//        shouldPause = true;
//        this.ruleNameToBePaused = ruleNameToBePaused;
//    }

//    public synchronized void pauseWorkStepByStep() {
//        shouldPause = true;
//        this.ruleNameToBePaused = "STEP";
//    }
    public synchronized void resumeWork() {
        if (shouldPause) {
            shouldPause = false;
            setStatus(EngineStatus.SHOULDRESUME);
            notify();
        }
    }

    public synchronized void resumeWorkToNextStep() {
        setStatus(EngineStatus.STEPRESUMING);
        notify();
//        if (shouldPause) {
//            notify();
//        }
    }

    public void makePause() {
        setStatus(EngineStatus.PAUSED);
        getEngineTimer().pause();
        updateElapsedTime();
        if (listener != null) {
            listener.onPause();
        }
    }

    public void makeResume() {
        if (listener != null) {
            listener.onResume();
        }
        setStatus(EngineStatus.RESUMED);
        getEngineTimer().resume();
    }

    public void makeStepPause(EventMachine ruleEM) {
        setStatus(EngineStatus.STEPFINISHED);
        getEngineTimer().pause();
        updateElapsedTime();
        updatePausedAtRule(ruleEM.getWorkerName());
        if (listener != null) {
            listener.onStepPause(ruleEM);
        }
        System.out.println("  Rule " + ruleEM + " is step paused..");
    }

    public void makeStepResume(EventMachine ruleEM) {
        updatePausedAtRule("-");
        System.out.println("  Rule " + ruleEM + " is step resumed..");
        if (listener != null) {
            listener.onStepResume(ruleEM);
        }
        setStatus(EngineStatus.STEPRUNNING);
        getEngineTimer().resume();
    }

    public void stopTableau(Tableau tableau) {
        //Disactivated when bencmarking
        //------------------------------
        System.out.println("----------------- Stopping tableau: " + tableau.getName() + "-------------");
        //------------------------------
        tableau.setShouldStopStrategy(true);
//        System.out.println("Strategies Container before stop: " + getStrategiesContainer());
//        getStrategiesContainer().remove(tableau.getStrategy());
//        System.out.println("Strategies Container after stop: " + getStrategiesContainer());
    }

    public String getStatus() {
        return status.toString();
    }

    public boolean shouldStop() {
        return shouldStop;
    }

    public boolean shouldPause() {
        return shouldPause;
    }

    public void setStatus(EngineStatus status) {
        this.status = status;
        updateEngineStatus();
    }

    public ArrayList<String> getRulesNames() {
        return rulesNames;
    }

    public void setRulesNames(ArrayList<String> workersNames) {
        this.rulesNames = workersNames;
    }

    public String getRuleNameToBePaused() {
        return ruleNameToBePaused;
    }

    public void setRuleNameToBePaused(String ruleNameToBePaused) {
        this.ruleNameToBePaused = ruleNameToBePaused;
    }

    public ArrayList getRulesBreakPoints() {
        return rulesBreakPoints;
    }

    public void setRulesBreakPoints(ArrayList rulesBreakPoints) {
        this.rulesBreakPoints = rulesBreakPoints;
    }

    public Wallet getCurrentWallet() {
        return currentWallet;
    }

    public void updateTableauxCount() {
        if (listener != null && currentWallet != null) {
            listener.onTableauxCountChanged(currentWallet.getGraphes().size());
        }
    }

    public void updateEngineStatus() {
        if (listener != null) {
            listener.onStatusChanged(status);
        }
    }

    public void updateElapsedTime() {
        if (listener != null) {
            listener.onElapsedTimeChanged(getEngineTimer().getElapsedTime());
        }
    }

    public void updateAppliedRules() {
        if (listener != null) {
            listener.onAppliedRulesChanged(appliedRules);
        }
    }

    public void updateTotalAppliedRules() {
        if (listener != null) {
            listener.onTotalAppliedRulesChanged(totalAppliedRules);
        }
    }

    public void updateLastAppliedRule(final String ruleName, final String onTableauName) {
        if (listener != null) {
            listener.onRuleApplied(ruleName, onTableauName);
        }
    }

    public void updatePausedAtRule(final String ruleName) {
        if (listener != null) {
            listener.onPausedAtRule(ruleName);
        }
    }

    /**
     * @return the MainFrame, or null if running in headless mode
     * @deprecated Use EngineListener callbacks instead of directly accessing MainFrame
     */
    @Deprecated
    public MainFrame getMainFrame() {
        return mainFrame;
    }

    /**
     * @return the EngineListener for this engine
     */
    public EngineListener getListener() {
        return listener;
    }

    public boolean isRunningBySteps() {
        return runningBySteps;
    }

    public void setRunningBySteps(boolean runningBySteps) {
        this.runningBySteps = runningBySteps;
    }

    public int getAppliedRules() {
        return appliedRules;
    }

    public void increaseAppliedRules() {
        this.appliedRules++;
    }

    public int getTotalAppliedRules() {
        return totalAppliedRules;
    }

    /**
     * @return the engineTimer
     */
    public EngineTimer getEngineTimer() {
        return engineTimer;
    }

    /**
     * @return the benchmarker
     */
    public Benchmarker getBenchmarker() {
        return benchmarker;
    }
}
