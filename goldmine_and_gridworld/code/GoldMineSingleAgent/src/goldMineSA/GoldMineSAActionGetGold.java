package goldMineSA;

import java.util.ArrayList;
import java.util.List;

import burlap.oomdp.core.Domain;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.Action;
import burlap.oomdp.singleagent.ActionObserver;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.common.SimpleGroundedAction;
/**
 * GetGold Action:
 * 
 * collects a gold, the first parameter should be a miner, and the second one a gold instance.
 * After executed, this action removes the gold from the state
 * 
 * @author Ruben Glatt
 */
public class GoldMineSAActionGetGold extends Action {

	//Parameter classes for grounding check
	private String[] parameterClasses = {GoldMineSAConstants.CLS_MINER,GoldMineSAConstants.CLS_GOLD};

	public GoldMineSAActionGetGold(String actionName, Domain domain) {
		super(actionName,domain);
	}

	
	
	@Override
	public State performAction(State s, GroundedAction groundedAction){
		
		State resultState = s.copy();
		if(!this.applicableInState(s, groundedAction)){
			return resultState; //can't do anything if it's not applicable in the state so return the current state
		}
		
		resultState = performActionHelper(resultState, groundedAction);
		
		/*for(ActionObserver observer : this.actionObservers){
			observer.actionEvent(resultState, groundedAction, resultState);
		}*/
		
		return resultState;
		
	}
	
	@Override
	protected State performActionHelper(State s, GroundedAction groundedAction) {
		//The first parameter is a Miner, and the second one is a gold
	    
		String[] params = groundedAction.getParametersAsString();
		//System.out.println(params[1]);
		//ObjectInstance miner = s.getObject(params[0]);
	    ObjectInstance gold = s.getObject(params[1]);
		gold.setValue(GoldMineSAConstants.ATT_X, GoldMineSAConstants.OUTSIDE_GRID);
		gold.setValue(GoldMineSAConstants.ATT_Y, GoldMineSAConstants.OUTSIDE_GRID);	
		//gold.setValue(GoldMineSAConstants.ATT_ACTIVE, false);
		//System.out.println("removed gold");
	    //Gold has been removed from the state
		s.removeObject(gold);
		return s;
	}

	@Override
	public boolean applicableInState(State s, GroundedAction groundedAction) {
		//This action is applicable if the miner and gold are in the same positions
		//CONDITION
		String[] params = groundedAction.getParametersAsString();
		ObjectInstance miner = s.getObject(params[0]);
	    ObjectInstance gold = s.getObject(params[1]);
	    //if (gold != null && gold.getBooleanValForAttribute(GoldMineSAConstants.ATT_ACTIVE)){
	    if (gold != null){
		    int xMiner,xGold,yMiner,yGold;
		    xMiner = miner.getIntValForAttribute(GoldMineSAConstants.ATT_X);
		    xGold = gold.getIntValForAttribute(GoldMineSAConstants.ATT_X);
		    yMiner = miner.getIntValForAttribute(GoldMineSAConstants.ATT_Y);
		    yGold = gold.getIntValForAttribute(GoldMineSAConstants.ATT_Y);
		    if(xMiner == xGold && yGold == yMiner)
		    	return true;
	    }
	    return false;
	}

	@Override
	public boolean isPrimitive() {
		return true;
	}

	@Override
	public boolean isParameterized() {
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


}
