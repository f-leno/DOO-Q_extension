package goldMineSA;

import java.util.List;

import javax.management.RuntimeErrorException;

import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.behavior.singleagent.learning.tdmethods.QLearningStateNode;
import burlap.oomdp.statehashing.HashableStateFactory;
import burlap.oomdp.statehashing.HashableState;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.GroundedAction;


/**
 * Tabular Q-learning algorithm [1]. This implementation will work correctly with Options [2]. The implementation can either be used for learning or planning,
 * the latter of which is performed by running many learning episodes in succession in a {@link burlap.oomdp.singleagent.environment.SimulatedEnvironment}.
 * If you are going to use this algorithm for planning, call the {@link #initializeForPlanning(burlap.oomdp.singleagent.RewardFunction, burlap.oomdp.core.TerminalFunction, int)}
 * method before calling {@link #planFromState(burlap.oomdp.core.states.State)}.
 * The number of episodes used for planning can be determined
 * by a threshold maximum number of episodes, or by a maximum change in the Q-function threshold.
 * <br/><br/>
 * By default, this agent will use an epsilon-greedy policy with epsilon=0.1. You can change the learning policy to
 * anything with the {@link #setLearningPolicy(burlap.behavior.policy.Policy)} policy.
 * <br/><br/>
 * If you
 * want to use a custom learning rate decay schedule rather than a constant learning rate, use the
 * {@link #setLearningRateFunction(burlap.behavior.learningrate.LearningRate)}.
 * <br/><br/>
 * 
 * <p/>
 * 1. Watkins, Christopher JCH, and Peter Dayan. "Q-learning." Machine learning 8.3-4 (1992): 279-292. <br/>
 * 2. Sutton, Richard S., Doina Precup, and Satinder Singh. "Between MDPs and semi-MDPs: A framework for temporal abstraction in reinforcement learning." Artificial intelligence 112.1 (1999): 181-211.
 * 
 * @author James MacGlashan
 *
 */
public class GoldMineSAAlgorithmQLearning extends QLearning {

	protected long qTableSize=0;
	
	/**
	 * Initializes the same Q-value initialization everywhere. Note that if the provided policy is derived from the Q-value of this learning agent (as it should be),
	 * you may need to set the policy to point to this object after call this constructor; the constructor will not do this automatically in case it was by design
	 * to use the policy that was learned in some other domain. By default the agent will only save the last learning episode and a call to the {@link #planFromState(State)} method
	 * will cause the valueFunction to use only one episode for planning; this should probably be changed to a much larger value if you plan on using this
	 * algorithm as a planning algorithm.
	 * @param domain the domain in which to learn
	 * @param gamma the discount factor
	 * @param hashingFactory the state hashing factory to use for Q-lookups
	 * @param qInit the initial Q-value to user everywhere
	 * @param learningRate the learning rate
	 * @param learningPolicy the learning policy to follow during a learning episode.
	 * @param maxEpisodeSize the maximum number of steps the agent will take in a learning episode for the agent stops trying.
	 */
	public GoldMineSAAlgorithmQLearning(Domain domain, double gamma, HashableStateFactory hashingFactory,
			double qInit, double learningRate, Policy learningPolicy, int maxEpisodeSize) {
		super(domain, gamma, hashingFactory, qInit, learningRate, learningPolicy, maxEpisodeSize);
	}

	public long getQTableSize() {
		return this.qTableSize;
	}
	
	
	/**
	 * Returns the {@link QLearningStateNode} object stored for the given hashed state. If no {@link QLearningStateNode} object.
	 * is stored, then it is created and has its Q-value initialize using this objects {@link burlap.behavior.valuefunction.ValueFunctionInitialization} data member.
	 * @param s the hashed state for which to get the {@link QLearningStateNode} object
	 * @return the {@link QLearningStateNode} object stored for the given hashed state. If no {@link QLearningStateNode} object.
	 */
	@Override
	protected QLearningStateNode getStateNode(HashableState s){
		//System.out.println("getStateNode");
		QLearningStateNode node = qIndex.get(s);
		if(node == null){
			node = new QLearningStateNode(s);
			List<GroundedAction> gas = this.getAllGroundedActions(s.s);
			if(gas.size() == 0){
				gas = this.getAllGroundedActions(s.s);
				throw new RuntimeErrorException(new Error("No possible actions in this state, cannot continue Q-learning"));
			}
			for(GroundedAction ga : gas){
				if(ga.applicableInState(s.s)){
					node.addQValue(ga, qInitFunction.qValue(s.s, ga));
					this.qTableSize++;
				}
			}
			qIndex.put(s, node);
		}
		return node;
	}
	
	@Override
	public void resetSolver(){
		super.resetSolver();
		this.qTableSize = 0;
	}
}
