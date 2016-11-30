/**
 * 
 */
package goldMineSA;

import burlap.oomdp.auxiliary.common.SinglePFTF;
import burlap.oomdp.core.PropositionalFunction;

/**
 * This terminal function is triggered when there is no more gold in the environment
 * @author Ruben Glatt
 *
 */
public class GoldMineSAFuncTerminal extends SinglePFTF {

	/**
	 * Constructor
	 * @param pf instance of CollectedAllGold propositional Function
	 */
	public GoldMineSAFuncTerminal(PropositionalFunction pf) {
		super(pf);
	}	


}
