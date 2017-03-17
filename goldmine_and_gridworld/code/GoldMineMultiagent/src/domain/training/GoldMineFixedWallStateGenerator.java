/**
 * 
 */
package domain.training;

import java.util.List;
import java.util.Random;

import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.State;
import burlap.oomdp.stochasticgames.SGAgent;
import burlap.oomdp.stochasticgames.SGStateGenerator;
import domain.GoldMineConstants;

/**
 * @author Felipe Leno da Silva
 * A generator of random states to use in experiments.
 * The wall positions are fixed, and all states are generated with the same number of miners and gold pieces
 *
 */
public class GoldMineFixedWallStateGenerator extends SGStateGenerator {

	protected Random rand;
	protected State sampleState;
	protected int numMiners;
	protected int numGold;
	protected int sizeX;
	protected int sizeY;
	protected Random savedRand;
	
	

	/**
	 * Default constructor that takes as parameter a sample state of this domain.
	 * @param sampleState A sample state of this domain, this state is used to copy wall objects and define the number of
	 * miners and gold pieces.
	 * @param rand Random object which can be used to ensure experiment reproducibility.
	 * @param sizeX grid world size in horizontal direction
	 * @param sizeY grid world size in vertical direction
	 */
	public GoldMineFixedWallStateGenerator(State sampleState, Random rand, int sizeX, int sizeY) {
		this.sampleState = sampleState;
		this.rand = rand;		 		 
		this.numMiners = sampleState.getObjectsOfClass(GoldMineConstants.CLS_AGENT).size();
		this.numGold = sampleState.getObjectsOfClass(GoldMineConstants.CLS_GOLD).size();
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}

	/**
	 * Generate a state as specified by the sampleState
	 * @param agents agents to be assigned to miner objects
	 * @return the random state
	 */
	@Override
	public State generateState(List<SGAgent> agents) {
		
		//All walls are generated as in the original state
		State newState = this.sampleState.copy();

		//Wrong number of agents
		if(agents.size()>this.numMiners){
			throw new RuntimeException("The number of requested agents is greater than "
					+ "the number of miners in the environment. Miners: "+this.numMiners+" - Agents: "+ agents.size());
		}

		sortMinerPositions(newState,agents);
		sortGoldPositions(newState);

		return newState;
	}
	/**
	 * Sort new positions to all gold pieces
	 * @param newState state to be changed
	 */
	protected void sortGoldPositions(State newState) {
		List<ObjectInstance> golds = newState.getObjectsOfClass(GoldMineConstants.CLS_GOLD);

		for(ObjectInstance gold : golds){
			gold.setValue(GoldMineConstants.ATT_X, getXValue());
			gold.setValue(GoldMineConstants.ATT_Y, getYValue());
		}

	}
	/**
	 * Sort new positions for all Miner objects and link them with agents
	 * @param newState state
	 * @param agents agents
	 */
	protected void sortMinerPositions(State newState, List<SGAgent> agents) {
		List<ObjectInstance> miners = newState.getObjectsOfClass(GoldMineConstants.CLS_AGENT);

		for(int i=0; i<agents.size(); i++){
			//Agent is linked by the name with a miner instance
			newState.renameObject(miners.get(i), agents.get(i).getAgentName());
			miners.get(i).setValue(GoldMineConstants.ATT_X, getXValue());
			miners.get(i).setValue(GoldMineConstants.ATT_Y, getYValue());
		}

	}
	/**
	 * Set a given seed to ensure that the next states can be repeated.
	 * @param seed seed
	 */
	public void lockSeed(long seed){
		this.savedRand = this.rand;
		this.rand = new Random(seed);
	}
	/**
	 * Restore the seed previously saved with lockSeed. If this method
	 * is used without previous use of lockSeed, a RuntimeException is thrown
	 */
	public void restoreSeed(){
		if(this.savedRand==null){
			throw new RuntimeException("restoreSeed() method used before previous use of lockSeed()");
		}
		this.rand = this.savedRand;
		this.savedRand = null;
	}
	/**
	 * Get a random value for X attribute
	 * @return a int value with valid x position
	 */
	private int getXValue() {
		return rand.nextInt(this.sizeX);
	}
	/**
	 * Get a random value for Y attribute
	 * @return a int value with valid Y position
	 */
	private int getYValue() {
		return rand.nextInt(this.sizeY);
	}

}
