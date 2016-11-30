/**
 * 
 */
package goldMineSA;

import java.util.List;

import burlap.oomdp.core.Domain;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.stochasticgames.JointAction;

/**
 * GoldMine Reward Function:
 *  
 * 
 * @author Ruben Glatt
 */
public class GoldMineSAFuncReward implements RewardFunction {

	protected float    goldValue          = GoldMineSAConstants.REWARD_GOLD_DEFAULT;
	protected float    wallMultiplier     = GoldMineSAConstants.REWARD_WALL_DEFAULT;
	protected float    minerMultiplier    = GoldMineSAConstants.REWARD_MINER_DEFAULT;
	protected float    noReward           = GoldMineSAConstants.REWARD_NOTHING_DEFAULT;
	
	protected Domain   domain;
	protected double   gamma;
	
	/**
	 * Create a GoldMineJointReward with specified parameters
	 * @param domain domain
	 * @param gamma discount rate
	 * @param goldValue value for gold piece collection
	 * @param wallMultiplier multiplier for wall collision
	 * @param minerMultiplier multiplier for agent collision
	 */
	public GoldMineSAFuncReward(Domain domain, double gamma){
		this.domain = domain;
		this.gamma = gamma;
	}
	
	@Override
	public double reward(State s, GroundedAction a, State sPrime) {
		
		int numberGold            = 0;
		int numberWallCollisions  = 0;  // in this case always stays 0
		int numberAgentCollisions = countMinerCollisions(sPrime);
		
		if(a.actionName().equals(GoldMineSAConstants.ACTION_GOLD_COLLECT)){
			numberGold = 1;
		}

		double reward = (this.goldValue * numberGold) 
				      *  Math.pow(this.gamma, this.minerMultiplier * numberAgentCollisions + this.wallMultiplier * numberWallCollisions);
		return reward;		
	}
	
	/**
	 * Returns the number of collisions between agents in the resulting state sPrime
	 * @param sPrime the resulting state from taking the joint action
	 * @return number of wall collisions in this time step
	 */
	protected int countMinerCollisions(State sPrime) {
		int numMinerCollision = 0;
		List<ObjectInstance> miners = sPrime.getObjectsOfClass(GoldMineSAConstants.CLS_MINER);
		int x1,x2,y1,y2;
		//Check all combination of agents to seek for overlapping agents
		for(int i=0; i < miners.size()-1;i++){
			for(int j=i+1; j<miners.size(); j++){
				x1 = miners.get(i).getIntValForAttribute(GoldMineSAConstants.ATT_X);
				y1 = miners.get(i).getIntValForAttribute(GoldMineSAConstants.ATT_Y);
				x2 = miners.get(j).getIntValForAttribute(GoldMineSAConstants.ATT_X);
				y2 = miners.get(j).getIntValForAttribute(GoldMineSAConstants.ATT_Y);
				//Check for overlapping
				if(x1 == x2 && y1 == y2){
					numMinerCollision++;
				}
			}
		}
		return numMinerCollision;
	}
	
}
