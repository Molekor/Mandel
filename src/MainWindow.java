import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.Rectangle;
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
	private static final String NEW = "Neu";
	private static final String BACK = "Zurück";
	private int mx1, mx2, my1;
	Panel p1 = new Panel();
	Panel p2 = new Panel();
	Panel p25 = new Panel();
	Panel p27 = new Panel();
	CheckboxGroup cbg   = new CheckboxGroup();
	Checkbox cb1        = new Checkbox(MainWindow.GRAYSCALE_LABEL,cbg,false);
	Checkbox cb2        = new Checkbox("Mach bunt!",cbg,true);
	Button bLos = new Button(MainWindow.GO);
	Button bInit = new Button(MainWindow.NEW);
	Button bZurueck = new Button(MainWindow.BACK);
	ActionProcessor actionProcessor;
	private KaestchenListener kaestchenListener;
	private MyCanvas pictureArea;
	
	public MainWindow(String title, MyCanvas pictureArea, ActionProcessor actionProcessor) {
		this.setTitle(title);
		this.actionProcessor = actionProcessor;
		this.kaestchenListener = new KaestchenListener();
		this.pictureArea = pictureArea;
		pictureArea.addMouseListener(kaestchenListener);
		pictureArea.addMouseMotionListener(kaestchenListener);
        p1.setLayout(new BorderLayout());
        p1.add(pictureArea,BorderLayout.CENTER);
        p2.setLayout(new GridLayout(1,0));
        p25.add(cb2);
        p25.add(cb1);
        p27.add(bLos);
        p27.add(bInit);
        p27.add(bZurueck);
        p2.add(p25);
        p2.add(p27);
        this.setLayout(new BorderLayout());
        this.add(p1,BorderLayout.CENTER);
        this.add(p2,BorderLayout.SOUTH);
        
        bLos.addActionListener(this);
        bInit.addActionListener(this);
    	bZurueck.addActionListener(this);
        cb1.addItemListener(this);
        cb2.addItemListener(this);
	}

    
    public void itemStateChanged (ItemEvent ie) {
    	this.actionProcessor.paletteChange(((String)ie.getItem()).equals(MainWindow.GRAYSCALE_LABEL));
    } 
    
	private class KaestchenListener  extends MouseAdapter implements MouseMotionListener {
		
        public void mouseDragged(MouseEvent event) {
            mx2=event.getX();
            MainWindow.this.actionProcessor.selectingArea(
            		new Rectangle(mx1,my1,Math.abs(mx2-mx1),
            				(int)((double)MainWindow.this.pictureArea.getHeight()*(double)(Math.abs(mx2-mx1)/(double)MainWindow.this.pictureArea.getWidth() )))
            		);
    	}
        
        public void mouseMoved(MouseEvent mme){}
        
        public void mousePressed(MouseEvent mpe) {
            mx1=mpe.getX();
            my1=mpe.getY();
            MainWindow.this.actionProcessor.unselectArea();
        }
	        
        public void mouseReleased(MouseEvent mre) {
        	if(mre.getX() != mx1) {
        		MainWindow.this.actionProcessor.startCalculation();
        	}
        }
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(GO)) {
			MainWindow.this.actionProcessor.startCalculation();
		} else if(e.getActionCommand().equals(BACK)) {
			MainWindow.this.actionProcessor.goBack();
		} else if(e.getActionCommand().equals(NEW)) {
			MainWindow.this.actionProcessor.resetView();

		}
	}
}
