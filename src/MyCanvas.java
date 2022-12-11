import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

@SuppressWarnings("serial")
class MyCanvas extends JPanel {

	private BufferedImage dbImage1, dbImage2;
	private Graphics dbg1, dbg2;
	private Rectangle selectedArea;

	public MyCanvas(ComponentListener resizeListener) {
		super();
		addComponentListener(resizeListener);
	}

	public void initGraphics() {
		selectedArea = null;
		dbImage1 = new BufferedImage(this.getSize().width, this.getSize().height, BufferedImage.TYPE_INT_ARGB);
		dbg1 = dbImage1.getGraphics();
		dbImage2 = new BufferedImage(this.getSize().width, this.getSize().height, BufferedImage.TYPE_INT_ARGB);
		dbg2 = dbImage2.getGraphics();
		dbg2.setColor(Color.black);
		dbg2.fillRect(1, 1, 10000, 10000);// dbImage2.getWidth(), dbImage2.getHeight());
		this.paintImmediately(1, 1, this.getSize().width, this.getSize().height);
	}

	public void paint(Graphics g) {
		// Kopiere das Mandelbrot-Bild auf den Vordergrund
		dbg1.drawImage(dbImage2, 0, 0, this);
		// bei gedr�cktem Mausknopf das weisse Rechteck �ber das 1. Hintergrundbild
		// zeichnen
		if (selectedArea != null) {
			dbg1.setColor(Color.BLACK);
			dbg1.drawRect(selectedArea.x, selectedArea.y, selectedArea.width, selectedArea.height);
			dbg1.setColor(Color.WHITE);
			dbg1.drawRect(selectedArea.x + 1, selectedArea.y + 1, selectedArea.width - 2, selectedArea.height - 2);
		}
		// Nun fertig gezeichnetes Bild auf dem richtigen Bildschirm anzeigen
		g.drawImage(dbImage1, 0, 0, this);
	}

	public void setPixel(int x, int y, Color color) {
		try {
			dbImage2.setRGB(x, y, color.getRGB());
		} catch (Exception e) {
			System.out.println("OOB: " + x + "," + y);
			e.printStackTrace();
		}
	}

	public void setSelection(Rectangle rectangle) {
		this.selectedArea = rectangle;
	}

}