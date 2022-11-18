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
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.lang.Math;
import java.applet.*;
public class Mandel extends JFrame //extends Applet
{
	Random myRandom = new Random();
	int maxIter=20000;
	Color[] meineFarben = new Color[maxIter];
	myCanvas bildflaeche = new myCanvas();
	Panel p1 = new Panel();
	Panel p2 = new Panel();
	Panel p21 = new Panel();
	Panel p22 = new Panel();
	Panel p23 = new Panel();
	Panel p24 = new Panel();
	Panel p25 = new Panel();
	Panel p26 = new Panel();
	Panel p27 = new Panel();
	Panel p28 = new Panel();
	Label lXmax = new Label("X max:");
	Label lXmin = new Label("X min:");
	Label lYmax = new Label("Y max:");
	Label lYmin = new Label("Y min:");
	Label lAktuell = new Label("Bild Nr.:");
	TextField tfXmax = new TextField("",8);
	TextField tfYmax = new TextField("",8);
	TextField tfXmin = new TextField("",8);
	TextField tfYmin = new TextField("",8);
	TextField tfAktuell = new TextField("0",3);
	CheckboxGroup cbg   = new CheckboxGroup();
	Checkbox cb1        = new Checkbox("S / W",cbg,true);
	Checkbox cb2        = new Checkbox("Mach bunt!",cbg,false);
	Button bLos = new Button("    Los    ");
	Button bInit = new Button("    Neu    ");
	Button bZurueck = new Button(" Zurück ");
	
	int breiteAnzeige = 0;
        int hoeheAnzeige  = 0;
	double xmin,xmax,ymin,ymax,xfaktor=0,yfaktor=0;
	int mx1,mx2,my1,my2,aktuell=0;
	boolean mousedown=false,bunt=false,neuBerechnen=true,updated=false,erstesMal=true,losmalen=true,warten=true;
	Stack<Double> xminHist = new Stack <Double>();
	Stack<Double> xmaxHist = new Stack <Double>();
	Stack<Double> yminHist = new Stack <Double>();
	Stack<Double> ymaxHist = new Stack <Double>();
	Date myDate1,myDate2,myDate3;
/***************************************************************************/
	class myCanvas extends JPanel implements ImageObserver   
	{
		public Image dbImage1,dbImage2;
		public Graphics dbg1,dbg2;
		
		public myCanvas() 
		{
			super();
			setBackground(Color.gray);
			addComponentListener(new ComponentAdapter() 
			{
				public void componentResized(ComponentEvent evt) 
				{
					initGraphics();
					breiteAnzeige = getWidth();
					hoeheAnzeige = getHeight();
					berechne();
					myCanvas.this.repaint();
				}
			});
		}
	
		private void initGraphics() {
			dbImage1 = createImage (this.getSize().width, this.getSize().height);
			dbg1 = dbImage1.getGraphics ();
			dbImage2 = createImage (this.getSize().width, this.getSize().height);
			dbg2 = dbImage2.getGraphics ();
		}
	
		public void paint (Graphics g)	
		{
			dbg1.drawImage(dbImage2,0,0,this);
			//bei gedrücktem Mausknopf das weisse Rechteck über das 1. Hintergrundbild zeichnen
			if (mousedown)
			{
				dbg1.setColor(Color.WHITE);
				dbg1.drawRect(mx1,my1,Math.abs(mx1-mx2),Math.abs(my1-my2));               
			}
			// Nun fertig gezeichnetes Bild auf dem richtigen Bildschirm anzeigen
			g.drawImage (dbImage1, 0, 0, this);
			myDate2 = new Date();
		}

		public void setPixel(int x, int y, Color color) {
			dbg2.setColor(color);
			dbg2.drawLine(x,y,x,y);
		}

		public void reset() {
			initGraphics();
		}
		    
	}
/***************************************************************************/
    
    
    public static void main (String[] args)
    {
        Mandel myMandel = new Mandel("Jos buntes Mandelbrotmengenprogramm");
        myMandel.setSize(800,600);
        myMandel.setLocation(10,10);
        myMandel.setVisible(true);
    }
    
    public Mandel(String name)
    {
    	this.init();
    }
    
    public void init()
    {
        xmin=-2.5;
        xmax=0.8;
        ymin=-1.25;
        ymax=1.25;
        xminHist.push(new Double(xmin));
        yminHist.push(new Double(ymin));
        xmaxHist.push(new Double(xmax));
        ymaxHist.push(new Double(ymax));
        
        meineFarben=new Color[maxIter];
            
        p1.setLayout(new BorderLayout());
        p1.add(bildflaeche,BorderLayout.CENTER);
        p21.add(lXmin);
        p21.add(tfXmin);
        p22.add(lXmax);
        p22.add(tfXmax);
        p23.add(lYmin);
        p23.add(tfYmin);
        p24.add(lYmax);
        p24.add(tfYmax);
        p25.add(cb1);
        p25.add(cb2);
        p26.add(lAktuell);
        p26.add(tfAktuell);
        p27.add(bLos);
        p27.add(bInit);
        p28.add(bZurueck);
        p2.add(p21);
        p2.add(p22);
        p2.add(p23);
        p2.add(p24);
        p2.add(p25);
        p2.add(p26);
        p2.add(p27);
        p2.add(p28);
        p2.setLayout(new GridLayout(0,1));
        this.setLayout(new BorderLayout());
        add(p1,BorderLayout.CENTER);
        add(p2,BorderLayout.EAST);
        
        bLos.addActionListener(new losActionListener());
        bInit.addActionListener(new initActionListener());
    	bZurueck.addActionListener(new zurueckActionListener());
        bildflaeche.addMouseListener(new kaestchenListener());
        bildflaeche.addMouseMotionListener(new zieher());
        cb1.addItemListener(new CheckboxListener());
        cb2.addItemListener(new CheckboxListener());
        
        tfXmin.setText(((Double)xmin).toString());
        tfXmax.setText(((Double)xmax).toString());
        tfYmin.setText(((Double)ymin).toString());
        tfYmax.setText(((Double)ymax).toString());
        
        erzeugeFarben(meineFarben);
        berechne();
    }
    
    public void erzeugeFarben(Color[] meineFarben)
    {
	int zaehler=0,zaehlerzaehler=1,farbe=0;
        for (int i = 0; i < maxIter; i++)
        {
            if (bunt)
            {
                meineFarben[i] = new Color(myRandom.nextInt(255),myRandom.nextInt(255),myRandom.nextInt(255));        
            }
            else
            {
                /*
                zaehler++; //=farbzaehler;
                if (zaehler >zaehlerzaehler)
                {
                    zaehler=0;
                    farbe++;
                }
                */
                farbe++;
                if (farbe > 255)
                {
                    farbe=0;
                    //zaehlerzaehler++;
                }
                meineFarben[i]=new Color(farbe,farbe,farbe);//((int)215*i/maxIter),((int)215*i/maxIter),((int)215*i/maxIter));
                //meineFarben[i]=new Color(255-(int)(i*255/maxIter),255-(int)(i*255/maxIter),255-(int)(i*255/maxIter));
            }
        }
        meineFarben[maxIter-1] = new Color(0,0,0);
    }
    
    public void berechne()
	{
    	System.out.println("Starte Berechnung " + breiteAnzeige + " x " + hoeheAnzeige + " MaxIter: " + maxIter);
		int iter=0,i;
		double fx,fy,real,imaginary; //realQuad,imaginaryQuad;
		double betrag_quadrat=0,temp=0;	
		myDate1 = new Date();		
		xfaktor = Math.abs(xmax-xmin) / breiteAnzeige;
		yfaktor = Math.abs(ymax-ymin) / hoeheAnzeige;
		for (int px = 1 ; px < breiteAnzeige ; px++)
		{
		    for (int py = 1 ; py < hoeheAnzeige ; py++)
		    {
                fx= ((double)px*xfaktor)+(xmin);
                fy = ((double)py*yfaktor)+(ymin);
                iter=0;
                betrag_quadrat = 0;
                real=0;
                imaginary=0;
                for (i = 1 ; i < maxIter ; i++)
                {
                    temp=real;
                    real = real * real - imaginary * imaginary + fx;
                    imaginary = 2 * temp * imaginary + fy;
                    if ((real * real + imaginary * imaginary)>4)
                        break;
                }
                bildflaeche.setPixel(px,py,meineFarben[i-1]);
                if(py== 1) {
                	bildflaeche.paintImmediately(1,py,bildflaeche.getWidth(),bildflaeche.getHeight());
                }
		    }
		}
		myDate2=new Date();
		System.out.println("Rechenzeit: "+((myDate2.getTime()-myDate1.getTime()))+" millisek");
		//bildflaeche.repaint();
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
            xmin=Double.parseDouble(tfXmin.getText());
            ymin=Double.parseDouble(tfYmin.getText());
            xmax=Double.parseDouble(tfXmax.getText());
            ymax=Double.parseDouble(tfYmax.getText());
            tfAktuell.setText(new Integer(aktuell).toString());
		warten=true;
			bildflaeche.reset();
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
                tfXmax.setText(new Double(xmax).toString());
                tfXmin.setText(new Double(xmin).toString());
                tfYmax.setText(new Double (ymax).toString());
                tfYmin.setText(new Double (ymin).toString());
                tfAktuell.setText(new Integer(aktuell).toString());
                berechne();
                bildflaeche.repaint();		
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
            tfXmax.setText(new Double(xmax).toString());
            tfXmin.setText(new Double(xmin).toString());
            tfYmax.setText(new Double (ymax).toString());
            tfYmin.setText(new Double (ymin).toString());
		tfAktuell.setText(new Integer(aktuell).toString());
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
        }
        public void mouseReleased(MouseEvent mre)
        {
            mousedown=false;
            mx2=mre.getX();
            my2=mre.getY();
            System.out.println("xmin alt:"+xmin);
            xfaktor = Math.abs(xmax-xmin)/breiteAnzeige;
		yfaktor = Math.abs(ymax-ymin)/hoeheAnzeige;
            tfXmin.setText(new Double(xmin + xfaktor*mx1).toString());
            tfYmin.setText(new Double(ymin + yfaktor*my1).toString());
            tfXmax.setText(new Double(xmax - xfaktor*(breiteAnzeige-mx2)).toString());
            tfYmax.setText(new Double(ymax - yfaktor*(hoeheAnzeige-my2)).toString());		
            System.out.println("xmin neu:"+xmin);
            System.out.println("mouseReleased: Texte gesetzt:"+xmax+xmin+ymax+ymin);
            repaint();
        }
    }
    
    class zieher implements MouseMotionListener
    {
        public void mouseDragged(MouseEvent mde)
        {
            mx2=mde.getX();
            my2=mde.getY();
            bildflaeche.repaint();
        }
        public void mouseMoved(MouseEvent mme)
        {
            //System.out.println("MOOVE");
        }    
    }
    
    class CheckboxListener implements ItemListener
    {
        public void itemStateChanged (ItemEvent ie)
        {
            String cbName = (String)ie.getItem();
            if (cbName.equalsIgnoreCase("S / W"))
            {
                bunt=false;
            }
            else
            {
                bunt=true;
            }
            erzeugeFarben(meineFarben);
            Mandel.this.berechne();
        }
    }
}