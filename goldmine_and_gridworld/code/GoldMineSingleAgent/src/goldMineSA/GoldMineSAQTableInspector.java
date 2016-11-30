/**
 * 
 */
package goldMineSA;

import java.util.Random;

/**
 * @author Ruben Glatt
 * Interface to return a counting of QTable Size
 */
public interface GoldMineSAQTableInspector {
	/**
	 * Return the current size of Q-Table
	 * @return number of Q-Table Entries
	 */
	public long getQTableSize();

	/**
	 * Starts or stops the exploration
	 * @param value Activates/deactivates exploration
	 */
	public void setExploration(boolean value);
}
