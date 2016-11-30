/**
 * 
 */
package goldMineSA;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;

import experiment.ExperimentResultSaver;
import experiment.GoldMineConstantStateGenerator;
import experiment.QTableInspectorAgent;
import burlap.behavior.learningrate.LearningRate;
import burlap.behavior.policy.Policy;
import burlap.behavior.singleagent.EpisodeAnalysis;
import burlap.behavior.singleagent.auxiliary.EpisodeSequenceVisualizer;
import burlap.behavior.singleagent.auxiliary.performance.LearningAlgorithmExperimenter;
import burlap.behavior.singleagent.auxiliary.performance.PerformanceMetric;
import burlap.behavior.singleagent.auxiliary.performance.TrialMode;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.oomdp.auxiliary.DomainGenerator;
import burlap.oomdp.auxiliary.StateGenerator;
import burlap.oomdp.auxiliary.common.ConstantStateGenerator;
import burlap.oomdp.auxiliary.common.NullTermination;
import burlap.oomdp.core.Attribute;
import burlap.oomdp.core.Attribute.AttributeType;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.ObjectClass;
import burlap.oomdp.core.TerminalFunction;
import burlap.oomdp.core.objects.MutableObjectInstance;
import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.MutableState;
import burlap.oomdp.core.states.State;
import burlap.oomdp.legacy.StateParser;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.singleagent.SADomain;
import burlap.oomdp.singleagent.common.NullRewardFunction;
import burlap.oomdp.singleagent.environment.SimulatedEnvironment;
import burlap.oomdp.singleagent.explorer.TerminalExplorer;
import burlap.oomdp.singleagent.explorer.VisualExplorer;
import burlap.oomdp.statehashing.HashableStateFactory;
import burlap.oomdp.visualizer.StateRenderLayer;
import burlap.oomdp.visualizer.Visualizer;


/**
 * @author Ruben Glatt
 *
 */
public class GoldMineSA implements DomainGenerator {

	// grid limits
	public int sizeX;
	public int sizeY;
	
	/**
	 * Standard constructor that initiates objects of this class with the
	 * parameters from GoldMineSAConstants
	 */
	public GoldMineSA(){
		this.sizeX = GoldMineSAConstants.DEFAULT_SIZE_X;
		this.sizeY = GoldMineSAConstants.DEFAULT_SIZE_Y;
	}
	
	/**
	 * Constructor to define the world size
	 * @param sizeX size of horizontal axis
	 * @param sizeY size of vertical axis
	 */
	public GoldMineSA(int sizeX,int sizeY){
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}
	
	//-------------------------
	// define some getters
	//-------------------------
	public int getSizeX() {
		return this.sizeX;
	}

	public int getSizeY() {
		return this.sizeY;
	}
	
	
	@Override
	public Domain generateDomain() {
		//-------------------------------------------------------------
		// Initialize a single agent domain
		//-------------------------------------------------------------
		SADomain domain            = new SADomain();

		//-------------------------------------------------------------
		// Define attributes and set their parameters
		//-------------------------------------------------------------
		Attribute att_x            = new Attribute(domain, 
                                                   GoldMineSAConstants.ATT_X, 
                                                   Attribute.AttributeType.INT);
        att_x.setLims(0, this.getSizeX()-1);
        Attribute att_y            = new Attribute(domain, 
                                                   GoldMineSAConstants.ATT_Y, 
                                                   Attribute.AttributeType.INT);
        //Attribute att_active       = new Attribute(domain,
		//							        		GoldMineSAConstants.ATT_ACTIVE, 
		//							                Attribute.AttributeType.BOOLEAN);
        att_y.setLims(0, this.getSizeY()-1);
        Attribute att_position     = new Attribute(domain, 
                                                   GoldMineSAConstants.ATT_POSITION, 
                                                   Attribute.AttributeType.DISC);
		att_position.setDiscValues(GoldMineSAConstants.ATT_POSITION_VALUES);
		
		//--------------------------------------------------------------
		// Create Classes and add attributes
		//-------------------------------------------------------------
		ObjectClass cls_miner      = new ObjectClass(domain, GoldMineSAConstants.CLS_MINER);
		cls_miner.addAttribute(att_x);
		cls_miner.addAttribute(att_y);
		ObjectClass cls_gold       = new ObjectClass(domain, GoldMineSAConstants.CLS_GOLD);
		cls_gold.addAttribute(att_x);
		cls_gold.addAttribute(att_y);
		//cls_gold.addAttribute(att_active);
		ObjectClass cls_wall       = new ObjectClass(domain, GoldMineSAConstants.CLS_WALL);
		cls_wall.addAttribute(att_x);
		cls_wall.addAttribute(att_y);
		cls_wall.addAttribute(att_position);

		//---------------------------------------------------------------
		// Create possible actions
		//---------------------------------------------------------------
		new GoldMineSAActionMoveMiner(GoldMineSAConstants.NORTH, domain, this.sizeX, this.sizeY);
		new GoldMineSAActionMoveMiner(GoldMineSAConstants.SOUTH, domain, this.sizeX, this.sizeY);
		new GoldMineSAActionMoveMiner(GoldMineSAConstants.EAST,  domain, this.sizeX, this.sizeY);
		new GoldMineSAActionMoveMiner(GoldMineSAConstants.WEST,  domain, this.sizeX, this.sizeY);
		new GoldMineSAActionGetGold(GoldMineSAConstants.ACTION_GOLD_COLLECT , domain);

		//----------------------------------------------------------------
		// Create proposition functions to observe certain events
		//----------------------------------------------------------------
		String[] involved_classes = {GoldMineSAConstants.CLS_MINER, GoldMineSAConstants.CLS_WALL};
		new GoldMineSAPropFuncWallCollision(GoldMineSAConstants.PF_WALL_NORTH, domain, involved_classes, GoldMineSAConstants.NORTH);
		new GoldMineSAPropFuncWallCollision(GoldMineSAConstants.PF_WALL_SOUTH, domain, involved_classes, GoldMineSAConstants.SOUTH);
		new GoldMineSAPropFuncWallCollision(GoldMineSAConstants.PF_WALL_WEST,  domain, involved_classes, GoldMineSAConstants.WEST);
		new GoldMineSAPropFuncWallCollision(GoldMineSAConstants.PF_WALL_EAST,  domain, involved_classes, GoldMineSAConstants.EAST);
		new GoldMineSAPropFuncGoldCollected(domain);
		
		return domain;
	}

	
	public StateRenderLayer getStateRenderLayer(){
		StateRenderLayer rl = new StateRenderLayer();
		rl.addObjectClassPainter(GoldMineSAConstants.CLS_WALL,new GoldMineSAPainterWall(sizeX,sizeY));
		rl.addObjectClassPainter(GoldMineSAConstants.CLS_GOLD, new GoldMineSAPainterGold(sizeX,sizeY));
		rl.addObjectClassPainter(GoldMineSAConstants.CLS_MINER, new GoldMineSAPainterMiner(sizeX,sizeY));
		return rl;
	}
	

	public Visualizer getVisualizer(){
		return new Visualizer(this.getStateRenderLayer());
	}


	public static void main(String [] args){
		// set environment variables
		GoldMineSA goldMineWorld = new GoldMineSA();
		final Domain domain = goldMineWorld.generateDomain();
		State nextState = GoldMineSAConstants.setStartingState(domain);
		final HashableStateFactory hashFactory = new GoldMineSAHashableStateFactory();
		
		// set learning variables
		Double epsilon = 0.1;
		RewardFunction rf = new GoldMineSAFuncReward(domain, GoldMineSAConstants.DEFAULT_GAMMA);
		TerminalFunction tf = GoldMineSAConstants.getStandardTerminalFunction(domain);
		
		int maxEpisodeSize = -1; // unlimited
		GoldMineSAAlgorithmQLearning qLearner = new GoldMineSAAlgorithmQLearning(domain, 
				                               GoldMineSAConstants.DEFAULT_GAMMA, 
				                               hashFactory,
				                               GoldMineSAConstants.DEFAULT_Q, 
				                               GoldMineSAConstants.DEFAULT_ALPHA, 
				                               null, 
				                               maxEpisodeSize);
		int policySeed = 0;
		Policy learningPolicy = new GoldMineSAAlgorithmEGreedy(qLearner, epsilon, policySeed);
		qLearner.setLearningPolicy(learningPolicy);
		// 
		// String pathAndBaseNameToUse = "experiments/Q/result";
		String pathAndBaseNameToUse = "";
		
		// Run trials
		int stateSeed               = 0;
		nextState = GoldMineSAConstants.sortSampleState(nextState, 
				new Random(stateSeed), 
                goldMineWorld.getSizeX(), 
                goldMineWorld.getSizeY());
		
		int nTrials                 = 70;
		int episodes                = 5000;
		int evaluationInterval      = 10;   // min 2 or no exploration!
		int maxExploitSteps         = 1000;
		
		System.out.println("    0.0s: ############# START TRIALS #############");
		double startTime = System.currentTimeMillis();
		DecimalFormat df = new DecimalFormat("####0.0");
		for (int j = 0; j < nTrials; j++){
			SimulatedEnvironment env    = new SimulatedEnvironment(domain,rf, tf, nextState);
			
			double currentTime = System.currentTimeMillis();
			String duration    = String.format("%1$8s", df.format((currentTime-startTime)/1000));
			System.out.println(duration+"s: ############# TRIAL "+(j+1)+" of "+nTrials+": ");

			String fileContent = "epis;QTable;steps;cumReward;discountedReward\n"; 
			for(int i = 0; i < episodes; i++){
				if ((i+1) % evaluationInterval == 0){
					long              qTableSize           = qLearner.getQTableSize();
					Policy            exploitationPolicy   = new GoldMineSAAlgorithmEGreedy(qLearner, 0.0, policySeed);
					//EpisodeAnalysis   ea                   = exploitationPolicy.evaluateBehavior(nextState, rf, tf, maxExploitSteps);
					EpisodeAnalysis   ea                   = exploitationPolicy.evaluateBehavior(env, maxExploitSteps);
					//String            yamlOut              = ea.serialize();
					//EpisodeAnalysis   read                 = EpisodeAnalysis.parseEpisode(domain, yamlOut);
					//int               episodeSteps         = read.actionSequence.size();
					List<Double>      rewardSequence       = ea.rewardSequence;
					int               episodeSteps         = rewardSequence.size();
					double            rewardSum            = Math.round(ea.getDiscountedReturn(1)*100.0)/100.0;
					double            discountedRewardSum  = Math.round(ea.getDiscountedReturn(GoldMineSAConstants.DEFAULT_GAMMA)*100.0)/100.0;
					fileContent += (i+1)+";"+qTableSize+";"+episodeSteps+";"+df.format(rewardSum)+";"+df.format(discountedRewardSum)+";\n";
					
					currentTime  = System.currentTimeMillis();
					duration     = String.format("%1$8s", df.format((currentTime-startTime)/1000));
					System.out.println(duration +"s: Episode "    + String.format("%1$4s", (i+1)) 
							                    +", qTableSize "  + String.format("%1$8s", qTableSize) 
							                    +", Steps "       + String.format("%1$4s", episodeSteps)
							                    +", Reward "      + String.format("%1$6s", df.format(rewardSum))
							                    +", Discounted Reward "      + String.format("%1$6s", df.format(discountedRewardSum)));
					//System.out.println(yamlOut.substring(0, 10000));
					//System.out.println(ea.getActionSequenceString());
					//System.out.println(ea.getState(episodeSteps).toString());
				}
				qLearner.runLearningEpisode(env);
				/*
				EpisodeAnalysis ea = qLearner.runLearningEpisode(env);
				List<GroundedAction> actionSequence = ea.actionSequence;
				int counter = 0;
				GroundedAction lastAction = null;
				for (GroundedAction currentAction : actionSequence){
					counter++;
					if (lastAction != null && lastAction.actionName() == currentAction.actionName() && currentAction.actionName()==GoldMineSAConstants.ACTION_GOLD_COLLECT){
						String[] actionParams = currentAction.getParametersAsString();
						System.out.println("Episode "+i+": "+actionParams[0]+" "+currentAction.actionName()+" at step "+(counter-1)+" and "+counter);
					}
					lastAction = currentAction;
				}
				*/
				
				env.resetEnvironment();
			}
				
			try (Writer writer = new BufferedWriter(new OutputStreamWriter(
			              new FileOutputStream(pathAndBaseNameToUse+(j+1)+".csv"), "utf-8"))) {
					writer.write(fileContent);
					writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			stateSeed++;
			nextState = GoldMineSAConstants.sortSampleState(nextState, 
					new Random(stateSeed), 
                    goldMineWorld.getSizeX(), 
                    goldMineWorld.getSizeY());
			qLearner.resetSolver();
			System.gc();
		}
		double currentTime = System.currentTimeMillis();
		//String duration = String.format("%06f", round((currentTime-startTime)/1000),2);
		String duration = String.format("%1$8s", df.format((currentTime-startTime)/1000));
		System.out.println(duration +"s: ############# END TRIALS ##############");
	}

}
