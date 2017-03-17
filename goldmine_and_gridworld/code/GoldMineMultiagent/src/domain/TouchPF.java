/**
 * 
 */
package domain;

import burlap.oomdp.core.Domain;
import burlap.oomdp.core.PropositionalFunction;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.State;

/**
 * @author Felipe Leno da Silva
 * Propositional function for "touch" relations. Can identify if an agent is adjacent to a wall.
 */
public class TouchPF extends PropositionalFunction {

	protected String direction;
	
	protected static String[] parameters = {GoldMineConstants.CLS_AGENT,GoldMineConstants.CLS_WALL};
	
	/**
	 * Constructor for a "touch" Propositional function.
	 * @param domain domain
	 * @param name name for the PF
	 * @param direction direction for the touch relation. Example: GoldMineConstants.NORTH for touchN relation.
	 */
	public TouchPF(Domain domain,String name, String direction) {
		super(name, domain, parameters);
		this.direction = direction;
	}

	/* (non-Javadoc)
	 * @see burlap.oomdp.core.PropositionalFunction#isTrue(burlap.oomdp.core.states.State, java.lang.String[])
	 */
	@Override
	public boolean isTrue(State s, String[] params) {
		ObjectInstance miner = s.getObject(params[0]);
		ObjectInstance wall = s.getObject(params[1]);


		int ax = miner.getIntValForAttribute(GoldMineConstants.ATT_X);
		int ay = miner.getIntValForAttribute(GoldMineConstants.ATT_Y);

		int wx = wall.getIntValForAttribute(GoldMineConstants.ATT_X);
		int wy = wall.getIntValForAttribute(GoldMineConstants.ATT_Y);
		String wallDir = wall.getStringValForAttribute(GoldMineConstants.ATT_POSITION);

		//First check if the wall is in the same position as the miner. If so, the proposition is true if the wall is in the same
		//direction as the touch relation
		if(ax==wx && ay==wy)
			if(wallDir.equals(this.direction))
				return true;
			else
				return false;

		//In case the wall is not in the same position, check if the wall is in another position but prevent miner movement
		String testWallDirection=null;
		int deltaX=0;
		int deltaY=0;

		switch(this.direction){
		case GoldMineConstants.NORTH:
			testWallDirection = GoldMineConstants.SOUTH;
			deltaY = +1;
			break;
		case GoldMineConstants.SOUTH:
			testWallDirection = GoldMineConstants.NORTH;
			deltaY = -1;
			break;
		case GoldMineConstants.EAST:
			testWallDirection = GoldMineConstants.WEST;
			deltaX = +1;
			break;
		case GoldMineConstants.WEST:
			testWallDirection = GoldMineConstants.EAST;
			deltaX = -1;
			break;

		}
		
		//
		if(ax+deltaX == wx && ay+deltaY == wy && testWallDirection.equals(wallDir))
			return true;
		
		return false;
	}

}
