package goldMineSA;

import java.util.List;

import domain.GoldMineConstants;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.PropositionalFunction;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.State;



/**
 * 
 * This propositionalFunction verifies if all gold has been collected
 * @author Ruben Glatt
 *
 */
public class GoldMineSAPropFuncGoldCollected extends PropositionalFunction {
	
		public GoldMineSAPropFuncGoldCollected(Domain domain){
			super(GoldMineSAConstants.PF_GOLD_COLLECTED, domain, new String []{});
		}
		
		@Override
		public boolean isTrue(State s, String[] params) {
			List<ObjectInstance> goldObjects = s.getObjectsOfClass(GoldMineSAConstants.CLS_GOLD);
			boolean isTrue = true;
			int i=0;
			int x=0;
			while(i<goldObjects.size() && isTrue){
				x = goldObjects.get(i).getIntValForAttribute(GoldMineSAConstants.ATT_X);
				//check if the gold piece has been already collected
				if(x != GoldMineSAConstants.OUTSIDE_GRID){
					isTrue = false;
				}
				i++;
			}
			return isTrue;
		}
		
		
		
	
}
