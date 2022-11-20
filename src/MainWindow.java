import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.TextField;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class MainWindow  extends JFrame implements ItemListener {
	
	private static final String GRAYSCALE_LABEL = "S / W";
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
	Checkbox cb1        = new Checkbox(MainWindow.GRAYSCALE_LABEL,cbg,true);
	Checkbox cb2        = new Checkbox("Mach bunt!",cbg,false);
	Button bLos = new Button("    Los    ");
	Button bInit = new Button("    Neu    ");
	Button bZurueck = new Button(" Zurück ");
	ActionProcessor actionProcessor;
	public MainWindow(String title, myCanvas pictureArea, ActionProcessor actionProcessor) {
		this.setTitle(title);
		this.actionProcessor = actionProcessor;
        p1.setLayout(new BorderLayout());
        p1.add(pictureArea,BorderLayout.CENTER);
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
        
//        bLos.addActionListener(new losActionListener());
//        bInit.addActionListener(new initActionListener());
//    	bZurueck.addActionListener(new zurueckActionListener());
//        pictureArea.addMouseListener(new kaestchenListener());
//        pictureArea.addMouseMotionListener(new zieher());
        cb1.addItemListener(this);
        cb2.addItemListener(this);
	}
    public void itemStateChanged (ItemEvent ie) {
    	this.actionProcessor.paletteChange(((String)ie.getItem()).equals(MainWindow.GRAYSCALE_LABEL));
    	// ((String)ie.getItem()).equalsIgnoreCase("S / W"));
    }
	public void setViewportRectangle(Rectangle rectangle) {
        tfXmin.setText(((Double)rectangle.getX()).toString());
        tfXmax.setText(((Double)rectangle.getMaxX()).toString());
        tfYmin.setText(((Double)rectangle.getY()).toString());
        tfYmax.setText(((Double)rectangle.getMaxY()).toString());
	}

}
