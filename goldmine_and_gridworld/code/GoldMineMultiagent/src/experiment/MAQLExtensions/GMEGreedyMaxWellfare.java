package experiment.MAQLExtensions;

import java.util.Random;

import burlap.behavior.stochasticgames.agents.maql.MultiAgentQLearning;
import burlap.behavior.stochasticgames.madynamicprogramming.policies.EGreedyMaxWellfare;

/**
 * 
 * @author Felipe Leno da Silva
 * Small modification of EGreedyMaxWellfare to allow experiment reproducibility
 */
public class GMEGreedyMaxWellfare extends EGreedyMaxWellfare {
	public GMEGreedyMaxWellfare(double epsilon) {
		super(epsilon);
	}

	public GMEGreedyMaxWellfare(double epsilon, boolean breakTiesRandomly) {
		super(epsilon, breakTiesRandomly);
	}

	public GMEGreedyMaxWellfare(MultiAgentQLearning actingAgent, double epsilon, boolean breakTiesRandomly) {
		super(actingAgent, epsilon, breakTiesRandomly);
	}

	public GMEGreedyMaxWellfare(MultiAgentQLearning actingAgent, double epsilon) {
		super(actingAgent, epsilon);
	}

	/**
	 * Set Random variable
	 * @param rand random
	 */
	public void setRandom(Random rand){
		this.rand = rand;
	}
	
	/**
	 *  set the Epsilon
	 * @param epsilon the epsilon
	 */
	public void setEpsilon(double epsilon){
		this.epsilon = epsilon;
	}

}
