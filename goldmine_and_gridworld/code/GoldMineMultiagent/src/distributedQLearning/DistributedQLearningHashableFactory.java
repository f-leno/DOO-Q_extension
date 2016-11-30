/**
 * 
 */
package distributedQLearning;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.State;
import burlap.oomdp.core.values.Value;
import burlap.oomdp.statehashing.HashableState;
import burlap.oomdp.statehashing.HashableStateFactory;
import goldmine.GoldMineHashFactory;

/**
 * changes the hashableFactory to correspond to a factorated state description.
 * @author Felipe Leno da Silva
 *
 */
public class DistributedQLearningHashableFactory extends GoldMineHashFactory implements HashableStateFactory {
	
	public HashableState hashState(State s) {
		return new GoldMineConcreteHashableState(s,"");
	}
	
	
	public class GoldMineConcreteHashableState extends GoldMineHashFactory.GoldMineHashableState{

		public GoldMineConcreteHashableState(State s, String agentIdentifier) {
			super(s, agentIdentifier);
		}

		/**
		 * This method returns true only when the comparing object is a GoldMineConcreteHashableState with the same hash
		 */
		/*public boolean equals(Object obj) {
			//Checking the hashCode of all objecs]ts
			if(obj instanceof GoldMineConcreteHashableState){
				return this.hashCode() == ((GoldMineConcreteHashableState) obj).hashCode();
				
			}
			return false;
		}*/
		
		
		/**
		 * All objects are unique
		 * @param o object
		 * @return object hash
		 */
		protected int computeObjectHashCode(ObjectInstance o){

			
			HashCodeBuilder hashCodeBuilder = new HashCodeBuilder(7, 17);
			//For all objects, its id is added in the hash
			hashCodeBuilder.append(o.getName());
			//Also adds its class in case two objects have the same id
			hashCodeBuilder.append(o.getClassName());

			List<Value> values = o.getValues();
			for(Value v : values){
								
				//All attributes can be described by int/discrete values
				hashCodeBuilder.append(v.getDiscVal());
			}


			return hashCodeBuilder.toHashCode();
		}
		
		
		public int computeHashCode() {
			//-----------------------------------------
			// Regular State
			//-----------------------------------------
			List<ObjectInstance> objects = s.getAllObjects();
			int [] hashCodes = new int[objects.size()];
			
			//Compute a hashcode to all objects
			for(int i = 0; i < hashCodes.length; i++){
				hashCodes[i] = computeObjectHashCode(objects.get(i));
			}
			
			//sort for invariance to order --Copied from SimpleHashableStateFactory
			Arrays.sort(hashCodes);
						
			HashCodeBuilder hashCodeBuilder = new HashCodeBuilder(17, 31);
			for(int code : hashCodes){
				hashCodeBuilder.append(code);
			}
			int code = hashCodeBuilder.toHashCode();

			return code;
		}

		
		
	}

}
