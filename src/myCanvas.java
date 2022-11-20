import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/***************************************************************************/

class myCanvas extends JPanel
{
	/**
 * 
 */
private final Mandel mandel;
	private BufferedImage dbImage1,dbImage2;
	private Graphics dbg1;
	private Rectangle rectangle;
	
	public myCanvas(Mandel mandel) 
	{
		super();
		this.mandel = mandel;
		setBackground(Color.gray);
		addComponentListener(new ComponentAdapter() 
		{
			public void componentResized(ComponentEvent evt) 
			{
				initGraphics();
				myCanvas.this.mandel.breiteAnzeige = getWidth();
				myCanvas.this.mandel.hoeheAnzeige = getHeight();
				myCanvas.this.mandel.berechne();
				myCanvas.this.repaint();
			}
		});
	}

	public void initGraphics() {
		rectangle = null;
		dbImage1 = new BufferedImage(this.getSize().width, this.getSize().height, BufferedImage.TYPE_INT_ARGB);
		dbg1 = dbImage1.getGraphics ();
		dbImage2 = new BufferedImage(this.getSize().width, this.getSize().height, BufferedImage.TYPE_INT_ARGB);
		dbImage2.getGraphics().setColor(Color.GRAY);
		dbImage2.getGraphics().fillRect(1, 1, this.getSize().width, this.getSize().height);
		this.paintImmediately(1,1,this.getSize().width, this.getSize().height);
	}

	public void paint (Graphics g)	
	{
		// Kopiere das Mandelbrot-Bild auf den Vordergrund
		dbg1.drawImage(dbImage2,0,0,this);
		//bei gedrücktem Mausknopf das weisse Rechteck über das 1. Hintergrundbild zeichnen
		if (this.mandel.mousedown && rectangle != null)
		{
			dbg1.setColor(Color.WHITE);
			dbg1.drawRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);               
		}
		// Nun fertig gezeichnetes Bild auf dem richtigen Bildschirm anzeigen
		g.drawImage (dbImage1, 0, 0, this);
	}

	public void setPixel(int x, int y, Color color) {
		dbImage2.setRGB(x, y, color.getRGB());
	}

	public void setSelection(Rectangle rectangle) {
		this.rectangle = rectangle;
	}
	    
}