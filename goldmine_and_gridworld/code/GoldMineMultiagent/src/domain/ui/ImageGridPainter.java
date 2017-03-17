/**
 * 
 */
package domain.ui;

import java.awt.Graphics2D;
import java.awt.Image;

import burlap.oomdp.core.objects.ObjectInstance;
import burlap.oomdp.core.states.State;
import burlap.oomdp.visualizer.ObjectPainter;
import domain.GoldMineConstants;

/**
 * @author Felipe Leno da Silva
 *
 *Class that paints a image on a gridworld. The sprite must be specified on the abstract method getImage().
 */
public abstract class ImageGridPainter implements ObjectPainter {

	protected int sizeX; //max grid size on horizontal direction
	protected int sizeY; //max grid size on vertical direction
	
	
	/**
	 * Constructor that receives the max dimensions of the grid
	 * @param sizeX grid size on horizontal direction
	 * @param sizeY  grid size on vertical direction
	 */
	public ImageGridPainter(int sizeX, int sizeY){
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}
	
	
	/* (non-Javadoc)
	 * @see burlap.oomdp.visualizer.ObjectPainter#paintObject(java.awt.Graphics2D, burlap.oomdp.core.states.State, burlap.oomdp.core.objects.ObjectInstance, float, float)
	 */
	@Override
	public void paintObject(Graphics2D g2, State s, ObjectInstance ob,
			float cWidth, float cHeight) {
		
		//determine the width of a single cell on our canvas 
		//such that the whole map can be painted
		float width = cWidth / (this.sizeX);
		float height = cHeight / (this.sizeY);
		
		int ax = ob.getIntValForAttribute(GoldMineConstants.ATT_X);
		int ay = ob.getIntValForAttribute(GoldMineConstants.ATT_Y);
		
		//left coordinate of cell on our canvas
		float rx = ax*width;
		
		//top coordinate of cell on our canvas
		//coordinate system adjustment because the java canvas 
		//origin is in the top left instead of the bottom right
		float ry = cHeight - height - ay*height;
	
		g2.drawImage(this.getImage(), (int)rx, (int)ry, (int)width, (int)height, null);
		
		//g2.fill(new Ellipse2D.Float(rx, ry, width, height));

	}
	
	/**
	 * This method must be implemented by subclasses and returns the image to be printed. The image will be rescaled to fit a cell.
	 * @return Image to be printed on the cell
	 */
	public abstract Image getImage();

}
