import java.awt.Rectangle;

public class ActionProcessor {

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

}
