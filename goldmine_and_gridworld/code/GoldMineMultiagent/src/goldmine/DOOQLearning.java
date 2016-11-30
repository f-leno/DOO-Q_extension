/**
 * 
 */
package goldmine;

import java.util.List;
import java.util.Map;
import java.util.Random;

import burlap.behavior.policy.Policy;
import burlap.behavior.valuefunction.QValue;
import burlap.behavior.valuefunction.ValueFunctionInitialization;
import burlap.oomdp.core.AbstractGroundedAction;
import burlap.oomdp.core.states.State;
import burlap.oomdp.statehashing.HashableStateFactory;
import burlap.oomdp.stochasticgames.JointAction;
import burlap.oomdp.stochasticgames.SGAgent;
import burlap.oomdp.stochasticgames.SGAgentType;
import burlap.oomdp.stochasticgames.SGDomain;
import burlap.oomdp.stochasticgames.World;
import burlap.oomdp.stochasticgames.agentactions.GroundedSGAgentAction;
import experiment.QTableInspectorAgent;

/**
 * @author Felipe Leno da Silva
 * 
 * An implementation of Distributed Object-Oriented Q-Learning (DOOQ) as SGAgents in Burlap.
 * This algorithm works for Multiagent domains where full observability holds and each agent acts independently.
 * 
 * The learningPolicy is initially set as the policy described by equation (4) in the article. However, this can be changed with the method setLearningPolicy.
 * 
 * More information in the article.
 *
 */
public class DOOQLearning extends SGAgent implements QTableInspectorAgent {

	
	protected double gamma; //the discount rate
	protected ValueFunctionInitialization qInit; //QValue initialization
	protected HashableStateFactory hashFactory;
	protected Policy learningPolicy;
	protected SGDomain domain;
	protected DistributedQTable qTable; //Q Table for DOO-Q
	protected boolean exploring = true;
	

	/**
	 * Constructor to specify necessary parameters. The HashableStateFactory must be set with setHashableStateFactory()
	 * 
	 * @param domain Multiagent domain
	 * @param initialQValue All Q-values will be updated following this parameter
	 * @param gamma Discount rate
	 * @param epsilon epsilon value for exploration
	 * @param rand Random class to be used in DOOQPolicy (experiment reproducibility)
	 * 
	 */
	public DOOQLearning(SGDomain domain, double initialQValue, double gamma, double epsilon, Random rand) {
		super();
		this.qInit = new ValueFunctionInitialization.ConstantValueFunctionInitialization(initialQValue);
		this.gamma = gamma;
		this.hashFactory = null;
		this.domain = domain;
		this.qTable = new DistributedQTable(domain, qInit, true);
		this.learningPolicy = new DOOQPolicy(this.domain,this.qTable,epsilon,rand);
	}
	/**
	 * Constructor to specify necessary parameters.
	 * 
	 * @param domain Multiagent domain
	 * @param initialQValue All Q-values will be updated following this parameter
	 * @param gamma Discount rate
	 * @param epsilon epsilon value for exploration
	 * @param hashFactory A hash factory to provide a state description through hashes.
	 * @param rand Random class to be used in DOOQPolicy (experiment reproducibility)
	 */
	public DOOQLearning(SGDomain domain, double initialQValue, double gamma, double epsilon, HashableStateFactory hashFactory,Random rand) {
		super();
		this.qInit = new ValueFunctionInitialization.ConstantValueFunctionInitialization(initialQValue);
		this.gamma = gamma;
		this.hashFactory = hashFactory;
		this.domain = domain;
		this.qTable = new DistributedQTable(domain, hashFactory, qInit, true);
		this.learningPolicy = new DOOQPolicy(this.domain,this.qTable,epsilon,this.hashFactory,rand);
	}
	/**
	 * Constructor to specify necessary parameters. The HashableStateFactory must be set with setHashableStateFactory()
	 * 
	 * @param domain Multiagent domain
	 * @param initialQValue All Q-values will be updated following this parameter
	 * @param gamma Discount rate
	 * @param epsilon epsilon value for exploration
	 * 
	 */
	public DOOQLearning(SGDomain domain, double initialQValue, double gamma, double epsilon) {
		super();
		this.qInit = new ValueFunctionInitialization.ConstantValueFunctionInitialization(initialQValue);
		this.gamma = gamma;
		this.hashFactory = null;
		this.domain = domain;
		this.qTable = new DistributedQTable(domain, qInit, true);
		this.learningPolicy = new DOOQPolicy(this.domain,this.qTable,epsilon);
	}
	/**
	 * Constructor to specify necessary parameters.
	 * 
	 * @param domain Multiagent domain
	 * @param initialQValue All Q-values will be updated following this parameter
	 * @param gamma Discount rate
	 * @param epsilon epsilon value for exploration
	 * @param hashFactory A hash factory to provide a state description through hashes.
	 */
	public DOOQLearning(SGDomain domain, double initialQValue, double gamma, double epsilon, HashableStateFactory hashFactory) {
		super();
		this.qInit = new ValueFunctionInitialization.ConstantValueFunctionInitialization(initialQValue);
		this.gamma = gamma;
		this.hashFactory = hashFactory;
		this.domain = domain;
		this.qTable = new DistributedQTable(domain, hashFactory, qInit, true);
		this.learningPolicy = new DOOQPolicy(this.domain,this.qTable,epsilon,this.hashFactory);
	}
	/**
	 * Alternative constructor specifying a Policy and DistributedQTable.
	 * 
	 * @param domain Multiagent domain
	 * @param gamma Discount rate
	 * @param policy learning policy
	 * @param qTable source of Q-Values
	 * @param hashFactory A hash factory to provide a state description through hashes.
	 */
	public DOOQLearning(SGDomain domain, double gamma, Policy policy, DistributedQTable qTable, HashableStateFactory hashFactory) {
		super();
		this.gamma = gamma;
		this.hashFactory = hashFactory;
		this.domain = domain;
		this.qTable = qTable;
		this.learningPolicy = policy;
	}


	/* (non-Javadoc)
	 * @see burlap.oomdp.stochasticgames.SGAgent#gameStarting()
	 */
	@Override
	public void gameStarting() {
		//-------------------------
		//No procedure to perform
		//-------------------------
	}
	

	/**
	 * Besides the default initialization of this method, sets the agentName on the distributed Q-table
	 * @param w world
	 * @param as agentType
	 */
	public void joinWorld(World w, SGAgentType as) {
		super.joinWorld(w, as);
		//Set agent name in other classes
		this.qTable.setAgentName(this.worldAgentName);
		if(this.learningPolicy instanceof DOOQPolicy){
			((DOOQPolicy)learningPolicy).setAgentName(this.worldAgentName);
		}
	}
	
	/* (non-Javadoc)
	 * @see burlap.oomdp.stochasticgames.SGAgent#getAction(burlap.oomdp.core.states.State)
	 */
	@Override
	public GroundedSGAgentAction getAction(State s) {
		AbstractGroundedAction action = this.learningPolicy.getAction(s);
		
		GroundedSGAgentAction returnAction = (GroundedSGAgentAction) action;
		returnAction.actingAgent = this.worldAgentName;
		
		return returnAction;
	}

	/* (non-Javadoc)
	 * @see burlap.oomdp.stochasticgames.SGAgent#observeOutcome(burlap.oomdp.core.states.State, burlap.oomdp.stochasticgames.JointAction, java.util.Map, burlap.oomdp.core.states.State, boolean)
	 */
	@Override
	public void observeOutcome(State s, JointAction jointAction,
			Map<String, Double> jointReward, State sprime, boolean isTerminal) {
		
		//-----------
		//Get reward
		//-----------
		double r = jointReward.get(this.worldAgentName);
		 
		//-----------------
		//Get local Action
		//-----------------
		List<GroundedSGAgentAction> actions =  jointAction.getActionList();
		int i=0;
		//search for action performed by this agent
		while(!actions.get(i).actingAgent.equals(this.worldAgentName)){
			i++;
		}
		AbstractGroundedAction localAction = actions.get(i);
		
		//----------------------
		// Update Q Value
		//----------------------
		if(this.exploring)
			this.updateQValue(s,sprime,r,localAction);
	}


	/* (non-Javadoc)
	 * @see burlap.oomdp.stochasticgames.SGAgent#gameTerminated()
	 */
	@Override
	public void gameTerminated() {
		//-------------------------
		//No procedure to perform
		//-------------------------
	}
	
	/**
	 * Update the Q-Table
	 * @param s state where the action was applied
	 * @param r reward received
	 * @param localAction local action
	 */
	protected void updateQValue(State s, State sprime, double r,
			AbstractGroundedAction localAction) {
		//Get Current value
		QValue q = this.qTable.getQ(s, localAction);
		
		//Max q on next state
		double maxQPrime = this.qTable.value(sprime); 
		
		
		//Q update equation (see article)
		double nextQ = Math.max( q.q,
				                 r + this.gamma * maxQPrime);
		
		//Check if the policy requires updates
		if(this.learningPolicy instanceof DOOQPolicy){
			if(nextQ >  this.qTable.value(s)){
				((DOOQPolicy) learningPolicy).updatePolicy(s, localAction);
			}
		}
		//Finally, update Q-table
		q.q = nextQ;
	}
	

	

	/**
	 * The learning policy is initially set as described by Equation (4) in the article. However, this method can be used to change the default policy 
	 * @param learningPolicy new policy
	 */
	public void setLearningPolicy(Policy learningPolicy){
		this.learningPolicy = learningPolicy;
	}
	
	/**
	 * Returns the current learning policy (can be used to access and change parameters of the default policy) 
	 * @return the current policy
	 */
	public Policy getLearningPolicy(){
		return this.learningPolicy;
	}
	
	/**
	 * Alternative set method to set hashableStateFctories after instanciation
	 * @param factory the factory
	 */
	public void setHashableStateFactory(HashableStateFactory factory){
		this.hashFactory = factory;
		//Check if the policy also needs to be updated
		if(this.learningPolicy instanceof DOOQPolicy){
			((DOOQPolicy)this.learningPolicy).setHashableStateFactory(factory);
			this.qTable.setHashableStateFactory(factory);
		}
		
	}
	/**
	 * This method sets exploration on/off, however, it only works when using DOOQPolicy
	 * @param value true for exploration = on.
	 */
	public void setExploration(boolean value) {
		if(this.learningPolicy instanceof DOOQPolicy){
			((DOOQPolicy) this.learningPolicy).setExploration(value);
		}
		this.exploring = value;
		
	}
	
	
	@Override
	public long getQTableSize() {
		return this.qTable.getQTableSize();
	
	}
	@Override
	public void resetAgent(Random rand) {
		this.qTable = new DistributedQTable(domain, hashFactory, qInit, true);
		this.qTable.setAgentName(this.worldAgentName);
		((DOOQPolicy)this.learningPolicy).resetPolicy(qTable);
		((DOOQPolicy)this.learningPolicy).setAgentName(this.worldAgentName);
		
		
		System.gc();
	}
	
}
