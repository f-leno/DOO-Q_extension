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
 * Class that paints a gold piece  in the screen. Similar to AgentPainter
 *
 */
public class GoldPainter extends ImageGridPainter {

	public GoldPainter(int maxX, int maxY) {
		super(maxX, maxY);
	}

	/* (non-Javadoc)
	 * @see domain.ui.ImageGridPainter#getImage()
	 */
	@Override
	public Image getImage() {
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("gold.png");
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
