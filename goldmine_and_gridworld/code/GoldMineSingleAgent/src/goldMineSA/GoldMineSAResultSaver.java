/**
 * 
 */
package goldMineSA;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import burlap.oomdp.core.states.State;
import burlap.oomdp.stochasticgames.JointAction;
import burlap.oomdp.stochasticgames.WorldObserver;

/**
 * @author Ruben Glatt
 * Class that stores experiments results in csv files.
 * This class is intended to be used to store exploitation results after some episodes of exploration. 
 *
 */
public class GoldMineSAResultSaver implements WorldObserver {

	protected int episodeID;
	protected int initialEpisode;
	protected int episodeIncrement;
	protected int currentTrial;
	protected List<GoldMineSAQTableInspector> agents;
	protected String fileName;
	protected File outputFile;
	protected PrintWriter writer;
	
	//--------
	//Metrics
	//--------
	protected double cumulativeReward;
	protected int numberOfSteps;
	protected double qTableSize;
	
	/**
	 * Default constructor
	 * @param initialEpisode the number of the first episode to be stored
	 * @param episodeIncrement the interval between each result
	 * @param fileName output name. The trial number will also be used to define the output file's name
	 */
	public GoldMineSAResultSaver(int initialEpisode, int episodeIncrement, String fileName, List<GoldMineSAQTableInspector> agents){
		this.episodeID = initialEpisode;
		this.initialEpisode = initialEpisode;
		this.episodeIncrement = episodeIncrement;
		this.fileName = fileName;
		this.currentTrial = 0;
		this.agents = agents;
		this.outputFile = this.CreateOutputFile();
	}
	

	


	/**
	 * Reset all metrics to be written in the next line
	 */
	public void gameStarting(State s) {
		this.cumulativeReward = 0;
		this.numberOfSteps = 0;
		this.qTableSize = 0;
	}

	/* (non-Javadoc)
	 * @see burlap.oomdp.stochasticgames.WorldObserver#observe(burlap.oomdp.core.states.State, burlap.oomdp.stochasticgames.JointAction, java.util.Map, burlap.oomdp.core.states.State)
	 */
	@Override
	public void observe(State s, JointAction ja, Map<String, Double> reward, State sp) {
		// All agents receive the same reward, thus, any reward is enough
		this.cumulativeReward += reward.get(ja.getActionList().get(0).actingAgent);
		this.numberOfSteps++;

	}

	/* (non-Javadoc)
	 * @see burlap.oomdp.stochasticgames.WorldObserver#gameEnding(burlap.oomdp.core.states.State)
	 */
	@Override
	public void gameEnding(State s) {
		// Final reward processing
		for(GoldMineSAQTableInspector agent : agents){
			this.qTableSize += agent.getQTableSize();
		}
		//Mean of all agents
		this.qTableSize /= agents.size();
		recordGame();
		
		this.episodeID += this.episodeIncrement;
		

	}
	
	





	/**
	 * Save one line on file
	 */
	private void recordGame() {
		// Episode;QtableSIze;steps;reward
		String resultLine = this.episodeID        + ";" +
				            this.qTableSize       + ";" +
				            this.numberOfSteps    + ";" +
				            this.cumulativeReward + ";" +
				            "\n";
		writeInFile(resultLine);
		
	}





	/**
	 * Start a new Trial, creating a new output File
	 */
	public void startTrial(){
		this.currentTrial++;
		this.outputFile = this.CreateOutputFile();
		createFileHeader();
	}
	/**
	 * End current Trial, must be used after recording all results
	 */
	public void endTrial(){
		closeFile();
		this.outputFile = null;
		this.writer = null;
		this.episodeID = this.initialEpisode;
		
	}

	/**
	 * Creates the header for experiment files
	 */
	protected void createFileHeader() {
		String header = "epis;QTable;steps;cumReward;\n";
		this.writeInFile(header);		
	}
	






	/**
	 * Returns the file being used to export results
	 * @return a reference to the file
	 */
	public File getOutputFile(){
		return this.outputFile;
	}
	
	/**
	 * Create a new output File
	 * @return a reference to the new file
	 */
	protected File CreateOutputFile() {
		File outputFile = new File(this.fileName+this.currentTrial+".csv");
		try{
			if(!outputFile.exists()){
				outputFile.createNewFile();				
			}
			this.writer = new PrintWriter(outputFile);
			return outputFile;			
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Problems creating the output File in ExperimentResultSaver");
		}
	}
	/**
	 * write in file
	 * @param text text to be written
	 */
	private void writeInFile(String text) {
		writer.write(text);		
	}
	/**
	 * Close file for final recording
	 */
	private void closeFile() {
		writer.close();
		
	}
}
