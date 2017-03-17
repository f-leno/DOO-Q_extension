/**
 * 
 */
package domain.ui;

import domain.GoldMineConstants;
import burlap.oomdp.visualizer.Visualizer;

/**
 * @author Felipe Leno da Silva
 *
 * Visualizer for the Multiagent GoldMine domain. Walls are thick lines, while miners and gold pieces have graphical representations.
 */
public class GoldMineMAVisualizer {
	
	/**
	 * Returns a visualizer for the goldMine Gridworld.
	 * @param maxX max value for x coordinate
	 * @param maxY max value for y coordinate
	 * @return A Visualizer object
	 */
	public static Visualizer getVisualizer(int maxX, int maxY){
		Visualizer visualizer = new Visualizer();
		
		visualizer.addObjectClassPainter(GoldMineConstants.CLS_GOLD, new GoldPainter(maxX,maxY));
		visualizer.addObjectClassPainter(GoldMineConstants.CLS_AGENT, new AgentPainter(maxX,maxY));
		visualizer.addObjectClassPainter(GoldMineConstants.CLS_WALL, new WallPainter(maxX,maxY));
		
		return visualizer;
	}
}
