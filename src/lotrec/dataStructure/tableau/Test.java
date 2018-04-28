package lotrec.dataStructure.tableau;

import lotrec.dataStructure.expression.*;
import lotrec.dataStructure.graph.*;
import lotrec.process.*;
import lotrec.util.Duplicator;
import java.util.Enumeration;
import lotrec.dataStructure.tableau.action.*;

public final class Test {

  // main

  /**
  Tests the package.
   */
  public static void main(String[] args) {
    /*try {
        
      //definition de quelques schemas, utilises par la suite

      SchemeVariable n0 = new DefaultScheme();
      SchemeVariable n1 = new DefaultScheme();
      SchemeVariable n2 = new DefaultScheme();
      SchemeVariable n3 = new DefaultScheme();

      SchemeVariable dn0 = new DefaultScheme();
      SchemeVariable dn1 = new DefaultScheme();
      SchemeVariable dn2 = new DefaultScheme();
      SchemeVariable dn3 = new DefaultScheme();

      // definition des connecteurs usuels

      Connector andConnector = new Connector("and", 2, "_ & _");
      Connector orConnector = new Connector("or", 2, "_ | _");
      Connector notConnector = new Connector("not", 1, "~_");
      Connector falseConnector = new Connector("false", 0, "false");
      Connector diamondConnector = new Connector("diamond", 1, "<>_");
      Connector boxConnector = new Connector("box", 1, "[]_");

      // definition de quelques expressions

      Expression relation = new VariableExpression("r");
      Expression a = new VariableExpression("A");
      Expression b = new VariableExpression("B");
      Expression c = new VariableExpression("C");

      ExpressionWithSubExpressions not_a = new ExpressionWithSubExpressions(notConnector);
      not_a.setExpression(a, 0);
      ExpressionWithSubExpressions not_not_a = new ExpressionWithSubExpressions(notConnector);
      not_not_a.setExpression(not_a, 0);

      ExpressionWithSubExpressions a_and_b = new ExpressionWithSubExpressions(andConnector);
      a_and_b.setExpression(a, 0);
      a_and_b.setExpression(b, 1);

      ExpressionWithSubExpressions a_or_b = new ExpressionWithSubExpressions(orConnector);
      a_or_b.setExpression(a, 0);
      a_or_b.setExpression(b, 1);

      ExpressionWithSubExpressions diamond_a = new ExpressionWithSubExpressions(diamondConnector);
      diamond_a.setExpression(a, 0);

      ExpressionWithSubExpressions box_a = new ExpressionWithSubExpressions(boxConnector);
      box_a.setExpression(a, 0);

      //////////////////////////////////////////////////////////////
      // EXEMPLE DE LA METHODE DES TABLEAUX MONOMODALE SIMPLIFIEE //
      //////////////////////////////////////////////////////////////

      // definition de la strategy globale d'execution, il vaut toujours mieux en avoir une de top niveau definie des le debut
      Strategy globalStrategy = new Strategy(new DynamicKeep());

      // regles de reecriture
      
      // definition de la regle NON NON
      
      Rule notNotRule = new Rule("not not");
      notNotRule.add(new ExpressionDescriptor(n0, not_not_a));
      notNotRule.add(new AddExpressionAction(n0, a));
      
      // definition de la regle FAUX
      
      Rule falseRule = new Rule("false");
      falseRule.add(new ExpressionDescriptor(n0, a));
      falseRule.add(new ExpressionDescriptor(n0, not_a));
      falseRule.add(new AddExpressionAction(n0, new ExpressionWithSubExpressions(falseConnector)));
      
      // definition de la regle ET

      Rule andRule = new Rule("and");
      andRule.add(new ExpressionDescriptor(n0, a_and_b));
      andRule.add(new AddExpressionAction(n0, a));
      andRule.add(new AddExpressionAction(n0, b));

      // autres regles
      
      // definition de la regle d'arret
      
      Rule closeRule = new Rule("close");
      closeRule.add(new ExpressionDescriptor(n0, new ExpressionWithSubExpressions(falseConnector)));
      closeRule.add(new StopStrategyAction(n0));
      
      // definition de la regle OU

      Rule orRule = new Rule("or");
      orRule.add(new ExpressionDescriptor(n0, a_or_b));
      DuplicateAction duplicateAction = new DuplicateAction(n0, globalStrategy);
      duplicateAction.add(n0, dn0);
      orRule.add(duplicateAction);
      orRule.add(new AddExpressionAction(n0, a));
      orRule.add(new AddExpressionAction(dn0, b));

      // definition de la regle DIAMOND

      Rule diamondRule = new Rule("diamond");
      diamondRule.add(new ExpressionDescriptor(n0, diamond_a));
      diamondRule.add(new LinkAction(n0, n1, relation));
      diamondRule.add(new AddExpressionAction(n1, a));

      // definition de la regle K

      Rule kRule = new Rule("k");
      kRule.add(new ExpressionDescriptor(n0, box_a));
      kRule.add(new LinkDescriptor(n0, n1, relation));
      kRule.add(new AddExpressionAction(n1, a));

      // creation des machines evenementielles

      EventMachine falseMachine = falseRule.createMachine();
      EventMachine notNotMachine = notNotRule.createMachine();
      EventMachine closeMachine = closeRule.createMachine();
      EventMachine andMachine = andRule.createMachine();
      EventMachine orMachine = orRule.createMachine();
      EventMachine diamondMachine = diamondRule.createMachine();
      EventMachine kMachine = kRule.createMachine();
      
      Dispatcher dispatcher = new Dispatcher();
      dispatcher.addProcessListener(falseMachine);
      dispatcher.addProcessListener(notNotMachine);
      dispatcher.addProcessListener(closeMachine);
      dispatcher.addProcessListener(andMachine);
      dispatcher.addProcessListener(orMachine);
      dispatcher.addProcessListener(diamondMachine);
      dispatcher.addProcessListener(kMachine);

      // definition de la strategy pour les regles classiques, excluant le ou

      Keep classicKeep = new DynamicKeep();
      classicKeep.add(closeMachine, null);
      classicKeep.add(falseMachine, null);
      classicKeep.add(notNotMachine, null);
      classicKeep.add(andMachine, null);

      // definition de la strategy pour les regles monomodales

      Keep monomodalKeep = new DynamicKeep();
      monomodalKeep.add(diamondMachine, null);
      monomodalKeep.add(kMachine, null);

      // constitution de la strategy pour un tableau

      Strategy strategy = new Strategy(new DynamicKeep());
      strategy.add(classicKeep, null);
      strategy.add(monomodalKeep, null);
      strategy.add(orMachine, null);
      globalStrategy.add(strategy, null);

      ////////////////////////////////////////////////////////////////////

      // creation de la valise qui accueillera les tableaux crees

      Wallet wallet = new Wallet();
      /*
      wallet.addProcessListener(new ProcessListener() {
      public void process(ProcessEvent event) {
      System.out.println("event : "+event);
      }
      });
       ---------/*--------
      // creation du tableau a traiter

      Tableau tableau = new Tableau();
      tableau.addProcessListener(dispatcher);
      tableau.setStrategy(strategy);
      wallet.add(tableau);

      TableauNode node0 = new TableauNode();
      tableau.add(node0);

      Expression p = new VariableExpression("p");
      Expression q = new VariableExpression("q");
      Expression r = new VariableExpression("r");
      Expression s = new VariableExpression("s");
      
      ExpressionWithSubExpressions p_or_q = new ExpressionWithSubExpressions(orConnector);
      p_or_q.setExpression(p, 0);
      p_or_q.setExpression(q, 1);

      ExpressionWithSubExpressions p_or_q_or_r = new ExpressionWithSubExpressions(orConnector);
      p_or_q_or_r.setExpression(p_or_q, 0);
      p_or_q_or_r.setExpression(r, 1);

      ExpressionWithSubExpressions diamond_p_or_q = new ExpressionWithSubExpressions(diamondConnector);
      diamond_p_or_q.setExpression(p_or_q, 0);

      ExpressionWithSubExpressions diamond_p_or_q_or_r = new ExpressionWithSubExpressions(diamondConnector);
      diamond_p_or_q_or_r.setExpression(p_or_q_or_r, 0);

      ExpressionWithSubExpressions not_q = new ExpressionWithSubExpressions(notConnector);
      not_q.setExpression(q, 0);

      ExpressionWithSubExpressions box_r = new ExpressionWithSubExpressions(boxConnector);
      box_r.setExpression(r, 0);
      
      ExpressionWithSubExpressions box_not_q = new ExpressionWithSubExpressions(boxConnector);
      box_not_q.setExpression(not_q, 0);
      
      ExpressionWithSubExpressions box_r_and_diamond_p_or_q_or_r = new ExpressionWithSubExpressions(andConnector);
      box_r_and_diamond_p_or_q_or_r.setExpression(box_r, 0);
      box_r_and_diamond_p_or_q_or_r.setExpression(diamond_p_or_q_or_r, 1);

      ExpressionWithSubExpressions box_r_and_diamond_p_or_q_or_r_and_box_not_q = new ExpressionWithSubExpressions(andConnector);
      box_r_and_diamond_p_or_q_or_r_and_box_not_q.setExpression(box_r_and_diamond_p_or_q_or_r, 0);
      box_r_and_diamond_p_or_q_or_r_and_box_not_q.setExpression(box_not_q, 1);

      node0.add(new MarkedExpression(box_r_and_diamond_p_or_q_or_r_and_box_not_q));

      System.out.print("strategy working...");
      while(!globalStrategy.isQuiet()) {
        System.out.print("#");
        globalStrategy.work();
      }
      System.out.println();

      System.out.println("Wallet : "+wallet);
      for(Enumeration enumr = wallet.getGraphes(); enumr.hasMoreElements();) {
        Tableau t = (Tableau)enumr.nextElement();
        System.out.println(" . Tableau : "+t);
        for(Enumeration enum_ = t.getNodes(); enum_.hasMoreElements();) {
          TableauNode n = (TableauNode)enum_.nextElement();
          System.out.println("   . TableauNode : "+n);
          for(Enumeration enum__ = n.getMarkedExpressions(); enum__.hasMoreElements();) {
            MarkedExpression e = (MarkedExpression)enum__.nextElement();
            System.out.println("     . MarkedExpression : "+e);
          }
          for(Enumeration enum__ = n.getNextEdges(); enum__.hasMoreElements();) {
            TableauEdge e = (TableauEdge)enum__.nextElement();
            System.out.println("     . TableauEdge next : "+e);
          }
          for(Enumeration enum__ = n.getLastEdges(); enum__.hasMoreElements();) {
            TableauEdge e = (TableauEdge)enum__.nextElement();
            System.out.println("     . TableauEdge last : "+e);
          }
        }
      }

    } catch(Exception exception) {
      System.out.println(exception);
    }
    */
  }
}
