/**
 * 
 */
package domain;

import java.util.List;

import burlap.oomdp.core.PropositionalFunction;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.State;
import burlap.oomdp.stochasticgames.SGDomain;

/**
 * @author Felipe Leno da Silva
 * Propositional function that becomes true when there is no more gold in the environment
 */
public class NoGoldPF extends PropositionalFunction {

	protected static String parameters = "";
	
	public NoGoldPF(SGDomain domain, String name){
		super(name,domain,parameters);
	}
	
	/**
	 * This Function is true when all gold pieces have been collected
	 * @param s current state
	 * @param params this PF has no parameter
	 */
	@Override
	public boolean isTrue(State s, String[] params) {
		List<ObjectInstance> goldObjects = s.getObjectsOfClass(GoldMineConstants.CLS_GOLD);
		
		boolean isTrue = true;
		int i=0;
		
		int x=0;
		while(i<goldObjects.size() && isTrue){
			x = goldObjects.get(i).getIntValForAttribute(GoldMineConstants.ATT_X);
			
			//check if the gold piece has been already collected
			if(x != GoldMineConstants.OUTSIDE_GRID){
				isTrue = false;
			}
			i++;
			
		}
		
		return isTrue;
		
		
		
	}

	
	
}
