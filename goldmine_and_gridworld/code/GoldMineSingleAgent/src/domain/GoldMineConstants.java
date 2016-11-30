/**
 * 
 */
package domain;

import java.util.ArrayList;
import java.util.List;

import burlap.oomdp.core.Domain;
import burlap.oomdp.core.GroundedProp;
import burlap.oomdp.core.states.State;

/**
 * Defines all constants to be used in other classes.
 * @author Felipe Leno da Silva
 *
 */
public class GoldMineConstants {

	//Constant to indicate a gold piece is not on the environment anymore
	public static final int OUTSIDE_GRID = -1;
	
	//Directions
	public static final String NORTH = "n";
	public static final String SOUTH = "s";
	public static final String EAST = "e";
	public static final String WEST = "w";


	//Attributes
	public static final String ATT_X = "x";
	public static final String ATT_Y = "y";
	public static final String ATT_POSITION = "pos";
	public static final String[] ATT_POSITION_VALUES = {NORTH,SOUTH,EAST,WEST};
	
	//Classes
	public static final String CLS_AGENT = "miner";
	public static final String CLS_GOLD = "gold";
	public static final String CLS_WALL = "wall";
	
	
	//Actions
	public static final String ACTION_NORTH = "moveNorth";
	public static final String ACTION_SOUTH = "moveSouth";
	public static final String ACTION_EAST = "moveEast";
	public static final String ACTION_WEST = "moveWest";
	public static final String ACTION_NOOP = "noop";
	public static final String ACTION_GETGOLD = "getGold";
	
	//Propositional Functions
	public static final String PF_ALL_GOLD = "noGold";
	public static final String PF_TOUCH_NORTH = "touchN";
	public static final String PF_TOUCH_SOUTH = "touchS";
	public static final String PF_TOUCH_WEST = "touchW";
	public static final String PF_TOUCH_EAST = "touchE";

	//Default grid size
	public static final int DEFAULT_SIZE_X = 5;
	public static final int DEFAULT_SIZE_Y = 5;

	//Reward constants
	public static final float REWARD_GOLD_DEFAULT = +100;
	public static final float REWARD_WALL_DEFAULT = 1.5f;
	public static final float REWARD_MINER_DEFAULT = 2.0f;
	public static final float REWARD_NOTHING_DEFAULT = +0;
	
	
	
	
	
	//Initialization of variable used in isMovementAction method.
	private static ArrayList<String> movementActions;
	
	static{
		movementActions = new ArrayList<String>();
		movementActions.add(ACTION_NORTH); movementActions.add(ACTION_SOUTH);
		movementActions.add(ACTION_WEST); movementActions.add(ACTION_EAST);	
	}
	/**
	 * This method checks if a given actionName is one of the four movement actions (north, south, west and east)
	 * @param actionName action identifier
	 * @return returns whether actionName is a movement action or no 
	 */
	public static boolean isMovementAction(String actionName) {
	
		return movementActions.contains(actionName);
	}
	/**
	 * Check if a given agent has a wall in a given direction defined by propFunction
	 * @param propFunction propositional function to define the direction where the wall can be
	 * @param agent the object name of the agent
	 * @return true if touch() is true
	 */
	public static boolean checkTouch(Domain domain, State s, String propFunction, String agent) {
		List<GroundedProp> props = domain.getPropFunction(propFunction).getAllGroundedPropsForState(s);
		
		for(GroundedProp p : props){
			if(p.params[0].equals(agent) && p.isTrue(s))
				return true;
		}
		return false;
		
	}

	
	



}
