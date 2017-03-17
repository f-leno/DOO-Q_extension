/**
 * 
 */
package experiment;

import java.util.List;
import java.util.Random;

import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.State;
import burlap.oomdp.stochasticgames.SGAgent;
import burlap.oomdp.stochasticgames.common.ConstantSGStateGenerator;
import domain.GoldMineConstants;

/**
 * @author Felipe Leno da Silva
 * A generator of random states to use in experiments.
 * The wall positions are fixed, and all states are generated with the same number of miners and gold pieces
 *
 */
public class GoldMineConstantStateGenerator extends ConstantSGStateGenerator {



	protected State sampleState;
	protected int sizeX;
	protected int sizeY;
	protected Random rnd;


	/**
	 * Default constructor that takes as parameter a sample state of this domain.
	 * @param sampleState A sample state of this domain, this state is used to copy wall objects and define the number of
	 * miners and gold pieces.
	 * @param rand Random object which can be used to ensure experiment reproducibility.
	 * @param sizeX grid world size in horizontal direction
	 * @param sizeY grid world size in vertical direction
	 */
	public GoldMineConstantStateGenerator(State sampleState, Random rand, int sizeX, int sizeY) {
		super(sortSampleState(sampleState,rand,sizeX,sizeY));
		
		this.rnd = rand;
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
		List<ObjectInstance> golds = newState.getObjectsOfClass(GoldMineConstants.CLS_GOLD);

		for(ObjectInstance gold : golds){
			gold.setValue(GoldMineConstants.ATT_X, getXValue(rand,sizeX));
			gold.setValue(GoldMineConstants.ATT_Y, getYValue(rand,sizeY));
		}
		
		//-----------------------------------------------------
		// Sort Miner Positions
		//-----------------------------------------------------
		List<ObjectInstance> miners = newState.getObjectsOfClass(GoldMineConstants.CLS_AGENT);

		for(int i=0; i<miners.size(); i++){
			miners.get(i).setValue(GoldMineConstants.ATT_X, getXValue(rand,sizeX));
			miners.get(i).setValue(GoldMineConstants.ATT_Y, getYValue(rand,sizeY));
		}
		
		
		return newState;
	}

	/**
	 * Set a given seed to ensure that the next states can be repeated.
	 * @param seed seed
	 */
	public void lockSeed(long seed){
		Random rand = new Random(seed);
		this.srcState = sortSampleState(this.sampleState, rand, sizeX, sizeY);
		
		this.rnd = rand;
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
	
	/**
	 * Sorts the next initial state
	 */
	public State generateState(List<SGAgent> agents) {
	
		State stateReturn = super.generateState(agents);
		sortNewPositions(stateReturn);
		
		return stateReturn;
		
		
	}

	/**
	 * Change the position of the objects
	 * @param stateReturn base state
	 */
	private void sortNewPositions(State stateReturn) {
		//------------------------------------------
		// Sort gold positions
		//---------------------------------------------
		List<ObjectInstance> golds = stateReturn.getObjectsOfClass(GoldMineConstants.CLS_GOLD);
		boolean[] changed = new boolean[golds.size()];
		
		for(int i=0;i<changed.length;i++){
			changed[i] = false;
		}
		
		for(int i=0;i<golds.size();i++){
			while(!changed[i]){
				int changeIndex = this.rnd.nextInt(golds.size());
				if(changeIndex!=i){
					changed[i]=true; changed[changeIndex]=true;
					
					ObjectInstance gold1 = golds.get(i);
					ObjectInstance gold2 = golds.get(changeIndex);
					int auxX = gold1.getIntValForAttribute(GoldMineConstants.ATT_X);
					int auxY = gold1.getIntValForAttribute(GoldMineConstants.ATT_Y);
					gold1.setValue(GoldMineConstants.ATT_X, gold2.getIntValForAttribute(GoldMineConstants.ATT_X));
					gold1.setValue(GoldMineConstants.ATT_Y, gold2.getIntValForAttribute(GoldMineConstants.ATT_Y));
					gold2.setValue(GoldMineConstants.ATT_X, auxX);
					gold2.setValue(GoldMineConstants.ATT_Y, auxY);
				}
				
			}
			
		}
		//-----------------------------------------------------
		// Sort Miner Positions
		//-----------------------------------------------------
		List<ObjectInstance> miners = stateReturn.getObjectsOfClass(GoldMineConstants.CLS_AGENT);
		changed = new boolean[miners.size()];
		
		for(int i=0;i<changed.length;i++){
			changed[i] = false;
		}

		for(int i=0;i<miners.size();i++){
			while(!changed[i]){
				int changeIndex = this.rnd.nextInt(miners.size());
				if(changeIndex!=i){
					changed[i]=true; changed[changeIndex]=true;
					
					ObjectInstance miner1 = miners.get(i);
					ObjectInstance miner2 = miners.get(changeIndex);
					int auxX = miner1.getIntValForAttribute(GoldMineConstants.ATT_X);
					int auxY = miner1.getIntValForAttribute(GoldMineConstants.ATT_Y);
					miner1.setValue(GoldMineConstants.ATT_X, miner2.getIntValForAttribute(GoldMineConstants.ATT_X));
					miner1.setValue(GoldMineConstants.ATT_Y, miner2.getIntValForAttribute(GoldMineConstants.ATT_Y));
					miner2.setValue(GoldMineConstants.ATT_X, auxX);
					miner2.setValue(GoldMineConstants.ATT_Y, auxY);
				}
				
			}
			
		}
		
	}
	
	

}


