
public class ActionProcessor {

	private Mandel mandel;
	
	public ActionProcessor(Mandel mandel) {
		this.mandel = mandel;
	}

	public void paletteChange(boolean grayscale) {
		this.mandel.changePalette(grayscale);
	}

}
