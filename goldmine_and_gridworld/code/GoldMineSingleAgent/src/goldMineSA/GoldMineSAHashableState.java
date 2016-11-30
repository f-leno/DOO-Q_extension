/**
 * 
 */
package goldMineSA;

import burlap.oomdp.core.states.State;
import burlap.oomdp.statehashing.HashableState;

/**
 * @author Ruben Glatt
 *
 */
public class GoldMineSAHashableState extends HashableState {

	public GoldMineSAHashableState(State s) {
		super(s);		
	}

	/* (non-Javadoc)
	 * @see burlap.oomdp.core.states.State#copy()
	 */
	@Override
	public State copy() {
		return super.s.copy();
	}

	/* (non-Javadoc)
	 * @see burlap.oomdp.statehashing.HashableState#hashCode()
	 */
	@Override
	public int hashCode() {
		String stringHash = GoldMineSAFuncStateHasher.hashState(s);
		return stringHash.hashCode();
	}

	/* (non-Javadoc)
	 * @see burlap.oomdp.statehashing.HashableState#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof HashableState){
			HashableState otherObj = (HashableState) obj;
			return otherObj.hashCode()==this.hashCode();
		}
		
		return false;
		
	}

}
