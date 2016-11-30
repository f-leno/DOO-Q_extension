/**
 * 
 */
package domain;

import java.util.List;

import burlap.domain.stochasticgames.gridgame.GridGame;
import burlap.domain.stochasticgames.gridgame.GridGameStandardMechanics;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.TransitionProbability;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.State;
import burlap.oomdp.stochasticgames.JointAction;
import burlap.oomdp.stochasticgames.JointActionModel;
import burlap.oomdp.stochasticgames.agentactions.GroundedSGAgentAction;

/**
 * @author Felipe Leno da Silva
 * Standard Transition Dynamics for Multiagent Goldmine. More details are available in the article.
 *
 */
public class GoldMineStandardMechanics extends JointActionModel {

	protected Domain domain;
	
	public GoldMineStandardMechanics(Domain domain){
		this.domain = domain;
	}

	/**
	 * This method has not been implemented. A null object is returned
	 */
	public List<TransitionProbability> transitionProbsFor(State s,
			JointAction ja) {
		// Not Yet Implemented
		return null;
	}

	/* (non-Javadoc)
	 * @see burlap.oomdp.stochasticgames.JointActionModel#actionHelper(burlap.oomdp.core.states.State, burlap.oomdp.stochasticgames.JointAction)
	 */
	@Override
	protected State actionHelper(State s, JointAction ja) {
		List <GroundedSGAgentAction> gsas = ja.getActionList();

		//Process each action individually (in this domain, actions have stationary effects in relation to the state space,
		// only the reward function is nonstationary)
		for(GroundedSGAgentAction gsa : gsas){
			//Movement actions
			if(GoldMineConstants.isMovementAction(gsa.actionName())){
				Location2 loc = this.getLocation(s, gsa.actingAgent);
				loc = this.processMovement(s,loc,gsa);

				ObjectInstance agent = s.getObject(gsa.actingAgent);
				agent.setValue(GoldMineConstants.ATT_X, loc.x);
				agent.setValue(GoldMineConstants.ATT_Y, loc.y);				
			}
			else{
				//GetGoldAction
				if(gsa.actionName().equals(GoldMineConstants.ACTION_GETGOLD)){
					ObjectInstance agent = s.getObject(gsa.actingAgent);
					ObjectInstance gold = s.getObject(gsa.getParametersAsString()[0]);

					int ax,ay, gx,gy;
					ax = agent.getIntValForAttribute(GoldMineConstants.ATT_X);
					ay = agent.getIntValForAttribute(GoldMineConstants.ATT_Y);

					gx = gold.getIntValForAttribute(GoldMineConstants.ATT_X);
					gy = gold.getIntValForAttribute(GoldMineConstants.ATT_Y);

					//This action has effect only when the agent and gold piece are in the same position
					if(ax==gx && ay==gy){
						gold.setValue(GoldMineConstants.ATT_X, GoldMineConstants.OUTSIDE_GRID);
						gold.setValue(GoldMineConstants.ATT_Y, GoldMineConstants.OUTSIDE_GRID);
					}
				}
			}


		}


		return s;
	}

	/**
	 * Process the position change for one agent
	 * @param s current state
	 * @param loc agent's location
	 * @param gsa grounded action (used to define movement direction)
	 * @return new location after movement
	 */
	protected Location2 processMovement(State s, Location2 loc, GroundedSGAgentAction gsa) {
		
		int deltaX=0,deltaY=0;
		
		//Check if there is a wall hampering and moves
		switch(gsa.actionName()){
			case GoldMineConstants.ACTION_NORTH:
				if(!GoldMineConstants.checkTouch(this.domain,s,GoldMineConstants.PF_TOUCH_NORTH, gsa.actingAgent))
					deltaY = +1;
				break;
			case GoldMineConstants.ACTION_SOUTH:
				if(!GoldMineConstants.checkTouch(this.domain,s,GoldMineConstants.PF_TOUCH_SOUTH, gsa.actingAgent))
					deltaY = -1;
				break;
			case GoldMineConstants.ACTION_EAST:
				if(!GoldMineConstants.checkTouch(this.domain,s,GoldMineConstants.PF_TOUCH_EAST, gsa.actingAgent))
					deltaX = +1;
				break;
			case GoldMineConstants.ACTION_WEST:
				if(!GoldMineConstants.checkTouch(this.domain,s,GoldMineConstants.PF_TOUCH_WEST, gsa.actingAgent))
					deltaX = -1;
				break;	
		}
			
		loc.x += deltaX;
		loc.y += deltaY;
		
		
		
		return loc;
	}

	/**
	 * Returns the x-y position of an agent stored in a Location2 object.
	 * @param s the state in which the agent exists
	 * @param agentName the name of the agent.
	 * @return a {@link GridGameStandardMechanics.Location2} object containing the agents position in the world.
	 */
	protected Location2 getLocation(State s, String agentName){

		ObjectInstance a = s.getObject(agentName);
		Location2 loc = new Location2(a.getIntValForAttribute(GridGame.ATTX), a.getIntValForAttribute(GridGame.ATTY));

		return loc;
	}

	/**
	 * A class for storing 2 dimensional position information. Add and subtract operations are defined for it.
	 * @author James MacGlashan
	 *
	 */
	class Location2{

		/**
		 * The x position
		 */
		public int x;

		/**
		 * The y position
		 */
		public int y;


		/**
		 * Constructs with the given position
		 * @param x the x position
		 * @param y the y position
		 */
		public Location2(int x, int y){
			this.x = x;
			this.y = y;
		}

		/**
		 * Constructs a new instance from a previous {@link Location2} instance
		 * @param l the {@link Location2} instance to copy.
		 */
		public Location2(Location2 l){
			this.x = l.x;
			this.y = l.y;
		}


		/**
		 * Returns a new {@link Location2} object that is the sum of this object and the provided object. This objects values
		 * are not affected by this operation.
		 * @param o the other object whose values should be added.
		 * @return a new {@link Location2} object that is the sum of this object and the provided object.
		 */
		public Location2 add(Location2 o){
			return new Location2(x+o.x, y+o.y);
		}


		/**
		 * Returns a new {@link Location2} object that is the subtraction of a provided object from this object (this - o). This objects values
		 * are not affected by this operation.
		 * @param o the other object whose values should be subtract.
		 * @return a new {@link Location2} object that is the subtraction of a provided object from this object (this - o).
		 */
		public Location2 subtract(Location2 o){
			return new Location2(x-o.x, y-o.y);
		}


		@Override
		public boolean equals(Object o){
			if(!(o instanceof Location2)){
				return false;
			}

			Location2 ol = (Location2)o;

			return x == ol.x && y == ol.y;

		}

	}

}
