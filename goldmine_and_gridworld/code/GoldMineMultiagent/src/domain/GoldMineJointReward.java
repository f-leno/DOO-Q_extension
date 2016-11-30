/**
 * 
 */
package domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import burlap.oomdp.core.Domain;
import burlap.oomdp.core.GroundedProp;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.State;
import burlap.oomdp.stochasticgames.JointAction;
import burlap.oomdp.stochasticgames.JointReward;
import burlap.oomdp.stochasticgames.agentactions.GroundedSGAgentAction;

/**
 * @author Felipe Leno da Silva
 * 
 * A Joint Reward function to GoldMine domain. All agents receive the same reward, which is calculated according to the number of collisions 
 * and collected gold pieces. It is possible to specify values for each of these events through the constructor. The default values are:
 * Wall Collisions multiplier: 1.5
 * Miner Collisions multiplier: 2.0
 * Collected GOld Pieces: +100
 * reward when nothing occurs: +1  
 *
 */
public class GoldMineJointReward implements JointReward {

	protected float goldValue = GoldMineConstants.REWARD_GOLD_DEFAULT;
	protected float wallMultiplier = GoldMineConstants.REWARD_WALL_DEFAULT;
	protected float minerMultiplier = GoldMineConstants.REWARD_MINER_DEFAULT;
	protected float nothingValue = GoldMineConstants.REWARD_NOTHING_DEFAULT;
	
	protected Domain domain;
	protected double gamma;


	/**
	 * Create a GoldMineJointReward with specified parameters
	 * @param domain domain
	 * @param gamma discount rate
	 * @param goldValue value for gold piece collection
	 * @param wallMultiplier multiplier for wall collision
	 * @param minerMultiplier multiplier for agent collision
	 */
	public GoldMineJointReward(Domain domain,double gamma, int goldValue, float wallMultiplier, float minerMultiplier){
		this.goldValue = goldValue;
		this.wallMultiplier = wallMultiplier;
		this.minerMultiplier = minerMultiplier;
		this.domain = domain;
		this.gamma = gamma;
	}
	/**
	 * Creates an instance with the default values.
	 * @param domain domain
	 * @param gamma discount rate
	 */
	public GoldMineJointReward(Domain domain, double gamma){
		this.domain = domain;
		this.gamma = gamma;
	}
	/* (non-Javadoc)
	 * @see burlap.oomdp.stochasticgames.JointReward#reward(burlap.oomdp.core.states.State, burlap.oomdp.stochasticgames.JointAction, burlap.oomdp.core.states.State)
	 */
	@Override
	public Map<String, Double> reward(State s, JointAction ja, State sp) {
		int numberGold = countCollectedGold(s,ja,sp);
		int numberWallCollisions = countWallCollisions(s,ja,sp);
		int numberAgentCollisions = countAgentCollisions(s,ja,sp);

		//The reward s is equal for all agents (cooperative domain)
		double reward = 0;


		//If nothing happened, the reward is +1
		if(numberGold == 0 && numberAgentCollisions==0 && numberWallCollisions==0)
			reward = this.nothingValue;
		else{
			// (100*nGold) * gamma^(2.0 * n_colAg + 1.5 * n_colWall)
			reward = (this.goldValue * numberGold) *  Math.pow(this.gamma, 
				this.minerMultiplier * numberAgentCollisions + this.wallMultiplier * numberWallCollisions);

		}

		List<String> agentNames = ja.getAgentNames();

		Map<String, Double> agentRewards = new HashMap<String, Double>();
		//Set the same reward to all agents
		for(String agent : agentNames){
			agentRewards.put(agent, reward);
		}

		return agentRewards;

	}

	/**
	 * Returns the number of collisions between agents for the tuple <s,ja,sp>
	 * @param s  that state in which the joint action was taken.
	 * @param ja the joint action taken.
	 * @param sp the resulting state from taking the joint action
	 * @return number of wall collisions in this time step
	 */
	protected int countAgentCollisions(State s, JointAction ja, State sp) {
		int numAgentCollision = 0;
		
		List<ObjectInstance> agents = sp.getObjectsOfClass(GoldMineConstants.CLS_AGENT);
		
		int x1,x2,y1,y2;
		
		//Check all combination of agents to seek for overlapping agents
		for(int i=0; i < agents.size()-1;i++){
			for(int j=i+1; j<agents.size(); j++){
				x1 = agents.get(i).getIntValForAttribute(GoldMineConstants.ATT_X);
				y1 = agents.get(i).getIntValForAttribute(GoldMineConstants.ATT_Y);
				x2 = agents.get(j).getIntValForAttribute(GoldMineConstants.ATT_X);
				y2 = agents.get(j).getIntValForAttribute(GoldMineConstants.ATT_Y);
				
				//Check for overlapping
				if(x1 == x2 && y1 == y2){
					numAgentCollision++;
				}
			}
		}
		
		return numAgentCollision;

	}
	/**
	 * Returns the number of collisions with walls for the tuple <s,ja,sp>
	 * @param s  that state in which the joint action was taken.
	 * @param ja the joint action taken.
	 * @param sp the resulting state from taking the joint action
	 * @return number of wall collisions in this time step
	 */
	protected int countWallCollisions(State s, JointAction ja, State sp) {
		int numCollisions = 0;
		List<GroundedSGAgentAction> actions = ja.getActionList();

		for(GroundedSGAgentAction action: actions){
			//Check what was the action and the PF that lead to a collision
			List<GroundedProp> prop = null;
			boolean endIter = false;

			switch(action.actionName()){
			case GoldMineConstants.ACTION_NORTH:
				prop = this.domain.getPropFunction(GoldMineConstants.PF_TOUCH_NORTH).getAllGroundedPropsForState(s);			
				break;
			case GoldMineConstants.ACTION_SOUTH:
				prop = this.domain.getPropFunction(GoldMineConstants.PF_TOUCH_SOUTH).getAllGroundedPropsForState(s);				
				break;
			case GoldMineConstants.ACTION_EAST:
				prop = this.domain.getPropFunction(GoldMineConstants.PF_TOUCH_EAST).getAllGroundedPropsForState(s);
				break;
			case GoldMineConstants.ACTION_WEST:
				prop = this.domain.getPropFunction(GoldMineConstants.PF_TOUCH_WEST).getAllGroundedPropsForState(s);
				break;
			default:
				endIter = true;
				break;
			}
			//If the action is to move
			if(!endIter){
				//For example, if action = north and touchN is true for the acting agent
				boolean okAction = true;
				int nProp = 0;

				while(okAction && nProp < prop.size()){
					//If the proposition is true for the agent
					if(prop.get(nProp).params[0].equals(action.actingAgent) && prop.get(nProp).isTrue(s)){
						okAction = false;
					}
					nProp++;
				}

				//If the action results in a wall collision...
				if(!okAction){
					numCollisions++;
				}
			}


		}
		return numCollisions;

	}
	/**
	 * Returns the number of "GetGold" actions executed in this time step
	 * @param s  that state in which the joint action was taken.
	 * @param ja the joint action taken.
	 * @param sp the resulting state from taking the joint action
	 * @return number of collected gold pieces in this time step
	 */
	protected int countCollectedGold(State s, JointAction ja, State sp) {
		int numGetGold = 0;
		
		Map<String,Integer> collectedGold = new HashMap<String,Integer>();
		
		List<GroundedSGAgentAction> actions = ja.getActionList();
		//COunts the number of getGold actions

		for(GroundedSGAgentAction action: actions){
			// If action==getGold
			if(action.actionName().equals(GoldMineConstants.ACTION_GETGOLD)){
				String goldName = action.getParametersAsString()[0];
				//Only count non collected gold.
				if(!collectedGold.containsKey(goldName)){
					numGetGold++;
					collectedGold.put(goldName, 0);
				}
				
			}
		}
		return numGetGold;

	}

}
