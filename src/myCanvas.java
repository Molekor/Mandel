import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/***************************************************************************/

@SuppressWarnings("serial")
class myCanvas extends JPanel
{
	/**
 * 
 */
	private BufferedImage dbImage1,dbImage2;
	private Graphics dbg1;
	private Rectangle selectedArea;
	
	public myCanvas(ComponentListener resizeListener) 
	{
		super();
		setBackground(Color.BLACK);
		addComponentListener(resizeListener);
	}

	public void initGraphics() {
		selectedArea = null;
		// TODO: use the writableRaster for the image?
		dbImage1 = new BufferedImage(this.getSize().width, this.getSize().height, BufferedImage.TYPE_INT_ARGB);
		dbg1 = dbImage1.getGraphics ();
		dbImage2 = new BufferedImage(this.getSize().width, this.getSize().height, BufferedImage.TYPE_INT_ARGB);
		dbImage2.getGraphics().setColor(Color.BLACK);
		dbImage2.getGraphics().fillRect(1, 1, 10000,10000);//dbImage2.getWidth(), dbImage2.getHeight());
		//this.paintImmediately(1,1,this.getSize().width, this.getSize().height);
	}

	public void paint (Graphics g)	
	{
		// Kopiere das Mandelbrot-Bild auf den Vordergrund
		dbg1.drawImage(dbImage2,0,0,this);
		//bei gedr�cktem Mausknopf das weisse Rechteck �ber das 1. Hintergrundbild zeichnen
		if (selectedArea != null)
		{
			dbg1.setColor(Color.RED);
			dbg1.drawRect(selectedArea.x, selectedArea.y, selectedArea.width, selectedArea.height);               
		}
		// Nun fertig gezeichnetes Bild auf dem richtigen Bildschirm anzeigen
		g.drawImage (dbImage1, 0, 0, this);
	}

	public void setPixel(int x, int y, Color color) {
		dbImage2.setRGB(x, y, color.getRGB());
	}

	public void setSelection(Rectangle rectangle) {
		this.selectedArea = rectangle;
	}
	    
}