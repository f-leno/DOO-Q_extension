/**
 * 
 */
package domain;

import java.util.Random;

import burlap.behavior.stochasticgames.GameAnalysis;
import burlap.debugtools.DPrint;
import burlap.oomdp.auxiliary.DomainGenerator;
import burlap.oomdp.auxiliary.common.SinglePFTF;
import burlap.oomdp.core.Attribute;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.ObjectClass;
import burlap.oomdp.core.TerminalFunction;
import burlap.oomdp.core.objects.MutableObjectInstance;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.MutableState;
import burlap.oomdp.core.states.State;
import burlap.oomdp.stochasticgames.JointReward;
import burlap.oomdp.stochasticgames.SGAgentType;
import burlap.oomdp.stochasticgames.SGDomain;
import burlap.oomdp.stochasticgames.World;
import burlap.oomdp.stochasticgames.agentactions.SimpleSGAgentAction;
import burlap.oomdp.stochasticgames.common.ConstantSGStateGenerator;
import burlap.oomdp.stochasticgames.common.VisualWorldObserver;
import burlap.oomdp.visualizer.Visualizer;
import domain.ui.GoldMineMAVisualizer;
import goldmine.DOOQLearning;
import goldmine.GoldMineHashFactory;


/**
 * @author Felipe Leno da Silva
 * -- Changes on  burlap.oomdp.stochasticgames.agentactions.ObParamSGAgentAction --
 */
public class MultiagentGoldMine implements DomainGenerator {

	protected int sizeX; //grid size in x
	protected int sizeY; //grid size in y
	
	
	protected SGAgentType agentType; //We suppose that all agents are homogenuous
	
	/**
	 * Allows the specification of a grid size
	 * @param sizeX horizontal size
	 * @param sizeY vertical size
	 */
	public MultiagentGoldMine(int sizeX,int sizeY){
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}
	/**
	 * Standard constructor that initiates this class with the standard parameters
	 */
	public MultiagentGoldMine(){
		this.sizeX = GoldMineConstants.DEFAULT_SIZE_X;
		this.sizeY = GoldMineConstants.DEFAULT_SIZE_Y;
	}
	
	
	/**
	 * Generates a Multiagent Goldmine domain.
	 * See the domain description in the article
	 */
	public Domain generateDomain() {
		SGDomain domain = new SGDomain();
		
		//--------------------
		//Attribute Definition
		//--------------------
		Attribute xatt = new Attribute(domain, GoldMineConstants.ATT_X, Attribute.AttributeType.INT);
		xatt.setLims(0, this.getSizeX()-1);
		
		Attribute yatt = new Attribute(domain, GoldMineConstants.ATT_Y, Attribute.AttributeType.INT);
		yatt.setLims(0, this.getSizeY()-1);
		
		Attribute posatt = new Attribute(domain, GoldMineConstants.ATT_POSITION, Attribute.AttributeType.DISC);
		posatt.setDiscValues(GoldMineConstants.ATT_POSITION_VALUES);
		
		
		//-----------------------
		// Class Definition
		//----------------------
		ObjectClass agentClass = new ObjectClass(domain, GoldMineConstants.CLS_AGENT);
		agentClass.addAttribute(xatt);
		agentClass.addAttribute(yatt);
		
		ObjectClass goldClass = new ObjectClass(domain, GoldMineConstants.CLS_GOLD);
		goldClass.addAttribute(xatt);
		goldClass.addAttribute(yatt);
		
		ObjectClass wallClass = new ObjectClass(domain, GoldMineConstants.CLS_WALL);
		wallClass.addAttribute(xatt);
		wallClass.addAttribute(yatt);
		wallClass.addAttribute(posatt);
		
		//-----------------
		//Action Definition
		//-----------------
		
		//Movement Actions
		new SimpleSGAgentAction(domain, GoldMineConstants.ACTION_NORTH);
		new SimpleSGAgentAction(domain, GoldMineConstants.ACTION_SOUTH);
		new SimpleSGAgentAction(domain, GoldMineConstants.ACTION_EAST);
		new SimpleSGAgentAction(domain, GoldMineConstants.ACTION_WEST);
		new SimpleSGAgentAction(domain, GoldMineConstants.ACTION_NOOP);
		
		//GetGold action
		new GoldMineGetGoldAction(domain, GoldMineConstants.ACTION_GETGOLD);
		
				
		//-------------------------
		// Propositional Functions
		//-------------------------
		
		//Identifies if all gold pieces have been collected
		new NoGoldPF(domain, GoldMineConstants.PF_ALL_GOLD);
		
		//Touch relation
		new TouchPF(domain, GoldMineConstants.PF_TOUCH_NORTH, GoldMineConstants.NORTH);
		new TouchPF(domain, GoldMineConstants.PF_TOUCH_SOUTH, GoldMineConstants.SOUTH);
		new TouchPF(domain, GoldMineConstants.PF_TOUCH_WEST, GoldMineConstants.WEST);
		new TouchPF(domain, GoldMineConstants.PF_TOUCH_EAST, GoldMineConstants.EAST);
		

		domain.setJointActionModel(new GoldMineStandardMechanics(domain));
		
		this.agentType = new SGAgentType(GoldMineConstants.CLS_AGENT, domain.getObjectClass(GoldMineConstants.CLS_AGENT), domain.getAgentActions());
		
		
		return domain;
	}
	
	/**
	 * Returns a state for 2x2 grids, intended for debugging
	 * @param domain domain
	 * @return the state
	 */
	public State getMinimalState(Domain domain){
		State s = new MutableState();
		
		//Agents
		ObjectInstance agent1 = new MutableObjectInstance(domain.getObjectClass(GoldMineConstants.CLS_AGENT), "agent1");
		agent1.setValue(GoldMineConstants.ATT_X, 0);
		agent1.setValue(GoldMineConstants.ATT_Y, 0);
		
		ObjectInstance agent2 = new MutableObjectInstance(domain.getObjectClass(GoldMineConstants.CLS_AGENT), "agent2");
		agent2.setValue(GoldMineConstants.ATT_X, 1);
		agent2.setValue(GoldMineConstants.ATT_Y, 1);
		
		ObjectInstance agent3 = new MutableObjectInstance(domain.getObjectClass(GoldMineConstants.CLS_AGENT), "agent3");
		agent3.setValue(GoldMineConstants.ATT_X, 0);
		agent3.setValue(GoldMineConstants.ATT_Y, 1);
		
		//GoldPieces		
		ObjectInstance gold1 = new MutableObjectInstance(domain.getObjectClass(GoldMineConstants.CLS_GOLD), "gold1");
		gold1.setValue(GoldMineConstants.ATT_X, 1);
		gold1.setValue(GoldMineConstants.ATT_Y, 0);
			
		ObjectInstance gold2 = new MutableObjectInstance(domain.getObjectClass(GoldMineConstants.CLS_GOLD), "gold2");
		gold2.setValue(GoldMineConstants.ATT_X, 1);
		gold2.setValue(GoldMineConstants.ATT_Y, 1);
		
		s = addDefaultWalls(domain,s);
		
		s.addObject(agent1);
		s.addObject(agent2);
		s.addObject(agent3);
		s.addObject(gold1);
		s.addObject(gold2);
		
		return s;
	}
	/**
	 * Returns a example states (intended for tests)
	 * @param domain domain for which a state is created
	 * @return the example state
	 */
	public State getExampleState(Domain domain){
		State s = new MutableState();
		
		//Agents
		ObjectInstance agent1 = new MutableObjectInstance(domain.getObjectClass(GoldMineConstants.CLS_AGENT), "agent1");
		agent1.setValue(GoldMineConstants.ATT_X, 0);
		agent1.setValue(GoldMineConstants.ATT_Y, 1);
		
		ObjectInstance agent2 = new MutableObjectInstance(domain.getObjectClass(GoldMineConstants.CLS_AGENT), "agent2");
		agent2.setValue(GoldMineConstants.ATT_X, 1);
		agent2.setValue(GoldMineConstants.ATT_Y, 3);
		
		ObjectInstance agent3 = new MutableObjectInstance(domain.getObjectClass(GoldMineConstants.CLS_AGENT), "agent3");
		agent3.setValue(GoldMineConstants.ATT_X, 4);
		agent3.setValue(GoldMineConstants.ATT_Y, 1);
		
		//GoldPieces		
		ObjectInstance gold1 = new MutableObjectInstance(domain.getObjectClass(GoldMineConstants.CLS_GOLD), "gold1");
		gold1.setValue(GoldMineConstants.ATT_X, 1);
		gold1.setValue(GoldMineConstants.ATT_Y, 0);
		
		ObjectInstance gold2 = new MutableObjectInstance(domain.getObjectClass(GoldMineConstants.CLS_GOLD), "gold2");
		gold2.setValue(GoldMineConstants.ATT_X, 1);
		gold2.setValue(GoldMineConstants.ATT_Y, 4);
		
		ObjectInstance gold3 = new MutableObjectInstance(domain.getObjectClass(GoldMineConstants.CLS_GOLD), "gold3");
		gold3.setValue(GoldMineConstants.ATT_X, 2);
		gold3.setValue(GoldMineConstants.ATT_Y, 0);
		
		ObjectInstance gold4 = new MutableObjectInstance(domain.getObjectClass(GoldMineConstants.CLS_GOLD), "gold4");
		gold4.setValue(GoldMineConstants.ATT_X, 2);
		gold4.setValue(GoldMineConstants.ATT_Y, 2);
		
		ObjectInstance gold5 = new MutableObjectInstance(domain.getObjectClass(GoldMineConstants.CLS_GOLD), "gold5");
		gold5.setValue(GoldMineConstants.ATT_X, 3);
		gold5.setValue(GoldMineConstants.ATT_Y, 3);
		
		ObjectInstance gold6 = new MutableObjectInstance(domain.getObjectClass(GoldMineConstants.CLS_GOLD), "gold6");
		gold6.setValue(GoldMineConstants.ATT_X, 4);
		gold6.setValue(GoldMineConstants.ATT_Y, 2);
		
		//Walls
		ObjectInstance wall1 = new MutableObjectInstance(domain.getObjectClass(GoldMineConstants.CLS_WALL), "wall1");
		wall1.setValue(GoldMineConstants.ATT_X, 1);
		wall1.setValue(GoldMineConstants.ATT_Y, 1);
		wall1.setValue(GoldMineConstants.ATT_POSITION, GoldMineConstants.SOUTH);
		
		ObjectInstance wall2 = new MutableObjectInstance(domain.getObjectClass(GoldMineConstants.CLS_WALL), "wall2");
		wall2.setValue(GoldMineConstants.ATT_X, 1);
		wall2.setValue(GoldMineConstants.ATT_Y, 3);
		wall2.setValue(GoldMineConstants.ATT_POSITION, GoldMineConstants.NORTH);
		
		ObjectInstance wall3 = new MutableObjectInstance(domain.getObjectClass(GoldMineConstants.CLS_WALL), "wall3");
		wall3.setValue(GoldMineConstants.ATT_X, 2);
		wall3.setValue(GoldMineConstants.ATT_Y, 3);
		wall3.setValue(GoldMineConstants.ATT_POSITION, GoldMineConstants.NORTH);
		
		ObjectInstance wall4 = new MutableObjectInstance(domain.getObjectClass(GoldMineConstants.CLS_WALL), "wall3");
		wall4.setValue(GoldMineConstants.ATT_X, 3);
		wall4.setValue(GoldMineConstants.ATT_Y, 1);
		wall4.setValue(GoldMineConstants.ATT_POSITION, GoldMineConstants.NORTH);
		
		s = addDefaultWalls(domain,s);
		
		s.addObject(agent1);
		s.addObject(agent2);
		s.addObject(agent3);
		s.addObject(gold1);
		s.addObject(gold2);
		s.addObject(gold3);
		s.addObject(gold4);
		s.addObject(gold5);
		s.addObject(gold6);
		s.addObject(wall1);
		s.addObject(wall2);
		s.addObject(wall3);
		s.addObject(wall4);
		
		return s;
	}

	/**
	 * Add the enclosing walls
	 * @param domain domain description
	 * @param s state where the walls will be added
	 * @return modified state
	 */
	protected State addDefaultWalls(Domain domain, State s) {
		int xWall,yWall;
		
		String wallName;
		ObjectInstance wallObj;
		int num = 0;
		
		//yWall fixed 
		for(int i = 0; i < this.sizeX; i++){
			xWall = i;
			yWall = 0;
			wallName = "dWall"+num++;
			
			wallObj = new MutableObjectInstance(domain.getObjectClass(GoldMineConstants.CLS_WALL),wallName);
			wallObj.setValue(GoldMineConstants.ATT_X, xWall);
			wallObj.setValue(GoldMineConstants.ATT_Y, yWall);
			wallObj.setValue(GoldMineConstants.ATT_POSITION, GoldMineConstants.SOUTH);
			s.addObject(wallObj);
			
			yWall = this.sizeY-1;
			wallName = "dWall"+num++;
						
			wallObj = new MutableObjectInstance(domain.getObjectClass(GoldMineConstants.CLS_WALL),wallName);
			wallObj.setValue(GoldMineConstants.ATT_X, xWall);
			wallObj.setValue(GoldMineConstants.ATT_Y, yWall);
			wallObj.setValue(GoldMineConstants.ATT_POSITION, GoldMineConstants.NORTH);
			s.addObject(wallObj);	
		}
		
		//xWall fixed on 0
		for(int i = 0; i < this.sizeY; i++){
			yWall = i;
			xWall = 0;
			wallName = "dWall"+num++;
			
			wallObj = new MutableObjectInstance(domain.getObjectClass(GoldMineConstants.CLS_WALL),wallName);
			wallObj.setValue(GoldMineConstants.ATT_X, xWall);
			wallObj.setValue(GoldMineConstants.ATT_Y, yWall);
			wallObj.setValue(GoldMineConstants.ATT_POSITION, GoldMineConstants.WEST);
			s.addObject(wallObj);
			
			xWall = this.sizeX-1;
			wallName = "dWall"+num++;
						
			wallObj = new MutableObjectInstance(domain.getObjectClass(GoldMineConstants.CLS_WALL),wallName);
			wallObj.setValue(GoldMineConstants.ATT_X, xWall);
			wallObj.setValue(GoldMineConstants.ATT_Y, yWall);
			wallObj.setValue(GoldMineConstants.ATT_POSITION, GoldMineConstants.EAST);
			s.addObject(wallObj);	
		}
		
		
		return s;
	}
	/**
	 * Returns a terminal function that ends the episode when the PF NoGoldPF is true
	 * @param domain domain description
	 * @return terminal function
	 */
	public static TerminalFunction getStandardTerminalFunction(Domain domain){
		return new SinglePFTF(domain.getPropFunction(GoldMineConstants.PF_ALL_GOLD), true);		
	}
	
	/**
	 * main method created for domain tests
	 * 	 */
	public static void main(String[] args) {
		MultiagentGoldMine gen = new MultiagentGoldMine();
		//MultiagentGoldMine gen = new MultiagentGoldMine(2,2);
		SGDomain domain = (SGDomain) gen.generateDomain();
		
		double gamma = 0.9;
		double epsilon = 0.1;
		
		JointReward rewardFunction = new GoldMineJointReward(domain,gamma);
		
		State initialState = gen.getExampleState(domain);
		//State initialState = gen.getMinimalState(domain);
		
		World w = new World(domain, rewardFunction, new SinglePFTF(domain.getPropFunction(GoldMineConstants.PF_ALL_GOLD)),
				new ConstantSGStateGenerator(initialState));
		
		//SGAgent agent1 = new RandomSGAgent();
		DOOQLearning agent1 = new DOOQLearning(domain,0,gamma,epsilon, new Random(0));
		DOOQLearning agent2 = new DOOQLearning(domain,0,gamma,epsilon, new Random(1));
		DOOQLearning agent3 = new DOOQLearning(domain,0,gamma,epsilon, new Random(2));
		
		agent1.joinWorld(w, gen.agentType);
		agent2.joinWorld(w, gen.agentType);
		agent3.joinWorld(w, gen.agentType);
		
		agent1.setHashableStateFactory(new GoldMineHashFactory(agent1.getAgentName()));
		agent2.setHashableStateFactory(new GoldMineHashFactory(agent2.getAgentName()));
		agent3.setHashableStateFactory(new GoldMineHashFactory(agent3.getAgentName()));
		
	
		Visualizer visualizer = GoldMineMAVisualizer.getVisualizer(gen.getSizeX(), gen.getSizeY());
		//VisualExplorer vExplorer = new VisualExplorer(domain, visualizer, initialState);
		VisualWorldObserver vExplorer = new VisualWorldObserver(domain, visualizer);
		//vExplorer.setFrameDelay(20);
		//vExplorer.initGUI();
		
		//w.addWorldObserver(vExplorer);
		int epis = 0;
		//Run training
		
		//Disable Debug Messages.
		DPrint.toggleCode(w.getDebugId(),false);
		
		//---------------------------
		// Starting Exploration
		//-----------------------------
		int numberEpi = 5000;
		int[] steps = new int[numberEpi];
		while(epis<numberEpi-1){
			
			//update our visualizer to the start state of the game
			visualizer.updateState(initialState);
			//run the game.
			GameAnalysis a = w.runGame();
			epis++;
			steps[epis] = a.maxTimeStep();
			System.out.println("OK - "+epis+"-- "+a.maxTimeStep()+"\n");
		}
		
		for(int i=0; i<epis/100;i++){
			float mean = 0;
			for(int y=i*100; y<i*100 + 100; y++){
				mean+= steps[y];
			}
			mean /= 100;
			System.out.println("\nMean "+i+":    "+mean+"\n");
		}
		
		
		//---------------------------------------
		// Starting one episode of exploitation
		//---------------------------------------
		agent1.setExploration(false);
		agent2.setExploration(false);
		agent3.setExploration(false);
		
		//One game of exploiting
		w.addWorldObserver(vExplorer);
		vExplorer.initGUI();
		//Enables Debug Messages.
	    DPrint.toggleCode(w.getDebugId(),true);
		vExplorer.setFrameDelay(1500);
		visualizer.updateState(initialState);
		w.runGame();
		
		
		
	}
	
	public int getSizeX() {
		return sizeX;
	}

	public int getSizeY() {
		return sizeY;
	}
	
	public SGAgentType getAgentType() {
		return this.agentType;
	}
	
	
}
