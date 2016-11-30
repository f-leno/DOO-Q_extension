/**
 * 
 */
package goldMineSA;

import java.util.List;

import burlap.oomdp.core.Domain;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.State;
import burlap.oomdp.legacy.StateParser;

/**
 * Hash Generator that ignores walls (they only exist to process transitions and applicable actions).
 * @author Ruben Glatt
 *
 */
public class GoldMineSAStateParser implements StateParser {

	Domain domain;
	List<ObjectInstance> storeWall; //Will keep walls saved
	
	public GoldMineSAStateParser(int sizeX,int sizeY){
		this.domain = new GoldMineSA(sizeX,sizeY).generateDomain();
	}
	
	public GoldMineSAStateParser(Domain domain){
		this.domain = domain;
		
	}
	/* (non-Javadoc)
	 * @see burlap.oomdp.legacy.StateParser#stateToString(burlap.oomdp.core.states.State)
	 */
	@Override
	public String stateToString(State s) {
		String hash = GoldMineSAFuncStateHasher.hashState(s);

		
		if(storeWall==null){
			saveWalls(s);
		}
		
		return hash;
		
	}
	
	/**
	 * Store wall objects
	 * @param s state to copy wall objects
	 */
	private void saveWalls(State s){
		storeWall = s.getObjectsOfClass(GoldMineSAConstants.CLS_WALL);
	}

	/* (non-Javadoc)
	 * @see burlap.oomdp.legacy.StateParser#stringToState(java.lang.String)
	 */
	@Override
	public State stringToState(String str) {
		
		String[] obcomps = str.split("-");
		String miners = obcomps[0];
		String golds = obcomps[1];
		
		//Separating Miners
		String [] sepMiners = miners.split(", ");
		
		//Separating Golds
		String [] sepGolds = golds.split(", ");
		
		int numberMiners = sepMiners.length;
		int numberGolds = sepGolds.length;
		
		String x = GoldMineSAConstants.ATT_X;
		String y = GoldMineSAConstants.ATT_Y;
		
		State s = GoldMineSAConstants.generateEmptyState(domain,numberMiners,numberGolds,storeWall);
		
		//Edit Miners
		List<ObjectInstance> minersObj = s.getObjectsOfClass(GoldMineSAConstants.CLS_MINER);
		
		for(int i=0;i<sepMiners.length;i++){
			String [] lcomps = sepMiners[i].split(" ");
			String name = lcomps[0];
			int lx = Integer.parseInt(lcomps[1]);
			int ly = Integer.parseInt(lcomps[2]);
			
			ObjectInstance miner = minersObj.get(i);
			miner.setName(name);
			miner.setValue(x, lx);
			miner.setValue(y, ly);
		}
		
		//Edit Golds (does not edit names)
		List<ObjectInstance> goldsObj = s.getObjectsOfClass(GoldMineSAConstants.CLS_GOLD);
		
		for(int i=0;i<sepMiners.length;i++){
			String [] lcomps = sepGolds[i].split(" ");
			int lx = Integer.parseInt(lcomps[0]);
			int ly = Integer.parseInt(lcomps[1]);
			
			ObjectInstance gold = goldsObj.get(i);
			gold.setValue(x, lx);
			gold.setValue(y, ly);
		}
				
		return s;
	}

}
