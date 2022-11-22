/* Mandel.java
    Java-Applet zur Anzeige der klassischen Mandelbrot - Menge
    Johannes Rodenwald
    Version: 0.4
    Datum: 21.11.2005
*/

//Interressante Koordinaten:
/*
xmin	xmax	ymin	ymax
-.05	.03	.75	.85
*/
/*
TODO:
Umstellen auf BigDecimal!

*/
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Stack;

public class Mandel implements PixelCalculationObserver {
	
	private MainWindow mainWindow;
	private int maxIter=10000;
	private Color[] meineFarben;
	private MyCanvas bildflaeche;
	private ActionProcessor actionProcessor;
	private Rectangle selectedArea;
	public final double XMIN_START = -3.;
	public final double XMAX_START = 2.;
	private double xmin;
	private double xmax;
	private double ymin;
	private double ymax;
	private boolean firstDraw=true;
	private Stack<Double> xminHist = new Stack <Double>();
	private Stack<Double> xmaxHist = new Stack <Double>();
	private Stack<Double> yminHist = new Stack <Double>();
	private Stack<Double> ymaxHist = new Stack <Double>();
	private ThreadCoordinator threadCoordinator;  
	
    public static void main (String[] args) {
    	new Mandel();
    }
    
    public Mandel() {
    	System.out.println("STARTING MANDEL:"+Thread.currentThread().getPriority());
    	this.actionProcessor = new ActionProcessor(this);
    	bildflaeche = new MyCanvas(this.actionProcessor);
    	this.mainWindow = new MainWindow("Jos buntes Mandelbrotmengenprogramm", bildflaeche, actionProcessor);
    	this.mainWindow.setSize(500,400);
    	this.mainWindow.setLocation(1,1);
    	this.mainWindow.setVisible(true);
        meineFarben = PaletteCreator.erzeugeFarben(maxIter, false);
        this.threadCoordinator = new ThreadCoordinator(this);
    }
    
    public void berechne() {
       	int breiteAnzeige = bildflaeche.getWidth();
    	int hoeheAnzeige = bildflaeche.getHeight(); 
        // If a new section is selected, zoom in on it before calculation
    	if (this.selectedArea != null) {
	        double zoom = (double)Mandel.this.selectedArea.width / (double)breiteAnzeige;
	        double xPosFactor = (double)Mandel.this.selectedArea.x / (double)breiteAnzeige;
	        double xRange = xmax-xmin;
	        xmin = xmin + xRange * xPosFactor;
	        xmax = xmin + xRange * zoom;
	        double yPosFactor = (double)Mandel.this.selectedArea.y / (double)hoeheAnzeige;
	        double yRange = ymax - ymin;
	        ymin = ymin + yRange * yPosFactor;
	        ymax = ymin + yRange * zoom;
	        this.selectedArea = null;
        }
    	System.out.println("Starte Berechnung X(" + xmin +"," + xmax+ ") Y(" + ymin + "," + ymax + ") "
		+ breiteAnzeige + " x " + hoeheAnzeige + " = " + (breiteAnzeige*hoeheAnzeige) + " MaxIter: " + maxIter);
    	this.threadCoordinator.startCalculation(breiteAnzeige, hoeheAnzeige, xmin, xmax, ymin, ymax, maxIter);
		this.threadCoordinator.run();
		this.bildflaeche.repaint();
	}
       
    public synchronized void pixelCalculationComplete(Point pixel, int iterations) {
        bildflaeche.setPixel(pixel.x,pixel.y,meineFarben[iterations-1]);
        bildflaeche.repaint();
    }
    
    public void changePalette(boolean grayscale) {
        bildflaeche.initGraphics();
        meineFarben = PaletteCreator.erzeugeFarben(maxIter, grayscale);
        Mandel.this.berechne();
    }

	public void setAreaSelection(Rectangle rectangle) {
		this.selectedArea = rectangle;
		bildflaeche.setSelection(rectangle);
		bildflaeche.repaint();
	}

	public void unselectArea() {
		this.selectedArea = null;
		bildflaeche.setSelection(null);
		bildflaeche.repaint();	
	}

	public void startCalculation() {
        xminHist.push(Double.valueOf(xmin));
        yminHist.push(Double.valueOf(ymin));
        xmaxHist.push(Double.valueOf(xmax));
        ymaxHist.push(Double.valueOf(ymax));
		bildflaeche.initGraphics();
	    berechne();
	}

	public void goBack() {
        if (!xminHist.empty()) {
            xmin=xminHist.pop();
            xmax=xmaxHist.pop();
            ymin=yminHist.pop();
            ymax=ymaxHist.pop();
            this.selectedArea = null;
            bildflaeche.initGraphics();
            berechne();	
        }
	}

	public void resetView() {
		meineFarben = PaletteCreator.erzeugeFarben(maxIter, false);
		this.firstDraw=true;
		this.adjustXYtoCanvas();
		xminHist = new Stack <Double>();
		xmaxHist = new Stack <Double>();
		yminHist = new Stack <Double>();
		ymaxHist = new Stack <Double>();
		xminHist.push(Double.valueOf(xmin));
		yminHist.push(Double.valueOf(ymin));
		xmaxHist.push(Double.valueOf(xmax));
		ymaxHist.push(Double.valueOf(ymax));
		this.selectedArea = null;
        bildflaeche.initGraphics();
        berechne();
	}

	public void canvasResized() {
		this.selectedArea = null;
		this.bildflaeche.initGraphics();
		this.adjustXYtoCanvas();
		this.berechne();
	}

	private void adjustXYtoCanvas() {
		double aspectRatio = (double)this.bildflaeche.getHeight() / (double) this.bildflaeche.getWidth();
		if(this.firstDraw) {
			this.firstDraw = false;
			xmin = this.XMIN_START;
			xmax = this.XMAX_START;
			ymin = ((xmin - xmax) * aspectRatio) /2;
			ymax = -ymin;
		} else {
			// Keep the x-range and scale the y-range to the new aspect ratio
			double middle = (ymax + ymin) / 2;
			ymin = middle - (xmax-xmin)*aspectRatio/2;
			ymax = middle + (xmax-xmin)*aspectRatio/2;
			this.firstDraw=false;
		}
	}
}