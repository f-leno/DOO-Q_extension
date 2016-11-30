/**
 * 
 */
package goldMineSA;

import java.util.List;
import java.util.Random;

import burlap.datastructures.HashedAggregator;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.State;
import burlap.oomdp.stochasticgames.SGAgent;
import burlap.oomdp.stochasticgames.SGStateGenerator;
import burlap.oomdp.stochasticgames.common.ConstantSGStateGenerator;
import domain.GoldMineConstants;

/**
 * @author Ruben Glatt
 * A generator of random states to use in experiments.
 * The wall positions are fixed, and all states are generated with the same number of miners and gold pieces
 *
 */
public class GoldMineSAStateGenerator extends ConstantSGStateGenerator {


	protected State sampleState;
	protected int sizeX;
	protected int sizeY;


	/**
	 * Default constructor that takes as parameter a sample state of this domain.
	 * @param sampleState A sample state of this domain, this state is used to copy wall objects and define the number of
	 * miners and gold pieces.
	 * @param rand Random object which can be used to ensure experiment reproducibility.
	 * @param sizeX grid world size in horizontal direction
	 * @param sizeY grid world size in vertical direction
	 */
	public GoldMineSAStateGenerator(State sampleState, Random rand, int sizeX, int sizeY) {
		super(sortSampleState(sampleState,rand,sizeX,sizeY));

		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sampleState = sampleState;
	}

	/**
	 * Generate a random state
	 * @param sampleState A sample state to identify the object number
	 * @param rand Random class object
	 * @param sizeX size in horizontal direction
	 * @param sizeY size in vertical direction
	 */
	public static State sortSampleState(State sampleState,Random rand,int sizeX,int sizeY) {
		
		//All walls are generated as in the original state
		State newState = sampleState.copy();
		
		//------------------------------------------
		// Sort gold positions
		//---------------------------------------------
		List<ObjectInstance> golds = newState.getObjectsOfClass(GoldMineSAConstants.CLS_GOLD);

		for(ObjectInstance gold : golds){
			gold.setValue(GoldMineSAConstants.ATT_X, getXValue(rand,sizeX));
			gold.setValue(GoldMineSAConstants.ATT_Y, getYValue(rand,sizeY));
		}
		
		//-----------------------------------------------------
		// Sort Miner Positions
		//-----------------------------------------------------
		List<ObjectInstance> miners = newState.getObjectsOfClass(GoldMineSAConstants.CLS_MINER);

		for(int i=0; i<miners.size(); i++){
			miners.get(i).setValue(GoldMineSAConstants.ATT_X, getXValue(rand,sizeX));
			miners.get(i).setValue(GoldMineSAConstants.ATT_Y, getYValue(rand,sizeY));
		}
		
		return newState;
	}

	/**
	 * Set a given seed to ensure that the next states can be repeated.
	 * @param seed seed
	 */
	public void lockSeed(long seed){
		this.srcState = sortSampleState(this.sampleState, new Random(seed), sizeX, sizeY);
	}

	/**
	 * Get a random value for X attribute
	 * @return a int value with valid x position
	 */
	private static int getXValue(Random rand,int sizeX) {
		return rand.nextInt(sizeX);
	}
	/**
	 * Get a random value for Y attribute
	 * @return a int value with valid Y position
	 */
	private static int getYValue(Random rand, int sizeY) {
		return rand.nextInt(sizeY);
	}
	
	public State generateState(List<SGAgent> agents) {
		
		State s = this.srcState.copy();
		HashedAggregator<String> counts = new HashedAggregator<String>();
		
		for(SGAgent a : agents){
			String agentClassName = a.getAgentType().oclass.name;
			int index = (int) counts.v(agentClassName);
			List<ObjectInstance> possibleAgentObjects = s.getObjectsOfClass(agentClassName);
			if(possibleAgentObjects.size() <= index){
				throw new RuntimeException("Error: Constant state used by ConstanteStateSGGenerator does not have enough oo-mdp objects for agents defined by class: " + agentClassName);
			}
			ObjectInstance agentObject = possibleAgentObjects.get(index);
			s.renameObject(agentObject, a.getAgentName());
			
			counts.add(agentClassName, 1.);
			
		}
		
		return s;
		
	}

}


