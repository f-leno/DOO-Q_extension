/**
 * 
 */
package experiment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeoutException;

import burlap.behavior.stochasticgames.PolicyFromJointPolicy;
import burlap.behavior.stochasticgames.madynamicprogramming.backupOperators.MaxQ;
import burlap.debugtools.DPrint;
import burlap.oomdp.auxiliary.common.SinglePFTF;
import burlap.oomdp.core.TerminalFunction;
import burlap.oomdp.core.states.State;
import burlap.oomdp.stochasticgames.JointReward;
import burlap.oomdp.stochasticgames.SGAgent;
import burlap.oomdp.stochasticgames.SGDomain;
import burlap.oomdp.stochasticgames.World;
import distributedQLearning.DistributedQLearning;
import domain.GoldMineConstants;
import domain.GoldMineJointReward;
import domain.MultiagentGoldMine;
import experiment.MAQLExtensions.GMEGreedyMaxWellfare;
import experiment.MAQLExtensions.GMMultiAgentQLearning;
import goldmine.DOOQLearning;
import goldmine.GoldMineHashFactory;

/**
 * @author Felipe Leno da Silva
 * Class used to execute experiments for BRACIS2016
 */
public class ExperimentBRACIS2016 {





	public static void main(String[] args) {

		boolean testDOOQLearning = true;
		boolean testMAQLearning = true;
		boolean testDQLLearning = true;



		//------------------------------------------------
		// Building the Multiagent Goldmine domain
		//-----------------------------------------------
		MultiagentGoldMine gen = new MultiagentGoldMine();
		SGDomain domain = (SGDomain) gen.generateDomain();

		//Parameters
		double gamma = 0.9;
		double epsilon = 0.1;
		double initialQ = 0;
		double learningRate = 0.2;
		int nTrials = 70;
		int numberEpiTotal = 5000;
		int episExploitation = 1;

		long timeOutTrial = 5000; //Time out for one trial in seconds

		int originalStateSeed =  0;
		int originalAgent1Seed = 1;
		int originalAgent2Seed = 100;
		int originalAgent3Seed = 200;

		int stateSeed = originalStateSeed;
		int agent1Seed = originalAgent1Seed;
		int agent2Seed = originalAgent2Seed;
		int agent3Seed = originalAgent3Seed;




		//Seeds
		Random randState = new Random(stateSeed);
		Random randAgent1 = new Random(agent1Seed);
		Random randAgent2 = new Random(agent2Seed);
		Random randAgent3 = new Random(agent3Seed);

		JointReward rewardFunction = new GoldMineJointReward(domain,gamma);
		TerminalFunction tf = new SinglePFTF(domain.getPropFunction(GoldMineConstants.PF_ALL_GOLD));
		State exampleState = gen.getExampleState(domain);
		//GoldMineFixedWallStateGenerator stateGenerator = new GoldMineFixedWallStateGenerator(exampleState,randState,gen.getSizeX(),gen.getSizeY());
		GoldMineConstantStateGenerator stateGenerator = new GoldMineConstantStateGenerator(exampleState,randState,gen.getSizeX(),gen.getSizeY());



		if(testDOOQLearning){			
			World w = new World(domain, rewardFunction, tf, stateGenerator);

			//-----------------------------------------------
			// Creating agents
			//-----------------------------------------------
			DOOQLearning agent1 = new DOOQLearning(domain,initialQ,gamma,epsilon, randAgent1);
			DOOQLearning agent2 = new DOOQLearning(domain,initialQ,gamma,epsilon, randAgent2);
			DOOQLearning agent3 = new DOOQLearning(domain,initialQ,gamma,epsilon, randAgent3);

			agent1.joinWorld(w, gen.getAgentType());
			agent2.joinWorld(w, gen.getAgentType());
			agent3.joinWorld(w, gen.getAgentType());

			agent1.setHashableStateFactory(new GoldMineHashFactory(agent1.getAgentName()));
			agent2.setHashableStateFactory(new GoldMineHashFactory(agent2.getAgentName()));
			agent3.setHashableStateFactory(new GoldMineHashFactory(agent3.getAgentName()));
			//Disable Debug Messages.
			DPrint.toggleCode(w.getDebugId(),false);


			List<QTableInspectorAgent> agents = new ArrayList<QTableInspectorAgent> ();
			agents.add(agent1);
			agents.add(agent2);
			agents.add(agent3);

			int initialEpisode = 100;
			int episodeIncrement = 100;
			String file  = "experiments/DOOQ/result";
			ExperimentResultSaver resultSaver = new ExperimentResultSaver(initialEpisode,episodeIncrement,file,agents,gamma);


			SGTestAlgorithm testDOOQ =  new SGTestAlgorithm(resultSaver, episodeIncrement, numberEpiTotal, episExploitation, w);
			//-----------------------
			// Starting Trials
			//-----------------------
			for(int k = 0; k<nTrials;k++){
				try{
					ExperimentBRACIS2016.executeWithTimeOut(testDOOQ,timeOutTrial);
				}
				catch (TimeoutException e){
					resultSaver.endTrial();
					agent1.setExploration(true);
					agent2.setExploration(true);
					agent3.setExploration(true);
					System.out.println("\nTime Out on Trial\n");
				}
				catch(Exception e){
					e.printStackTrace();
				}
				//Resets for next Trial
				agent1Seed++;
				agent2Seed++;
				agent3Seed++;
				stateSeed++;
				//stateLockSeed++;

				agent1.resetAgent(new Random(agent1Seed));
				agent2.resetAgent(new Random(agent2Seed));
				agent3.resetAgent(new Random(agent3Seed));
				stateGenerator.lockSeed(stateSeed);
				System.gc();
				System.out.println("Trial "+(k+1)+" ok-------------------------- DOOQ");
			}






		}





		//------------------------------------------------
		// MAQ-Learning test
		//------------------------------------------------

		stateSeed = originalStateSeed;
		agent1Seed = originalAgent1Seed;
		agent2Seed = originalAgent2Seed;
		agent3Seed = originalAgent3Seed;
		stateGenerator.lockSeed(stateSeed);

		if(testMAQLearning){
			World w = new World(domain, rewardFunction, tf, stateGenerator);

			//-----------------------------------------------
			// Creating agents
			//-----------------------------------------------
			GoldMineHashFactory hashFac1 =  new GoldMineHashFactory();
			GoldMineHashFactory hashFac2 =  new GoldMineHashFactory();
			GoldMineHashFactory hashFac3 =  new GoldMineHashFactory();
			GMMultiAgentQLearning agent1 = new GMMultiAgentQLearning(domain, gamma, learningRate,
					hashFac1, initialQ, new MaxQ(), true,epsilon);
			GMMultiAgentQLearning agent2 = new GMMultiAgentQLearning(domain, gamma, learningRate,
					hashFac2, initialQ, new MaxQ(), true, epsilon);
			GMMultiAgentQLearning agent3 = new GMMultiAgentQLearning(domain, gamma, learningRate,
					hashFac3, initialQ, new MaxQ(), true, epsilon);

			agent1.joinWorld(w, gen.getAgentType());
			agent2.joinWorld(w, gen.getAgentType());
			agent3.joinWorld(w, gen.getAgentType());

			hashFac1.setAgentIdentifier(agent1.getAgentName());
			hashFac2.setAgentIdentifier(agent2.getAgentName());
			hashFac3.setAgentIdentifier(agent3.getAgentName());




			//set their policies to be a epsilon greedy maxwelfare (which CoCo-Q uses) policy over the joint actions
			//with ties broken randomly
			GMEGreedyMaxWellfare ja1 = new GMEGreedyMaxWellfare(agent1, epsilon);
			GMEGreedyMaxWellfare ja2 = new GMEGreedyMaxWellfare(agent2, epsilon);
			GMEGreedyMaxWellfare ja3 = new GMEGreedyMaxWellfare(agent3, epsilon);
			ja1.setBreakTiesRandomly(true);
			ja2.setBreakTiesRandomly(true);
			ja3.setBreakTiesRandomly(true);

			agent1.setLearningPolicy(new PolicyFromJointPolicy(agent1.getAgentName(), ja1));
			agent2.setLearningPolicy(new PolicyFromJointPolicy(agent2.getAgentName(), ja2));
			agent3.setLearningPolicy(new PolicyFromJointPolicy(agent3.getAgentName(), ja3));

			//Disable Debug Messages.
			DPrint.toggleCode(w.getDebugId(),false);

			List<QTableInspectorAgent> agents = new ArrayList<QTableInspectorAgent> ();
			agents.add(agent1);
			agents.add(agent2);
			agents.add(agent3);

			int initialEpisode = 100;
			int episodeIncrement = 100;
			String file  = "experiments/MAQL/result";
			ExperimentResultSaver resultSaver = new ExperimentResultSaver(initialEpisode,episodeIncrement,file,agents,gamma);

			SGTestAlgorithm testMAQL =  new SGTestAlgorithm(resultSaver, episodeIncrement, numberEpiTotal, episExploitation, w);
			//-----------------------
			// Starting Trials
			//-----------------------
			for(int k = 0; k<nTrials;k++){
				try{
					ExperimentBRACIS2016.executeWithTimeOut(testMAQL,timeOutTrial);
				}
				catch (TimeoutException e){
					resultSaver.endTrial();
					agent1.setExploration(true);
					agent2.setExploration(true);
					agent3.setExploration(true);
					System.out.println("\nTime Out on Trial\n");
				}
				catch(Exception e){
					e.printStackTrace();
				}
				//Resets for next Trial
				agent1Seed++;
				agent2Seed++;
				agent3Seed++;
				stateSeed++;
				//stateLockSeed++;

				agent1.resetAgent(new Random(agent1Seed));
				agent2.resetAgent(new Random(agent2Seed));
				agent3.resetAgent(new Random(agent3Seed));
				stateGenerator.lockSeed(stateSeed);
				System.gc();
				System.out.println("Trial "+(k+1)+" ok--------------------------MQL");
			}


		}








		//------------------------------------------------
		// Distributed Q-Learning test
		//------------------------------------------------

		stateSeed = originalStateSeed;
		agent1Seed = originalAgent1Seed;
		agent2Seed = originalAgent2Seed;
		agent3Seed = originalAgent3Seed;
		stateGenerator.lockSeed(stateSeed);

		if(testDQLLearning){
			World w = new World(domain, rewardFunction, tf, stateGenerator);

			//-----------------------------------------------
			// Creating agents
			//-----------------------------------------------

			DistributedQLearning agent1 = new DistributedQLearning(domain,initialQ,gamma,epsilon, randAgent1);
			DistributedQLearning agent2 = new DistributedQLearning(domain,initialQ,gamma,epsilon, randAgent2);
			DistributedQLearning agent3 = new DistributedQLearning(domain,initialQ,gamma,epsilon, randAgent3);

			agent1.joinWorld(w, gen.getAgentType());
			agent2.joinWorld(w, gen.getAgentType());
			agent3.joinWorld(w, gen.getAgentType());

			
			//Disable Debug Messages.
			DPrint.toggleCode(w.getDebugId(),false);


			List<QTableInspectorAgent> agents = new ArrayList<QTableInspectorAgent> ();
			agents.add(agent1);
			agents.add(agent2);
			agents.add(agent3);

			int initialEpisode = 100;
			int episodeIncrement = 100;
			String file  = "experiments/DQL/result";
			ExperimentResultSaver resultSaver = new ExperimentResultSaver(initialEpisode,episodeIncrement,file,agents,gamma);


			SGTestAlgorithm testDQL =  new SGTestAlgorithm(resultSaver, episodeIncrement, numberEpiTotal, episExploitation, w);
			//-----------------------
			// Starting Trials
			//-----------------------
			for(int k = 0; k<nTrials;k++){
				try{
					ExperimentBRACIS2016.executeWithTimeOut(testDQL,timeOutTrial);
				}
				catch (TimeoutException e){
					resultSaver.endTrial();
					agent1.setExploration(true);
					agent2.setExploration(true);
					agent3.setExploration(true);
					System.out.println("\nTime Out on Trial\n");
				}
				catch(Exception e){
					e.printStackTrace();
				}
				//Resets for next Trial
				agent1Seed++;
				agent2Seed++;
				agent3Seed++;
				stateSeed++;
				//stateLockSeed++;

				agent1.resetAgent(new Random(agent1Seed));
				agent2.resetAgent(new Random(agent2Seed));
				agent3.resetAgent(new Random(agent3Seed));
				stateGenerator.lockSeed(stateSeed);
				System.gc();
				System.out.println("Trial "+(k+1)+" ok--------------------------DQL");
			}


		}

		System.out.println("End of Experiment");





	}

	/**
	 * Execute a procedure with a timeout. Throws a InterruptedException if the timeout has been exceeded.
	 * @param test the Runnable object to be executed
	 * @param timeOutTrial the time out.
	 * @throws InterruptedException Throws a InterruptedException if the timeout has been exceeded.
	 */
	private static void executeWithTimeOut(Runnable test, long timeOutTrial) throws TimeoutException {

		Thread t = new Thread(test);

		long startTime = System.currentTimeMillis();
		long endTime = startTime + (timeOutTrial*1000L);
		boolean timeout = false;

		t.start();

		while(!timeout && t.isAlive()){
			try{
				Thread.sleep(50);
			}
			catch(Exception e){}
			if(System.currentTimeMillis() > endTime){
				timeout = true;
				t.stop();
			}
		}
		if(timeout){
			throw new TimeoutException();
		}


	}

	/**
	 * Used to allow interruption of extremely slow experiments (through an ExecutorSevice)
	 */
	static public class SGTestAlgorithm implements Runnable{

		ExperimentResultSaver resultSaver; //Stores the experiment in files
		int episodeIncrement; //Constant to indicate the number of episodes before exploiting
		int numberEpiTotal; //Total number of episodes in a trial
		int episExploitation; //Number of episodes of exploitation
		World w; //World to execute tests

		public SGTestAlgorithm(ExperimentResultSaver resultSaver,int episodeIncrement, int numberEpiTotal, int episExploitation, World w){
			this.resultSaver = resultSaver;
			this.episodeIncrement = episodeIncrement;
			this.numberEpiTotal = numberEpiTotal;
			this.episExploitation = episExploitation;
			this.w = w;
		}




		@Override
		public void run() {

			List<QTableInspectorAgent> agents = new ArrayList<QTableInspectorAgent>(); // agents being tested
			for(SGAgent ag: w.getRegisteredAgents()){
				agents.add((QTableInspectorAgent)ag);
			}
			resultSaver.startTrial();
			//---------------------------
			// Starting Exploration
			//-----------------------------
			int epis = 1;
			int numberBin = episodeIncrement;

			long start = System.currentTimeMillis();
			long end = 0;
			//Until limit of episodes
			while(epis<=numberEpiTotal){


				//run the game.
				w.runGame();

				//Exploitation test
				if(epis % numberBin == 0){
					//---------------------------------------
					// Starting episodes of exploitation
					//---------------------------------------
					for(int i=0;i<agents.size();i++){
						agents.get(i).setExploration(false);
					}


					w.addWorldObserver(resultSaver);
					//stateGenerator.lockSeed(stateLockSeed);

					for(int m = 0;m<episExploitation;m++){
						w.runGame(500); //Extrair métricas
					}

					//stateGenerator.restoreSeed();					
					w.removeWorldObserver(resultSaver);

					for(int i=0;i<agents.size();i++){
						agents.get(i).setExploration(true);
					}

					end = System.currentTimeMillis();
					System.out.println("OK "+epis+"---- "+((end - start)/1000)+"s");
					start = end;
				}
				epis++;
			}
			resultSaver.endTrial();

		}
	}


}
