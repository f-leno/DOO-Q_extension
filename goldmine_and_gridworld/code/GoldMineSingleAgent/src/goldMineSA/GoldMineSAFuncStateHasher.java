/**
 * 
 */
package goldMineSA;

import java.util.List;

import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.State;

/**
 * @author Ruben Glatt
 *
 */
public class GoldMineSAFuncStateHasher {

	/**
	 * return a String hash for a given state
	 * @param s state
	 * @return hash
	 */
	public static String hashState(State s) {
		StringBuffer sbuf = new StringBuffer(256);
		
		List<ObjectInstance> miners = s.getObjectsOfClass(GoldMineSAConstants.CLS_MINER);
		List<ObjectInstance> golds = s.getObjectsOfClass(GoldMineSAConstants.CLS_GOLD);
		
		String x = GoldMineSAConstants.ATT_X;
		String y = GoldMineSAConstants.ATT_Y;

		//TODO: order miners by name
		
		//Including all miners on State Description
		for(ObjectInstance l : miners){
			sbuf.append(l.getName()).append(l.getIntValForAttribute(x)).append(" ").append(l.getIntValForAttribute(y)).append(", ");
		}
		//Remove Comma
		sbuf.delete(sbuf.length()-2, sbuf.length()-1);
		
		//Start of golds
		sbuf.append("-");
		
		for(ObjectInstance l : golds){
			sbuf.append(l.getIntValForAttribute(x)).append(" ").append(l.getIntValForAttribute(y)).append(", ");
		}
		//Remove Comma
		sbuf.delete(sbuf.length()-2, sbuf.length()-1);
		
		return sbuf.toString();
	}

}
