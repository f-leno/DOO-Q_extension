/**
 * 
 */
package goldmine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import burlap.behavior.policy.Policy;
import burlap.behavior.valuefunction.QFunction;
import burlap.behavior.valuefunction.QValue;
import burlap.oomdp.core.AbstractGroundedAction;
import burlap.oomdp.core.states.State;
import burlap.oomdp.statehashing.HashableState;
import burlap.oomdp.statehashing.HashableStateFactory;
import burlap.oomdp.stochasticgames.SGDomain;
import burlap.oomdp.stochasticgames.agentactions.GroundedSGAgentAction;
import burlap.oomdp.stochasticgames.agentactions.SGAgentAction;

/**
 * @author Felipe Leno da Silva
 * Policy that is iteratively improved until the optimal policy, as described on the article.
 * This policy must be updated in each decision step using the updatePolicy() method.
 * The currently implemented exploration strategy is e-greedy, and the exploration is turned on/off
 * using the setExploration() method.
 *
 */
public class DOOQPolicy extends Policy {

	protected SGDomain domain;
	protected QFunction qSource; //Class to define Q-Values
	protected boolean exploring = true; //Is the agent exploring?
	protected double epsilon = 0.05; //epsilon used when exploration is turned on
	protected Random rand; //Random Class
	protected HashableStateFactory hashFactory; //HashFactory for q-value retrieval.
	protected HashMap<HashableState,AbstractGroundedAction> policyMemory; //Current best action for each state
	protected String agentName;
	protected boolean useAbstractActions;

	/**
	 * Default constructor for DOO-Q policy. exploration is turned on and the Random object used for e-greedy exploration is initiated 
	 * with a default seed. Exploration can be turned off with setExploration() method and setRandom() can be used to change the default seed
	 * @param domain domain.
	 * @param qSource Source for Q-Values... usually a DistributedQTable is used for this policy.
	 * @param epsilon epsilon parameter for epsilon-Greedy exploration
	 * 
	 */
	public DOOQPolicy(SGDomain domain, QFunction qSource, double epsilon, HashableStateFactory hashFactory){
		this.qSource = qSource;
		this.epsilon = epsilon;
		this.rand = new Random();
		this.exploring = true;		
		this.hashFactory = hashFactory;
		this.domain = domain;
		this.policyMemory = new HashMap<HashableState,AbstractGroundedAction>(100000);
		//this.useAbstractActions = useAbstractActions;
	}
	/**
	 * Default constructor for DOO-Q policy. exploration is turned on and the Random object used for e-greedy exploration is initiated 
	 * with a default seed. Exploration can be turned off with setExploration() method and setRandom() can be used to change the default seed
	 * @param domain domain.
	 * @param qSource Source for Q-Values... usually a DistributedQTable is used for this policy.
	 * @param epsilon epsilon parameter for epsilon-Greedy exploration
	 * @param rand Random class to be used in DOOQPolicy (experiment reproducibility)
	 * 
	 */
	public DOOQPolicy(SGDomain domain, QFunction qSource, double epsilon, HashableStateFactory hashFactory,	Random rand) {
		this.qSource = qSource;
		this.epsilon = epsilon;
		this.rand = rand;
		this.exploring = true;		
		this.hashFactory = hashFactory;
		this.domain = domain;
		this.policyMemory = new HashMap<HashableState,AbstractGroundedAction>(100000); 
	}

	/**
	 * Constructor without setting HashableStateFactory. The setHashableStateFactory() method must be used before use of this 
	 * object. Exploration is turned on and the Random object used for e-greedy exploration is initiated 
	 * with a default seed. Exploration can be turned off with setExploration() method and setRandom() can be used to change the default seed
	 * @param domain domain.
	 * @param qSource Source for Q-Values... usually a DistributedQTable is used for this policy.
	 * @param epsilon epsilon parameter for epsilon-Greedy exploration
	 * @param hashFactory Hash factory for q-value retrieval... the same object from DistributedQTable should be informed here.
	 */
	public DOOQPolicy(SGDomain domain, QFunction qSource, double epsilon){
		this.qSource = qSource;
		this.epsilon = epsilon;
		this.rand = new Random();
		this.exploring = true;		
		this.hashFactory = null;
		this.domain = domain;
		this.policyMemory = new HashMap<HashableState,AbstractGroundedAction>(100000);
		//this.useAbstractActions = useAbstractActions;
	}
	/**
	 * Constructor without setting HashableStateFactory. The setHashableStateFactory() method must be used before use of this 
	 * object. Exploration is turned on and the Random object used for e-greedy exploration is initiated 
	 * with a default seed. Exploration can be turned off with setExploration() method and setRandom() can be used to change the default seed
	 * @param domain domain.
	 * @param qSource Source for Q-Values... usually a DistributedQTable is used for this policy.
	 * @param epsilon epsilon parameter for epsilon-Greedy exploration
	 * @param hashFactory Hash factory for q-value retrieval... the same object from DistributedQTable should be informed here.
	 * @param rand Random class to be used in DOOQPolicy (experiment reproducibility)
	 */
	public DOOQPolicy(SGDomain domain, QFunction qSource, double epsilon, Random rand) {
		this.qSource = qSource;
		this.epsilon = epsilon;
		this.rand = rand;
		this.exploring = true;		
		this.hashFactory = null;
		this.domain = domain;
		this.policyMemory = new HashMap<HashableState,AbstractGroundedAction>(100000);
	}

	

	/**
	 *  This method will first look at the policy memory to define if this state has been already visited.
	 *  For this case the best action is already know, however, in case this state has never been visited, the best action is 
	 *  extracted from the QSource. For this situation the action associated to the max q-value is chosen.
	 */
	@Override
	public AbstractGroundedAction getAction(State s) {
		AbstractGroundedAction bestAction = null;

		//Checks if a random Action must be applied
		if(checkExploration()){
			bestAction = getRandomAction(s);
		}
		else{
			bestAction = getBestAction(s);
		}

		
		//Since for this implementation the associated (unparameterized) action is always stored, a parameterized version of this action is returned
		List<SGAgentAction> actions = new ArrayList<SGAgentAction>();
		
		actions.add(this.domain.getSGAgentAction(bestAction.actionName()));
		
		//Get first grounded action (it should only exist one).
		//bestAction = SGAgentAction.getAllApplicableGroundedActionsFromActionList(s, this.agentName, actions).get(0);
		List<GroundedSGAgentAction> listG = SGAgentAction.getAllApplicableGroundedActionsFromActionList(s, this.agentName, actions);
		//Por que diabos getApplicableActions retorna uma lista vazia, quando uma a��o � aplicavel?
		if(listG.size()==0){
			System.out.println("aqui");
			listG = SGAgentAction.getAllApplicableGroundedActionsFromActionList(s, this.agentName, actions);
		}
		bestAction = listG.get(0);
			

		return bestAction;
	}

	


	/**
	 * Check if a random action should be applied
	 * @return true if exploring
	 */
	protected boolean checkExploration() {
		
		if(this.exploring)
			return this.epsilon>=rand.nextDouble();
			
		return false;		
	}
	
	/**
	 * This method select a random action for exploration.
	 * A random abstract action is chosen, then a random grounding is returned
	 * @param s current state
	 * @return a random grounded action
	 */
	protected AbstractGroundedAction getRandomAction(State s) {
		List<SGAgentAction> allActions = domain.getAgentActions();
		
		AbstractGroundedAction returnedAction = null;
		
		//Randomize action until an applicable one is found
		while(returnedAction==null){
			SGAgentAction absAction = allActions.get(rand.nextInt(allActions.size()));
			List<SGAgentAction> lActions = new ArrayList<SGAgentAction>();  lActions.add(absAction);
			
			List<GroundedSGAgentAction> groundActions = SGAgentAction.getAllApplicableGroundedActionsFromActionList(s, this.agentName, lActions);//Action.getAllApplicableGroundedActionsFromActionList(lActions, s);
			//If this action is applicable...
			if(groundActions.size()>0){
				//select random grounding
				returnedAction = groundActions.get(rand.nextInt(groundActions.size()));
			}
			
		}
		return returnedAction;	
	}


	/**
	 * This method is called when exploiting (both when exploration is off or the epsilon greedy random resulted in exploitation)
	 * The best action is defined and returned.
	 * @param s current state
	 * @return best action
	 */
	protected AbstractGroundedAction getBestAction(State s){
		AbstractGroundedAction action = null;
		//------------------------------------------
		// Search for the current state on the memory
		//------------------------------------------------
		HashableState hashState = this.hashFactory.hashState(s);

		//if the current state exists
		if(policyMemory.containsKey(hashState)){
			action = policyMemory.get(hashState);
		}

		//If the state has not been updated on the policy, the best action is chosen based on the QFunction
		else{
			List<QValue> qValues = this.qSource.getQs(s);
			
			//Actions that has the same MaxQ Value (chosen Randomly)
			List<AbstractGroundedAction> actionsBestValue = new ArrayList<AbstractGroundedAction>();
			//find max Q and set the correspondent action as the return
			double maxQ = Double.NEGATIVE_INFINITY;
			
			for(QValue q: qValues){
				if(maxQ<q.q){
					actionsBestValue.clear();
					maxQ = q.q;
					actionsBestValue.add(q.a);
				}
				else{
					//If the actions has the same value, it is added to the list
					if(maxQ==q.q){
						actionsBestValue.add(q.a);
					}
				}
			}
			//Get random action from the list
			action = actionsBestValue.get(rand.nextInt(actionsBestValue.size()));
		}
		
		return action;
	}

	/* (non-Javadoc)
	 * @see burlap.behavior.policy.Policy#getActionDistributionForState(burlap.oomdp.core.states.State)
	 */
	@Override
	public List<ActionProb> getActionDistributionForState(State s) {
		//-----------------------------
		// Not currently implemented
		//-----------------------------
		return null;
	}

	/* (non-Javadoc)
	 * @see burlap.behavior.policy.Policy#isStochastic()
	 */
	@Override
	public boolean isStochastic() {
		return false;
	}

	/* (non-Javadoc)
	 * @see burlap.behavior.policy.Policy#isDefinedFor(burlap.oomdp.core.states.State)
	 */
	@Override
	public boolean isDefinedFor(State s) {
		return true;
	}

	/**
	 * This method must be called when the maxQ value changes, which means that the action for the current state must be updated.
	 * @param s current state
	 * @param a applied local action
	 */
	public void updatePolicy(State s, AbstractGroundedAction a){
		HashableState hashState = this.hashFactory.hashState(s);
		this.policyMemory.put(hashState, a);
	}
	/**
	 * Defines if the exploration is set to on or off
	 * @param exploring current value for exploration
	 */
	public void setExploration(boolean exploring){
		this.exploring = exploring;
	}
	/**
	 * Returns whether the agent is exploring or not.
	 * @return true if exploration is on and false if exploration is off.
	 */
	public boolean isExploring(){
		return this.exploring;
	}
	/**
	 * Allows a Random class definition to experiment reproducibility
	 * @param rand Random class (properly initiated)
	 */
	public void setRandom(Random rand){
		this.rand = rand;
	}


	/**
	 * Set hashableStateFactory after instanciation
	 * @param factory the factory
	 */
	public void setHashableStateFactory(HashableStateFactory factory) {
		this.hashFactory = factory;
		
	}
	
	/**
	 * Set the agent owner of this DistributedQTable
	 * @param agentName agentName
	 */
	public void setAgentName(String agentName){
		this.agentName = agentName;
	}
	public double getEpsilon() {
		return this.epsilon;
	}
	/**
	 * Resets the policy
	 * @param qTable
	 */
	public void resetPolicy(QFunction qTable) {
		this.qSource = qTable;
		this.policyMemory = new HashMap<HashableState,AbstractGroundedAction>(100000);
		
	}

}
