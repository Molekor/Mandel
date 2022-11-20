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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Date;
import java.util.Stack;
public class Mandel {
	MainWindow mainWindow;
	int maxIter=10000;
	Color[] meineFarben;
	myCanvas bildflaeche;
	private ActionProcessor actionProcessor;
	
	int breiteAnzeige = 0;
    int hoeheAnzeige  = 0;
    double xmin=-2.5;
    double xmax=0.8;
    double ymin=-1.25;
    double ymax=1.25;
	double xfaktor=0,yfaktor=0;
	int mx1,mx2,my1,my2,aktuell=0;
	boolean mousedown=false,bunt=false;
	Stack<Double> xminHist = new Stack <Double>();
	Stack<Double> xmaxHist = new Stack <Double>();
	Stack<Double> yminHist = new Stack <Double>();
	Stack<Double> ymaxHist = new Stack <Double>();
	Date myDate1,myDate2,myDate3;
/***************************************************************************/
    
    
    public static void main (String[] args) {
        Mandel myMandel = new Mandel();
    }
    
    public Mandel() {
    	this.actionProcessor = new ActionProcessor(this);
    	bildflaeche = new myCanvas(this);
    	this.mainWindow = new MainWindow("Jos buntes Mandelbrotmengenprogramm", bildflaeche, actionProcessor);
    	this.mainWindow.setSize(800,600);
    	this.mainWindow.setLocation(10,10);
    	this.mainWindow.setVisible(true);
        xminHist.push(new Double(xmin));
        yminHist.push(new Double(ymin));
        xmaxHist.push(new Double(xmax));
        ymaxHist.push(new Double(ymax));
        meineFarben = PaletteCreator.erzeugeFarben(maxIter, true);
        berechne();
    }
    
    public void berechne() {
    	System.out.println("Starte Berechnung " + breiteAnzeige + " x " + hoeheAnzeige + " MaxIter: " + maxIter);
		int i;
		double fx,fy,real,imaginary; //realQuad,imaginaryQuad;
		double temp=0;	
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
/***************************************************************************/
    class losActionListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            aktuell++;
            xminHist.push(new Double(xmin));
            yminHist.push(new Double(ymin));
            xmaxHist.push(new Double(xmax));
            ymaxHist.push(new Double(ymax));
//            xmin=Double.parseDouble(tfXmin.getText());
//            ymin=Double.parseDouble(tfYmin.getText());
//            xmax=Double.parseDouble(tfXmax.getText());
//            ymax=Double.parseDouble(tfYmax.getText());
//            tfAktuell.setText(new Integer(aktuell).toString());
			bildflaeche.initGraphics();
            berechne();
        }
    }
    class zurueckActionListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            if (!xminHist.empty())
            {
                aktuell--;
                System.out.println("Gehe zurück...");
                xmin=xminHist.pop();
                xmax=xmaxHist.pop();
                ymin=yminHist.pop();
                ymax=ymaxHist.pop();
                xfaktor = Math.abs(xmax-xmin)/breiteAnzeige;
                yfaktor = Math.abs(ymax-ymin)/hoeheAnzeige;
//                tfXmax.setText(new Double(xmax).toString());
//                tfXmin.setText(new Double(xmin).toString());
//                tfYmax.setText(new Double (ymax).toString());
//                tfYmin.setText(new Double (ymin).toString());
//                tfAktuell.setText(new Integer(aktuell).toString());
                bildflaeche.initGraphics();
                berechne();	
            }	
        }
   }
    class initActionListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
        	aktuell=0;
            xmin=-2.5;
	        xmax=0.8;
	        ymin=-1.25;
	        ymax=1.25;
			xminHist = new Stack <Double>();
			xmaxHist = new Stack <Double>();
			yminHist = new Stack <Double>();
			ymaxHist = new Stack <Double>();
			xminHist.push(new Double(xmin));
			yminHist.push(new Double(ymin));
			xmaxHist.push(new Double(xmax));
			ymaxHist.push(new Double(ymax));
//            tfXmax.setText(new Double(xmax).toString());
//            tfXmin.setText(new Double(xmin).toString());
//            tfYmax.setText(new Double (ymax).toString());
//            tfYmin.setText(new Double (ymin).toString());
//            tfAktuell.setText(new Integer(aktuell).toString());
            bildflaeche.initGraphics();
            berechne();
		    bildflaeche.repaint();
        }            
    }

    class kaestchenListener  extends MouseAdapter
    {
        public void mousePressed(MouseEvent mpe)
        {
            mx1=mpe.getX();
            my1=mpe.getY();
            mx2=mx1;
            my2=my1;
            mousedown=true;
            bildflaeche.setSelection(null);
            bildflaeche.repaint();
        }
        public void mouseReleased(MouseEvent mre)
        {
            mousedown=false;
            mx2=mre.getX();
            my2=mre.getY();
            System.out.println("xmin alt:"+xmin);
            xfaktor = Math.abs(xmax-xmin)/breiteAnzeige;
            yfaktor = Math.abs(ymax-ymin)/hoeheAnzeige;
//            tfXmin.setText(new Double(xmin + xfaktor*mx1).toString());
//            tfYmin.setText(new Double(ymin + yfaktor*my1).toString());
//            tfXmax.setText(new Double(xmax - xfaktor*(breiteAnzeige-mx2)).toString());
//            tfYmax.setText(new Double(ymax - yfaktor*(hoeheAnzeige-my2)).toString());		
            System.out.println("xmin neu:"+xmin);
            System.out.println("mouseReleased: Texte gesetzt:"+xmax+xmin+ymax+ymin);
            bildflaeche.repaint();
        }
    }
    
    class zieher implements MouseMotionListener
    {
        public void mouseDragged(MouseEvent mde)
        {
            mx2=mde.getX();
            my2=mde.getY();
            bildflaeche.setSelection(new Rectangle(mx1,my1,Math.abs(mx2-mx1),Math.abs(my2-my1)));
            bildflaeche.repaint();
        }
        
        public void mouseMoved(MouseEvent mme){}    
    }
    
    class CheckboxListener implements ItemListener
    {
        public void itemStateChanged (ItemEvent ie) {
        	// ((String)ie.getItem()).equalsIgnoreCase("S / W"));
        }
    }
    
    public void changePalette(boolean grayscale) {
        bildflaeche.initGraphics();
        meineFarben = PaletteCreator.erzeugeFarben(maxIter, grayscale);
        Mandel.this.berechne();
    }
}