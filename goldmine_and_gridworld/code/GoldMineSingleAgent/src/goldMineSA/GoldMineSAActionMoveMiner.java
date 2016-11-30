/**
 * 
 */
package goldMineSA;

import java.util.ArrayList;
import java.util.List;

import burlap.oomdp.core.Domain;
import burlap.oomdp.core.GroundedProp;
import burlap.oomdp.core.PropositionalFunction;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.Action;
import burlap.oomdp.singleagent.ActionObserver;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.common.SimpleGroundedAction;

/**
 * 
 * This class can represent any of the 4 Movement Actions (north, south, east, west)
 * @author Ruben Glatt
 *
 */
public class GoldMineSAActionMoveMiner extends Action {
	//These attributes define this action's effect
	private int xIncrement;
	private int yIncrement;
	
	private int limitX = 0;
	private int limitY = 0;
	
	private boolean wallCollision = false;

	//Parameter classes for grounding check
	private String[] parameterClasses = {GoldMineSAConstants.CLS_MINER};

	/**
	 * Constructor that creates an action
	 * @param actionName Defines what action will be created. This parameter can be set as: MoveMiner.ACTIONNORTH, MoveMiner.ACTIONSOUTH,
	 * MoveMiner.ACTIONEAST, or MoveMiner.ACTIONWEST
	 * @param domain action domain
	 * @param parameter Class name to apply movement
	 * @param limitX X limit for movements
	 * @param limitY Y limit for movements
	 */
	public GoldMineSAActionMoveMiner(String actionName, Domain domain,int limitX,int limitY){
		super(actionName,domain);
		this.limitX = limitX;
		this.limitY = limitY;
		//Define action's effect
		switch(actionName){
		case GoldMineSAConstants.NORTH:
			this.yIncrement = 1;
			this.xIncrement = 0; break;
		case GoldMineSAConstants.SOUTH:
			this.yIncrement = -1;
			this.xIncrement = 0; break;
		case GoldMineSAConstants.EAST:
			this.yIncrement = 0;
			this.xIncrement = 1; break;
		case GoldMineSAConstants.WEST:
			this.yIncrement = 0;
			this.xIncrement = -1; break;
		}
	}

	@Override
	public boolean applicableInState(State s, GroundedAction groundedAction) {
		String[] params = groundedAction.getParametersAsString();
		//System.out.print(groundedAction.actionName() +": "+ params[0] );
		ObjectInstance miner = s.getObject(params[0]);
		int currX = miner.getIntValForAttribute(GoldMineSAConstants.ATT_X);
		int currY = miner.getIntValForAttribute(GoldMineSAConstants.ATT_Y);

		PropositionalFunction pf=null; //Variable to achieve wall comparison
		//Check propositional Walls and grid limit
		switch(this.getName()){
			case GoldMineSAConstants.NORTH:
				if(currY == this.limitY-1){
					//System.out.println(" --- COLLISION TOP limit");
					this.wallCollision = true;
					break;
				}
				//wall to north
				pf = this.domain.getPropFunction(GoldMineSAConstants.PF_WALL_NORTH);	
				break;
			case GoldMineSAConstants.SOUTH:
				if(currY == 0){
					//System.out.println(" --- COLLISION BOTTOM limit");
					this.wallCollision = true;
					break;
				}
				//wall to south
				pf = this.domain.getPropFunction(GoldMineSAConstants.PF_WALL_SOUTH);
				break;
			case GoldMineSAConstants.EAST:
				if(currX == this.limitX-1){
					//System.out.println(" --- COLLISION RIGHT limit");
					this.wallCollision = true;
					break;
				}
				//wall to east
				pf = this.domain.getPropFunction(GoldMineSAConstants.PF_WALL_EAST);
				break;
			case GoldMineSAConstants.WEST:
				if(currX == 0){
					//System.out.println(" --- COLLISION LEFT limit");
					this.wallCollision = true;
					break;
				}
					//return false;
				//wall to west
				pf = this.domain.getPropFunction(GoldMineSAConstants.PF_WALL_WEST);
				break;
		}
		
		if (this.wallCollision){
			this.wallCollision = false;
			return false;
		}
		//System.out.print(" testing "+ pf.getName() +": ");
		
		// TODO what is happening here?? why?
		List<GroundedProp> allProp = pf.getAllGroundedPropsForState(s); // all miner - wall combinations 3*24=72
		//Find if there is a proposition with the same miner object
		for(GroundedProp gp: allProp){
			// there is a collision
			if(gp.isTrue(s)){
				//Get miner from proposition
				ObjectInstance obj = s.getObject(gp.params[0]);
				//if he is the same miner from the groundedAction, this action is not applicable
				if(obj==miner){
					//System.out.println(" --- COLLISION "+ gp.params[1]);
					return false;
				}
			}
		}
		//System.out.println(" --- OK ");
		//Up to this point, there is no wall to hamper the movement
		return true;
	}


	@Override
	public boolean isPrimitive() {
		// TODO Auto-generated method stub
		return true;
	}


	@Override
	public boolean isParameterized() {
		// TODO Auto-generated method stub
		return true;
	}


	@Override
	public GroundedAction getAssociatedGroundedAction() {
		return new SimpleGroundedAction(this);
	}


	@Override
	public List<GroundedAction> getAllApplicableGroundedActions(State s) {
		//----------------------------------
		//DefaultCode from last Version
		//-------------------------------------
		List <GroundedAction> res = new ArrayList<GroundedAction>();

		//otherwise need to do parameter binding
		List <List <String>> bindings = s.getPossibleBindingsGivenParamOrderGroups(parameterClasses, parameterClasses);

		for(List <String> params : bindings){
			//Possible binding without considering conditions
			String [] aprams = params.toArray(new String[params.size()]);
			GroundedAction gp = new GoldMineSAGroundedAction(this, aprams);
			//Checking conditions
			if(this.applicableInState(s, gp)){				
				res.add(gp);
			}
		}
		return res;
	}

	@Override
	protected State performActionHelper(State s, GroundedAction groundedAction) {
		String[] params = groundedAction.getParametersAsString();
		//As there is only one parameter, the object will be acessed directly
		ObjectInstance obj = s.getObject(params[0]);
		int currX = obj.getIntValForAttribute(GoldMineSAConstants.ATT_X);
		int currY = obj.getIntValForAttribute(GoldMineSAConstants.ATT_Y);

		//Change object's position
		obj.setValue(GoldMineSAConstants.ATT_X, currX + this.xIncrement);
		obj.setValue(GoldMineSAConstants.ATT_Y, currY + this.yIncrement);

		return s;
	}



}
