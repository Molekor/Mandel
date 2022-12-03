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
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Stack;

public class Mandel implements PixelCalculationObserver {
	
	public static final int PRECISION = 30;
	MathContext mc = new MathContext(Mandel.PRECISION, RoundingMode.HALF_UP);
	private MainWindow mainWindow;
	private int maxIter=20000;
	private Color[] meineFarben;
	private MyCanvas bildflaeche;
	private ActionProcessor actionProcessor;
	private Rectangle selectedArea;
	public final BigDecimal XMIN_START = BigDecimal.valueOf(-3.);
	public final BigDecimal XMAX_START = BigDecimal.valueOf(2.);
	private BigDecimal xmin;
	private BigDecimal xmax;
	private BigDecimal ymin;
	private BigDecimal ymax;
	private boolean firstDraw=true;
	private Stack<BigDecimal> xminHist = new Stack <>();
	private Stack<BigDecimal> xmaxHist = new Stack <>();
	private Stack<BigDecimal> yminHist = new Stack <>();
	private Stack<BigDecimal> ymaxHist = new Stack <>();
	private ThreadCoordinator threadCoordinator;  
	
	
	private int calccount=0;
	
    public static void main (String[] args) {
    	new Mandel();
    }
    
    public Mandel() {
    	System.out.println("STARTING MANDEL:"+Thread.currentThread().getPriority());
    	this.threadCoordinator = new ThreadCoordinator(this);
    	this.actionProcessor = new ActionProcessor(this);
    	bildflaeche = new MyCanvas(this.actionProcessor);
    	this.mainWindow = new MainWindow("Jos buntes Mandelbrotmengenprogramm", bildflaeche, actionProcessor);
    	this.mainWindow.setSize(800,600);
    	this.mainWindow.setLocation(1,1);
    	this.mainWindow.setVisible(true);
        meineFarben = PaletteCreator.erzeugeFarben(maxIter, false);
        
    }
    
    public void berechne() {
       	int breiteAnzeige = bildflaeche.getWidth();
    	int hoeheAnzeige = bildflaeche.getHeight(); 
        // If a new section is selected, zoom in on it before calculation
    	if (this.selectedArea != null) {
	        BigDecimal zoom = new BigDecimal(Mandel.this.selectedArea.width).divide(new BigDecimal(breiteAnzeige), mc);
	        BigDecimal xPosFactor = new BigDecimal(Mandel.this.selectedArea.x).divide(new BigDecimal(breiteAnzeige), mc);
	        BigDecimal xRange = xmax.subtract(xmin);
	        xmin = xmin.add(xRange.multiply(xPosFactor, mc));
	        xmax = xmin.add(xRange.multiply(zoom, mc));
	        BigDecimal yPosFactor = new BigDecimal(Mandel.this.selectedArea.y).divide(new BigDecimal(hoeheAnzeige), mc);
	        BigDecimal yRange = ymax.subtract(ymin);
	        ymin = ymin.add(yRange.multiply(yPosFactor, mc));
	        ymax = ymin.add(yRange.multiply(zoom, mc));
	        this.selectedArea = null;
        }
    	System.out.println("Starte Berechnung X(" + xmin +"," + xmax+ ") Y(" + ymin + "," + ymax + ") "
		+ breiteAnzeige + " x " + hoeheAnzeige + " = " + (breiteAnzeige*hoeheAnzeige) + " MaxIter: " + maxIter);
    	this.threadCoordinator.startCalculation(breiteAnzeige, hoeheAnzeige, xmin, xmax, ymin, ymax, maxIter);
		this.threadCoordinator.run();
		this.bildflaeche.repaint();
	}
       
    public synchronized void pixelCalculationComplete(Point pixel, int iterations) {
    	this.calccount++;
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
        xminHist.push((xmin));
        yminHist.push(ymin);
        xmaxHist.push(xmax);
        ymaxHist.push(ymax);
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
		xminHist = new Stack <>();
		xmaxHist = new Stack <>();
		yminHist = new Stack <>();
		ymaxHist = new Stack <>();
		xminHist.push(xmin);
		yminHist.push(ymin);
		xmaxHist.push(xmax);
		ymaxHist.push(ymax);
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
		BigDecimal aspectRatio = new BigDecimal(this.bildflaeche.getHeight()).divide(new BigDecimal(this.bildflaeche.getWidth()), mc);
		if(this.firstDraw) {
			this.firstDraw = false;
			xmin = this.XMIN_START;
			xmax = this.XMAX_START;
			ymin = xmin.subtract(xmax).multiply(aspectRatio).divide(new BigDecimal(2), Mandel.PRECISION, RoundingMode.HALF_UP);
			ymax = ymin.multiply(BigDecimal.valueOf(-1));
		} else {
			// Keep the x-range and scale the y-range to the new aspect ratio
			BigDecimal middle = ymax.add(ymin).divide(BigDecimal.valueOf(2), mc);
			ymin = middle.subtract(xmax.subtract(xmin).multiply(aspectRatio).divide(BigDecimal.valueOf(2), mc));
			ymax = middle.add(xmax.subtract(xmin).multiply(aspectRatio).divide(BigDecimal.valueOf(2), mc));
			this.firstDraw=false;
		}
	}
}