package goldMineSA;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import burlap.behavior.policy.EpsilonGreedy;
import burlap.behavior.valuefunction.QFunction;
import burlap.behavior.valuefunction.QValue;
import burlap.oomdp.core.AbstractGroundedAction;
import burlap.oomdp.core.AbstractObjectParameterizedGroundedAction;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.State;
import burlap.oomdp.singleagent.GroundedAction;

/**
 * 
 * @author Ruben Glatt
 * Small modification of EGreedyMaxWellfare to allow experiment reproducibility
 */
public class GoldMineSAAlgorithmEGreedy extends EpsilonGreedy {
	
	public GoldMineSAAlgorithmEGreedy(QFunction qplanner, double epsilon, long seed) {
		super(qplanner, epsilon);
		this.setRandom(new Random(seed));
	}

	/**
	 * Set Random variable
	 * @param rand random
	 */
	public void setRandom(Random rand){
		this.rand = rand;
	}
	
	
	@Override
	public AbstractGroundedAction getAction(State s) {
		//System.out.print("Getting action");
		List<QValue> qValues = this.qplanner.getQs(s);
		double sum = 0;
		double roll = rand.nextDouble();
		AbstractGroundedAction aga = null;
		GroundedAction ga;
		
		if(roll <= epsilon){
		// do a random action
			int selected;
			
			do {
				selected = rand.nextInt(qValues.size());
				ga = (GroundedAction) qValues.get(selected).a;
			} while (!ga.applicableInState(s));
			aga = AbstractObjectParameterizedGroundedAction.Helper.translateParameters(ga, qValues.get(selected).s, s);
			//System.out.println("..... decided random: "+ aga.actionName());
		} else {
		// follow the policy to get the action
			List <QValue> maxActions = new ArrayList<QValue>();
			maxActions.add(qValues.get(0));
			double maxQ = qValues.get(0).q;
			for(int i = 1; i < qValues.size(); i++){
				QValue q = qValues.get(i);
				if(q.q == maxQ){
					maxActions.add(q);
				}
				else if(q.q > maxQ){
					maxActions.clear();
					maxActions.add(q);
					maxQ = q.q;
				}
			}
			do {
				// select a random action from those with the maximal qValue and get its parameters
				int selected = rand.nextInt(maxActions.size());
				aga = maxActions.get(selected).a;
				aga = AbstractObjectParameterizedGroundedAction.Helper.translateParameters(aga, maxActions.get(selected).s, s);
				String[] actionParams = aga.getParametersAsString();
				//System.out.print("..... decided from "+maxActions.size()+" maxQ: "+ aga.actionName());
				
				// if the action is goldCollect investigate further, otherwise leave the loop
				if (actionParams.length == 2){
					//System.out.print(" ("+actionParams[0]+"-"+actionParams[1]+")..... rethinking");
					// get the gold object that should be investigated
					ObjectInstance gold = s.getObject(actionParams[1]);
					// if the object does not exist or is outside the grid remove the qValue from the maxAction list
					if (gold == null 
							|| gold.getIntValForAttribute(GoldMineSAConstants.ATT_X) == GoldMineSAConstants.OUTSIDE_GRID 
							|| gold.getIntValForAttribute(GoldMineSAConstants.ATT_Y) == GoldMineSAConstants.OUTSIDE_GRID ){
						maxActions.remove(selected);
						// if the last maxAction list has been removed from the list select an applicable random action and leave the loop
						if (maxActions.size() == 0){
							int selectedQValue;
							do {
								selectedQValue = rand.nextInt(qValues.size());
								ga = (GroundedAction) qValues.get(selectedQValue).a;
							} while (!ga.applicableInState(s));
							aga = AbstractObjectParameterizedGroundedAction.Helper.translateParameters(ga, qValues.get(selectedQValue).s, s);
							//System.out.println("..... decided random: "+ aga.actionName());
							break;
						} 
					} else {
						//System.out.println(" ..... All good");
						break;
					}
				} else {
					//System.out.println("");
					break;
				}
			} while (maxActions.size()>0);
			
		}
		//System.out.println("decided: "+ aga.actionName());
		//return translated action parameters if the action is parameterized with objects in a object identifier independent domain
		return aga;
	}
	
}
