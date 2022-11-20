import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class MainWindow  extends JFrame implements ItemListener, ActionListener {
	
	private static final String GRAYSCALE_LABEL = "S / W";
	private static final String GO = "Los";
	private int mx1, mx2, my1, my2;
	Panel p1 = new Panel();
	Panel p2 = new Panel();
	Panel p25 = new Panel();
	Panel p27 = new Panel();
	CheckboxGroup cbg   = new CheckboxGroup();
	Checkbox cb1        = new Checkbox(MainWindow.GRAYSCALE_LABEL,cbg,true);
	Checkbox cb2        = new Checkbox("Mach bunt!",cbg,false);
	Button bLos = new Button(MainWindow.GO);
	Button bInit = new Button("    Neu    ");
	Button bZurueck = new Button(" Zurück ");
	ActionProcessor actionProcessor;
	public boolean mousedown;
	private KaestchenListener kaestchenListener;
	
	public MainWindow(String title, myCanvas pictureArea, ActionProcessor actionProcessor) {
		this.setTitle(title);
		this.actionProcessor = actionProcessor;
		this.kaestchenListener = new KaestchenListener();
		pictureArea.addMouseListener(kaestchenListener);
		pictureArea.addMouseMotionListener(kaestchenListener);
        p1.setLayout(new BorderLayout());
        p1.add(pictureArea,BorderLayout.CENTER);
        p2.setLayout(new GridLayout(1,0));
        p25.add(cb1);
        p25.add(cb2);
        p27.add(bLos);
        p27.add(bInit);
        p27.add(bZurueck);
        p2.add(p25);
        p2.add(p27);
        this.setLayout(new BorderLayout());
        this.add(p1,BorderLayout.CENTER);
        this.add(p2,BorderLayout.SOUTH);
        
        bLos.addActionListener(this);
//        bInit.addActionListener(new initActionListener());
//    	bZurueck.addActionListener(new zurueckActionListener());
//        pictureArea.addMouseListener(new kaestchenListener());
//        pictureArea.addMouseMotionListener(new zieher());
        cb1.addItemListener(this);
        cb2.addItemListener(this);
	}

    
    public void itemStateChanged (ItemEvent ie) {
    	this.actionProcessor.paletteChange(((String)ie.getItem()).equals(MainWindow.GRAYSCALE_LABEL));
    }
	  
	private class KaestchenListener  extends MouseAdapter implements MouseMotionListener {
		
        public void mouseDragged(MouseEvent mde) {
            mx2=mde.getX();
            my2=mde.getY();
            MainWindow.this.actionProcessor.selectingArea(new Rectangle(mx1,my1,mx2-mx1, my2-my1));
        }
        
        public void mouseMoved(MouseEvent mme){}
        
        public void mousePressed(MouseEvent mpe) {
            mx1=mpe.getX();
            my1=mpe.getY();
            MainWindow.this.actionProcessor.unselectArea();
        }
	        
        public void mouseReleased(MouseEvent mre) {
            mousedown=false;
            mx2=mre.getX();
            my2=mre.getY();
            MainWindow.this.actionProcessor.selectingArea(new Rectangle(mx1,my1,mx2-mx1, my2-my1));
        }
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(GO)) {
			MainWindow.this.actionProcessor.startCalculation();
		}
		System.out.println(e.toString());
	}
}
