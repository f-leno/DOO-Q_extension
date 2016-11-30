/**
 * 
 */
package goldMineSA;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import burlap.oomdp.auxiliary.common.SinglePFTF;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.GroundedProp;
import burlap.oomdp.core.TerminalFunction;
import burlap.oomdp.core.objects.MutableObjectInstance;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.MutableState;
import burlap.oomdp.core.states.State;

/**
 * Defines all constants to be used in other classes.
 * @author Ruben Glatt
 *
 */
public class GoldMineSAConstants {

	// Object is not on grid anymore
	public static final int      OUTSIDE_GRID            = -1;
	// Directions
	public static final String   NORTH                   = "n";
	public static final String   SOUTH                   = "s";
	public static final String   EAST                    = "e";
	public static final String   WEST                    = "w";
	// Attributes
	public static final String   ATT_X                   = "x";
	public static final String   ATT_Y                   = "y";
	//public static final String   ATT_ACTIVE              = "active";
	public static final String   ATT_POSITION            = "pos";
	public static final String[] ATT_POSITION_VALUES     = {NORTH,SOUTH,EAST,WEST};
	// Classes
	public static final String   CLS_MINER               = "miner";
	public static final String   CLS_GOLD                = "gold";
	public static final String   CLS_WALL                = "wall";
	// Propositional Function names
	public static final String   PF_GOLD_COLLECTED       = "goldCollected";
	public static final String   PF_WALL_NORTH           = "wallN";
	public static final String   PF_WALL_SOUTH           = "wallS";
	public static final String   PF_WALL_WEST            = "wallW";
	public static final String   PF_WALL_EAST            = "wallE";
	// Default grid size
	public static final int      DEFAULT_SIZE_X          = 5;
	public static final int      DEFAULT_SIZE_Y          = 5;
	// Reward constants
	public static final float    REWARD_GOLD_DEFAULT     = +100f;
	public static final float    REWARD_WALL_DEFAULT     = 1.5f;
	public static final float    REWARD_MINER_DEFAULT    = 2.0f;
	public static final float    REWARD_NOTHING_DEFAULT  = +0f;
	// Default learning parameter
	public static final float    DEFAULT_ALPHA           = 0.2f;
	public static final float    DEFAULT_GAMMA           = 0.9f;
	public static final float    DEFAULT_Q               = 0f;
	
	// Actions
	public static final String   ACTION_GOLD_COLLECT     = "goldCollect";
	
	
	/**
	 * Returns a terminal function that ends the episode when all gold is collected
	 * @param domain domain description
	 * @return terminal function
	 */
	public static TerminalFunction getStandardTerminalFunction(Domain domain){
		return new SinglePFTF(domain.getPropFunction(GoldMineSAConstants.PF_GOLD_COLLECTED), true);		
	}
	
	
	/**
	 * Create a state where all objects dont have their attribute values set (except walls)
	 * @param domain domain
	 * @param numberMiners number of Miner objects
	 * @param numberGolds number of Gold objects
	 * @param storeWall Wall objects
	 * @return state
	 */
	public static State generateEmptyState(Domain domain, int numberMiners, int numberGolds,
			List<ObjectInstance> storeWall) {
		State s = new MutableState();

		for(int i=1;i<=numberMiners;i++){
			ObjectInstance miner = new MutableObjectInstance(domain.getObjectClass(GoldMineSAConstants.CLS_MINER), "miner"+i);
			s.addObject(miner);
		}

		for(int i=1;i<=numberGolds;i++){
			ObjectInstance gold = new MutableObjectInstance(domain.getObjectClass(GoldMineSAConstants.CLS_MINER), "gold"+i);
			s.addObject(gold);
		}	
		s.addAllObjects(storeWall);
		return s;
	}
	
	
	/**
	 * Returns the standard starting state for the experiments
	 * @param domain domain for which a state is created
	 * @return the example state
	 */
	public static State setStartingState(Domain domain){
		// Initialize a new empty state
		State s = new MutableState();
		
		// Generate agent objects and set their location
		ObjectInstance miner1 = new MutableObjectInstance(domain.getObjectClass(GoldMineSAConstants.CLS_MINER), "miner1");
		miner1.setValue(GoldMineSAConstants.ATT_X, 0);
		miner1.setValue(GoldMineSAConstants.ATT_Y, 1);
		
		ObjectInstance miner2 = new MutableObjectInstance(domain.getObjectClass(GoldMineSAConstants.CLS_MINER), "miner2");
		miner2.setValue(GoldMineSAConstants.ATT_X, 1);
		miner2.setValue(GoldMineSAConstants.ATT_Y, 3);
		
		ObjectInstance miner3 = new MutableObjectInstance(domain.getObjectClass(GoldMineSAConstants.CLS_MINER), "miner3");
		miner3.setValue(GoldMineSAConstants.ATT_X, 4);
		miner3.setValue(GoldMineSAConstants.ATT_Y, 1);
		
		// Generate gold objects and set their location		
		ObjectInstance gold1 = new MutableObjectInstance(
				domain.getObjectClass(GoldMineSAConstants.CLS_GOLD), 
				"gold1");
		gold1.setValue(GoldMineSAConstants.ATT_X, 1);
		gold1.setValue(GoldMineSAConstants.ATT_Y, 0);
		//gold1.setValue(GoldMineSAConstants.ATT_ACTIVE, true);
		
		ObjectInstance gold2 = new MutableObjectInstance(
				domain.getObjectClass(GoldMineSAConstants.CLS_GOLD), 
				"gold2");
		gold2.setValue(GoldMineSAConstants.ATT_X, 1);
		gold2.setValue(GoldMineSAConstants.ATT_Y, 4);
		//gold2.setValue(GoldMineSAConstants.ATT_ACTIVE, true);
		
		ObjectInstance gold3 = new MutableObjectInstance(
				domain.getObjectClass(GoldMineSAConstants.CLS_GOLD), 
				"gold3");
		gold3.setValue(GoldMineSAConstants.ATT_X, 2);
		gold3.setValue(GoldMineSAConstants.ATT_Y, 0);
		//gold3.setValue(GoldMineSAConstants.ATT_ACTIVE, true);
		
		ObjectInstance gold4 = new MutableObjectInstance(
				domain.getObjectClass(GoldMineSAConstants.CLS_GOLD), 
				"gold4");
		gold4.setValue(GoldMineSAConstants.ATT_X, 2);
		gold4.setValue(GoldMineSAConstants.ATT_Y, 2);
		//gold4.setValue(GoldMineSAConstants.ATT_ACTIVE, true);
		
		ObjectInstance gold5 = new MutableObjectInstance(
				domain.getObjectClass(GoldMineSAConstants.CLS_GOLD), 
				"gold5");
		gold5.setValue(GoldMineSAConstants.ATT_X, 3);
		gold5.setValue(GoldMineSAConstants.ATT_Y, 3);
		//gold5.setValue(GoldMineSAConstants.ATT_ACTIVE, true);
		
		ObjectInstance gold6 = new MutableObjectInstance(
				domain.getObjectClass(GoldMineSAConstants.CLS_GOLD), 
				"gold6");
		gold6.setValue(GoldMineSAConstants.ATT_X, 4);
		gold6.setValue(GoldMineSAConstants.ATT_Y, 2);
		//gold6.setValue(GoldMineSAConstants.ATT_ACTIVE, true);
		
		// Generate wall objects and set their location and position
		ObjectInstance wall1 = new MutableObjectInstance(
				domain.getObjectClass(GoldMineSAConstants.CLS_WALL), 
				"wall1");
		wall1.setValue(GoldMineSAConstants.ATT_X, 1);
		wall1.setValue(GoldMineSAConstants.ATT_Y, 1);
		wall1.setValue(GoldMineSAConstants.ATT_POSITION, GoldMineSAConstants.SOUTH);
		
		ObjectInstance wall2 = new MutableObjectInstance(
				domain.getObjectClass(GoldMineSAConstants.CLS_WALL), 
				"wall2");
		wall2.setValue(GoldMineSAConstants.ATT_X, 1);
		wall2.setValue(GoldMineSAConstants.ATT_Y, 3);
		wall2.setValue(GoldMineSAConstants.ATT_POSITION, GoldMineSAConstants.NORTH);
		
		ObjectInstance wall3 = new MutableObjectInstance(
				domain.getObjectClass(GoldMineSAConstants.CLS_WALL), 
				"wall3");
		wall3.setValue(GoldMineSAConstants.ATT_X, 2);
		wall3.setValue(GoldMineSAConstants.ATT_Y, 3);
		wall3.setValue(GoldMineSAConstants.ATT_POSITION, GoldMineSAConstants.NORTH);
		
		ObjectInstance wall4 = new MutableObjectInstance(
				domain.getObjectClass(GoldMineSAConstants.CLS_WALL), 
				"wall4");
		wall4.setValue(GoldMineSAConstants.ATT_X, 3);
		wall4.setValue(GoldMineSAConstants.ATT_Y, 1);
		wall4.setValue(GoldMineSAConstants.ATT_POSITION, GoldMineSAConstants.NORTH);
		
		// Fill the empty state with all enclosing walls
		s = addEnclosingWalls(domain,s, GoldMineSAConstants.DEFAULT_SIZE_X, GoldMineSAConstants.DEFAULT_SIZE_X);
		
		// add the generated objects to the state
		s.addObject(miner1);
		s.addObject(miner2);
		s.addObject(miner3);
		s.addObject(gold1);
		s.addObject(gold2);
		s.addObject(gold3);
		s.addObject(gold4);
		s.addObject(gold5);
		s.addObject(gold6);
		s.addObject(wall1);
		s.addObject(wall2);
		s.addObject(wall3);
		s.addObject(wall4);
		
		// return the state
		return s;
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
	 * Add the enclosing walls
	 * @param domain domain description
	 * @param s state where the walls will be added
	 * @return modified state
	 */
	protected static State addEnclosingWalls(Domain domain, State s, int sizeX, int sizeY) {
		// initialize needed variables
		int xWall,yWall;
		String wallName;
		ObjectInstance wallObj;
		int num = 0;
		
		// generate lower and upper wall objects and add to state
		for(int i = 0; i < sizeX; i++){
			xWall = i;
			// lower wall for each column
			yWall = 0;
			wallName = "dWall"+num++;
			wallObj = new MutableObjectInstance(
					domain.getObjectClass(GoldMineSAConstants.CLS_WALL),wallName);
			wallObj.setValue(GoldMineSAConstants.ATT_X, xWall);
			wallObj.setValue(GoldMineSAConstants.ATT_Y, yWall);
			wallObj.setValue(GoldMineSAConstants.ATT_POSITION, GoldMineSAConstants.SOUTH);
			s.addObject(wallObj);
			
			// upper wall for each column
			yWall = sizeY-1;
			wallName = "dWall"+num++;		
			wallObj = new MutableObjectInstance(
					domain.getObjectClass(GoldMineSAConstants.CLS_WALL),wallName);
			wallObj.setValue(GoldMineSAConstants.ATT_X, xWall);
			wallObj.setValue(GoldMineSAConstants.ATT_Y, yWall);
			wallObj.setValue(GoldMineSAConstants.ATT_POSITION, GoldMineSAConstants.NORTH);
			s.addObject(wallObj);	
		}
		
		// generate left and right wall objects and add to state
		for(int i = 0; i < sizeY; i++){
			yWall = i;
			// left wall for each row
			xWall = 0;
			wallName = "dWall"+num++;
			wallObj = new MutableObjectInstance(
					domain.getObjectClass(GoldMineSAConstants.CLS_WALL),wallName);
			wallObj.setValue(GoldMineSAConstants.ATT_X, xWall);
			wallObj.setValue(GoldMineSAConstants.ATT_Y, yWall);
			wallObj.setValue(GoldMineSAConstants.ATT_POSITION, GoldMineSAConstants.WEST);
			s.addObject(wallObj);
			
			// right wall for each row
			xWall = sizeX-1;
			wallName = "dWall"+num++;
			wallObj = new MutableObjectInstance(
					domain.getObjectClass(GoldMineSAConstants.CLS_WALL),wallName);
			wallObj.setValue(GoldMineSAConstants.ATT_X, xWall);
			wallObj.setValue(GoldMineSAConstants.ATT_Y, yWall);
			wallObj.setValue(GoldMineSAConstants.ATT_POSITION, GoldMineSAConstants.EAST);
			s.addObject(wallObj);	
		}
		
		// return the new state
		return s;
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
	

}
