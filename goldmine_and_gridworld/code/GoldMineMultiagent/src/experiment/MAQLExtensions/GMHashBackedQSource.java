/**
 * 
 */
package experiment.MAQLExtensions;

import java.util.Map;

import burlap.behavior.stochasticgames.madynamicprogramming.JAQValue;
import burlap.behavior.stochasticgames.madynamicprogramming.QSourceForSingleAgent.HashBackedQSource;
import burlap.behavior.valuefunction.ValueFunctionInitialization;
import burlap.oomdp.core.states.State;
import burlap.oomdp.statehashing.HashableStateFactory;
import burlap.oomdp.stochasticgames.JointAction;

/**
 * @author Felipe Leno da Silva
 *
 *Small modification in HashBackedQSource to allow the QTable size definition
 */
public class GMHashBackedQSource extends HashBackedQSource {
	protected long qSize=0;
	
	
	
	
	
	
	
	public GMHashBackedQSource(HashableStateFactory hashingFactory, ValueFunctionInitialization qInit) {
		super(hashingFactory, qInit);
		// TODO Auto-generated constructor stub
	}

	@Override
	public JAQValue getQValueFor(State s, JointAction ja) {
		
		Map<JointAction, JAQValue> jaQS = this.getJAMap(s);
		
		JAQValue q = jaQS.get(ja);
		if(q != null){
			return q;
		}
		
		
		//we didn't find a joint action match that is sorted, but
		//first make sure it's not an object identifier difference between states that is causing a failure of matching
		if(ja.isParameterized()){
			//is there a matching joint action after we translate?
			for(JAQValue sq : jaQS.values()){
				JointAction translated = (JointAction)ja.translateParameters(s, sq.s);
				if(translated.equals(sq.ja)){
					//there is a stored Q value, so return it
					return sq;
				}
			}
		}
		
		//if we got here then we need to create the q-value
		q = new JAQValue(s, ja, this.qInit.qValue(s, ja));
		jaQS.put(ja, q);
		//--------------------------------------------
		// Here is the modification
		//---------------------------------------
		this.qSize++;
		
		
		return q;
	}
	
	public long getQTableSize(){
		return this.qSize;
	}
}
