/**
 * 
 */
package distributedQLearning;

import java.util.Random;

import burlap.oomdp.stochasticgames.SGAgentType;
import burlap.oomdp.stochasticgames.SGDomain;
import burlap.oomdp.stochasticgames.World;
import goldmine.DOOQLearning;
import goldmine.DOOQPolicy;

/**
 * This class was created to allow executing Distributed Q-Learning in experiments.
 * DistributedQLearning is a subclass of DOOQLearning that properly iniciates all parameters to execute the regular Distributed Q-Learning 
 * without abstraction
 * @author Felipe Leno da Silva
 *
 */
public class DistributedQLearning extends DOOQLearning {
	


	/**
	 * Parameters used in our experiment.
	 */
	public DistributedQLearning(SGDomain domain, double initialQValue, double gamma,double epsilon, Random randAgent){
		super(domain, initialQValue,gamma,epsilon,randAgent);	
		
		
	}
	
	/**
	 * Automatically chooses a proper hashable state factory
	 */
	public void joinWorld(World w, SGAgentType as) {
	
		super.joinWorld(w, as);
		this.hashFactory = new DistributedQLearningHashableFactory();	
		
		//Update other classes
		if(this.learningPolicy instanceof DOOQPolicy){
			((DOOQPolicy)this.learningPolicy).setHashableStateFactory(this.hashFactory);
			this.qTable.setHashableStateFactory(this.hashFactory);
		}
	}
	
	
}
