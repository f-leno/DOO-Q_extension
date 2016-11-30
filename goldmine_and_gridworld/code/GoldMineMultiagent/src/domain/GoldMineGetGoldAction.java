/**
 * 
 */
package domain;

import java.util.ArrayList;
import java.util.List;

import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.State;
import burlap.oomdp.stochasticgames.SGDomain;
import burlap.oomdp.stochasticgames.agentactions.GroundedSGAgentAction;
import burlap.oomdp.stochasticgames.agentactions.ObParamSGAgentAction;
import burlap.oomdp.stochasticgames.agentactions.SGAgentAction;

/**
 * @author Felipe Leno da Silva
 * 
 * GetGold actions as described by the article.
 * 
 * Receives a gold piece reference
 *
 */
public class GoldMineGetGoldAction extends ObParamSGAgentAction {

	protected static final String[] parameters = {GoldMineConstants.CLS_GOLD};
	
	public GoldMineGetGoldAction(SGDomain d, String name) {
		super(d, name, parameters);	
	}

	/* (non-Javadoc)
	 * @see burlap.oomdp.stochasticgames.agentactions.ObParamSGAgentAction#parametersAreObjectIdentifierIndependent()
	 */
	@Override
	public boolean parametersAreObjectIdentifierIndependent() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	/**
	 * This action is only applicable when the acting agent is in the same position as the gold object received by parameter
	 * @param s current state
	 * @param GroundedSGAgentAction ground action
	 * @returns is applicable?
	 */
	public boolean applicableInState(State s, GroundedSGAgentAction gsa) {
		
		//Check if associated action is applicable (any parameter)
		if(gsa.getParametersAsString()==null){
			return applicableAssociated(s,gsa);
		}
		
		String agentName = gsa.actingAgent;
		String goldName = gsa.getParametersAsString()[0];
		
		boolean applicable = false;
		
		ObjectInstance agent = s.getObject(agentName);
		ObjectInstance gold = s.getObject(goldName);
		
		int xAgent, xGold, yAgent, yGold;
		
		xAgent = agent.getIntValForAttribute(GoldMineConstants.ATT_X);
		yAgent = agent.getIntValForAttribute(GoldMineConstants.ATT_Y);
		
		xGold = gold.getIntValForAttribute(GoldMineConstants.ATT_X);
		yGold = gold.getIntValForAttribute(GoldMineConstants.ATT_Y);
		
		if(xAgent == xGold && yAgent == yGold)
			applicable = true;
		
		
		
		return applicable;
	}

	/**
	 * Check if any possible grounding of this action is applicable
	 * @param s state
	 * @param gsa grounded associated action
	 * @return is applicable
	 */
	protected boolean applicableAssociated(State s, GroundedSGAgentAction gsa) {
		List<SGAgentAction> actions = new ArrayList<SGAgentAction>();
		actions.add(gsa.action);
		List<GroundedSGAgentAction> list = SGAgentAction.
											getAllApplicableGroundedActionsFromActionList(s, gsa.actingAgent, actions);
		//Is there any possible applicable action?
		return list.size()>0;
	}

}
