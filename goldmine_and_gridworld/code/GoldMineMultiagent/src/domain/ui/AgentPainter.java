/**
 * 
 */
package domain.ui;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

/**
 * @author Felipe Leno da Silva
 *
 *Class that paints a miner image on the screen. The image to be painted is miner.png.
 *This class is an implementation of ImageGridPainter which paints a miner image.
 */
public class AgentPainter extends ImageGridPainter {

	public AgentPainter(int maxX, int maxY) {
		super(maxX, maxY);
	}

	
	@Override
	public Image getImage() {
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("miner.png");
		Image image=null;
		
		try {
			image = ImageIO.read(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return image;

	}


	

}
