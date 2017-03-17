/**
 * 
 */
package experiment;

import java.util.Random;

/**
 * @author Felipe Leno da Silva
 * Interface to return a counting of QTable Size
 */
public interface QTableInspectorAgent {
	/**
	 * Return the current size of Q-Table
	 * @return number of Q-Table Entries
	 */
	public long getQTableSize();

	/**
	 * Fully resets agent, erasing Q table and possible policies
	 */
	public void resetAgent(Random rand);

	/**
	 * Starts or stops the exploration
	 * @param value Activates/deactivates exploration
	 */
	public void setExploration(boolean value);
}
