/**
 * 
 */
package goldMineSA;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.State;
import burlap.oomdp.visualizer.ObjectPainter;

/**
 * Paints Gold objects
 * @author Ruben Glatt
 *
 */
public class GoldMineSAPainterGold implements ObjectPainter {

	int sizeX,sizeY;
	
	/**
	 * Painter for Gold in the grid
	 * @param sizeX horizontal size of the grid
	 * @param sizeY vertical size of the grid
	 */
	public GoldMineSAPainterGold(int sizeX, int sizeY){
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}
	/* (non-Javadoc)
	 * @see burlap.oomdp.visualizer.ObjectPainter#paintObject(java.awt.Graphics2D, burlap.oomdp.core.states.State, burlap.oomdp.core.objects.ObjectInstance, float, float)
	 */
	@Override
	public void paintObject(Graphics2D g2, State s, ObjectInstance ob,
			float cWidth, float cHeight) {
		
		//Gold is painted in yellow
		g2.setColor(Color.YELLOW);
		
		//set up floats for the width and height of our domain
		
		
		
		//determine the width of a single cell on our canvas 
		//such that the whole map can be painted
		float width = cWidth / sizeX;
		float height = cHeight / sizeY;
		
		int ax = ob.getIntValForAttribute(GoldMineSAConstants.ATT_X);
		int ay = ob.getIntValForAttribute(GoldMineSAConstants.ATT_Y);
		
		//left coordinate of cell on our canvas
		float rx = ax*width;
		
		//top coordinate of cell on our canvas
		//coordinate system adjustment because the java canvas 
		//origin is in the top left instead of the bottom right
		float ry = cHeight - height - ay*height;
	
		//paint the rectangle
		g2.fill(new Ellipse2D.Float(rx, ry, width, height));

	}

}
