/**
 * 
 */
package goldMineSA;

import java.awt.Color;
import java.awt.Graphics2D;

import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.State;
import burlap.oomdp.visualizer.ObjectPainter;

/**
 * Wall painter
 * @author Ruben Glatt
 *
 */
public class GoldMineSAPainterWall implements ObjectPainter {
	int sizeX,sizeY;
	/**
	 * Painter for walls in the grid
	 * @param sizeX horizontal size of the grid
	 * @param sizeY vertical size of the grid
	 */
	public GoldMineSAPainterWall(int sizeX, int sizeY){
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}

	/* (non-Javadoc)
	 * @see burlap.oomdp.visualizer.StaticPainter#paint(java.awt.Graphics2D, burlap.oomdp.core.states.State, float, float)
	 */
	@Override
	public void paintObject(Graphics2D g2, State s, ObjectInstance ob,
			float cWidth, float cHeight) {
		
		g2.setColor(Color.BLACK);
		
		
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
		
		String direction = ob.getStringValForAttribute(GoldMineSAConstants.ATT_POSITION);
		
		
		float xInitPoint=0,yInitPoint=0,xEndPoint=0,yEndPoint=0;
		
		//Defines where to paint walls
		switch(direction){
		case GoldMineSAConstants.NORTH:
			xInitPoint = rx;
			yInitPoint = ry;
			xEndPoint = xInitPoint + width;
			yEndPoint = yInitPoint;
			break;
		case GoldMineSAConstants.SOUTH:
			xInitPoint = rx;
			yInitPoint = ry + height;
			xEndPoint = xInitPoint + width;
			yEndPoint = yInitPoint;
			break;
		case GoldMineSAConstants.WEST:
			xInitPoint = rx;
			yInitPoint = ry;
			xEndPoint = xInitPoint;
			yEndPoint = yInitPoint + height;
			break;
		case GoldMineSAConstants.EAST:
			xInitPoint = rx+width;
			yInitPoint = ry;
			xEndPoint = xInitPoint;
			yEndPoint = yInitPoint + height;
			break;
		}
				
		g2.drawLine((int)xInitPoint, (int)yInitPoint, (int)xEndPoint, (int)yEndPoint);
		
	}

}
