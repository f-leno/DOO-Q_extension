/**
 * 
 */
package experiment.MAQLExtensions;

import java.util.Random;

import burlap.behavior.learningrate.LearningRate;
import burlap.behavior.stochasticgames.agents.maql.MultiAgentQLearning;
import burlap.behavior.stochasticgames.madynamicprogramming.SGBackupOperator;
import burlap.behavior.valuefunction.ValueFunctionInitialization;
import burlap.oomdp.statehashing.HashableStateFactory;
import burlap.oomdp.stochasticgames.SGDomain;
import experiment.QTableInspectorAgent;

/**
 * @author Felipe Leno da Silva
 * Small Modification in MultiAgentQLearning to extract Q Table size 
 */
public class GMMultiAgentQLearning extends MultiAgentQLearning implements QTableInspectorAgent{

	private double epsilon;

	public GMMultiAgentQLearning(SGDomain d, double discount, double learningRate, HashableStateFactory hashFactory,
			double qInit, SGBackupOperator backupOperator, boolean queryOtherAgentsForTheirQValues, double epsilon) {
		super(d, discount, learningRate, hashFactory, qInit, backupOperator, queryOtherAgentsForTheirQValues);
		this.myQSource = new GMHashBackedQSource(this.hashingFactory, this.qInit);
		this.epsilon = epsilon;
	}

	public GMMultiAgentQLearning(SGDomain d, double discount, LearningRate learningRate,
			HashableStateFactory hashFactory, ValueFunctionInitialization qInit, SGBackupOperator backupOperator,
			boolean queryOtherAgentsForTheirQValues, double epsilon) {
		super(d, discount, learningRate, hashFactory, qInit, backupOperator, queryOtherAgentsForTheirQValues);
		this.myQSource = new GMHashBackedQSource(this.hashingFactory, this.qInit);
		this.epsilon = epsilon;
	}

	@Override
	public long getQTableSize() {
		long qSize = ((GMHashBackedQSource)this.myQSource).getQTableSize();
		return qSize;
	}

	@Override
	public void resetAgent(Random rand) {
		this.myQSource = new GMHashBackedQSource(this.hashingFactory, this.qInit);
		((GMEGreedyMaxWellfare)this.learningPolicy.getJointPolicy()).setRandom(rand);
		
		System.gc();
		
	}

	@Override
	public void setExploration(boolean exploring) {
		if(exploring)
			((GMEGreedyMaxWellfare)this.learningPolicy.getJointPolicy()).setEpsilon(this.epsilon);
		else
			((GMEGreedyMaxWellfare)this.learningPolicy.getJointPolicy()).setEpsilon(0.0);
		
	}

}
