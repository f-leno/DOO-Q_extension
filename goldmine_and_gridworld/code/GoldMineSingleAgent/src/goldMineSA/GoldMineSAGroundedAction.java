/**
 * 
 */
package goldMineSA;

import burlap.oomdp.singleagent.Action;
import burlap.oomdp.singleagent.GroundedAction;

/**
 * Class to extend the default grounded action
 * @author Ruben Glatt
 *
 */
public class GoldMineSAGroundedAction extends GroundedAction {

	String[] params;

	
	public GoldMineSAGroundedAction(Action action, String[] params) {
		super(action);
		this.params = params;
	}


	/* (non-Javadoc)
	 * @see burlap.oomdp.core.AbstractGroundedAction#initParamsWithStringRep(java.lang.String[])
	 */
	@Override
	public void initParamsWithStringRep(String[] params) {
		this.params = params;

	}

	/* (non-Javadoc)
	 * @see burlap.oomdp.core.AbstractGroundedAction#getParametersAsString()
	 */
	@Override
	public String[] getParametersAsString() {
		// TODO Auto-generated method stub
		return params;
	}

	/* (non-Javadoc)
	 * @see burlap.oomdp.singleagent.GroundedAction#copy()
	 */
	@Override
	public GroundedAction copy() {
		try {
			GoldMineSAGroundedAction ga= new GoldMineSAGroundedAction(action,params);
			return ga;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}
