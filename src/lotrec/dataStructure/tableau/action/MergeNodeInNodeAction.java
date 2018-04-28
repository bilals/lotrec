package lotrec.dataStructure.tableau.action;

import java.util.ArrayList;
import lotrec.dataStructure.ParametersDescriptions;
import lotrec.dataStructure.ParametersTypes;
import lotrec.dataStructure.expression.InstanceSet;
import lotrec.dataStructure.expression.MarkedExpression;
import lotrec.dataStructure.expression.SchemeVariable;
import lotrec.dataStructure.tableau.Tableau;
import lotrec.dataStructure.tableau.TableauNode;
import lotrec.process.AbstractAction;
import lotrec.process.AbstractWorker;
import lotrec.process.EventMachine;
import lotrec.process.Routine;

/**
 *
 * @author said
 */
public class MergeNodeInNodeAction extends AbstractAction {

    private SchemeVariable mergedNodeScheme;
    private SchemeVariable bigNodeScheme;

    @ParametersTypes(types = {"node", "node"})
    @ParametersDescriptions(descriptions = {"the node to be merged in the second \"node\" argument",
        "the second node in which the first \"node\" argument will be merged"
    })
    public MergeNodeInNodeAction(SchemeVariable n1, SchemeVariable n2) {
        super();
        this.mergedNodeScheme = n1;
        this.bigNodeScheme = n2;
    }

    private void getRules(AbstractWorker w, ArrayList<EventMachine> rules){
        if(w instanceof EventMachine){
            rules.add((EventMachine)w);
        }else if(w instanceof Routine){
            Routine r=(Routine) w;
            for(AbstractWorker subWorker : r.getWorkers()){
                getRules(subWorker,rules);
            }
        }
    }

    @Override
    public Object apply(EventMachine em, Object modifier) {
        //Both nodes should be instantiated
        //Both nodes should be in the same Tableau (Graph)..
        //Formulas (and their marks) and Marks of mergedNode are copied to bigNode [WITHOUT GENERATING ANY NEW EVENT..]
        //In-Edges, Out-edges of mergedNode are redirected to bigNode              [WITHOUT GENERATING ANY NEW EVENT..]
        //For each of these objects of mergedNode re-oriented to bigNode:
        //   Every (not treated yet) ProcessEvent and Every ActionPack in every EventMachine of the Tableau
        //         should be adjusted to point to bigNode and the new objects added to it...
        InstanceSet instanceSet = (InstanceSet) modifier;
        TableauNode mergedNode = (TableauNode) instanceSet.get(mergedNodeScheme);
        TableauNode bigNode = (TableauNode) instanceSet.get(bigNodeScheme);//k will be merged in n
        if (mergedNode.equals(bigNode)) {
            System.out.println("Merging " + mergedNodeScheme + " and " + bigNodeScheme + " cannot be done.." +
                    "\nThey represent the same node: " + mergedNode);
        } else {
            System.out.println(mergedNode + " will be merged in " + bigNode);
            Tableau t = (Tableau) mergedNode.getGraph();
            

            ArrayList<EventMachine> rules = new ArrayList();
            getRules(t.getStrategy(),rules);
//            for(EventMachine em : rules)
//                System.out.println(em.getWorkerName());

            for(MarkedExpression merExp : mergedNode.getMarkedExpressions()){
                boolean isInBigNode=false;
                for(MarkedExpression bigExp : bigNode.getMarkedExpressions()){
                    if(merExp.expression.equals(bigExp.expression)){
                        // if merExp.expression has an equivalent expression in bigNode
                        // we just add the marks of merExp to its equivalent.. 
                        // But only the marks that are not there...
                        isInBigNode = true;
                        for(Object mark : merExp.getMarks()){
                            if(!bigExp.getMarks().contains(mark)){
                                //we have to add "mark" to getMarks of bigExp
                            }
                        }
                        break;
                    }
                }
                if(!isInBigNode){
                    //we have to add merExp and all its marks to bigNode
                }
            }


//            t.remove(mergedNode);
            instanceSet.put(mergedNodeScheme, bigNode);// the image of n2 is re-oriented toward the image of n1
        }
        return instanceSet;
    }
}
