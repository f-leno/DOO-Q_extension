/**
 * 
 */
package goldmine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.management.RuntimeErrorException;

import burlap.behavior.valuefunction.QFunction;
import burlap.behavior.valuefunction.QValue;
import burlap.behavior.valuefunction.ValueFunctionInitialization;
import burlap.oomdp.core.AbstractGroundedAction;
import burlap.oomdp.core.AbstractObjectParameterizedGroundedAction;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.states.State;
import burlap.oomdp.statehashing.HashableState;
import burlap.oomdp.statehashing.HashableStateFactory;
import burlap.oomdp.stochasticgames.SGDomain;
import burlap.oomdp.stochasticgames.agentactions.GroundedSGAgentAction;
import burlap.oomdp.stochasticgames.agentactions.SGAgentAction;

/**
 * @author Felipe Leno da Silva
 * A distributed Q-table which stores only local Q-values. This table can be used to learn a optimal policy in cooperative MOO-MDPs. 
 * See the related article
 *
 */
public class DistributedQTable implements QFunction {

	protected HashMap<HashableState, QLearningStateNode>	qValues; //QValues to be stored
	protected HashableStateFactory 			hashingFactory; //Hash generator
	protected ValueFunctionInitialization	qInit;	
	protected boolean useAbstractActions;
	protected Domain domain;
	protected String agentName;
	
	//Counter of Q-Table Entries
	protected long entriesNumber=0;
	
	
	/**
	 * Distributed Q Table constructor without specifying hashableStateFactory, which must be set with the method 
	 * setHashableStateFactory() before use. 
	 * 
	 * @param useAbstractActions this parameter must be true if actions are parameter independent and only depend on the acting agent
	 */
	public DistributedQTable(Domain domain,ValueFunctionInitialization qInit, boolean useAbstractActions){
		this.hashingFactory = null;		
		this.useAbstractActions = useAbstractActions;
		this.qInit = qInit;
		this.domain = domain;
		qValues = new HashMap<HashableState, QLearningStateNode>(100000);
	}
	
	/**
	 * Distributed Q Table constructor 
	 * @param hashFactory A HashableStateFactory that will be used to define a hash for each state.
	 * @param useAbstractActions this parameter must be true if actions are parameter independent and only depend on the acting agent
	 */
	public DistributedQTable(Domain domain, HashableStateFactory hashFactory,ValueFunctionInitialization qInit, boolean useAbstractActions){
		this.hashingFactory = hashFactory;		
		this.useAbstractActions = useAbstractActions;
		this.qInit = qInit;
		this.domain = domain;
		qValues = new HashMap<HashableState, QLearningStateNode>(100000);
	}

	
	
	/* (non-Javadoc)
	 * @see burlap.behavior.valuefunction.ValueFunction#value(burlap.oomdp.core.states.State)
	 */
	@Override
	public double value(State s) {
		return QFunction.QFunctionHelper.getOptimalValue(this, s);
	}

	/* (non-Javadoc)
	 * @see burlap.behavior.valuefunction.QFunction#getQs(burlap.oomdp.core.states.State)
	 */
	@Override
	public List<QValue> getQs(State s) {
		//The first step is to hash the state
		HashableState hs = this.hashingFactory.hashState(s);
		
		QLearningStateNode node = this.getStateNode(hs);
		return node.qEntry;
	}

	/* (non-Javadoc)
	 * @see burlap.behavior.valuefunction.QFunction#getQ(burlap.oomdp.core.states.State, burlap.oomdp.core.AbstractGroundedAction)
	 */
	@Override
	public QValue getQ(State s, AbstractGroundedAction a) {
		HashableState hs = this.hashingFactory.hashState(s);
		QLearningStateNode node = this.getStateNode(hs);

		//Check if abstract actions are used
		if(this.useAbstractActions){
			
			a = domain.getSGAgentAction(a.actionName()).getAssociatedGroundedAction(this.agentName);
		}
		else{
			a = (GroundedSGAgentAction)AbstractObjectParameterizedGroundedAction.Helper.translateParameters(a, hs.s, node.s.s);
		}
		
		for(QValue qv : node.qEntry){
			if(qv.a.equals(a)){
				return qv;
			}
		}
		
		return null; //no action for this state indexed
		
		
	}
	
	/**
	 * Returns the {@link QLearningStateNode} object stored for the given hashed state. If no {@link QLearningStateNode} object.
	 * is stored, then it is created and has its Q-value initialize using this objects {@link burlap.behavior.valuefunction.ValueFunctionInitialization} data member.
	 * @param s the hashed state for which to get the {@link QLearningStateNode} object
	 * @return the {@link QLearningStateNode} object stored for the given hashed state. If no {@link QLearningStateNode} object.
	 */
	protected QLearningStateNode getStateNode(HashableState s){
		
		QLearningStateNode node = qValues.get(s);
		
		if(node == null){
			node = new QLearningStateNode(s);
			List<GroundedSGAgentAction> gas;
			gas = this.getAllGroundedActions(s.s);
			
			if(gas.size() == 0){
				throw new RuntimeErrorException(new Error("No possible actions in this state, cannot continue Q-learning"));
			}
			for(GroundedSGAgentAction ga : gas){
				if(ga.applicableInState(s.s)){
					node.addQValue(ga, qInit.qValue(s.s, ga));
					this.entriesNumber++;
				}
			}
			
			qValues.put(s, node);
		}
		
		return node;
		
	}


	/**
	 * Return all possible grounded action for a given state.
	 * This methods uses the domain description given in the constructor and can return parameter independent action according
	 * to <i>useAbstractActions</i> given in the constructor
	 * @param s state
	 * @return list of grounded actions
	 */
	protected List<GroundedSGAgentAction> getAllGroundedActions(State s) {
		//List<Action> actions = domain.getActions();
		List<SGAgentAction> actions = domain.getAgentActions();
		
		 
		//If useAbstractActions == true, the return will be all possible grounded actions
		if (!this.useAbstractActions){
			return SGAgentAction.getAllApplicableGroundedActionsFromActionList(s, this.agentName, actions);
		}
		//If false, there will be only one associated grounded action for each Action.
		List<GroundedSGAgentAction> associatedActions = new ArrayList<GroundedSGAgentAction>();
		
		for(SGAgentAction action : actions){
			associatedActions.add(action.getAssociatedGroundedAction(this.agentName));
		}
		return associatedActions;
		
		
		
	}

	/**
	 * Change or set the current hashableStateFactory
	 * @param factory the factory
	 */
	public void setHashableStateFactory(HashableStateFactory factory) {
		this.hashingFactory = factory;
		
	}
	
	/**
	 * Set the agent owner of this DistributedQTable
	 * @param agentName agentName
	 */
	public void setAgentName(String agentName){
		this.agentName = agentName;
	}
	
	/**
	 * This class is used to store the associated {@link burlap.behavior.valuefunction.QValue} objects for a given hashed sated.
	 * This class was changed to store GroundedSGAgentAction instead of GroundedActions
	 * @author Felipe Leno
	 *
	 */
	protected class QLearningStateNode {

		/**
		 * A hashed state entry for which Q-value will be stored.
		 */
		public HashableState s;
		
		/**
		 * The Q-values for this object's state.
		 */
		public List<QValue>				qEntry;
		
		
		/**
		 * Creates a new object for the given hashed state. The list of {@link burlap.behavior.valuefunction.QValue} objects is initialized to be empty.
		 * @param s the hashed state for which to associate Q-values
		 */
		public QLearningStateNode(HashableState s) {
			this.s = s;
			qEntry = new ArrayList<QValue>();
		}

		
		/**
		 * Adds a Q-value to this state with the given numeric Q-value.
		 * @param a the action this Q-value is fore
		 * @param q the numeric Q-value
		 */
		public void addQValue(GroundedSGAgentAction a, double q){
			QValue qv = new QValue(s.s, a, q);
			qEntry.add(qv);
		}
		
		
	}

	public long getQTableSize() {
		return this.entriesNumber;
	}

}
