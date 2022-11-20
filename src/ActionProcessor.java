import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class ActionProcessor implements ComponentListener {

	private Mandel mandel;
	
	public ActionProcessor(Mandel mandel) {
		this.mandel = mandel;
	}

	public void paletteChange(boolean grayscale) {
		this.mandel.changePalette(grayscale);
	}

	public void selectingArea(Rectangle rectangle) {
		this.mandel.setAreaSelection(rectangle);
	}

	public void unselectArea() {
		this.mandel.unselectArea();
	}

	public void startCalculation() {
		this.mandel.startCalculation();
	}

	public void goBack() {
		this.mandel.goBack();
	}

	public void resetView() {
		this.mandel.resetView();
	}

	@Override
	public void componentResized(ComponentEvent e) {
		this.mandel.canvasResized();
	}

	@Override
	public void componentMoved(ComponentEvent e) {}

	@Override
	public void componentShown(ComponentEvent e) {}

	@Override
	public void componentHidden(ComponentEvent e) {}

}
