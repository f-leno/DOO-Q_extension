/**
 * 
 */
package goldMineSA;

import burlap.oomdp.core.Domain;
import burlap.oomdp.core.PropositionalFunction;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.State;

/**
 * Propositional function showing if any miner is affected by walls
 * @author Ruben Glatt
 *
 */
public class GoldMineSAPropFuncWallCollision extends PropositionalFunction {



	String direction;

	public GoldMineSAPropFuncWallCollision(String name, Domain domain, String[] parameterClasses,String direction) {
		super(name, domain, parameterClasses);
		this.direction = direction;
	}


	/* (non-Javadoc)
	 * @see burlap.oomdp.core.PropositionalFunction#isTrue(burlap.oomdp.core.states.State, java.lang.String[])
	 */
	@Override
	public boolean isTrue(State s, String[] params) {
		//System.out.print(this.getName()+": "+ params[0] +" "+ params[1]+"|");
		ObjectInstance miner = s.getObject(params[0]);
		ObjectInstance wall = s.getObject(params[1]);


		int ax = miner.getIntValForAttribute(GoldMineSAConstants.ATT_X);
		int ay = miner.getIntValForAttribute(GoldMineSAConstants.ATT_Y);

		int wx = wall.getIntValForAttribute(GoldMineSAConstants.ATT_X);
		int wy = wall.getIntValForAttribute(GoldMineSAConstants.ATT_Y);
		String wallDir = wall.getStringValForAttribute(GoldMineSAConstants.ATT_POSITION);

		if(ax==wx && ay==wy)
			if(wallDir.equals(this.direction))
				return true;
		
		// TODO WHY????
		//For example check if there is a wall on the above position when verifying (northWall)
		String otherDirection=null;
		int deltaX=0;
		int deltaY=0;

		switch(this.direction){
		case GoldMineSAConstants.NORTH:
			otherDirection = GoldMineSAConstants.SOUTH;
			deltaY = +1;
			break;
		case GoldMineSAConstants.SOUTH:
			otherDirection = GoldMineSAConstants.NORTH;
			deltaY = -1;
			break;
		case GoldMineSAConstants.EAST:
			otherDirection = GoldMineSAConstants.WEST;
			deltaX = +1;
			break;
		case GoldMineSAConstants.WEST:
			otherDirection = GoldMineSAConstants.EAST;
			deltaX = -1;
			break;
		}
		
		if(ax+deltaX == wx && ay+deltaY == wy && otherDirection.equals(wallDir))
			return true;
		
		
		return false;


	}

}
