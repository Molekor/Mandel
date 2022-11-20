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
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Stack;
public class Mandel {
	private MainWindow mainWindow;
	private int maxIter=10000;
	private Color[] meineFarben;
	private myCanvas bildflaeche;
	private ActionProcessor actionProcessor;
	private Rectangle selectedArea;

	public final double XMIN_START = -3.;
	public final double XMAX_START = 2.;
	private double xmin;
	private double xmax;
	private double ymin;
	private double ymax;
	private double xfaktor=0,yfaktor=0;
	private int mx1,mx2,my1,my2,aktuell=0;
	private boolean bunt=true, firstDraw=true;
	private Stack<Double> xminHist = new Stack <Double>();
	private Stack<Double> xmaxHist = new Stack <Double>();
	private Stack<Double> yminHist = new Stack <Double>();
	private Stack<Double> ymaxHist = new Stack <Double>();
	private Date myDate1,myDate2,myDate3;
/***************************************************************************/
    
    
    public static void main (String[] args) {
    	new Mandel();
    }
    
    public Mandel() {
    	this.actionProcessor = new ActionProcessor(this);
    	bildflaeche = new myCanvas(this.actionProcessor);
    	this.mainWindow = new MainWindow("Jos buntes Mandelbrotmengenprogramm", bildflaeche, actionProcessor);
    	this.mainWindow.setSize(900,600);
    	this.mainWindow.setLocation(10,10);
    	this.mainWindow.setVisible(true);
        meineFarben = PaletteCreator.erzeugeFarben(maxIter, false);
        //this.adjustXYtoCanvas(true);
    }
    
    public void berechne() {
    	int breiteAnzeige = bildflaeche.getWidth();
    	int hoeheAnzeige = bildflaeche.getHeight(); 
		int i;
		double fx,fy;
    	System.out.println("Starte Berechnung X(" + xmin +"," + xmax+ ") Y(" + ymin + "," + ymax + ") " + breiteAnzeige + " x " + hoeheAnzeige + " MaxIter: " + maxIter);
        aktuell++;
        xminHist.push(new Double(xmin));
        yminHist.push(new Double(ymin));
        xmaxHist.push(new Double(xmax));
        ymaxHist.push(new Double(ymax));
        if(this.selectedArea != null) {
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
		myDate1 = new Date();		
		xfaktor = Math.abs(xmax-xmin) / breiteAnzeige;
		yfaktor = Math.abs(ymax-ymin) / hoeheAnzeige;
		for (int px = 1 ; px < breiteAnzeige ; px++)
		{
		    for (int py = 1 ; py < hoeheAnzeige ; py++)
		    {
				fx= ((double)px*xfaktor)+(xmin);
		        fy = ((double)py*yfaktor)+(ymin);
		        i = PointCalculator.getIterationsForPoint(fx, fy, maxIter);
                bildflaeche.setPixel(px,py,meineFarben[i-1]);
                if((px % 5 ==0) && (py== hoeheAnzeige - 1)) {
                	bildflaeche.paintImmediately(px-5,1,5,bildflaeche.getHeight());
                }
		    }
		}
		myDate2=new Date();
		System.out.println("Rechenzeit: "+((myDate2.getTime()-myDate1.getTime()))+" millisek");
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
		bildflaeche.initGraphics();
	    berechne();
	}

	public void goBack() {
		
        if (!xminHist.empty())
        {
            aktuell--;
            System.out.println("Gehe zurück...");
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
		this.firstDraw=true;
		this.adjustXYtoCanvas();
    	aktuell=0;
		xminHist = new Stack <Double>();
		xmaxHist = new Stack <Double>();
		yminHist = new Stack <Double>();
		ymaxHist = new Stack <Double>();
		xminHist.push(new Double(xmin));
		yminHist.push(new Double(ymin));
		xmaxHist.push(new Double(xmax));
		ymaxHist.push(new Double(ymax));
		this.selectedArea = null;
        bildflaeche.initGraphics();
        berechne();
	    //bildflaeche.repaint();
	}

	public void canvasResized() {
		this.selectedArea = null;
		this.bildflaeche.initGraphics();
		this.adjustXYtoCanvas();
		this.berechne();
		this.bildflaeche.repaint();
	}

	private void adjustXYtoCanvas() {
		double aspectRatio = (double)this.bildflaeche.getHeight() / (double) this.bildflaeche.getWidth();
		if(this.firstDraw) {
			System.out.println("Initialize x,y values for new canvas:");
			this.firstDraw = false;
			xmin = this.XMIN_START;
			xmax = this.XMAX_START;
			ymin = ((xmin - xmax) * aspectRatio) /2;
			ymax = -ymin;
		} else {
			System.out.println("Calculate new x,y values for resized canvas:");
			// Keep the x-range and the top y value and scale the bottom y-range to the new aspect ratio
			double middle = (ymax + ymin) / 2;
			ymin = middle - (xmax-xmin)*aspectRatio/2;
			ymax = middle + (xmax-xmin)*aspectRatio/2;
			this.firstDraw=false;
		}
	}
}