/**
 * 
 */
package goldMineSA;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.State;
import burlap.oomdp.core.values.Value;
import burlap.oomdp.statehashing.HashableState;
import burlap.oomdp.statehashing.HashableStateFactory;

/**
 * @author Ruben Glatt
 * Produces hashes for the GoldMine domain. An agent sees all other agents equally. 
 * Also, the domain is  object independent regarding walls and gold pieces
 * 
 *
 */
public class GoldMineSAHashableStateFactory implements HashableStateFactory{

	/**
	 * Constructor for a Hash Factory without specifying the agent identifier, which must be informed in setAgentIdentifier() before use.
	 * In case the agent setAgentIdentifier is set, the outcome of hashState() may be unexpected.
	 * 
	 */
	public GoldMineSAHashableStateFactory(){
		
	}

	/**
	 * A GoldMineHashableState is instantiated with its default constructor
	 * @param s state to hash
	 * @return GoldMineHashableState instance.
	 */
	public HashableState hashState(State s) {
		return new GoldMineHashableState(s);
	}

	/**
	 * This hashFactory is only objectIdentifierDependent regarding the acting agent.
	 */
	public boolean objectIdentifierIndependent() {
		return true;
	}

	
	public class GoldMineHashableState extends HashableState.CachedHashableState{
		
		/**
		 * Default constructor
		 * @param s state to be hashed
		 * @param agentIdentifier associated agent object ID.
		 */
		public GoldMineHashableState(State s) {
			super(s);
		}

				
		@Override
		public State copy() {
			return new GoldMineHashableState(s.copy());
		}


		/**
		 * This method returns true only when the comparing object is a GoldMineHashableState with the same hash
		 */
		public boolean equals(Object obj) {
			//Checking the hashCode of all objecs]ts
			if(obj instanceof GoldMineHashableState){
				GoldMineHashableState cs = (GoldMineHashableState) obj;
				return this.hashCode() == cs.hashCode();
				/*List<ObjectInstance> objs = this.getAllObjects();
				for (ObjectInstance o : objs){
					ObjectInstance comp = cs.getObject(o.getName());
					if(!comp.equals(o)){
						return false;
					}
				}
				return true;*/
			}
			return false;
		}
		
		/**
		 * This method implementation was based on SimpleHashableStateFactory implementation, where the object invariance
		 * is assured by sorting objects by its hashCode. 
		 */
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
		
		/**
		 * Computes the hash code for an individual {@link burlap.oomdp.core.objects.ObjectInstance}.
		 * @param o the {@link burlap.oomdp.core.objects.ObjectInstance} whose hash code will be computed.
		 * @return the hash code for the {@link burlap.oomdp.core.objects.ObjectInstance}.
		 */
		protected int computeObjectHashCode(ObjectInstance o){

			//HashCodeBuilder hashCodeBuilder = new HashCodeBuilder(17, 31);
			HashCodeBuilder hashCodeBuilder = new HashCodeBuilder(991, 997);
			//If we are dealing with the agent himself, his name is added in the hash
			if(o.getClassName().equals(GoldMineSAConstants.CLS_MINER))
				hashCodeBuilder.append(o.getName());
			else
				hashCodeBuilder.append(o.getClassName());

			List<Value> values = o.getValues();
			for(Value v : values){
				//All attributes can be described by int/discrete values
				hashCodeBuilder.append(v.getDiscVal());
			}


			return hashCodeBuilder.toHashCode();
		}

		
		
	}
}
